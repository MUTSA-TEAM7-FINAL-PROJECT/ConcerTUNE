// src/components/home/LatestConcerts.jsx (최종 수정)

import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import concertService from "../../services/concertService"; 
import { useAuth } from "../../context/AuthContext"; 

const LatestConcerts = () => {
    const { user } = useAuth();
    const userId = user?.id || null;
    
    const [concerts, setConcerts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // ⭐⭐ 조회할 기본 개수를 5로 설정 ⭐⭐
    const DEFAULT_COUNT = 5; 

    useEffect(() => {
        const fetchUpcomingConcerts = async () => {
            try {
                setLoading(true);
                console.log("Fetching upcoming concerts for userId:", userId);
                const data = await concertService.getUpcomingLives(userId, DEFAULT_COUNT); 
                setConcerts(data);
            } catch (err) {
                console.error("다가오는 공연 정보를 불러오는 데 실패했습니다:", err);
                setError("다가오는 공연 정보를 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };
        fetchUpcomingConcerts();
    }, [userId]); 

    return (
        <section>
            <h2 className="text-2xl font-bold mb-4 flex justify-between items-center">
                다가오는 추천 공연
                <Link to="/concerts" className="text-sm font-medium text-indigo-600 hover:text-indigo-800">
                    더 보기 &rarr;
                </Link>
            </h2>
            {loading ? (
                <div className="text-center py-8 text-indigo-600">공연 정보 로딩 중...</div>
            ) : error ? (
                <div className="text-center py-8 text-red-600">{error}</div>
            ) : (
                // ⭐⭐ 모바일에서 2개, 데스크탑에서 5개 (lg:grid-cols-5) 표시하도록 수정 ⭐⭐
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
                    {concerts.map((concert) => (
                        <Link
                            to={`/concerts/${concert.id}`} 
                            key={concert.id}
                            className="border rounded-xl shadow-lg hover:shadow-2xl transition-all duration-300 overflow-hidden bg-white transform hover:-translate-y-1 relative group"
                        >
                            {concert.isBookmarked && (
                                <span className="absolute top-3 right-3 text-2xl text-yellow-500 z-10" title="북마크됨">★</span>
                            )}
                            
                            <div className="w-full aspect-[3/4] overflow-hidden"> 
                                <img
                                    src={concert.posterUrl || "https://placehold.co/300x400?text=No+Poster"}
                                    alt={concert.title}
                                    className="w-full h-full object-cover group-hover:opacity-90 transition-opacity duration-300"
                                />
                            </div>
                            <div className="p-4">
                                <h3 className="text-lg font-bold truncate text-gray-900">
                                    {concert.title}
                                </h3>
                                <p className="text-sm text-gray-500 truncate mt-1">
                                    예매일 : {concert.ticketDateTime ? new Date(concert.ticketDateTime).toLocaleDateString() : '예매일 미정'}
                                </p>
                            </div>
                        </Link>
                    ))}
                    {concerts.length === 0 && <p className="col-span-5 text-center text-gray-500">다가오는 공연 정보가 없습니다.</p>}
                </div>
            )}
        </section>
    );
};

export default LatestConcerts;