// Header.jsx
import React, { useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import NotificationDropdown from "./NotificatonDropdown";
import {
  FaUserCircle,
  FaSearch,
  FaBell,
  FaBars,
  FaTimes,
  FaChevronDown,
} from "react-icons/fa";

const Header = () => {
  const { isLoggedIn, user: currentUser, logout } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const navigate = useNavigate();
  const profileRef = useRef(null);

  useEffect(() => {
    function onClickOutside(e) {
      if (profileRef.current && !profileRef.current.contains(e.target)) {
        setProfileOpen(false);
      }
    }
    document.addEventListener("click", onClickOutside);
    return () => document.removeEventListener("click", onClickOutside);
  }, []);

  const handleSearchKey = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      const q = searchTerm.trim();
      if (q) {
        navigate(`/search?q=${encodeURIComponent(q)}`);
        setSearchTerm("");
        setMobileOpen(false);
      }
    }
  };

  return (
    <header className="sticky top-0 z-40 w-full bg-white/80 backdrop-blur-sm border-b border-gray-200 shadow-sm">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Link to="/" className="flex items-center gap-3">
              <div className="bg-indigo-600 text-white rounded-md px-3 py-1 text-lg font-extrabold shadow">
                CT
              </div>
              <span className="hidden sm:inline text-xl font-bold text-gray-900">
                ConcerTUNE
              </span>
            </Link>

            <ul className="hidden md:flex items-center gap-4 ml-4">
              <li>
                <Link
                  to="/concerts"
                  className="text-sm text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md transition"
                >
                  공연 정보
                </Link>
              </li>
              <li>
                <Link
                  to="/community/free"
                  className="text-sm text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md transition"
                >
                  커뮤니티
                </Link>
              </li>
              <li>
                <Link
                  to="/concerts/request"
                  className="text-sm text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md transition"
                >
                  공연 등록 요청
                </Link>
              </li>
              <li>
                <Link
                  to="/artist-manager/requests"
                  className="text-sm text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md transition"
                >
                  아티스트 관리 요청
                </Link>
              </li>
            </ul>
          </div>

          {/* center: search (desktop) */}
          <div className="flex-1 px-4">
            <div className="max-w-xl mx-auto">
              <div className="relative">
                <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                  <FaSearch />
                </span>
                <input
                  type="text"
                  placeholder="아티스트, 공연 검색"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyDown={handleSearchKey}
                  className="hidden sm:block w-full bg-white border border-gray-200 rounded-full py-2 pl-10 pr-4 text-sm shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-300"
                />

                {/* mobile search icon (opens mobile panel) */}
                <div className="sm:hidden">
                  <button
                    onClick={() => setMobileOpen((s) => !s)}
                    aria-label="Open menu"
                    className="p-2 rounded-md text-gray-600 hover:bg-gray-100"
                  >
                    {mobileOpen ? <FaTimes /> : <FaBars />}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {isLoggedIn ? (
              <>
                <div className="hidden sm:flex items-center gap-2">
                  <NotificationDropdown />
                </div>

                <button
                  className="sm:hidden p-2 rounded-md text-gray-600 hover:bg-gray-100"
                  aria-label="notifications"
                >
                  <FaBell />
                </button>

                <div className="relative" ref={profileRef}>
                  <button
                    onClick={() => setProfileOpen((s) => !s)}
                    className="flex items-center gap-2 rounded-full px-2 py-1 hover:bg-gray-100 transition"
                    aria-haspopup="true"
                    aria-expanded={profileOpen}
                  >
                    <img
                      src={currentUser?.profileImageUrl || ""}
                      alt={currentUser?.username || "user"}
                      onError={(e) => (e.currentTarget.src = "")}
                      className="w-9 h-9 rounded-full object-cover bg-gray-100"
                    />
                    <span className="hidden sm:inline text-sm text-gray-700">
                      {currentUser?.username}
                    </span>
                    <FaChevronDown className="text-gray-500" />
                  </button>

                  {profileOpen && (
                    <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-md shadow-lg py-1 z-50">
                      <Link
                        to={`/user/${currentUser?.id}`}
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                        onClick={() => setProfileOpen(false)}
                      >
                        마이페이지
                      </Link>
                      <div className="border-t my-1" />
                      <button
                        onClick={() => {
                          logout();
                          setProfileOpen(false);
                        }}
                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-50"
                      >
                        로그아웃
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="text-sm text-gray-700 px-3 py-1 rounded-md hover:bg-gray-100"
                >
                  로그인
                </Link>
                <Link
                  to="/auth/select"
                  className="text-sm bg-indigo-600 text-white px-3 py-1 rounded-md hover:bg-indigo-700"
                >
                  회원가입
                </Link>
              </div>
            )}
          </div>
        </div>

        {mobileOpen && (
          <div className="md:hidden mt-2 pb-4">
            <div className="px-2">
              <div className="flex items-center gap-2">
                <div className="relative flex-1">
                  <span className="absolute left-3 top-2 text-gray-400">
                    <FaSearch />
                  </span>
                  <input
                    type="text"
                    placeholder="아티스트나 공연을 검색하세요"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onKeyDown={handleSearchKey}
                    className="w-full pl-10 pr-3 py-2 rounded-md border border-gray-200 text-sm focus:outline-none"
                  />
                </div>
                <button
                  onClick={() => {
                    const q = searchTerm.trim();
                    if (q) {
                      navigate(`/search?q=${encodeURIComponent(q)}`);
                      setSearchTerm("");
                      setMobileOpen(false);
                    }
                  }}
                  className="px-3 py-2 bg-indigo-600 text-white rounded-md text-sm"
                >
                  검색
                </button>
              </div>

              <ul className="mt-3 space-y-1">
                <li>
                  <Link
                    to="/concerts"
                    className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-50"
                    onClick={() => setMobileOpen(false)}
                  >
                    공연 정보
                  </Link>
                </li>
                <li>
                  <Link
                    to="/community/free"
                    className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-50"
                    onClick={() => setMobileOpen(false)}
                  >
                    커뮤니티
                  </Link>
                </li>
                <li>
                  <Link
                    to="/concerts/request"
                    className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-50"
                    onClick={() => setMobileOpen(false)}
                  >
                    공연 등록 요청
                  </Link>
                </li>
                <li>
                  <Link
                    to="/artist-manager/requests"
                    className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-50"
                    onClick={() => setMobileOpen(false)}
                  >
                    아티스트 관리 요청
                  </Link>
                </li>
                {currentUser?.role === "ARTIST" && (
                  <li>
                    <Link
                      to="/artist/request"
                      className="block px-3 py-2 rounded-md text-indigo-600 hover:bg-gray-50"
                      onClick={() => setMobileOpen(false)}
                    >
                      아티스트 공연 등록
                    </Link>
                  </li>
                )}
              </ul>
            </div>
          </div>
        )}
      </nav>
    </header>
  );
};

export default Header;
