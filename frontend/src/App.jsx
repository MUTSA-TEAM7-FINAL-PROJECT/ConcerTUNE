import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import Home from "./pages/home";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AuthSelectPage from "./pages/AuthSelectPage";
import OAuth2Callback from "./pages/OAuth2Callback";
import ArtistDetailPage from "./pages/ArtistDetailPage";
import ConcertListPage from "./pages/ConcertListPage";
import CommunityPage from "./pages/CommunityPage";
import LiveRequestPage from "./pages/LiveRequestPage";
import PasswordResetPage from "./pages/PasswordResetPage";
import PostList from "./components/post/PostList";
import PostDetail from "./components/post/PostDetail";
import PostWriteEdit from "./components/post/PostWriteEdit";
import ConcertDetailPage from "./pages/ConcertDetailPage";
import ConcertRequestPage from "./pages/ConcertRequestPage";
import ProfilePage from "./pages/ProfilePage"; // 32-브랜치 (내 프로필 상세)
import ProfileUpdatePage from "./pages/ProfileUpdatePage"; // 32-브랜치
import UserPage from "./pages/UserPage"; // 32-브랜치 (타인 프로필)
import RequestListPage from "./pages/RequestListPage"; // develop 브랜치
import ArtistManagerRequestListPage from "./pages/ArtistManagerRequestListPage"; // develop 브랜치
import ArtistManagerRequestPage from "./pages/ArtistManagerRequestPage"; // develop 브랜치
import SearchResultPage from "./pages/SearchResultPage"; // develop 브랜치
import MyPage from "./pages/MyPage"; // develop 브랜치 (내 프로필 상세)

const App = () => {
  return (
    <BrowserRouter>
           {" "}
      <Routes>
               {" "}
        <Route path="/*" element={<MainLayout />}>
                    <Route path="" element={<Home />} />         {/* 검색 */}
                    <Route path="search" element={<SearchResultPage />} />
          {/* 공연/아티스트 */}
                    <Route path="concerts" element={<ConcertListPage />} />     
              <Route path="concerts/:id" element={<ConcertDetailPage />} />     
              <Route path="concerts/request" element={<ConcertRequestPage />} />
                   {" "}
          <Route path="artists/:artistId" element={<ArtistDetailPage />} />     

               {" "}
          <Route path="concerts/request-list" element={<RequestListPage />} /> 
                  <Route path="artists/request" element={<LiveRequestPage />} />
                   {" "}
          <Route
            path="artist-manager/requests"
            element={<ArtistManagerRequestPage />}
          />
                   {" "}
          <Route
            path="artist-manager/requests-list"
            element={<ArtistManagerRequestListPage />}
          />
                    {/* 커뮤니티 목록 및 탭 레이아웃 */}         {" "}
          <Route path="community" element={<CommunityPage />}>
                        <Route index element={<Navigate to="FREE" replace />} />
                        <Route path=":category" element={<PostList />} />       
             {" "}
          </Route>
                    {/* 게시글 상세 */}
                    <Route path="post/:postId" element={<PostDetail />} />     
              {/* 게시글 작성/수정 */}         {" "}
          <Route path="community/write/:category" element={<PostWriteEdit />} />
                   {" "}
          <Route
            path="community/edit/:category/:postId"
            element={<PostWriteEdit />}
          />
          {/* 사용자 프로필 (통합) */}
          {/* MyPage와 ProfilePage가 겹치므로 MyPage를 개인 프로필(/me)로 지정하고 ProfilePage는 제거 (혹은 기능 통합) */}
                    <Route path="me" element={<MyPage />} />
                    <Route path="me/update" element={<ProfileUpdatePage />} />
          {/* 타 사용자 프로필 */}
                    <Route path="user/:userId" element={<UserPage />} />       {" "}
        </Route>
                {/* MainLayout을 사용하지 않는 페이지 */}
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/auth/select" element={<AuthSelectPage />} />
                <Route path="/oauth2/callback" element={<OAuth2Callback />} />
                <Route path="/password-reset" element={<PasswordResetPage />} />
             {" "}
      </Routes>
         {" "}
    </BrowserRouter>
  );
};

export default App;
