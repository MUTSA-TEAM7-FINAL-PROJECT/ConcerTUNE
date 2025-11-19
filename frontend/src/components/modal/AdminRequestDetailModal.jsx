// components/AdminRequestDetailModal.jsx

import React from 'react';

const AdminRequestDetailModal = ({ request, statusMap, onClose, onStatusUpdate }) => {
    if (!request) return null;

    const currentStatus = request.requestStatus;
    const isPending = currentStatus === 'PENDING';

    // JSON/Map 데이터를 보기 좋게 렌더링하는 헬퍼 함수
    const renderJsonData = (data) => {
        if (!data) return "정보 없음";
        return JSON.stringify(data, null, 2).replace(/[{},"]/g, '').replace(/:\s/g, ' - ');
    };

    const handleUpdate = (newStatus) => {
        onStatusUpdate(request.requestId, newStatus);
    };

    return (
        // 💡 배경색을 bg-black, 투명도를 bg-opacity-10으로 "최소화"
        <div className="fixed inset-0 bg-black bg-opacity-10 backdrop-blur-xl overflow-y-auto h-full w-full z-50"> 
            <div className="relative top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 p-5 border w-4/5 md:w-3/5 shadow-lg rounded-md bg-white max-h-[90vh] overflow-y-auto">
                
                {/* 모달 헤더 */}
                <div className="flex justify-between items-center pb-3 border-b">
                    <h3 className="text-2xl font-bold text-gray-900">
                        {request.title} <span className="text-base font-normal text-gray-500">({request.requestId})</span>
                    </h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-3xl leading-none font-semibold">&times;</button>
                </div>

                {/* 상세 정보 */}
                <div className="mt-4 space-y-4 text-sm">
                    <p><strong>요청자:</strong> {request.requester}</p>
                    <p><strong>상태:</strong> <span className={`font-semibold ${isPending ? 'text-yellow-600' : currentStatus === 'APPROVED' ? 'text-green-600' : 'text-red-600'}`}>{statusMap[currentStatus]}</span></p>
                    <p><strong>요청일:</strong> {new Date(request.requestCreatedAt).toLocaleString()}</p>
                    <p><strong>최종 상태 변경일:</strong> {request.statusUpdatedAt ? new Date(request.statusUpdatedAt).toLocaleString() : 'N/A'}</p>
                    
                    <div className="pt-3 border-t">
                        <h4 className="text-lg font-semibold mb-2">공연 정보</h4>
                        <p><strong>설명:</strong> {request.description}</p>
                        <p><strong>장소:</strong> {request.venue}</p>
                        <p><strong>티켓 URL:</strong> <a href={request.ticketUrl} target="_blank" rel="noopener noreferrer" className="text-indigo-600 hover:text-indigo-800 break-all">{request.ticketUrl}</a></p>
                        {request.posterUrl && (
                            <div>
                                <strong>포스터:</strong>
                                <img src={request.posterUrl} alt="Poster" className="max-w-xs mt-2 border rounded-md" />
                            </div>
                        )}
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-3 border-t">
                        
                        {/* 1. 등록된 아티스트 이름 (한 줄에 하나씩) */}
                        <div>
                            <h4 className="font-semibold mb-1">등록된 아티스트 이름</h4>
                            <div className="bg-gray-100 p-2 rounded text-xs overflow-auto h-24 whitespace-pre-wrap">
                                {request.artistNames && request.artistNames.length > 0 ? (
                                    <ul className="list-disc list-inside space-y-1">
                                        {request.artistNames.map((name, index) => (
                                            <li key={index}>{name}</li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="text-gray-500">연결된 아티스트 없음</p>
                                )}
                            </div>
                        </div>
                        
                        {/* 2. 새 아티스트 요청 (name 필드만 추출) */}
                        <div>
                            <h4 className="font-semibold mb-1">새 아티스트 요청</h4>
                            <div className="bg-gray-100 p-2 rounded text-xs overflow-auto h-24 whitespace-pre-wrap">
                                {request.newArtistRequestsData && request.newArtistRequestsData.length > 0 ? (
                                    <ul className="list-disc list-inside space-y-1">
                                        {request.newArtistRequestsData.map((artist, index) => (
                                            <li key={index}>
                                                {artist.name} ({artist.isDomestic ? '국내' : '해외'})
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="text-gray-500">새 아티스트 요청 없음</p>
                                )}
                            </div>
                        </div>
                        
                        {/* 3. 좌석 가격 (기존 JSON 렌더링 유지) */}
                        <div>
                            <h4 className="font-semibold mb-1">좌석 가격</h4>
                            <pre className="bg-gray-100 p-2 rounded text-xs overflow-auto h-24 whitespace-pre-wrap">{renderJsonData(request.seatPrices)}</pre>
                        </div>
                        
                        {/* 4. 요청된 스케줄 (기존 JSON 렌더링 유지) */}
                        <div>
                            <h4 className="font-semibold mb-1">요청된 스케줄 </h4>
                            <pre className="bg-gray-100 p-2 rounded text-xs overflow-auto h-24 whitespace-pre-wrap">{renderJsonData(request.requestedSchedules)}</pre>
                        </div>
                        
                    </div>
                </div>

                {/* 모달 푸터 / 관리 버튼 */}
                <div className="mt-6 flex justify-end space-x-3 pt-4 border-t">
                    {isPending && (
                        <>
                            <button
                                onClick={() => handleUpdate('APPROVED')}
                                className="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-md hover:bg-green-700 transition"
                            >
                                요청 승인
                            </button>
                            <button
                                onClick={() => handleUpdate('REJECTED')}
                                className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-md hover:bg-red-700 transition"
                            >
                                요청 반려
                            </button>
                        </>
                    )}
                    {!isPending && (
                        <span className="text-sm text-gray-500 self-center">이미 처리된 요청입니다.</span>
                    )}
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-200 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-300 transition"
                    >
                        닫기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AdminRequestDetailModal;