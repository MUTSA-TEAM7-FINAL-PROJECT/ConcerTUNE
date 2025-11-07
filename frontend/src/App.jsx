import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
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
const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* MainLayout을 사용하는 페이지들 */}
        <Route element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/concerts" element={<ConcertListPage />} />
          <Route path="/artists/:artistId" element={<ArtistDetailPage />} />

          {/*<Route path="/community" element={<CommunityPage />}>
            <Route index element={<PostList category="free" />} /> // 기본 탭
            <Route path="free" element={<PostList category="/free" />} />
            <Route path="review" element={<PostList category="review" />} />
            <Route
              path="accompany"
              element={<PostList category="accompany" />}
            />
          </Route>

          <Route path="/post/:postId" element={<PostDetail />} />
          <Routh path="/write" element={<PostWrite />} /> */}

          <Route path="/artists/request" element={<LiveRequestPage />} />
        </Route>

        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/auth/select" element={<AuthSelectPage />} />
        <Route path="/oauth2/callback" element={<OAuth2Callback />} />
        <Route path="/password-reset" element={<PasswordResetPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
