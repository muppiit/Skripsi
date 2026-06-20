const DB_NAME = "cat_offline_exam";
const DB_VERSION = 2;
const ENCRYPTION_VERSION = 1;
const ENCRYPTION_KEY_STORAGE = "cat_offline_exam_key_v1";
const STORES = {
  examPackages: "exam_packages",
  answerDrafts: "answer_drafts",
  violationLogs: "violation_logs",
  syncQueue: "sync_queue",
  finalSubmissions: "final_submissions",
};

export const OFFLINE_PACKAGE_SEGMENTS = [
  "metadata",
  "ujian",
  "soalList",
  "peserta",
  "sessionContext",
  "timer",
  "security",
  "localState",
];

export const DOWNLOAD_STATUS = {
  pending: "PENDING",
  downloading: "DOWNLOADING",
  verified: "VERIFIED",
  complete: "COMPLETE",
  failed: "FAILED",
};

export const OFFLINE_UPLOAD_SEGMENTS = ["violations", "finalSubmission"];

export const UPLOAD_STATUS = {
  pending: "PENDING",
  uploading: "UPLOADING",
  uploaded: "UPLOADED",
  verified: "VERIFIED",
  complete: "COMPLETE",
  failed: "FAILED",
};

const stableStringify = (value) => {
  if (value === null || typeof value !== "object") {
    return JSON.stringify(value);
  }

  if (Array.isArray(value)) {
    return `[${value.map((item) => stableStringify(item)).join(",")}]`;
  }

  return `{${Object.keys(value)
    .sort()
    .map((key) => `${JSON.stringify(key)}:${stableStringify(value[key])}`)
    .join(",")}}`;
};

const bytesToBase64 = (bytes) => {
  const byteArray = new Uint8Array(bytes);
  let binary = "";
  byteArray.forEach((byte) => {
    binary += String.fromCharCode(byte);
  });
  return btoa(binary);
};

const base64ToBytes = (base64) =>
  Uint8Array.from(atob(base64), (char) => char.charCodeAt(0));

const bytesToHex = (bytes) =>
  Array.from(new Uint8Array(bytes))
    .map((byte) => byte.toString(16).padStart(2, "0"))
    .join("");

export const hashOfflinePayload = async (payload) => {
  if (!window.crypto?.subtle) {
    throw new Error("Browser tidak mendukung validasi checksum");
  }

  const encoded = new TextEncoder().encode(stableStringify(payload));
  const digest = await window.crypto.subtle.digest("SHA-256", encoded);
  return bytesToHex(digest);
};

export const createSegmentManifest = async (segments) => {
  const manifestSegments = {};

  for (const segmentName of OFFLINE_PACKAGE_SEGMENTS) {
    manifestSegments[segmentName] = {
      status: DOWNLOAD_STATUS.pending,
      checksum: await hashOfflinePayload(segments[segmentName] ?? null),
      verifiedAt: null,
    };
  }

  return {
    version: 1,
    strategy: "SEGMENTED_CHECKSUM",
    status: DOWNLOAD_STATUS.pending,
    segments: manifestSegments,
    packageChecksum: null,
    completedAt: null,
  };
};

export const createCompletePackageChecksum = (segments) =>
  hashOfflinePayload(
    OFFLINE_PACKAGE_SEGMENTS.reduce((result, segmentName) => {
      result[segmentName] = segments[segmentName] ?? null;
      return result;
    }, {})
  );

export const validateOfflineExamPackage = async (examPackage) => {
  if (!examPackage?.downloadManifest) return false;
  const manifest = examPackage.downloadManifest;

  if (manifest.status !== DOWNLOAD_STATUS.complete) return false;

  for (const segmentName of OFFLINE_PACKAGE_SEGMENTS) {
    const segment = manifest.segments?.[segmentName];
    if (segment?.status !== DOWNLOAD_STATUS.verified) return false;

    const checksum = await hashOfflinePayload(examPackage[segmentName] ?? null);
    if (checksum !== segment.checksum) return false;
  }

  const packageChecksum = await createCompletePackageChecksum(examPackage);
  return packageChecksum === manifest.packageChecksum;
};

export const createUploadManifest = async (submission) => {
  const segments = {
    violations: submission.violations || [],
    finalSubmission: {
      idUjian: submission.idUjian,
      idPeserta: submission.idPeserta,
      sessionId: submission.sessionId,
      answers: submission.answers || {},
      isAutoSubmit: submission.isAutoSubmit,
      finalTimeRemaining: submission.finalTimeRemaining,
      submittedAt: submission.submittedAt,
      metadata: submission.metadata || {},
    },
  };

  return {
    version: 1,
    strategy: "SEGMENTED_UPLOAD_IDEMPOTENCY",
    idempotencyKey:
      submission.idempotencyKey ||
      `${submission.idUjian || "unknown"}::${submission.idPeserta || "unknown"}::${
        submission.sessionId || "unknown"
      }`,
    status: UPLOAD_STATUS.pending,
    segments: {
      violations: {
        status: UPLOAD_STATUS.pending,
        checksum: await hashOfflinePayload(segments.violations),
        uploadedAt: null,
      },
      finalSubmission: {
        status: UPLOAD_STATUS.pending,
        checksum: await hashOfflinePayload(segments.finalSubmission),
        uploadedAt: null,
        serverResult: null,
      },
    },
    completedAt: null,
  };
};

const getEncryptionKey = async () => {
  if (!window.crypto?.subtle) {
    throw new Error("Browser tidak mendukung enkripsi lokal");
  }

  let rawKey = localStorage.getItem(ENCRYPTION_KEY_STORAGE);
  if (!rawKey) {
    const keyBytes = new Uint8Array(32);
    window.crypto.getRandomValues(keyBytes);
    rawKey = bytesToBase64(keyBytes);
    localStorage.setItem(ENCRYPTION_KEY_STORAGE, rawKey);
  }

  return window.crypto.subtle.importKey(
    "raw",
    base64ToBytes(rawKey),
    { name: "AES-GCM" },
    false,
    ["encrypt", "decrypt"]
  );
};

const encryptPayload = async (payload) => {
  const key = await getEncryptionKey();
  const iv = new Uint8Array(12);
  window.crypto.getRandomValues(iv);

  const plaintext = new TextEncoder().encode(JSON.stringify(payload));
  const encrypted = await window.crypto.subtle.encrypt(
    { name: "AES-GCM", iv },
    key,
    plaintext
  );

  return {
    id: payload.id,
    encrypted: true,
    encryptionVersion: ENCRYPTION_VERSION,
    algorithm: "AES-GCM",
    iv: bytesToBase64(iv),
    payload: bytesToBase64(encrypted),
    updatedAt: new Date().toISOString(),
  };
};

const decryptPayload = async (record) => {
  if (!record?.encrypted) return record;

  const key = await getEncryptionKey();
  const decrypted = await window.crypto.subtle.decrypt(
    { name: "AES-GCM", iv: base64ToBytes(record.iv) },
    key,
    base64ToBytes(record.payload)
  );

  const text = new TextDecoder().decode(decrypted);
  return JSON.parse(text);
};

const openOfflineExamDb = () =>
  new Promise((resolve, reject) => {
    if (!("indexedDB" in window)) {
      reject(new Error("Browser tidak mendukung IndexedDB"));
      return;
    }

    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = () => {
      const db = request.result;

      Object.values(STORES).forEach((storeName) => {
        if (!db.objectStoreNames.contains(storeName)) {
          db.createObjectStore(storeName, { keyPath: "id" });
        }
      });
    };

    request.onsuccess = () => resolve(request.result);
    request.onerror = () =>
      reject(request.error || new Error("Gagal membuka IndexedDB"));
  });

const runStoreAction = async (storeName, mode, action) => {
  const db = await openOfflineExamDb();

  return new Promise((resolve, reject) => {
    const transaction = db.transaction(storeName, mode);
    const store = transaction.objectStore(storeName);
    const request = action(store);

    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
    transaction.oncomplete = () => db.close();
    transaction.onerror = () => {
      db.close();
      reject(transaction.error);
    };
  });
};

export const buildOfflineExamPackageId = (idUjian, idPeserta) =>
  `${idUjian || "unknown"}::${idPeserta || "unknown"}`;

export const saveOfflineExamPackage = (examPackage) =>
  encryptPayload(examPackage).then((encryptedPackage) =>
    runStoreAction(STORES.examPackages, "readwrite", (store) =>
      store.put(encryptedPackage)
    )
  );

export const getOfflineExamPackage = (idUjian, idPeserta) =>
  runStoreAction(STORES.examPackages, "readonly", (store) =>
    store.get(buildOfflineExamPackageId(idUjian, idPeserta))
  ).then(decryptPayload);

export const saveOfflineAnswerDraft = (draft) =>
  encryptPayload(draft).then((encryptedDraft) =>
    runStoreAction(STORES.answerDrafts, "readwrite", (store) =>
      store.put(encryptedDraft)
    )
  );

export const addOfflineSyncQueueItem = (item) =>
  encryptPayload(item).then((encryptedItem) =>
    runStoreAction(STORES.syncQueue, "readwrite", (store) =>
      store.put(encryptedItem)
    )
  );

export const getOfflineSyncQueueItems = () =>
  runStoreAction(STORES.syncQueue, "readonly", (store) =>
    store.getAll()
  ).then((items) => Promise.all(items.map(decryptPayload)));

export const removeOfflineSyncQueueItem = (id) =>
  runStoreAction(STORES.syncQueue, "readwrite", (store) => store.delete(id));

export const addOfflineViolationLog = (log) =>
  encryptPayload(log).then((encryptedLog) =>
    runStoreAction(STORES.violationLogs, "readwrite", (store) =>
      store.put(encryptedLog)
    )
  );

export const getOfflineViolationLogs = () =>
  runStoreAction(STORES.violationLogs, "readonly", (store) =>
    store.getAll()
  ).then((logs) => Promise.all(logs.map(decryptPayload)));

export const saveOfflineFinalSubmission = (submission) =>
  encryptPayload(submission).then((encryptedSubmission) =>
    runStoreAction(STORES.finalSubmissions, "readwrite", (store) =>
      store.put(encryptedSubmission)
    )
  );

export const getOfflineFinalSubmission = (idUjian, idPeserta) =>
  runStoreAction(STORES.finalSubmissions, "readonly", (store) =>
    store.get(buildOfflineExamPackageId(idUjian, idPeserta))
  ).then(decryptPayload);

export const removeOfflineFinalSubmission = (idUjian, idPeserta) =>
  runStoreAction(STORES.finalSubmissions, "readwrite", (store) =>
    store.delete(buildOfflineExamPackageId(idUjian, idPeserta))
  );
