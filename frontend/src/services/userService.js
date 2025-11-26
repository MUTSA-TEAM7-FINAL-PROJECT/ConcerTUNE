import api from "./api";

const userService = {
  getFollowArtistFeeds: async () => {
    try {
      const response = await api.get(`/api/users/feeds/follow`);
      return response.data;
    } catch (err) {
      console.error("팔로우 아티스트 피드 조회 실패:", err);
      throw new Error(
        err.response?.data?.message || "팔로우 피드를 불러오는 데 실패했습니다."
      );
    }
  },

  getMyProfile: async () => {
    const res = await api.get("/api/users/me");
    return res.data;
  },

  // --- 32-유저-프로필-및-팔로우-프론트 기능 통합 시작 ---

  getUserProfile: async (userId) => {
    const res = await api.get(`/api/users/${userId}`);
    return res.data;
  },

  updateProfile: async (data) => {
    const res = await api.patch("/api/users/me", data);
    return res.data;
  },

  uploadProfileImage: async (file) => {
    const formData = new FormData();
    formData.append("image", file);

    const res = await api.post("/api/users/me/profile-image", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return res.data;
  },

  deleteProfileImage: async () => {
    const res = await api.delete("/api/users/me/profile-image");
    return res.data;
  },

  toggleFollow: async (targetId) => {
    const res = await api.post(`/api/users/${targetId}/follow`);
    return res.data;
  },

  // --- develop 기능 통합 시작 ---

  getPersonalizedSchedules: async (userId) => {
    try {
      console.log("Fetching personalized schedules for userId:", userId);
      const response = await api.get("/api/schedules/personalized", {
        params: { userId },
      });
      if (response.status === 204) {
        return [];
      }
      return response.data;
    } catch (err) {
      console.error("개인화된 스케줄 조회 실패:", err);
      throw new Error(
        err.response?.data?.message || "스케줄을 불러오는 데 실패했습니다."
      );
    }
  },
};

export default userService;
