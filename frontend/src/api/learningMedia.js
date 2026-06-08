import request from "@/utils/request";

export function addLearningMedia(data) {
  return request({
    url: "/learning-media",
    method: "post",
    data,
  });
}

export function getLearningMedias() {
  return request({
    url: "/learning-media?size=1000",
    method: "get",
  });
}

export function getLearningMediasSoftware(size = 1000) {
  return getLearningMedias(size).then((response) => {
    const content = response?.data?.content || [];
    return {
      ...response,
      data: {
        ...response.data,
        content: content.filter((item) => {
          const type = String(item?.type || "").toLowerCase();
          return type === "software" || type === "1";
        }),
      },
    };
  });
}

export function getLearningMediasHardware(size = 1000) {
  return getLearningMedias(size).then((response) => {
    const content = response?.data?.content || [];
    return {
      ...response,
      data: {
        ...response.data,
        content: content.filter((item) => {
          const type = String(item?.type || "").toLowerCase();
          return (
            type === "hardware" ||
            type === "2" ||
            type === "hybrid" ||
            type === "other" ||
            type === "lainnya" ||
            type === "lainya"
          );
        }),
      },
    };
  });
}

export function editLearningMedia(data, id) {
  return request({
    url: `/learning-media/${id}`,
    method: "put",
    data,
  });
}

export function deleteLearningMedia(data) {
  return request({
    url: `/learning-media/${data.id}`,
    method: "delete",
    data,
  });
}
