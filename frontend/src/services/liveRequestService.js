import api from "./api";

const liveRequestService = {
   getAllLiveRequestsForAdmin: async (pageableParams = {}) => {
        
        try {
            // 엔드포인트를 변수 없이 직접 사용
            const response = await api.get(`/api/liveRequest/list`, { 
                params: pageableParams 
            });
            
            // 서버는 Page<LiveRequestResponse> 객체를 반환한다고 가정
            return response.data; 
        } catch (err) {
            console.error("관리자 공연 요청 목록 조회 실패:", err);
            // 권한 없음 (403), 서버 오류 등 처리
            throw new Error(err.response?.data?.message || "요청 목록을 불러오는 데 실패했습니다.");
        }
    },
};

export default liveRequestService;