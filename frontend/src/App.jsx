import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import Home from "./pages/home";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ArtistDetailPage from "./pages/ArtistDetailPage";
import ConcertListPage from "./pages/ConcertListPage";
import CommunityPage from "./pages/CommunityPage";
import LiveRequestPage from "./pages/LiveRequestPage";

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
      </Routes>
    </BrowserRouter>
  );
};

export default App;
