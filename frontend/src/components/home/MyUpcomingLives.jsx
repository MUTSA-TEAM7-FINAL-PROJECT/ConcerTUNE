// src/components/home/MyUpcomingLives.jsx (새 컴포넌트)

import React, { useEffect, useState } from 'react';
import concertService from '../../services/concertService'; 
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext'; 
const MyUpcomingLives = () => {
    const { user } = useAuth();
    
    const [followedLives, setFollowedLives] = useState([]);
    const [nearestLive, setNearestLive] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!user?.id) {
            setIsLoading(false);
            return;
        }

        const fetchPersonalizedData = async () => {
            setIsLoading(true); 
            setError(null);
            
            try {
                const [nearestData, livesData] = await Promise.all([
                    concertService.getNearestBookmarkedLive(),
                    concertService.getUpcomingLivesOfFollowedArtists()
                ]);
                
                setNearestLive(nearestData && nearestData.id ? nearestData : null); 
                setFollowedLives(livesData || []);

            } catch (err) {
                setError("개인 맞춤 정보를 불러오는 데 실패했습니다."); 
            } finally {
                setIsLoading(false);
            }
        };
        fetchPersonalizedData();
    }, [user]);

    if (!user?.id) return null; 
    if (isLoading) return <div className="text-center py-4 text-indigo-600">개인 맞춤 정보 로딩 중...</div>;

    
   return (
        <section className="space-y-6">
            <h3 className="text-2xl font-bold border-b pb-2">🗓️ 다가오는 라이브</h3>
            
            <div className="pt-2"> 
                <h4 className="text-lg font-semibold text-indigo-800 mb-3">가장 가까운 북마크 공연</h4>
                {nearestLive ? (
                    <div className="bg-indigo-50 p-4 rounded-lg shadow-sm border-l-4 border-indigo-600">
                        <Link to={`/concerts/${nearestLive.id}`} className="text-gray-800 hover:text-indigo-600 transition">
                             {nearestLive.title} ({nearestLive.schedules?.[0]?.liveDate || '날짜 미정'})
                        </Link>
                    </div>
                ) : (
                    <p className="text-gray-500 text-sm">가장 가까운 북마크 라이브가 없습니다.</p>
                )}
            </div>

            <div className="pt-2">
                <h4 className="text-lg font-semibold text-gray-800 mb-3">팔로우 아티스트 예정 공연 ({followedLives.length}건)</h4>
                {followedLives.length > 0 ? (
                    <ul className="space-y-2">
                        {followedLives.slice(0, 10).map(live => ( 
                            <li key={live.id} className="text-sm p-2 bg-white rounded border border-gray-200 hover:bg-gray-50">
                                <Link to={`/concerts/${live.id}`}>
                                    {live.title} - {live.schedule?.liveDate || '날짜 미정'}
                                </Link>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500 text-sm">팔로우 아티스트의 예정된 라이브가 없습니다.</p>
                )}
            </div>
        </section>
    );
};

export default MyUpcomingLives;