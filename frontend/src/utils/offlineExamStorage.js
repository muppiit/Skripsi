const DB_NAME = "cat_offline_exam";
const DB_VERSION = 2;
const STORES = {
  examPackages: "exam_packages",
  answerDrafts: "answer_drafts",
  violationLogs: "violation_logs",
  syncQueue: "sync_queue",
  finalSubmissions: "final_submissions",
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
  runStoreAction(STORES.examPackages, "readwrite", (store) =>
    store.put(examPackage)
  );

export const getOfflineExamPackage = (idUjian, idPeserta) =>
  runStoreAction(STORES.examPackages, "readonly", (store) =>
    store.get(buildOfflineExamPackageId(idUjian, idPeserta))
  );

export const saveOfflineAnswerDraft = (draft) =>
  runStoreAction(STORES.answerDrafts, "readwrite", (store) => store.put(draft));

export const addOfflineSyncQueueItem = (item) =>
  runStoreAction(STORES.syncQueue, "readwrite", (store) => store.put(item));

export const getOfflineSyncQueueItems = () =>
  runStoreAction(STORES.syncQueue, "readonly", (store) => store.getAll());

export const removeOfflineSyncQueueItem = (id) =>
  runStoreAction(STORES.syncQueue, "readwrite", (store) => store.delete(id));

export const addOfflineViolationLog = (log) =>
  runStoreAction(STORES.violationLogs, "readwrite", (store) => store.put(log));

export const getOfflineViolationLogs = () =>
  runStoreAction(STORES.violationLogs, "readonly", (store) => store.getAll());

export const saveOfflineFinalSubmission = (submission) =>
  runStoreAction(STORES.finalSubmissions, "readwrite", (store) =>
    store.put(submission)
  );

export const getOfflineFinalSubmission = (idUjian, idPeserta) =>
  runStoreAction(STORES.finalSubmissions, "readonly", (store) =>
    store.get(buildOfflineExamPackageId(idUjian, idPeserta))
  );

export const removeOfflineFinalSubmission = (idUjian, idPeserta) =>
  runStoreAction(STORES.finalSubmissions, "readwrite", (store) =>
    store.delete(buildOfflineExamPackageId(idUjian, idPeserta))
  );
