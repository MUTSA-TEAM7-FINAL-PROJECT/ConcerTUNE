import api from "./api";

const myPageService = {
  // 내 프로필 불러오기
  getMyProfile: async () => {
    try {
      const res = await api.get("/api/users/me");
      return res.data;
    } catch (error) {
      console.error("내 프로필 조회 실패:", error);
      throw error;
    }
  },

  // 내 프로필 수정 (username, bio, phone, tags)
  updateMyProfile: async (updatedData) => {
    try {
      const res = await api.patch("/api/users/me", updatedData);
      return res.data;
    } catch (error) {
      console.error("내 프로필 수정 실패:", error);
      throw error;
    }
  },

  // 내 프로필 이미지 수정
  updateMyProfileImage: async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await api.patch("/api/users/me/profile-image", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      return res.data;
    } catch (error) {
      console.error("프로필 사진 변경 실패:", error);
      throw error;
    }
  },

  // 내 콘텐츠(북마크, 작성글 등)
  getMyContents: async () => {
    try {
      const res = await api.get("/api/users/me/contents");
      return res.data;
    } catch (error) {
      console.error("내 콘텐츠 조회 실패:", error);
      return {
        bookmarkedLives: [],
        followedArtists: [],
        myPosts: [],
      };
    }
  },
};

export default myPageService;
