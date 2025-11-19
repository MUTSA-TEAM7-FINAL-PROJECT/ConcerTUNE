import React from "react";
import { Link } from "react-router-dom";
import NotificationDropdown from "./NotificatonDropdown";
import { useAuth } from "../../context/AuthContext";

const Header = () => {
    const { isLoggedIn, user: currentUser, logout } = useAuth();

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
                            <Link to="/concerts" className="text-gray-600 hover:text-gray-900">
                                공연 정보
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/community/free"
                                className="text-gray-600 hover:text-gray-900"
                            >
                                커뮤니티
                            </Link>
                        </li>
                        
                        {/* 💡 일반 사용자 및 비로그인 시 공연 등록 요청 (ADMIN/ARTIST가 아닐 때) */}
                        {(!isLoggedIn || (currentUser?.role !== "ADMIN" && currentUser?.role !== "ARTIST")) && (
                            <li>
                                <Link
                                    to="/concerts/request"
                                    className="text-gray-600 hover:text-gray-900"
                                >
                                    공연 등록 요청
                                </Link>
                            </li>
                        )}
                        
                        {/* 💡 관리자(ADMIN)만 보이는 메뉴: 등록 요청 현황 */}
                        {currentUser?.role === "ADMIN" && (
                            <li>
                                <Link
                                    to="/admin/requests"
                                    className="text-indigo-600 font-bold hover:text-indigo-800"
                                >
                                    등록 요청 현황
                                </Link>
                            </li>
                        )}

                        {/* 💡 아티스트(ARTIST)만 보이는 메뉴: 아티스트 공연 등록 */}
                        {currentUser?.role === "ARTIST" && (
                            <li>
                                <Link
                                    to="/artist/request"
                                    className="text-indigo-600 font-medium hover:text-indigo-800"
                                >
                                    아티스트 공연 등록
                                </Link>
                            </li>
                        )}
                        
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

                {/* 인증 버튼 및 사용자 정보 */}
                {isLoggedIn ? (
                    <div className="flex items-center space-x-3">
                        <NotificationDropdown />

                        {/* 💡 기존 로그인 영역 내부의 아티스트 링크는 삭제 (메인 네비게이션으로 이동했기 때문) */}
                        
                        <span className="text-sm">
                            환영합니다, **{currentUser?.username}**님
                        </span>
                        <button
                            onClick={() => logout()}
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
                            to="/auth/select"
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