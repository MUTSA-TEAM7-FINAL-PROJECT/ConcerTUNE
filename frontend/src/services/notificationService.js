import api from "./api";

const notificationService = {
  // 내 알림 목록 조회
  getMyNotifications: async (isReadParam = "false") => {
    try {
      const res = await api.get(`/api/notifications/me?isRead=${isReadParam}`);
      return res;
    } catch (error) {
      console.error("알림 목록 조회 실패:", error);
      throw error;
    }
  },

  // 특정 알림 읽음 처리
  markAsRead: async (userNotificationId) => {
    try {
      const res = await api.patch(`/api/notifications/${userNotificationId}/read`);
      return res;
    } catch (error) {
      console.error("알림 읽음 처리 실패:", error);
      throw error;
    }
  },

  // 모든 알림 읽음 처리
  markAllAsRead: async () => {
    try {
      const res = await api.post("/api/notifications/me/read-all");
      return res;
    } catch (error) {
      console.error("모든 알림 읽음 처리 실패:", error);
      throw error;
    }
  },

  // 안 읽은 알림 유무 조회
  hasUnread: async () => {
    try {
      const res = await api.get("/api/notifications/me/unread-exists"); // 백엔드에서 boolean 반환
      return res;
    } catch (error) {
      console.error("안 읽은 알림 조회 실패:", error);
      throw error;
    }
  },
};

export default notificationService;
