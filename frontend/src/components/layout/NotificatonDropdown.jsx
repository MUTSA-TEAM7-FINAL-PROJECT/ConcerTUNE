import React, { useState, useEffect } from "react";
import notificationService from "../../services/notificationService";
import authService from "../../services/auth";
import { Link } from "react-router-dom";

const NotificationDropdown = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasUnread, setHasUnread] = useState(false);
  const isLoggedIn = authService.isAuthenticated();

  // 안 읽은 알림 상태 조회
  const fetchUnreadStatus = async () => {
    try {
      const res = await notificationService.hasUnread(); // boolean 반환
      setHasUnread(res.data);
    } catch (err) {
      console.error("안 읽은 알림 조회 실패", err);
    }
  };

  // 알림 목록 조회
  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const res = await notificationService.getMyNotifications("false"); // 안 읽은 알림만
      setNotifications(res.data);
    } catch (err) {
      console.error("알림 로딩 실패", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isLoggedIn) fetchUnreadStatus();
  }, [isLoggedIn]);

  useEffect(() => {
    if (isOpen && isLoggedIn) fetchNotifications();
  }, [isOpen, isLoggedIn]);

  const handleMarkAsRead = async (userNotificationId) => {
    try {
      await notificationService.markAsRead(userNotificationId);
      setNotifications((prev) => prev.filter((n) => n.id !== userNotificationId));
      fetchUnreadStatus(); // 빨간 점 업데이트
    } catch (err) {
      console.error("알림 읽음 처리 실패", err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
      fetchUnreadStatus(); // 빨간 점 업데이트
    } catch (err) {
      console.error("모든 알림 읽음 처리 실패", err);
    }
  };

  if (!isLoggedIn) return null;

  return (
    <div className="relative">
      {/* 알림 버튼 */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 rounded-full hover:bg-gray-100"
      >
        🔔
        {hasUnread && (
          <span className="absolute top-0 right-0 block h-2 w-2 rounded-full bg-red-500 ring-2 ring-white" />
        )}
      </button>

      {/* 드롭다운 메뉴 */}
      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white border border-gray-200 rounded-lg shadow-lg z-20">
          {/* 상단: 알림 제목 + 모두 읽음 처리 버튼 */}
          <div className="flex justify-between items-center p-3 font-bold border-b">
            <span>알림</span>
            <button
              onClick={handleMarkAllAsRead}
              className="text-sm text-indigo-600 hover:underline"
            >
              모두 읽음
            </button>
          </div>

          {/* 알림 목록 */}
          <div className="max-h-96 overflow-y-auto">
            {loading ? (
              <div className="p-4 text-center text-gray-500">로딩 중...</div>
            ) : notifications.length === 0 ? (
              <div className="p-4 text-center text-gray-500">새 알림이 없습니다.</div>
            ) : (
              notifications.map((notif) => (
                <div key={notif.id} className="border-b hover:bg-gray-50">
                  <Link
                    to={notif.link || "#"}
                    onClick={() => handleMarkAsRead(notif.id)}
                    className="block p-3 text-sm"
                  >
                    {notif.content}
                    <div className="text-xs text-gray-400 mt-1">
                      {new Date(notif.createAt).toLocaleString()}
                    </div>
                  </Link>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationDropdown;
