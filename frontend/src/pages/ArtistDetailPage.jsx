import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import MainLayout from "../components/layout/MainLayout";
import artistService from "../services/artistService";
import authService from "../services/auth";

const ArtistDetailPage = () => {
  const artistId = useParams();
  const [artist, setArtist] = useState(null);
  const [loading, setLoading] = useStatae(true);
  const [error, setError] = useState(null);

  // 로그인 상태 확인
  const isLoggedIn = authService.isAuthenticated();
  const [isFollowing, setIsFollowing] = useState(false);

  useEffect(() => {
    const fetchArtist = async () => {
      try {
        setLoading(true);
        const response = await artistService.getArtistById(artistId);
        setArtist(response.data);
        setIsFollowing(checkFollowStatus(response.data));
      } catch (err) {
        setError("아티스트 정보를 불러오는 데 실패했습니다.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchArtist();
  }, [artistId]);

  // 팔로우 버튼 클릭 핸들러
  const handleFollow = async () => {
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }
    try {
      await artistService.followArtist(artistId);
      setIsFollowing(true);
      // 팔로워 수 갱신을 위해 아티스트 정보 다시 불러오기
      setArtist((prev) => ({ ...prev, followerCount: prev.followerCount + 1 }));
    } catch (err) {
      alert("팔로우에 실패했습니다: " + err.response?.data?.message);
    }
  };

  // 언팔로우 버튼 클릭 핸들러
  const handleUnfollow = async () => {
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }
    try {
      await artistService.unfollowArtist(artistId);
      setIsUnfollowing(true); // UI 즉시 반영
      // 팔로워 수 갱신을 위해 artist 정보 다시 불러오기
      setArtist((prev) => ({ ...prev, followerCount: prev.followerCount - 1 }));
    } catch (err) {
      alert("언팔로우에 실패했습니다: " + err.response?.data?.message);
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
        {/* 아티스트 헤더 */}
        <div className="flex flex-col md:flex-row items-center gap-6 p-4 border rounded-lg">
          <img
            src={artist.artistImageUrl || "https://placehold.co/150"}
            alt={artist.artistName}
            className="w-36 h-36 rounded-full object-cover border-2"
          />
          <div className="flex-1 text-center md:text-left">
            <h1 className="text-4xl font-bold">{artist.artistName}</h1>
            <p className="text-gray-600 mt-2">
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
                    className="px-4 py-2 bg-gray-300 text-black rounded-md font-semibold"
                  >
                    언팔로우
                  </button>
                ) : (
                  <button
                    onClick={handleFollow}
                    className="px-4 py-2 bg-indigo-600 text-white rounded-md font-semibold hover:bg-indigo-700"
                  >
                    팔로우
                  </button>
                )}
              </div>
            )}
          </div>
        </div>

        {/* To do: 아티스트 관련 공연 목록, SNS 링크 등 추가 */}
      </div>
    </MainLayout>
  );
};

export default ArtistDetailPage;
