import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

import artistService from '../services/artistService'; 
import fileService from '../services/fileService'; 
import concertService from '../services/concertService';

const ConcertRequestPage = () => {
    const { isLoggedIn } = useAuth();
    const navigate = useNavigate();
    const fileInputRef = useRef(null);

    const [artists, setArtists] = useState([]); 
    const [searchTerm, setSearchTerm] = useState('');
    const [newArtistName, setNewArtistName] = useState('');
    
    const [requestData, setRequestData] = useState({
        title: '',
        description: '',
        venue: '',
        ticketUrl: '', // 💡 ticketUrl 상태 추가
        schedules: [{ liveDate: '', liveTime: '' }],
        selectedArtists: [], 
        seatPrices: [{ seatType: 'VIP석', price: 120000 }]
    });
    
    const [posterFile, setPosterFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // 아티스트 목록을 불러오는 함수
    const fetchArtists = async () => {
        try {
            const data = await artistService.getArtistsAll();
            setArtists(data);
        } catch (error) {
            console.error("Failed to fetch artists:", error);
            setError("아티스트 목록을 불러오는 데 실패했습니다.");
        } 
    };

    // 로그인 여부 확인 및 아티스트 목록 로드
    useEffect(() => {
        if (!isLoggedIn) {
            alert("로그인 후 공연 요청을 할 수 있습니다.");
            navigate('/login');
        } else {
            fetchArtists(); 
        }
    }, [isLoggedIn, navigate]);

    // 폼 입력값 변경 핸들러
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setRequestData(prev => ({ ...prev, [name]: value }));
    };

    // 포스터 파일 변경 핸들러
    const handleFileChange = (e) => {
        setPosterFile(e.target.files[0]);
    };

    // 좌석 가격 변경 핸들러
    const handlePriceChange = (index, field, value) => {
        const newSeatPrices = [...requestData.seatPrices];
        const parsedValue = field === 'price' ? (value === '' ? '' : parseInt(value)) : value;
        
        newSeatPrices[index] = { 
            ...newSeatPrices[index], 
            [field]: parsedValue
        };
        setRequestData(prev => ({ ...prev, seatPrices: newSeatPrices }));
    };

    // 좌석 가격 항목 추가
    const addSeatPrice = () => {
        setRequestData(prev => ({
            ...prev,
            seatPrices: [...prev.seatPrices, { seatType: '', price: '' }]
        }));
    };

    // 좌석 가격 항목 제거
    const removeSeatPrice = (index) => {
        setRequestData(prev => ({
            ...prev,
            seatPrices: prev.seatPrices.filter((_, i) => i !== index)
        }));
    };
    
    // 일정 변경 핸들러
    const handleScheduleChange = (index, field, value) => {
        const newSchedules = [...requestData.schedules];
        newSchedules[index] = { 
            ...newSchedules[index], 
            [field]: value 
        };
        setRequestData(prev => ({ ...prev, schedules: newSchedules }));
    };

    // 일정 추가
    const addSchedule = () => {
        setRequestData(prev => ({
            ...prev,
            schedules: [...prev.schedules, { liveDate: '', liveTime: '' }]
        }));
    };

    // 일정 제거
    const removeSchedule = (index) => {
        if (requestData.schedules.length > 1) {
            setRequestData(prev => ({
                ...prev,
                schedules: prev.schedules.filter((_, i) => i !== index)
            }));
        } else {
            alert("최소한 하나의 공연 일정이 필요합니다.");
        }
    };

    console.log(artists);
    // 검색어에 따라 아티스트 목록을 필터링하는 로직
    const filteredArtists = (artists ?? []).filter(artist =>
    artist?.artistName?.toLowerCase().includes(searchTerm.toLowerCase()) &&
    !requestData.selectedArtists.some(selected => selected.artistId === artist.artistId && !selected.isNew)
    );
    
    // 기존 아티스트 선택 핸들러 (검색 결과 클릭 시)
    const handleArtistSelection = (artist) => {
        if (artist && !requestData.selectedArtists.some(a => a.artistId === artist.artistId && !a.isNew)) {
            setRequestData(prev => ({
                ...prev,
                selectedArtists: [...prev.selectedArtists, { ...artist, isNew: false, isDomesticHint: null }]
            }));
            setSearchTerm('');
        }
    };

    // 새 아티스트 직접 입력 및 추가
    const addNewArtist = () => {
        const trimmedName = newArtistName.trim();
        if (trimmedName && !requestData.selectedArtists.some(a => a.artistName === trimmedName)) {
            const newId = (requestData.selectedArtists.length + 1) * -1;
            
            const isDomestic = window.confirm(`'${trimmedName}' 아티스트는 국내 아티스트인가요?`); 
            
            setRequestData(prev => ({
                ...prev,
                selectedArtists: [
                    ...prev.selectedArtists, 
                    { artistId: newId, artistName: trimmedName, isNew: true, isDomesticHint: isDomestic }
                ]
            }));
            setNewArtistName('');
        }
    };
    
    // 아티스트 제거
    const removeArtist = (artistId, isNew) => {
        setRequestData(prev => ({
            ...prev,
            selectedArtists: prev.selectedArtists.filter(a => !(a.artistId === artistId && a.isNew === isNew))
        }));
    };


    // 폼 제출 핸들러 (LiveRequestCreateDto 구조 기반)
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // 1. 필수 유효성 검사
        if (requestData.selectedArtists.length === 0) {
            alert("요청 대상 아티스트를 최소 1명 선택하거나 직접 입력해야 합니다.");
            return;
        }

        const validSchedules = requestData.schedules.filter(s => s.liveDate && s.liveTime);
        if (validSchedules.length === 0) {
            alert("유효한 공연 일자 및 시간을 최소 1개 입력해야 합니다.");
            return;
        }
        if (!requestData.title) {
            alert("공연 제목은 필수 입력 사항입니다.");
            return;
        }
        if (!posterFile) {
             alert("공연 포스터 이미지는 필수입니다.");
            return;
        }

        // Map<String, Integer> 형식으로 변환 및 유효성 검사
        const pricesMap = requestData.seatPrices.reduce((acc, item) => {
            const price = parseInt(item.price);
            if (item.seatType && price > 0 && !isNaN(price)) {
                acc[item.seatType] = price;
            }
            return acc;
        }, {});
        
        if (Object.keys(pricesMap).length === 0) {
             alert("최소한 하나의 유효한 좌석 가격(0원 초과)을 입력해야 합니다.");
             return;
        }

        setLoading(true);
        setError(null);
        let posterImageUrl = ''; 

        try {
            // 2. 포스터 파일 업로드 (URL을 먼저 받음)
            console.log("1. Uploading poster file...");
            posterImageUrl = await fileService.uploadFile(posterFile, 'liveRequest');
            console.log("Poster URL received:", posterImageUrl);


            // 3. DTO 필드에 맞게 아티스트 정보 구조화
            const existingArtistIds = requestData.selectedArtists
                .filter(a => !a.isNew)
                .map(a => a.artistId);
                
            const newArtistRequests = requestData.selectedArtists
                .filter(a => a.isNew)
                .map(a => ({ 
                    name: a.artistName, 
                    isDomestic: a.isDomesticHint,
                }));

            // 4. LiveRequestCreateDto (JSON DTO) 객체 생성
            const requestDto = {
                title: requestData.title,
                description: requestData.description,
                
                // DTO 필드명 posterUrl 사용
                posterUrl: posterImageUrl, 
                
                // 💡 사용자가 입력한 ticketUrl 값 사용
                ticketUrl: requestData.ticketUrl, 
                
                venue: requestData.venue,
                seatPrices: pricesMap, 
                schedules: validSchedules,

                existingArtistIds: existingArtistIds,
                newArtistRequests: newArtistRequests,
            };
            
            console.log("2. Sending Live Request (LiveRequestCreateDto JSON):", requestDto);

            await concertService.submitLiveRequest(requestDto); 

            alert("공연 요청이 성공적으로 등록되었습니다. 아티스트/운영진의 검토를 기다려주세요.");
            navigate('/concerts');
        } catch (err) {
            console.error("Request submission failed:", err);
            setError("공연 요청 등록에 실패했습니다. 입력 정보를 확인해주세요.");
        } finally {
            setLoading(false);
        }
    };

    if (!isLoggedIn) return null;
    if (error && !loading) return <div className="text-center mt-10 text-red-600">{error}</div>;

    return (
        <div className="w-full max-w-4xl mx-auto p-6 md:p-10 bg-white shadow-2xl rounded-xl my-10">
            <h1 className="text-3xl font-bold mb-8 text-indigo-700">🎤 신규 공연 요청 등록</h1>
            
            <form onSubmit={handleSubmit} className="space-y-8">
                
                {/* 1. 요청 대상 아티스트 (검색/추가 기능 강화) */}
                <div className="p-4 border border-indigo-200 rounded-lg bg-indigo-50 relative">
                    <label className="block text-xl font-bold text-gray-800 mb-3">
                        요청 대상 아티스트 <span className="text-red-500">*</span> (검색하거나 직접 입력)
                    </label>

                    {/* 기존 아티스트 검색 필드 및 자동 완성 목록 */}
                    <div className="relative">
                        <input
                            type="text"
                            placeholder="아티스트 이름 검색..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                        />
                        
                        {/* 검색 결과 목록 (드롭다운 스타일) */}
                        {searchTerm && filteredArtists.length > 0 && (
                            <div className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-40 overflow-y-auto">
                                {filteredArtists.map(artist => (
                                    <div 
                                        key={artist.artistId} 
                                        className="p-3 cursor-pointer hover:bg-indigo-50 border-b last:border-b-0 text-gray-800"
                                        onClick={() => handleArtistSelection(artist)}
                                    >
                                        {artist.artistName} <span className="text-xs text-indigo-500 ml-2">[선택하여 추가]</span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                    
                    {/* 신규 아티스트 직접 입력 섹션 */}
                    <div className="mt-4 pt-4 border-t border-indigo-200 flex gap-2 items-center">
                        <input
                            type="text"
                            placeholder="목록에 없는 신규 아티스트를 직접 입력"
                            value={newArtistName}
                            onChange={(e) => setNewArtistName(e.target.value)}
                            className="flex-1 border border-gray-300 rounded-lg p-3"
                        />
                        <button
                            type="button"
                            onClick={addNewArtist}
                            disabled={!newArtistName.trim()}
                            className="bg-purple-600 text-white px-4 py-3 rounded-lg hover:bg-purple-700 disabled:bg-gray-400 transition-colors whitespace-nowrap text-sm font-semibold"
                        >
                            + 신규 아티스트 요청
                        </button>
                    </div>

                    {/* 선택된 아티스트 목록 */}
                    <div className="mt-4 flex flex-wrap gap-2 pt-4 border-t border-indigo-200">
                        <span className="text-sm font-medium text-gray-700 w-full mb-1">선택된 아티스트:</span>
                        {requestData.selectedArtists.length > 0 ? (
                            requestData.selectedArtists.map((artist) => (
                                <span 
                                    key={`${artist.artistId}-${artist.isNew ? 'new' : 'existing'}`} 
                                    className={`flex items-center gap-1 rounded-full px-3 py-1 text-sm font-medium ${artist.isNew ? 'bg-purple-100 text-purple-700' : 'bg-green-100 text-green-700'}`}
                                >
                                    {artist.artistName} 
                                    {artist.isNew && <span className="text-xs ml-1">(신규)</span>}
                                    <button type="button" onClick={() => removeArtist(artist.artistId, artist.isNew)} className="ml-1 text-xs hover:text-red-500 font-bold">
                                        &times;
                                    </button>
                                </span>
                            ))
                        ) : (
                            <p className="text-sm text-gray-500">아티스트를 검색하여 추가해주세요.</p>
                        )}
                    </div>
                </div>
                
                <hr className="border-gray-200" />
                
                {/* 2. 공연 기본 정보 */}
                <div className="space-y-6">
                    <div>
                        <label htmlFor="title" className="block text-lg font-medium text-gray-700 mb-2">공연 제목 <span className="text-red-500">*</span></label>
                        <input
                            type="text"
                            id="title"
                            name="title"
                            value={requestData.title}
                            onChange={handleInputChange}
                            required
                            className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                        />
                    </div>
                    
                    <div>
                        <label htmlFor="description" className="block text-lg font-medium text-gray-700 mb-2">공연 상세 내용</label>
                        <textarea
                            id="description"
                            name="description"
                            rows="4"
                            value={requestData.description}
                            onChange={handleInputChange}
                            className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                        />
                    </div>

                    <div>
                        <label htmlFor="venue" className="block text-lg font-medium text-gray-700 mb-2">공연 희망 장소/지역</label>
                        <input
                            type="text"
                            id="venue"
                            name="venue"
                            value={requestData.venue}
                            onChange={handleInputChange}
                            className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                        />
                    </div>

                    {/* 💡 ticketUrl 입력 필드 추가 */}
                    <div>
                        <label htmlFor="ticketUrl" className="block text-lg font-medium text-gray-700 mb-2">티켓 예매 링크 (선택 사항)</label>
                        <input
                            type="url"
                            id="ticketUrl"
                            name="ticketUrl"
                            value={requestData.ticketUrl}
                            onChange={handleInputChange}
                            placeholder="예: https://ticket.melon.com/..."
                            className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                        />
                    </div>
                    {/* // 💡 ticketUrl 입력 필드 끝 */}

                    {/* 3. 포스터 파일 입력 */}
                    <div>
                        <label htmlFor="posterFile" className="block text-lg font-medium text-gray-700 mb-2">공연 포스터 이미지 <span className="text-red-500">*</span></label>
                        <input
                            type="file"
                            id="posterFile"
                            name="posterFile"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            accept="image/*"
                            required
                            className="w-full border border-gray-300 rounded-lg p-3 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
                        />
                        {posterFile && (
                            <p className="mt-2 text-sm text-gray-500">선택된 파일: **{posterFile.name}**</p>
                        )}
                    </div>
                </div>

                <hr className="border-gray-200" />

                {/* 4. 공연 일정 (다중 입력) */}
                <div className="p-4 border border-indigo-200 rounded-lg bg-indigo-50">
                    <h3 className="text-xl font-bold text-gray-800 mb-4">공연 희망 일정 <span className="text-red-500">*</span></h3>
                    {requestData.schedules.map((schedule, index) => (
                        <div key={index} className="flex gap-4 mb-3 items-center">
                            <span className="text-md font-semibold text-indigo-700 w-10">#{index + 1}</span>
                            <input
                                type="date"
                                value={schedule.liveDate}
                                onChange={(e) => handleScheduleChange(index, 'liveDate', e.target.value)}
                                required
                                className="flex-1 border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                            />
                            <input
                                type="time"
                                value={schedule.liveTime}
                                onChange={(e) => handleScheduleChange(index, 'liveTime', e.target.value)}
                                required
                                className="flex-1 border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-indigo-500 focus:ring-indigo-500"
                            />
                            {requestData.schedules.length > 1 && (
                                <button
                                    type="button"
                                    onClick={() => removeSchedule(index)}
                                    className="text-red-500 hover:text-red-700 text-2xl font-bold p-1"
                                >
                                    &times;
                                </button>
                            )}
                        </div>
                    ))}
                    <button
                        type="button"
                        onClick={addSchedule}
                        className="mt-3 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors font-medium text-sm"
                    >
                        + 일정 추가
                    </button>
                </div>

                <hr className="border-gray-200" />

                {/* 5. 좌석 가격 정보 (다중 입력) */}
                <div className="p-4 border border-green-200 rounded-lg bg-green-50">
                    <h3 className="text-xl font-bold text-gray-800 mb-4">좌석 종류 및 가격 <span className="text-red-500">*</span></h3>
                    {requestData.seatPrices.map((priceItem, index) => (
                        <div key={index} className="flex gap-4 mb-3 items-center">
                            <input
                                type="text"
                                placeholder="좌석 종류 (예: VIP석, R석)"
                                value={priceItem.seatType}
                                onChange={(e) => handlePriceChange(index, 'seatType', e.target.value)}
                                required
                                className="flex-1 border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-green-500 focus:ring-green-500"
                            />
                            <input
                                type="number"
                                placeholder="가격 (원)"
                                value={priceItem.price}
                                onChange={(e) => handlePriceChange(index, 'price', e.target.value)}
                                required
                                min="1"
                                className="flex-1 border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-green-500 focus:ring-green-500"
                            />
                            {requestData.seatPrices.length > 0 && (
                                <button
                                    type="button"
                                    onClick={() => removeSeatPrice(index)}
                                    className="text-red-500 hover:text-red-700 text-2xl font-bold p-1"
                                >
                                    &times;
                                </button>
                            )}
                        </div>
                    ))}
                    <button
                        type="button"
                        onClick={addSeatPrice}
                        className="mt-3 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors font-medium text-sm"
                    >
                        + 좌석 가격 추가
                    </button>
                </div>


                {/* 제출 버튼 */}
                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-indigo-700 text-white font-bold py-4 rounded-xl hover:bg-indigo-800 transition-colors text-xl disabled:bg-gray-400"
                >
                    {loading ? '요청 등록 중...' : '🔥 공연 요청 등록하기'}
                </button>
                
                {error && <p className="text-red-500 text-center mt-4 font-medium">{error}</p>}
                
            </form>
        </div>
    );
};

export default ConcertRequestPage;