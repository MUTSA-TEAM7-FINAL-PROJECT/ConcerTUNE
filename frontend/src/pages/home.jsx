import { React } from "react";
import { Link } from "react-router-dom";
import MainLayout from "../components/layout/MainLayout";

// 임시 mock-up 데이터
const mockConcerts = [
  {
    id: 1,
    title: "Fred Again 내한",
    venue: "Cakeshop 이태원",
    posterUrl: "https://placehold.co/300x400?text=Concert+1",
  },
  {
    id: 2,
    title: "3호선 버터플라이 콘서트",
    venue: "롤링홀 홍대",
    posterUrl: "https://placehold.co/300x400?text=Concert+2",
  },
  {
    id: 3,
    title: "Boris 내한공연",
    venue: "무신사 개러지 홀",
    posterUrl: "https://placehold.co/300x400?text=Concert+3",
  },
  {
    id: 4,
    title: "소음발광 콘서트",
    venue: "KT&G 상상마당",
    posterUrl: "https://placehold.co/300x400?text=Concert+4",
  },
];

const mockPosts = [
  {
    id: 101,
    category: "free",
    title: "미쳤다 보리스 내한ㄷㄷㄷ",
    writer: "보리스존슨",
  },
  {
    id: 102,
    category: "review",
    title: "오아시스 후기: 샐리가 기다릴 만 함ㄹㅇ",
    writer: "맨시티만세",
  },
  {
    id: 103,
    category: "accompany",
    title: "3호선 버터플라이 같이 가실 분",
    writer: "홍대가이",
  },
];

const Home = () => {
  return (
    <div className="space-y-12">
      <section className="text-center p-10 bg-white rounded-lg shadow-md">
        <h1 className="text-4xl font-bold text-gray-800 mb-3">
          당신이 원하는 콘서트에 마음을 TUNE해보세요!
        </h1>
        <p className="text-lg text-gray-600 mb-6">
          놓치기 아까운 인디밴드/내한공연 정보를 한눈에
        </p>
        <Link
          to="/concerts"
          className="px-6 py-3 bg-indigo-600 text-white font-semibold rounded-md hover:bg-indigo-700"
        >
          공연 둘러보기
        </Link>
      </section>

      <section>
        <h2 className="text-2xl font-bold mb-4">HOT 공연</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {mockConcerts.map((concert) => (
            <Link
              to={`/concerts/${concert.id}`} // TODO: 공연 상세 페이지 라우트 필요
              key={concert.id}
              className="border rounded-lg shadow hover:shadow-xl transition-shadow overflow-hidden"
            >
              <img
                src={concert.posterUrl}
                alt={concert.title}
                className="w-full h-64 object-cover"
              />
              <div className="p-4">
                <h3 className="text-lg font-semibold truncate">
                  {concert.title}
                </h3>
                <p className="text-sm text-gray-500 truncate">
                  {concert.venue}
                </p>
              </div>
            </Link>
          ))}
        </div>
      </section>

      <section>
        <h2 className="text-2xl font-bold mb-4">최신 커뮤니티 글</h2>
        <div className="space-y-3">
          {mockPosts.map((post) => (
            <Link
              to={`/post/${post.id}`} // TODO: 게시글 상세 페이지 라우트 필요
              key={post.id}
              className="block p-4 bg-white border rounded-lg shadow-sm hover:shadow-md transition-shadow"
            >
              <span
                className={`text-xs font-semibold ${
                  post.category === "review"
                    ? "text-blue-600"
                    : post.category === "accompany"
                    ? "text-green-600"
                    : "text-gray-600"
                }`}
              >
                [
                {post.category === "free"
                  ? "자유"
                  : post.category === "review"
                  ? "후기"
                  : "동행"}
                ]
              </span>
              <span className="ml-2 text-lg font-semibold text-gray-900">
                {post.title}
              </span>
              <span className="text-sm texy-gray-500 float-right">
                by {post.writer}
              </span>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;
