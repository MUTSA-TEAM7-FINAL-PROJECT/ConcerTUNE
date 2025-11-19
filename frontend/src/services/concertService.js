import api from "./api";


const API_URL = 'http://localhost:8080/api';

const concertService = {
 
 getConcerts: async (params) => {
    try {
      const response = await api.get(`/api/lives`, { params });
      return response.data; 
    } catch (err) {
      console.error("공연 목록 조회 실패:", err);
      throw new Error(err.response?.data?.message || "공연 목록을 불러오는 데 실패했습니다.");
    }
  },

  getConcert: async (concertId) => {
    try {
      const response = await api.get(`/api/lives/${concertId}`,);
      return response.data; 
    } catch (err) {
      console.error("공연 목록 조회 실패:", err);
      throw new Error(err.response?.data?.message || "공연 목록을 불러오는 데 실패했습니다.");
    }
  },

  submitLiveRequest: async (requestDto) => {
        try {
            const response = await api.post(`/api/liveRequest`, requestDto);
            return response.data; 
        } catch (err) {
            console.error("공연 요청 등록 실패:", err);
            throw new Error(err.response?.data?.message || "공연 요청 등록에 실패했습니다.");
        }
    },

    getLatest4Concerts: async () => {
      try {
          const response = await api.get(`/api/lives/latest`); 
          return response.data;
      } catch (err) {
          console.error("최신 공연 4개 조회 실패:", err);
          throw new Error(err.response?.data?.message || "최신 공연 목록을 불러오는 데 실패했습니다.");
      }
    },

    getPersonalizedConcerts: async () => {
        try {
            const response = await api.get(`/api/lives/personalized`); 
            return response.data;
        } catch (err) {
            console.error("맞춤 추천 공연 조회 실패:", err);
            throw new Error(err.response?.data?.message || "맞춤 추천 공연 목록을 불러오는 데 실패했습니다.");
        }
    },
};

export default concertService;