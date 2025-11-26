import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import userService from "../services/userService";
import {
  FaPencilAlt,
  FaStar,
  FaBookmark,
  FaCog,
  FaChevronLeft,
  FaChevronRight,
} from "react-icons/fa";
import FollowModal from "../components/modal/FollowModal";

const initialLoadingState = {
  id: null,
  username: "",
  bio: "",
  profileImageUrl: null,
  genrePreferences: [],
  followersCount: 0,
  followingCount: 0,
};

const GenericCarousel = ({ data, itemsToShow = 3, vertical = false }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const maxIndex = Math.max(0, (data?.length || 0) - itemsToShow);

  if (!data || data.length === 0)
    return <p className="text-gray-500 text-center py-4">정보가 없습니다.</p>;

  const transformValue = vertical
    ? `translateY(-${currentIndex * (100 / itemsToShow)}%)`
    : `translateX(-${currentIndex * (100 / itemsToShow)}%)`;

  return (
    <div className="relative p-2">
      <div className="overflow-hidden rounded-lg shadow-inner">
        <div
          className={`flex ${
            vertical ? "flex-col" : ""
          } transition-transform duration-300`}
          style={{ transform: transformValue }}
        >
          {data.map((item) => (
            <div
              key={item.id}
              style={{ width: vertical ? "100%" : `${100 / itemsToShow}%` }}
              className="px-2 flex-shrink-0"
            >
              {item.title ? (
                <div className="border rounded-xl shadow bg-white p-4 h-full">
                  {item.posterUrl && (
                    <img
                      src={item.posterUrl}
                      alt={item.title}
                      className="w-full h-56 object-cover mb-2"
                    />
                  )}
                  <h5 className="font-bold text-lg">{item.title}</h5>
                </div>
              ) : (
                <div className="p-2 flex items-center gap-3 border rounded-xl shadow bg-white">
                  <img
                    src={item.profileImageUrl || "/default-profile.png"}
                    alt={item.name}
                    className="w-12 h-12 rounded-full object-cover"
                  />
                  <span className="font-medium text-gray-800">{item.name}</span>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {data.length > itemsToShow && (
        <>
          <button
            onClick={() => setCurrentIndex((prev) => Math.max(0, prev - 1))}
            disabled={currentIndex === 0}
            className="absolute top-1/2 left-0 -translate-y-1/2 bg-white border p-2 rounded-full shadow disabled:opacity-30 ml-2"
          >
            <FaChevronLeft />
          </button>
          <button
            onClick={() =>
              setCurrentIndex((prev) => Math.min(maxIndex, prev + 1))
            }
            disabled={currentIndex >= maxIndex}
            className="absolute top-1/2 right-0 -translate-y-1/2 bg-white border p-2 rounded-full shadow disabled:opacity-30 mr-2"
          >
            <FaChevronRight />
          </button>
        </>
      )}
    </div>
  );
};

const UserPage = () => {
  const { user } = useAuth();
  const { userId } = useParams();
  const numericUserId = Number(userId);

  const [profileData, setProfileData] = useState(initialLoadingState);
  const [tabContents, setTabContents] = useState({
    bookmarkedLives: [],
    followedArtists: [],
    myPosts: [],
  });
  const [activeTab, setActiveTab] = useState("bookmarks");
  const [isLoading, setIsLoading] = useState(true);
  const [isFollowing, setIsFollowing] = useState(false);

  // 🔥 follow modal
  const [isFollowModalOpen, setIsFollowModalOpen] = useState(false);
  const [followType, setFollowType] = useState("followers");

  const isOwner = user && profileData.id && user.id === profileData.id;

  const loadUserData = async () => {
    try {
      setIsLoading(true);

      const profile = await userService.getUserProfile(numericUserId);
      setProfileData(profile);

      if (user && user.id !== profile.id) {
        const followStatus = await userService.checkFollowStatus(profile.id);
        setIsFollowing(followStatus);
      }

      const contents = await userService.getUserContents(numericUserId);
      setTabContents({
        bookmarkedLives: contents.bookmarkedLives || [],
        followedArtists: contents.followedArtists || [],
        myPosts: contents.myPosts || [],
      });
    } catch (err) {
      console.error("유저 페이지 로드 실패:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!isNaN(numericUserId) && user) {
      loadUserData();
    }
  }, [numericUserId, user]);

  const handleFollowToggle = async () => {
    try {
      await userService.toggleFollow(profileData.id);

      const nextState = !isFollowing;
      setIsFollowing(nextState);

      setProfileData((prev) => ({
        ...prev,
        followersCount: prev.followersCount + (nextState ? 1 : -1),
      }));
    } catch (err) {
      console.error("팔로우/언팔로우 실패:", err);
    }
  };

  if (isNaN(numericUserId)) {
    return (
      <div className="p-10 text-center text-red-500">
        유효한 사용자 ID가 아닙니다.
      </div>
    );
  }

  if (isLoading)
    return (
      <div className="text-center p-10 text-indigo-600">
        <FaCog className="animate-spin text-4xl mx-auto mb-4" />
        <p>프로필을 불러오는 중입니다...</p>
      </div>
    );

  const tabItems = [
    {
      key: "bookmarks",
      label: "북마크한 공연",
      icon: <FaBookmark />,
      carouselProps: { data: tabContents.bookmarkedLives, itemsToShow: 2 },
    },
    {
      key: "artists",
      label: "팔로우 아티스트",
      icon: <FaStar />,
      carouselProps: { data: tabContents.followedArtists, itemsToShow: 3 },
    },
    {
      key: "posts",
      label: "작성한 게시글",
      icon: <FaPencilAlt />,
      carouselProps: {
        data: tabContents.myPosts,
        itemsToShow: 10,
        vertical: true,
      },
    },
  ];

  const currentTab = tabItems.find((t) => t.key === activeTab);

  return (
    <div className="container mx-auto p-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="bg-white p-8 rounded-xl shadow-lg border h-fit">
          <div className="flex flex-col items-center mb-6">
            <img
              src={profileData.profileImageUrl || "/default-profile.png"}
              alt={`${profileData.username}님의 프로필`}
              className="w-24 h-24 rounded-full object-cover border-4 border-indigo-200 mb-3 shadow-md"
            />
            <h2 className="text-2xl font-bold text-gray-900">
              {profileData.username}
            </h2>
            <p className="text-sm text-gray-600 mt-2 text-center max-w-xs">
              {profileData.bio || "자기소개가 없습니다."}
            </p>
          </div>

          {!isOwner && user && (
            <button
              onClick={handleFollowToggle}
              className={`w-full my-3 py-2 rounded-full font-semibold transition ${
                isFollowing
                  ? "bg-gray-100 text-gray-700 border border-gray-300 hover:bg-gray-200"
                  : "bg-indigo-600 text-white hover:bg-indigo-700"
              }`}
            >
              {isFollowing ? "언팔로우" : "팔로우"}
            </button>
          )}

          <div className="flex justify-center space-x-6 text-center mb-6 border-t pt-4">
            <div
              className="cursor-pointer"
              onClick={() => {
                setFollowType("followers");
                setIsFollowModalOpen(true);
              }}
            >
              <p className="text-xl font-bold text-gray-800">
                {profileData.followersCount || 0}
              </p>
              <p className="text-xs text-gray-500">팔로워</p>
            </div>

            <div
              className="cursor-pointer"
              onClick={() => {
                setFollowType("followings");
                setIsFollowModalOpen(true);
              }}
            >
              <p className="text-xl font-bold text-gray-800">
                {profileData.followingCount || 0}
              </p>
              <p className="text-xs text-gray-500">팔로잉</p>
            </div>
          </div>
        </div>

        <div className="lg:col-span-2 mt-6 lg:mt-0">
          <div className="flex border-b mb-6 bg-white rounded-t-xl shadow-md overflow-x-auto">
            {tabItems.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`flex-1 text-center py-3 text-lg font-medium transition duration-150 flex items-center justify-center whitespace-nowrap ${
                  activeTab === tab.key
                    ? "border-b-4 border-indigo-600 text-indigo-600 bg-gray-50"
                    : "text-gray-600 hover:text-indigo-600"
                }`}
              >
                {tab.icon} <span className="ml-2">{tab.label}</span>
              </button>
            ))}
          </div>
          <div className="bg-white p-6 rounded-b-xl shadow-lg border -mt-6 pt-6">
            {currentTab && <GenericCarousel {...currentTab.carouselProps} />}
          </div>
        </div>
      </div>

      {isFollowModalOpen && (
        <FollowModal
          isOpen={true}
          type={followType}
          userId={profileData.id}
          onClose={() => setIsFollowModalOpen(false)}
        />
      )}
    </div>
  );
};

export default UserPage;
