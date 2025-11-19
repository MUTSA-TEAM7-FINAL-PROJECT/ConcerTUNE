import api from "./api";

const artistService = {
  // 아티스트 목록 조회
  getArtists: (name, pageable) => {
    const params = new URLSearchParams();
    if (name) {
      params.append("name", name);
    }
    if (pageable) {
      params.append("page", pageable.page); 
      params.append("size", pageable.size);
    }
    return api.get(`/api/artists?${params.toString()}`);
  },

  getArtistsAll: async () => { 
      try {
          const response = await api.get(`/api/artists/all`); 
          
          console.log("All Artists Response:", response);
          
          return response.data; 
          
      } catch (err) {
          console.error("아티스트 목록 조회 실패:", err);
          throw new Error(err.response?.data?.message || "아티스트 목록을 불러오는 데 실패했습니다.");
      }
  },

  // 아티스트 상세 정보 조회
  getArtistById: (artistId) => {
    return api.get(`/api/artists/${artistId}`);
  },

  // 아티스트 팔로우
  followArtist: (artistId) => {
    return api.post(`/api/artists/${artistId}/follow`);
  },

  // 아티스트 언팔로우
  unfollowArtist: (artistId) => {
    return api.delete(`/api/artists/${artistId}/follow`);
  },

  // 공연 등록 요청
  requestLiveConcert: (requestData) => {
    return api.post("/api/artists/requests", requestData);
  },
};

export default artistService;
