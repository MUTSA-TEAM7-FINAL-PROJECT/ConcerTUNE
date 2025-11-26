import React, { useState } from 'react';

const ConcertRequestDetailModal = ({ request, statusMap, onClose, onStatusUpdate, isAdmin = false }) => {
    if (!request) return null;

    const [showRejectInput, setShowRejectInput] = useState(false);
    const [localRejectionReason, setLocalRejectionReason] = useState('');

    const currentStatus = request.requestStatus;
    const isPending = currentStatus === 'PENDING';
    const isRejected = currentStatus === 'REJECTED';

    const renderDataBlock = (title, data) => {
        const isEmpty = !data || 
                        (Array.isArray(data) && data.length === 0) || 
                        (typeof data === 'object' && Object.keys(data).length === 0);

        if (isEmpty) {
            return (
                <div className="text-xs text-gray-500 bg-gray-50 p-2 rounded">
                    {title} 정보 없음
                </div>
            );
        }

        const formattedData = JSON.stringify(data, null, 2);

        return (
            <div className="bg-gray-100 p-2 rounded text-xs overflow-auto max-h-48 font-mono whitespace-pre-wrap">
                {formattedData}
            </div>
        );
    };
    
    const DetailItem = ({ label, value, isLink = false }) => (
        <div className="flex justify-between border-b border-gray-200 py-2">
            <span className="text-gray-500 font-medium">{label}</span>
            <span className="text-gray-900 text-right max-w-[60%] truncate">
                {isLink ? (
                    <a href={value} target="_blank" rel="noopener noreferrer" className="text-indigo-600 hover:text-indigo-800 break-all">
                        {value}
                    </a>
                ) : (
                    value || 'N/A'
                )}
            </span>
        </div>
    );

    const formatCurrency = (amount) => {
        if (typeof amount !== 'number' || isNaN(amount)) return 'N/A';
        return amount.toLocaleString('ko-KR', { style: 'currency', currency: 'KRW' });
    };
    
    const renderSeatPrices = (prices) => {
        const isEmptyMap = !prices || typeof prices !== 'object' || Array.isArray(prices) || Object.keys(prices).length === 0;

        if (isEmptyMap) {
            return (
                <div className="text-xs text-gray-500 bg-gray-50 p-2 rounded">
                    좌석 가격 정보 없음
                </div>
            );
        }

        const seatEntries = Object.entries(prices).sort((a, b) => b[1] - a[1]);

        return (
            <div className="bg-white border border-gray-200 rounded-lg overflow-hidden shadow-sm">
                {seatEntries.map(([seatName, price], index) => (
                    <div 
                        key={seatName}
                        className={`flex justify-between items-center p-3 text-sm 
                            ${index < seatEntries.length - 1 ? 'border-b border-gray-100' : ''} 
                            hover:bg-gray-50 transition`}
                    >
                        <span className="font-semibold text-gray-700">{seatName}</span>
                        <span className="font-bold text-indigo-600">
                            {formatCurrency(price)}
                        </span>
                    </div>
                ))}
            </div>
        );
    };
    
    const renderSchedules = (schedules) => {
        const isEmpty = !schedules || schedules.length === 0;
        
        if (isEmpty) {
            return (
                <div className="text-xs text-gray-500 bg-gray-50 p-2 rounded">
                    스케줄 정보 없음
                </div>
            );
        }
        
        const formatSchedule = (schedule) => {
            if (!schedule.liveDate || !schedule.liveTime) return '날짜 또는 시간 미정';

            const dateTimeStr = `${schedule.liveDate}T${schedule.liveTime.substring(0, 5)}`;
            
            try {
                return new Date(dateTimeStr).toLocaleString('ko-KR', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    weekday: 'short',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false 
                });
            } catch (e) {
                return '잘못된 시간 형식';
            }
        };

        return (
            <div className="space-y-2">
                {schedules.map((schedule, index) => (
                    <div 
                        key={index} 
                        className="p-3 bg-gray-100 rounded-lg text-sm font-medium text-gray-700 border border-gray-200 hover:bg-gray-200 transition"
                    >
                        {formatSchedule(schedule)}
                    </div>
                ))}
            </div>
        );
    }
    // -------------------------------------------------------------------

    // --- [수정된 핸들러] ---
    const handleUpdate = (newStatus) => {
        // 입력 필드가 보이는 상태에서 다른 버튼을 누르면 입력창 숨기기
        if (showRejectInput) {
            setShowRejectInput(false);
            setLocalRejectionReason('');
        }
        
        if (newStatus === 'APPROVED') {
            // 승인은 바로 처리
            onStatusUpdate(request.requestId, newStatus);
        } else if (newStatus === 'REJECTED') {
            // 반려 시 입력창 표시
            setShowRejectInput(true);
        }
    };
    
    const handleRejectConfirmation = () => {
        if (!localRejectionReason.trim()) {
            console.warn("반려 사유를 입력해주세요.");
            return;
        }

        onStatusUpdate(request.requestId, 'REJECTED', localRejectionReason.trim());
        setShowRejectInput(false);
        setLocalRejectionReason('');
    };

    return (
        <div className="fixed inset-0 bg-gray-900 bg-opacity-70 overflow-y-auto h-full w-full z-50 flex justify-center items-center p-4">
            <div className="relative p-6 border w-full md:w-3/5 lg:w-2/5 shadow-2xl rounded-xl bg-white max-h-[90vh] overflow-y-auto transition-all duration-300 transform scale-100">
                
                <div className="flex justify-between items-start pb-4 border-b border-gray-100 mb-4">
                    <h3 className="text-2xl font-extrabold text-gray-900">
                        {request.title}
                        <span className="block text-sm font-normal text-gray-400 mt-1">요청 ID: {request.requestId}</span>
                    </h3>
                    <button 
                        onClick={onClose} 
                        className="text-gray-400 hover:text-gray-600 text-3xl leading-none font-semibold transition-colors"
                        aria-label="Close"
                    >
                        &times;
                    </button>
                </div>

                <div className="space-y-6">
                    <div className="p-4 bg-indigo-50 rounded-lg shadow-inner">
                        <h4 className="text-lg font-bold text-indigo-700 mb-3">요청 및 상태</h4>
                        <DetailItem label="요청자" value={request.requester} />
                        <DetailItem 
                            label="상태" 
                            value={
                                <span className={`font-bold ${
                                    isPending ? 'text-yellow-600' : 
                                    currentStatus === 'APPROVED' ? 'text-green-600' : 'text-red-600'
                                }`}>
                                    {statusMap[currentStatus]}
                                </span>
                            } 
                        />
                        <DetailItem label="요청일" value={new Date(request.requestCreatedAt).toLocaleString()} />
                        <DetailItem label="최종 변경일" value={request.statusUpdatedAt ? new Date(request.statusUpdatedAt).toLocaleString() : 'N/A'} />
                        
                        {isRejected && request.rejectionReason && (
                            <div className="mt-3 p-3 bg-red-100 border-l-4 border-red-500 rounded">
                                <strong className="text-red-700">반려 사유:</strong> {request.rejectionReason}
                            </div>
                        )}
                    </div>

                    <div className="space-y-4">
                        <h4 className="text-xl font-bold border-b pb-2 text-gray-800">공연 상세 정보</h4>
                        <p className="text-md text-gray-700 mb-4">
                            <strong className="block text-gray-600 mb-1">설명:</strong> 
                            {request.description || '설명 없음'}
                        </p>
                        
                        <DetailItem label="장소" value={request.venue} />
                        <DetailItem label="티켓 URL" value={request.ticketUrl} isLink={true} />
                    </div>
                    
                    <div className="py-4 border-t border-gray-200">
                        <h4 className="text-lg font-semibold mb-2 text-gray-800">공연 스케줄 (일시)</h4>
                        {renderSchedules(request.schedules)}
                    </div>

                    {request.posterUrl && (
                        <div className="py-4 border-t border-gray-200">
                            <h4 className="text-lg font-semibold mb-2 text-gray-800">포스터</h4>
                            <img 
                                src={request.posterUrl} 
                                alt="Poster" 
                                className="max-w-full md:max-w-sm mt-2 border-4 border-gray-100 rounded-xl shadow-lg" 
                            />
                        </div>
                    )}
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t border-gray-200">
                        
                        <div>
                            <h4 className="font-semibold mb-2 text-gray-800">아티스트</h4>
                            <div className="bg-gray-100 p-2 rounded text-xs font-mono overflow-auto max-h-48">
                                {request.artistNames && request.artistNames.length > 0
                                    ? request.artistNames.join(', ')
                                    : '연결된 아티스트 없음'}
                            </div>
                        </div>

                        <div>
                            <h4 className="font-semibold mb-2 text-gray-800">좌석 가격</h4>
                            {renderSeatPrices(request.seatPrices)}
                        </div>
                        
                        <div className="md:col-span-2">
                            <h4 className="font-semibold mb-2 text-gray-800">새 아티스트 등록 요청 </h4>
                            {renderDataBlock("새 아티스트 요청", request.newArtistRequestsData)}
                        </div>
                        
                    </div>
                </div>
                
                {isAdmin && showRejectInput && (
                    <div className="mt-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                        <h4 className="font-bold text-red-700 mb-2">🚨 요청 반려 사유 입력</h4>
                        <textarea
                            className="w-full p-2 border border-red-300 rounded-lg text-sm focus:ring-red-500 focus:border-red-500"
                            rows="3"
                            placeholder="요청이 반려된 구체적인 사유를 입력하세요. (필수)"
                            value={localRejectionReason}
                            onChange={(e) => setLocalRejectionReason(e.target.value)}
                        />
                        <div className="flex justify-end space-x-3 mt-3">
                             <button
                                onClick={() => {
                                    setShowRejectInput(false);
                                    setLocalRejectionReason(''); 
                                }}
                                className="px-4 py-2 bg-gray-300 text-gray-800 text-sm font-semibold rounded-lg hover:bg-gray-400 transition"
                            >
                                취소
                            </button>
                            <button
                                onClick={handleRejectConfirmation}
                                className="px-4 py-2 bg-red-600 text-white text-sm font-bold rounded-lg hover:bg-red-700 transition shadow-md"
                            >
                                반려 확정
                            </button>
                        </div>
                    </div>
                )}


                <div className="mt-8 flex justify-end space-x-3 pt-4 border-t border-gray-200">
                    {isAdmin && (
                        <>
                            {isPending && !showRejectInput && (
                                <>
                                    <button
                                        onClick={() => handleUpdate('APPROVED')}
                                        className="px-5 py-2 bg-green-600 text-white text-sm font-bold rounded-lg hover:bg-green-700 transition shadow-md"
                                    >
                                        요청 승인
                                    </button>
                                    <button
                                        onClick={() => handleUpdate('REJECTED')}
                                        className="px-5 py-2 bg-red-600 text-white text-sm font-bold rounded-lg hover:bg-red-700 transition shadow-md"
                                    >
                                        요청 반려
                                    </button>
                                </>
                            )}
                            {!isPending && !showRejectInput && (
                                <span className="text-sm text-gray-500 self-center italic">이미 처리된 요청입니다.</span>
                            )}
                        </>
                    )}
                    
                    <button
                        onClick={onClose}
                        className="px-5 py-2 bg-gray-200 text-gray-700 text-sm font-bold rounded-lg hover:bg-gray-300 transition"
                    >
                        닫기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConcertRequestDetailModal;