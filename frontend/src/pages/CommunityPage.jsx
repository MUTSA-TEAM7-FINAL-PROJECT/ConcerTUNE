import React from "react";
import { NavLink, Outlet } from "react-router-dom";

// 탭 메뉴 스타일
const getLinkClass = ({ isActive }) => {
  return isActive
    ? "px-4 py-2 text-lg font-bold text-indigo-600 border-b-2 border-indigo-600"
    : "px-4 py-2 text-lg font-medium text-gray-500 hover:text-gra-800";
};

const CommunityPage = () => {
  return (
    <div className="w-full">
      {/* 탭 네비게이션 */}
      <nav className="flex space-x-4 border-b border-gray-200 mb-6">
        <NavLink to="/community/free" className={getLinkClass}>
          자유게시판
        </NavLink>
        <NavLink to="/community/review" className={getLinkClass}>
          공연 후기
        </NavLink>
        <NavLink to="/community/accompany" className={getLinkClass}>
          동행 구하기
        </NavLink>
      </nav>

      {/* 하위 라우트(PostList)가 렌더링될 위치 */}
      <Outlet />
    </div>
  );
};

export default CommunityPage;
