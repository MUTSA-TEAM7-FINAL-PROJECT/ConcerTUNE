import api from "./api";

const userService = {
  // 특정 유저 프로필 조회
  getUserProfile: async (userId) => {
    try {
      const res = await api.get(`/api/users/${userId}`);
      return res.data;
    } catch (error) {
      console.error("유저 정보 조회 실패:", error);
      throw error;
    }
  },

  // 팔로우 상태 확인
  checkFollowStatus: async (userId) => {
    try {
      const res = await api.get(`/api/users/${userId}/is-following`);
      return res.data; // Boolean
    } catch (error) {
      console.error("팔로우 상태 조회 실패:", error);
      throw error;
    }
  },

  // 팔로우 / 언팔로우
  toggleFollow: async (userId) => {
    try {
      const res = await api.post(`/api/users/${userId}/follow`);
      return res.data;
    } catch (error) {
      console.error("팔로우/언팔로우 실패:", error);
      throw error;
    }
  },

  // 팔로워 리스트
  getFollowers: async (userId, page = 0, size = 20) => {
    try {
      const res = await api.get(`/api/users/${userId}/followers`, {
        params: { page, size },
      });
      return res.data;
    } catch (error) {
      console.error("팔로워 목록 조회 실패:", error);
      return { content: [], totalPages: 0 };
    }
  },

  // 팔로잉 리스트
  getFollowings: async (userId, page = 0, size = 20) => {
    try {
      const res = await api.get(`/api/users/${userId}/followings`, {
        params: { page, size },
      });
      return res.data;
    } catch (error) {
      console.error("팔로잉 목록 조회 실패:", error);
      return { content: [], totalPages: 0 };
    }
  },

  // 내 콘텐츠(북마크, 작성글 등)
  getUserContents : async (userID) => {
    try {
      const res = await api.get(`/api/users/${userID}/contents`);
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

export default userService;
