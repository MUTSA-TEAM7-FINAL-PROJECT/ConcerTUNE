import React from "react";
import Header from "./Header";

{
  /* 공통 레이아웃 컴포넌트 */
}
const MainLayout = ({ children }) => {
  return (
    <div className="flex min-h-screen flex-col bg-gray-100">
      {/* 상단 헤더 */}
      <Header />

      {/* 메인 콘텐츠 영역 */}
      <main className="flex-1 w-full">
        <div className="container mx-auto px-4 py-8">{children}</div>
      </main>

      {/* Footer */}
      <footer className="w-full bg-gray-800 p-4 text-center text-white">
        © ConcerTUNE. All rights reserved.
      </footer>
    </div>
  );
};

export default MainLayout;
