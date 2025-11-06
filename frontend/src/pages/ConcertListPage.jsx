import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
// import concertService from "../services/concertService";

const ConcertListPage = () => {
  const [concerts, setConcerts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchConcerts = async () => {
      try {
        setLoading(true);
        // TODO: concertService.js만들고 API 호출
        // const response = await concertService.getConcerts({ page: 0, size: 20 });
        // setConcerts(response.data.content);

        // 임시 mock-up 데이터
        setConcerts([
          {
            id: 1,
            title: "펜타포트 락 페스티벌",
            venue: "송도달빛축제공원",
            posterUrl: "https://placehold.co/300x400",
          },
          {
            id: 2,
            title: "오아시스 내한공연",
            venue: "고양종합운동장",
            posterUrl: "https://placehold.co/300x400",
          },
        ]);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchConcerts();
  }, []);

  return (
    <div className="w-full">
      <h1 className="text-3xl font-bold mb-6">공연 정보</h1>
      {/* TODO: 날짜/장르 필터링 UI */}

      {loading ? (
        <p>로딩 중...</p>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {concerts.map((concert) => (
            <Link
              to={`/concerts/${concert.id}`}
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
      )}
    </div>
  );
};

export default ConcertListPage;
