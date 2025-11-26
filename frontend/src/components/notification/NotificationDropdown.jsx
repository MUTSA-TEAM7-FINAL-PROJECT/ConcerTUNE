import React, { useState, useEffect, useRef } from "react";
import notificationService from "../../services/notificationService";
import { useAuth } from "../../context/AuthContext"; // AuthContext에서 유저 정보 가져옴
import { Link } from "react-router-dom";
import { Client } from "@stomp/stompjs"; // npm install @stomp/stompjs
import SockJS from "sockjs-client"; // npm install sockjs-client

const NotificationDropdown = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasNew, setHasNew] = useState(false); // 새 알림 표시 상태

  const { isLoggedIn, user } = useAuth();
  const stompClient = useRef(null);

  // 1. 초기 알림 로드
  useEffect(() => {
    if (isLoggedIn) {
      fetchNotifications();
    }
  }, [isLoggedIn]);

  // 2. WebSocket 연결 (실시간 알림)
  useEffect(() => {
    if (isLoggedIn && user?.id) {
      connectWebSocket();
    }

    return () => {
      if (stompClient.current) {
        stompClient.current.deactivate();
      }
    };
  }, [isLoggedIn, user]);

  const connectWebSocket = () => {
    // API_URL이 http://localhost:8080 이라면 ws는 http://localhost:8080/ws
    const socketUrl = `${
      import.meta.env.VITE_API_URL || "http://localhost:8080"
    }/ws`;

    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      reconnectDelay: 5000, // 연결 끊기면 5초 뒤 재연결 시도
      onConnect: () => {
        console.log("WebSocket Connected for Notifications!");

        // 내 전용 알림 큐 구독: /queue/notifications/{userId}
        client.subscribe(`/queue/notifications/${user.id}`, (message) => {
          if (message.body) {
            const newNotification = JSON.parse(message.body);
            handleNewNotification(newNotification);
          }
        });
      },
      onStompError: (frame) => {
        console.error("Broker reported error: " + frame.headers["message"]);
        console.error("Additional details: " + frame.body);
      },
    });

    client.activate();
    stompClient.current = client;
  };

  const handleNewNotification = (notification) => {
    // 리스트 맨 앞에 새 알림 추가
    setNotifications((prev) => [notification, ...prev]);
    setHasNew(true); // 종소리에 빨간 점 표시

    // (선택) 브라우저 네이티브 알림 띄우기
    if (Notification.permission === "granted") {
      new Notification("ConcerTUNE 새 알림", { body: notification.content });
    }
  };

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const response = await notificationService.getMyNotifications("false");
      setNotifications(response.data);
      if (response.data.length > 0) setHasNew(true);
    } catch (error) {
      console.error("알림 로딩 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (userNotificationId) => {
    try {
      await notificationService.markAsRead(userNotificationId);
      setNotifications((prev) =>
        prev.filter((n) => n.id !== userNotificationId)
      );
      if (notifications.length <= 1) setHasNew(false);
    } catch (error) {
      console.error("알림 읽음 처리 실패:", error);
    }
  };

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
    if (!isOpen && hasNew) {
      // 열 때 빨간 점은 유지하되, 필요하다면 여기서 끌 수도 있음
      // setHasNew(false);
    }
  };

  if (!isLoggedIn) return null;

  return (
    <div className="relative">
      <button
        onClick={toggleDropdown}
        className="relative p-2 rounded-full hover:bg-gray-100 focus:outline-none"
      >
        <span className="text-2xl">🔔</span>
        {hasNew && (
          <span className="absolute top-1 right-1 block h-3 w-3 rounded-full bg-red-500 ring-2 ring-white animate-pulse" />
        )}
      </button>

      {/* 드롭다운 메뉴 */}
      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white border border-gray-200 rounded-lg shadow-xl z-50">
          <div className="p-4 font-bold border-b flex justify-between items-center bg-gray-50 rounded-t-lg">
            <span>알림함</span>
            <button
              onClick={fetchNotifications}
              className="text-xs text-indigo-600 hover:underline"
            >
              새로고침
            </button>
          </div>
          <div className="max-h-96 overflow-y-auto">
            {loading ? (
              <div className="p-4 text-center text-gray-500">로딩 중...</div>
            ) : notifications.length === 0 ? (
              <div className="p-8 text-center text-gray-500 flex flex-col items-center">
                <span className="text-4xl mb-2">📭</span>
                새로운 알림이 없습니다.
              </div>
            ) : (
              notifications.map((notif) => (
                <div
                  key={notif.id}
                  className="border-b hover:bg-purple-50 transition-colors"
                >
                  <Link
                    to={notif.link || "#"}
                    onClick={() => handleMarkAsRead(notif.id)}
                    className="block p-4"
                  >
                    <p className="text-sm text-gray-800 font-medium mb-1">
                      {notif.content}
                    </p>
                    <div className="text-xs text-gray-400">
                      {new Date(notif.createdAt).toLocaleString()}
                    </div>
                  </Link>
                </div>
              ))
            )}
          </div>
          {notifications.length > 0 && (
            <Link
              to="/notifications"
              className="block p-3 text-center text-sm text-indigo-600 font-bold border-t hover:bg-gray-50 rounded-b-lg"
            >
              전체 알림 보기
            </Link>
          )}
        </div>
      )}
    </div>
  );
};

export default NotificationDropdown;
