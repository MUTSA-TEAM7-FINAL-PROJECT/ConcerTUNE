import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext'; 
import userService from '../../services/userService'; 

const formatDate = (dateString) => {
    if (!dateString) return '';
    return dateString; 
};

const calculateDDay = (dateString) => {
    if (!dateString) return 'N/A';
    const today = new Date();
    today.setHours(0, 0, 0, 0); 
    
    const targetDate = new Date(dateString);
    targetDate.setHours(0, 0, 0, 0);

    const diffTime = targetDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) return 'D-DAY';
    return diffDays > 0 ? `D-${diffDays}` : `D+${Math.abs(diffDays)}`;
};


const FollowFeedSection = () => {
    const { user } = useAuth();
    const [followFeeds, setFollowFeeds] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!user) {
            setLoading(false);
            setFollowFeeds([]);
            return; 
        }

        const fetchFollowFeeds = async () => {
            try {
                setLoading(true);
                const data = await userService.getFollowArtistFeeds(); 
                setFollowFeeds(data);
                setError(null);
            } catch (err) {
                console.error("팔로우 피드 정보를 불러오는 데 실패했습니다:", err);
                if (err.response && err.response.status === 401) {
                    setError("로그인이 필요합니다.");
                } else {
                    setError("팔로우 아티스트 소식을 불러오는 데 실패했습니다.");
                }
            } finally {
                setLoading(false);
            }
        };
        fetchFollowFeeds();
    }, [user]);

    if (loading) {
        return (
            <div className="lg:col-span-1">
                <h3 className="text-2xl font-bold mb-4 text-gray-800">🎶 팔로우 아티스트 소식</h3>
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-6 animate-pulse">
                    {[1, 2, 3].map(i => (
                        <div key={i} className="border rounded-xl shadow-lg overflow-hidden bg-gray-100">
                            <div className="w-full aspect-[3/4] bg-gray-300"></div>
                            <div className="p-4 space-y-2">
                                <div className="h-6 bg-gray-300 rounded w-3/4"></div>
                                <div className="h-4 bg-gray-300 rounded w-1/2"></div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
    
    if (error) {
        return (
            <div className="lg:col-span-1">
                <h3 className="text-2xl font-bold mb-4 text-gray-800">🎶 팔로우 아티스트 소식</h3>
                <p className="text-center text-red-500 p-4 bg-red-50 rounded-xl border border-red-200">{error}</p>
            </div>
        );
    }
    
    if (followFeeds.length === 0) {
        return (
            <div className="lg:col-span-1">
                <h3 className="text-2xl font-bold mb-4 text-gray-800">🎶 팔로우 아티스트 소식</h3>
                <p className="text-center text-gray-500 p-4 bg-gray-100 rounded-xl border">팔로우하는 아티스트의 예정된 공연 소식이 없습니다.</p>
            </div>
        );
    }
    
    return (
        <div className="lg:col-span-1">
            <h3 className="text-2xl font-bold mb-4 text-gray-800">🎶 팔로우 아티스트 소식</h3>
            
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-6">
                {followFeeds.map((feed) => {
                    const dDay = calculateDDay(feed.scheduleDate);
                    const dDayClasses = dDay.includes('D-DAY') || dDay.includes('D-') 
                        ? 'bg-red-500 text-white' 
                        : 'bg-gray-700 text-white'; 
                    return (
                        <Link
                            to={`/concerts/${feed.liveId}`}
                            key={feed.liveId}
                            className="group relative border rounded-xl shadow-lg hover:shadow-2xl transition-all duration-300 overflow-hidden bg-white transform hover:-translate-y-1"
                        >
                            <div className="w-full aspect-[3/4] overflow-hidden"> 
                                <img
                                    src={feed.posterUrl || `https://placehold.co/300x400/4F46E5/FFFFFF?text=${encodeURIComponent(feed.liveTitle)}`}
                                    alt={`${feed.liveTitle} 포스터`}
                                    className="w-full h-full object-cover group-hover:opacity-90 transition-opacity duration-300"
                                    onError={(e) => { 
                                        e.target.onerror = null; 
                                        e.target.src=`https://placehold.co/300x400/4F46E5/FFFFFF?text=${encodeURIComponent(feed.liveTitle)}`;
                                    }}
                                />
                            </div>
                            
                            <div className={`absolute top-0 right-0 m-3 px-3 py-1 text-xs font-bold rounded-full shadow-lg ${dDayClasses}`}>
                                {dDay}
                            </div>
                            
                            <div className="p-4">
                                <h3 className="text-lg font-bold truncate text-gray-900 leading-tight">
                                    {feed.liveTitle}
                                </h3>
                                <p className="text-sm text-indigo-600 truncate mt-1 font-semibold">
                                    {feed.artistName}
                                </p>
                                <p className="text-xs text-gray-500 mt-1">
                                    {formatDate(feed.scheduleDate)}
                                </p>
                            </div>
                        </Link>
                    );
                })}
            </div>
        </div>
    );
};

export default FollowFeedSection;
