import React, { useState, useEffect } from "react";
import userService from "../services/userService";
import { Link } from "react-router-dom";

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [imgLoading, setImgLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("followers");

  const API_URL = import.meta.env.VITE_API_URL;

  // 안전한 이미지 URL 생성
  const getSafeProfileImage = () => {
    const url = user?.profileImageUrl;

    if (!url || url.trim() === "") {
      return defaultProfile; // 기본 이미지
    }

    // 외부 절대 URL (Google, Spotify)
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    // 서버 내부 URL (예: /api/upload/profiles/xxx)
    return `${API_URL}${url}`;
  };

  // 이미지 업로드
  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setImgLoading(true);

    try {
      const updatedUser = await userService.uploadProfileImage(file);
      setUser(updatedUser);
      localStorage.setItem("user", JSON.stringify(updatedUser));
    } catch (err) {
      console.error("이미지 업로드 실패:", err);
    } finally {
      setImgLoading(false);
    }
  };

  // 이미지 삭제
  const handleDeleteImage = async () => {
    if (!confirm("프로필 이미지를 삭제하시겠습니까?")) return;

    setImgLoading(true);

    try {
      const updatedUser = await userService.deleteProfileImage();
      console.log("삭제 후 서버 반환 데이터:", updatedUser);

      setUser(updatedUser);
      localStorage.setItem("user", JSON.stringify(updatedUser));
    } catch (err) {
      console.error("이미지 삭제 실패:", err);
    } finally {
      setImgLoading(false);
    }
  };

  // 내 프로필 불러오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await userService.getMyProfile();
        setUser(data);
      } catch (err) {
        console.error("내 프로필 조회 오류:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <div className="text-center p-10">로딩 중...</div>;
  if (!user)
    return <div className="text-center p-10">프로필을 불러올 수 없습니다.</div>;

  return (
    <section className="max-w-4xl mx-auto p-4">
      <div className="flex flex-col md:flex-row items-center gap-8 p-6 bg-white rounded-xl shadow">
        {/* 프로필 이미지 */}
        <div className="relative">
          {user && (
            <img
              src={getSafeProfileImage()}
              className="w-36 h-36 rounded-full object-cover border"
            />
          )}

          {imgLoading && (
            <div className="absolute inset-0 bg-white/70 flex items-center justify-center text-sm">
              처리중...
            </div>
          )}

          {/* 이미지 변경 버튼 */}
          <label className="absolute bottom-0 right-0 bg-indigo-600 text-white px-2 py-1 rounded-lg cursor-pointer text-sm">
            변경
            <input
              type="file"
              accept="image/*"
              onChange={handleImageUpload}
              className="hidden"
            />
          </label>

          {/* 삭제 버튼 */}
          {user?.profileImageUrl && (
            <button
              onClick={handleDeleteImage}
              className="bg-red-500 text-white px-2 py-1 rounded-lg text-sm ml-2"
            >
              삭제
            </button>
          )}
        </div>

        {/* 프로필 정보 */}
        <div className="flex-1 grid grid-cols-1 gap-4">
          <div className="p-4 border rounded-lg shadow-sm">
            <p className="text-xl font-semibold">{user.username}</p>
            <p className="text-gray-600">{user.bio || "소개가 없습니다."}</p>
          </div>

          <div className="p-4 border rounded-lg shadow-sm">
            <p className="font-semibold mb-2">내가 보거나 볼 공연 정보</p>
            <p className="text-gray-500 text-sm">추후 구현 예정</p>
          </div>
        </div>

        <Link
          to="/users/me/edit"
          className="px-4 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700"
        >
          수정하기
        </Link>
      </div>

      {/* 탭 영역 */}
      <div className="mt-10">
        <div className="flex border-b">
          {[
            ["followers", "팔로우 피드"],
            ["interest", "관심 등록 공연"],
            ["posts", "작성한 글"],
            ["reviews", "작성한 공연 후기"],
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

      <div className="mt-10 text-right">
        <button className="text-red-500 font-semibold">계정 삭제하기</button>
      </div>
    </section>
  );
};

export default ProfilePage;
