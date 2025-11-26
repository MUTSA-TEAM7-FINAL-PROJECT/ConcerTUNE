import api from "./api";

const subscriptionService = {
  initSubscription: async (artistId) => {
    try {
      const res = await api.post("/api/subscription/init", { artistId });
      return res.data; 
    } catch (error) {
      console.error("구독 초기화 실패:", error);
      throw error;
    }
  },

confirmSubscription: async ({ orderId, amount, authKey }) => {
    try {
      console.log("confirmSubscription 호출 (PARAM 방식):", { orderId, amount, authKey });
      
      const res = await api.get("/api/subscription/toss/success", { 
        params: {
            authKey,
            orderId,
            amount,
        }
      });
      
      return res.data;
    } catch (error) {
      console.error("구독 최종 승인 실패:", error);
      throw error; 
    }
  },

    getSubscriptionStatus: async (artistId) => {
    try {
        const res = await api.get(`/api/subscription/status/${artistId}`);
        return res.data.subscribed; // true / false
    } catch (error) {
        console.error("구독 상태 조회 실패:", error);
        return false;
    }
    },

    getSubscriptionDetail: async (artistId) => {
    try {
        const res = await api.get(`/api/subscription/detail/${artistId}`);
        return res.data; 
    } catch (error) {
        console.error("구독 상세 조회 실패:", error);
        throw error;
    }
    },
    cancelSubscription: async (artistId) => {
    try {
        const res = await api.delete(`/api/subscription/cancel/${artistId}`);
        return res.data;
    } catch (error) {
        console.error("구독 취소 실패:", error);
        throw error;
    }
    },
  
}

export default subscriptionService;
