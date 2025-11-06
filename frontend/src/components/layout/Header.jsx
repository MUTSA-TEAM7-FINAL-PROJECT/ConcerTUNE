import React from "react";
import { Link } from "react-router-dom";
import NotificationDropdown from "./NotificatonDropdown";
import authService from "../../services/auth";

const Header = () => {
  const isLoggedIn = authService.isAuthenticated();
  const currentUser = authService.getCurrentUser();

  return (
    <header className="sticky top-0 z-10 w-full bg-white shadow-md">
      <nav className="container mx-auto flex items-center justify-between p-4">
        {/* 로고, 메인 네비게이션 */}
        <div className="flex items-center space-x-8">
          <Link to="/" className="text-2xl font-bold text-gray-900">
            ConcerTUNE
          </Link>
          {/* 네비게이션 링크 */}
          <ul className="hidden items-center space-x-6 md:flex">
            <li>
              <Link to="/lives" className="text-gray-600 hover:text-gray-900">
                공연 정보
              </Link>
            </li>
            <li>
              <Link
                to="/posts/{category}"
                className="text-gray-600 hover:text-gray-900"
              >
                커뮤니티
              </Link>
            </li>
            {/* 다른 링크들 */}
          </ul>
        </div>

        {/* 검색창 */}
        <div className="flex items-center space-x-4">
          <div className="hidden sm:block">
            <input
              type="text"
              placeholder="아티스트, 공연 검색"
              className="rounded-md border border-gray-300 px-3 py-1.5 text-sm"
            />
          </div>
        </div>

        {/* 검색창 및 인증 버튼 */}
        {isLoggedIn ? (
          <div className="flex items-center space-x-3">
            <NotificationDropdown />

            {currentUser?.role === "ARTIST" && (
              <Link
                to="/artist/request"
                className="text-sm font-medium text-indigo-600 hover:text-indigo-800"
              >
                공연 등록
              </Link>
            )}

            <span className="text-sm">
              환영합니다, {currentUser?.username}님
            </span>
            <button
              onClick={() => authService.logout()}
              className="rounded-md px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-100"
            >
              로그아웃
            </button>
          </div>
        ) : (
          <div className="flex space-x-2">
            <Link
              to="/login"
              className="rounded-md px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-100"
            >
              로그인
            </Link>
            <Link
              to="/register"
              className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white hover:bg-indigo-700"
            >
              회원가입
            </Link>
          </div>
        )}
      </nav>
    </header>
  );
};

export default Header;
