import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import MainLayout from "../components/layout/MainLayout";
import artistService from "../services/artistService";
import authService from "../services/auth";
import { useAuth } from "../context/AuthContext";

const ArtistDetailPage = () => {
  const { artistId } = useParams();
  const { user, isLoggedIn } = useAuth();
  const [artist, setArtist] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [followLoading, setFollowLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("concerts");

  useEffect(() => {
    const fetchArtistDetails = async () => {
      try {
        setLoading(true);
        setError(null);

        // 아티스트 상세 정보 DTO 가져오기
        const response = await artistService.getArtistById(artistId);
        setArtist(response.data);

        // 로그인 시 아티스트를 팔로우하고 있는지 확인
        if (isLoggedIn) {
          setIsFollowing(false);
        }
      } catch (err) {
        setError("아티스트 정보를 불러오는 데 실패했습니다.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchArtistDetails();
  }, [artistId, isLoggedIn]);

  // 팔로우 핸들러
  const handleFollow = async () => {
    setFollowLoading(true);
    try {
      await artistService.followArtist(artistId);
      setIsFollowing(true);
      // 팔로워 수 갱신을 위해 아티스트 정보 다시 불러오기
      setArtist((prev) => ({ ...prev, followerCount: prev.followerCount + 1 }));
    } catch (err) {
      alert(
        "팔로우에 실패했습니다: " + err.response?.data?.message || err.message
      );
    } finally {
      setFollowLoading(false);
    }
  };

  // 언팔로우 버튼 클릭 핸들러
  const handleUnfollow = async () => {
    setFollowLoading(true);
    try {
      await artistService.unfollowArtist(artistId);
      setIsUnfollowing(true); // UI 즉시 반영
      // 팔로워 수 갱신을 위해 artist 정보 다시 불러오기
      setArtist((prev) => ({ ...prev, followerCount: prev.followerCount - 1 }));
    } catch (err) {
      alert(
        "언팔로우에 실패했습니다: " + err.response?.data?.message || err.message
      );
    } finally {
      setFollowLoading(false);
    }
  };

  if (loading)
    return (
      <MainLayout>
        <div className="text-center">로딩 중...</div>
      </MainLayout>
    );
  if (error)
    return (
      <MainLayout>
        <div className="text-center text-red-500">{error}</div>
      </MainLayout>
    );
  if (!artist)
    return (
      <MainLayout>
        <div className="text-center">아티스트 정보 없음</div>
      </MainLayout>
    );

  return (
    <MainLayout>
      <div className="max-w-4wl mx-auto p-4">
        {/* 아티스트 프로필 헤더 */}
        <div className="flex flex-col md:flex-row items-center gap-6 p-4 border rounded-lg">
          <img
            src={artist.artistImageUrl || "https://placehold.co/150"}
            alt={artist.artistName}
            className="w-36 h-36 rounded-full object-cover border-4 border-gray-100"
          />
          <div className="flex-1 text-center md:text-left">
            <h1 className="text-4xl font-bold">{artist.artistName}</h1>
            <p className="text-lg text-gray-600 mt-2">
              팔로워: {artist.followerCount}명
            </p>
            <div className="flex gap-2 mt-2 justify-center md:justify-start">
              {artist.genres.map((genre) => (
                <span
                  key={genre.genreId}
                  className="px-2 py-0.5 bg-gray-200 text-sm rounded-full"
                >
                  {genre.genreName}
                </span>
              ))}
            </div>

            {/* 팔로우, 언팔로우 버튼 */}
            {isLoggedIn && (
              <div className="mt-4">
                {isFollowing ? (
                  <button
                    onClick={handleUnfollow}
                    disabled={followLoading}
                    className="px-6 py-2 bg-gray-200 text-gray-800 rounded-md font-semibold disabled:opacity-50"
                  >
                    {followLoading ? "..." : "팔로잉"}
                  </button>
                ) : (
                  <button
                    onClick={handleFollow}
                    disabled={followLoading}
                    className="px-6 py-2 bg-indigo-600 text-white rounded-md font-semibold hover:bg-indigo-700 disabled:opacity-50"
                  >
                    {followLoading ? "..." : "팔로우"}
                  </button>
                )}
              </div>
            )}
          </div>
        </div>

        {/* 아티스트 상세 탭 */}
        <div className="mt-8">
          <div className="flex border-b border-gray-200">
            <button
              onClick={() => setActiveTab("concerts")}
              className={`px-6 py-3 font-semibold ${
                activeTab === "concerts"
                  ? "text-indigo-600 border-b-2 border-indigo-600"
                  : "text-gray-500"
              }`}
            >
              공연 정보
            </button>
            <button
              onClick={() => setActiveTab("reviews")}
              className={`px-6 py-3 font-semibold ${
                activeTab === "reviews"
                  ? "text-indigo-600 border-b-2 border-indigo-600"
                  : "text-gray-500"
              }`}
            >
              관련 후기
            </button>
          </div>

          {/* 탭 콘텐츠 */}
          <div className="mt-6">
            {activeTab === "concerts" && (
              <div className="text-center p-10 bg-white rounded-lg shadow-sm">
                <p>{/* TODO: 해당 아티스트의 공연 목록 */}</p>
              </div>
            )}
            {activeTab === "reviews" && (
              <div className="text-center p-10 bg-white rounded-lg shadow-sm">
                <p>
                  {/* TODO: 해당 아티스트를 태그한 커뮤니티 후기 글 불러오기 - PostList 컴포넌트 재사용 */}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default ArtistDetailPage;
