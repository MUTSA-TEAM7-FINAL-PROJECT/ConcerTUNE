import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import userService from "../services/userService";

const UserPage = () => {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState("posts");
  const [isFollowing, setIsFollowing] = useState(false);

  useEffect(() => {
    const loadUser = async () => {
      const data = await userService.getUserProfile(userId);
      setUser(data);
    };
    loadUser();
  }, [userId]);

  const handleFollow = async () => {
    await userService.toggleFollow(userId);
    setIsFollowing(!isFollowing);
  };

  if (!user) return <div className="text-center p-10">로딩 중...</div>;

  return (
    <section className="max-w-4xl mx-auto p-4">
      <div className="flex flex-col md:flex-row items-center gap-8 p-6 bg-white rounded-xl shadow">
        <img
          src={user.profileImageUrl || "/default-profile.png"}
          className="w-36 h-36 rounded-full object-cover border"
        />

        <div className="flex-1 grid gap-4">
          <div className="p-4 border rounded-lg shadow-sm">
            <p className="text-xl font-semibold">{user.username}</p>
            <p className="text-gray-600">{user.bio || "소개 없음"}</p>
          </div>

          <div className="p-4 border rounded-lg shadow-sm">
            <p className="font-semibold mb-2">공연 활동</p>
            <p className="text-gray-500 text-sm">추후 구현 예정</p>
          </div>
        </div>

        <button
          onClick={handleFollow}
          className={`px-4 py-2 rounded-lg font-semibold ${
            isFollowing
              ? "bg-gray-200 text-gray-700"
              : "bg-indigo-600 text-white hover:bg-indigo-700"
          }`}
        >
          {isFollowing ? "팔로잉" : "팔로우"}
        </button>
      </div>

      <div className="mt-10">
        <div className="flex border-b">
          {[
            ["posts", "작성한 글"],
            ["reviews", "공연 후기"],
          ].map(([key, label]) => (
            <button
              key={key}
              onClick={() => setActiveTab(key)}
              className={`px-6 py-3 font-semibold ${
                activeTab === key
                  ? "text-indigo-600 border-b-2 border-indigo-600"
                  : "text-gray-500"
              }`}
            >
              {label}
            </button>
          ))}
        </div>

        <div className="mt-6 p-4 bg-white rounded-xl shadow min-h-[300px]">
          <p className="text-center text-gray-500">
            {activeTab} 내용은 추후 구현 예정입니다.
          </p>
        </div>
      </div>
    </section>
  );
};

export default UserPage;
