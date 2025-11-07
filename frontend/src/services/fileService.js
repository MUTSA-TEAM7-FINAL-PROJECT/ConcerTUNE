import api from "./api";

const fileService = {
  // 파일을 S3에 업로드하고 URL 반환
  uploadFile: async (file, dir) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("dir", dir);

    try {
      // POST /api/files/upload
      const response = await api.post("/api/files/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      return response.data.url;
    } catch (err) {
      console.error("파일 업로드 실패:", err);
      throw new Error(
        err.response?.data?.message || "파일 업로드에 실패했습니다."
      );
    }
  },
};

export default fileService;
