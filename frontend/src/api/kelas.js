import request from "@/utils/request";

export function addKelas(data) {
  return request({
    url: "/kelas",
    method: "post",
    data,
  });
}

export function getKelas(studyProgramID) {
  return request({
    url: "/kelas",
    method: "get",
    params: studyProgramID ? { studyProgramID } : undefined,
  });
}

export function editKelas(data, id) {
  return request({
    url: `/kelas/${id}`,
    method: "put",
    data,
  });
}

export function deleteKelas(data) {
  return request({
    url: `/kelas/${data.idKelas}`,
    method: "delete",
    data,
  });
}
