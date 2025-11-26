import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import PersonalizedScheduleSection from "../components/home/PersonalizedScheduleSection";
import { useAuth } from "../context/AuthContext";
import ProfileEditModal from "../components/modal/ProfileEditModal";
import ProfileImageModal from "../components/modal/ProfileImageModal";
import FollowModal from "../components/modal/FollowModal";
import myPageService from "../services/myPageService";
import userService from "../services/userService";
import {
  FaPencilAlt,
  FaStar,
  FaBookmark,
  FaCog,
  FaListAlt,
  FaChevronLeft,
  FaChevronRight,
} from "react-icons/fa";

const initialLoadingState = {
  id: null,
  username: "",
  bio: "",
  profileImageUrl: "",
  genrePreferences: [],
  followersCount: 0,
  followingCount: 0,
};

const requestLinks = [
  { path: "/concerts/request-list", label: "공연 등록 요청 현황" },
  { path: "/artist-manager/requests-list", label: "아티스트 관리 요청 현황" },
];

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
                    src={item.profileImageUrl}
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

const MyPage = () => {
  const { user, setUser } = useAuth();
  const [profileData, setProfileData] = useState(initialLoadingState);
  const [tabContents, setTabContents] = useState({
    bookmarkedLives: [],
    followedArtists: [],
    myPosts: [],
  });
  const [activeTab, setActiveTab] = useState("bookmarks");
  const [isLoading, setIsLoading] = useState(true);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isImageModalOpen, setIsImageModalOpen] = useState(false);
  const [isFollowModalOpen, setIsFollowModalOpen] = useState(false);
  const [followType, setFollowType] = useState("followers");

  const loadMyData = async () => {
    try {
      setIsLoading(true);
      const profile = await myPageService.getMyProfile();
      setProfileData(profile);

      if (profile.id) {
        const contents = await userService.getUserContents(profile.id);
        setTabContents({
          bookmarkedLives: contents.bookmarkedLives || [],
          followedArtists: contents.followedArtists || [],
          myPosts: contents.myPosts || [],
        });
      }
    } catch (err) {
      console.error("마이페이지 로드 실패:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      loadMyData();
    }
  }, [user]);

  const handleProfileSave = async (updatedData) => {
    try {
      const updatedProfile = await myPageService.updateMyProfile(updatedData);

      setUser((prev) => ({
        ...prev,
        username: updatedProfile.username,
        bio: updatedProfile.bio,
        tags: updatedProfile.tags,
      }));

      await loadMyData();

      setIsEditModalOpen(false);
    } catch (err) {
      console.error("프로필 수정 실패:", err);
      alert("프로필 수정에 실패했습니다.");
    }
  };

  const handleProfileImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      const updatedProfile = await myPageService.uploadProfileImage(file);

      setUser((prev) => ({
        ...prev,
        profileImageUrl: updatedProfile.profileImageUrl,
      }));

      await loadMyData();
      setIsImageModalOpen(false);
    } catch (err) {
      console.error("프로필 이미지 업로드 실패:", err);
      alert("프로필 이미지 업로드에 실패했습니다.");
    }
  };

  if (!user)
    return (
      <div className="p-10 text-center text-gray-600">
        <p>로그인이 필요합니다.</p>
      </div>
    );

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
              src={profileData.profileImageUrl}
              alt={`${profileData.username}님의 프로필`}
              className="w-24 h-24 rounded-full object-cover border-4 border-indigo-200 mb-3 shadow-md cursor-pointer hover:opacity-80 transition"
              onClick={() => setIsImageModalOpen(true)}
            />
            <h2 className="text-2xl font-bold text-gray-900">
              {profileData.username}
            </h2>
            <p className="text-sm text-gray-600 mt-2 text-center max-w-xs">
              {profileData.bio || "자기소개가 없습니다."}
            </p>
          </div>

          <div className="flex justify-center space-x-6 text-center mb-6 border-t pt-4">
            <div>
              <p
                className="text-xl font-bold text-gray-800 cursor-pointer"
                onClick={() => {
                  setFollowType("followers");
                  setIsFollowModalOpen(true);
                }}
              >
                {profileData.followersCount || 0}
              </p>
              <p className="text-xs text-gray-500">팔로워</p>
            </div>
            <div>
              <p
                className="text-xl font-bold text-gray-800 cursor-pointer"
                onClick={() => {
                  setFollowType("followings");
                  setIsFollowModalOpen(true);
                }}
              >
                {profileData.followingCount || 0}
              </p>
              <p className="text-xs text-gray-500">팔로잉</p>
            </div>
          </div>

          <div className="w-full text-center mb-6">
            <h4 className="text-sm font-semibold mb-2 text-gray-700">
              선호 장르
            </h4>
            <div className="flex flex-wrap justify-center gap-2">
              {profileData.genrePreferences?.map((genre) => (
                <span
                  key={genre.genreId}
                  className="text-xs px-2 py-0.5 rounded-full bg-indigo-100 text-indigo-700 font-bold"
                >
                  #{genre.genreName}
                </span>
              ))}
              {profileData.genrePreferences?.length === 0 && (
                <span className="text-sm text-gray-500">
                  선호 장르를 설정해 주세요.
                </span>
              )}
            </div>
          </div>

          <div className="mt-2 space-y-3 w-full border-t pt-4">
            <button
              onClick={() => setIsEditModalOpen(true)}
              className="w-full block text-center py-2 border border-gray-300 rounded-full text-gray-700 hover:bg-gray-100 transition text-sm font-medium"
            >
              <FaPencilAlt className="inline mr-2" /> 프로필 정보 수정
            </button>

            {requestLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className="w-full block text-center py-2 border border-indigo-300 rounded-full text-indigo-600 hover:bg-indigo-50 transition text-sm font-medium"
              >
                <FaListAlt className="inline mr-2" /> {link.label}
              </Link>
            ))}
          </div>
        </div>

        <div className="lg:col-span-2">
          <PersonalizedScheduleSection userId={profileData.id} />
        </div>

        <div className="col-span-full mt-10">
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

      <ProfileEditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        initialData={profileData}
        onSave={handleProfileSave}
      />

      <ProfileImageModal
        isOpen={isImageModalOpen}
        onClose={() => setIsImageModalOpen(false)}
        handleFileChange={handleProfileImageChange}
        currentImageUrl={profileData.profileImageUrl}
      />

      <FollowModal
        isOpen={isFollowModalOpen}
        onClose={() => setIsFollowModalOpen(false)}
        userId={profileData.id}
        type={followType}
      />
    </div>
  );
};

export default MyPage;
