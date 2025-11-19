import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; 
import liveRequestService from '../services/liveRequestService'; 
import AdminRequestDetailModal from '../components/modal/AdminRequestDetailModal';
const PAGE_SIZE = 10; 

const AdminRequestListPage = () => {
    // 💡 isLoading 상태 추가
    const { user: currentUser, isLoading } = useAuth(); 
    const navigate = useNavigate();
    
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(0); 
    const [totalPages, setTotalPages] = useState(0);
    
    // 💡 모달 상태 및 선택된 요청 정보 상태
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedRequest, setSelectedRequest] = useState(null); 
    
    const statusMap = {
        PENDING: '대기 중 🟡',
        APPROVED: '승인 완료 🟢',
        REJECTED: '반려됨 🔴',
    };

    // 💡 페이지네이션 포함 데이터 로딩 함수
    const fetchRequests = async () => {
        setLoading(true);
        setError(null);

        try {
            const pageableParams = { 
                page: currentPage, 
                size: PAGE_SIZE, 
                sort: 'requestCreatedAt,desc' 
            };
            
            const pageData = await liveRequestService.getAllLiveRequestsForAdmin(pageableParams);
            
            setRequests(pageData.content || []);
            setTotalPages(pageData.totalPages); 
            setCurrentPage(pageData.number); 
            
        } catch (err) {
            setError('요청 목록을 불러오는 데 실패했습니다.');
            console.error("Failed to fetch admin requests:", err);
        } finally {
            setLoading(false);
        }
    };
    
    // 💡 페이지 변경 핸들러
    const handlePageChange = (pageIndex) => {
        if (pageIndex >= 0 && pageIndex < totalPages) {
            setCurrentPage(pageIndex);
        }
    };

    // 💡 모달 열기 핸들러 (행 클릭 시 호출)
    const handleOpenModal = (request) => {
        setSelectedRequest(request);
        setIsModalOpen(true);
    };

    // 💡 요청 상태 업데이트 핸들러 (목록에서 또는 모달에서 호출 가능)
    const handleStatusUpdate = async (requestId, newStatus) => {
        if (newStatus !== 'APPROVED' && newStatus !== 'REJECTED') {
            alert('유효하지 않은 상태 값입니다.');
            return;
        }
        
        if (!window.confirm(`${requestId}번 요청을 ${newStatus === 'APPROVED' ? '승인' : '반려'} 상태로 변경하시겠습니까?`)) {
            return;
        }

        try {
            await liveRequestService.updateRequestStatus(requestId, newStatus);
            
            // 상태 업데이트 후 로컬 상태 업데이트
            setRequests(prev => prev.map(req => 
                req.requestId === requestId ? { ...req, requestStatus: newStatus } : req
            ));
            
            // 모달이 열려 있다면, 모달 상태도 업데이트 (즉시 닫기 위해 null로 설정)
            if (isModalOpen) {
                setIsModalOpen(false);
                setSelectedRequest(null);
            }

            alert('상태가 성공적으로 업데이트되었습니다. 페이지를 새로고침합니다.');
            fetchRequests(); // 상태 변경 후 목록을 새로고침하여 최신 데이터 반영
            
        } catch (err) {
            alert('상태 업데이트에 실패했습니다.');
            console.error("Status update failed:", err);
        }
    };

    // 💡 권한 검사 및 데이터 로딩 로직
    useEffect(() => {
        if (isLoading) {
            // 로딩 중일 때는 아무것도 하지 않고 대기
            return; 
        }

        if (currentUser?.role !== 'ADMIN') {
            alert('접근 권한이 없습니다.');
            navigate('/');
            return;
        }
        
        fetchRequests();

    }, [currentUser, navigate, currentPage, isLoading]); 


    if (isLoading) return <div className="text-center mt-10 text-indigo-600">인증 정보를 확인 중입니다...</div>;
    if (loading) return <div className="text-center mt-10">요청 목록을 불러오는 중...</div>;
    if (error) return <div className="text-center mt-10 text-red-600">{error}</div>;

    return (
        <div className="container mx-auto p-6">
            <h1 className="text-3xl font-bold mb-8 text-indigo-700">관리자 등록 요청 현황 ({requests.length}건)</h1>

            <div className="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">제목</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">요청자</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">요청일</th>
                            {/* 💡 '관리' 열 제거 */}
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {requests.map((request) => (
                            // 💡 <tr>에 onClick 핸들러 추가
                            <tr 
                                key={request.requestId} 
                                className="hover:bg-gray-50 cursor-pointer"
                                onClick={() => handleOpenModal(request)}
                            >
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{request.requestId}</td>
                                <td 
                                    className="px-6 py-4 whitespace-nowrap text-sm text-indigo-600 font-medium"
                                    // 💡 이전의 onClick 핸들러 제거 (행 전체 클릭으로 대체)
                                >
                                    {request.title}
                                </td>
                                {/* 💡 요청자 이름 필드 (DTO: requester) */}
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{request.requester}</td> 
                                <td className="px-6 py-4 whitespace-nowrap text-sm">
                                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                        request.requestStatus === 'APPROVED' ? 'bg-green-100 text-green-800' :
                                        request.requestStatus === 'REJECTED' ? 'bg-red-100 text-red-800' :
                                        'bg-yellow-100 text-yellow-800'
                                    }`}>
                                        {statusMap[request.requestStatus] || request.requestStatus}
                                    </span>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(request.requestCreatedAt).toLocaleDateString()}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {requests.length === 0 && !loading && (
                <p className="text-center text-gray-500 mt-8">현재 대기 중인 공연 요청이 없습니다.</p>
            )}

            {/* 페이지네이션 컨트롤 */}
            {totalPages > 1 && (
                <div className="flex justify-center mt-6 space-x-2">
                    <button
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={currentPage === 0}
                        className="px-3 py-1 text-sm font-medium border rounded-md text-gray-700 hover:bg-gray-100 disabled:opacity-50"
                    >이전</button>
                    
                    {[...Array(totalPages)].map((_, index) => (
                        <button
                            key={index}
                            onClick={() => handlePageChange(index)}
                            className={`px-3 py-1 text-sm font-medium rounded-md ${
                                currentPage === index ? 'bg-indigo-600 text-white' : 'text-gray-700 hover:bg-gray-100 border border-gray-300'
                            }`}
                        >{index + 1}</button>
                    ))}

                    <button
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={currentPage === totalPages - 1}
                        className="px-3 py-1 text-sm font-medium border rounded-md text-gray-700 hover:bg-gray-100 disabled:opacity-50"
                    >다음</button>
                </div>
            )}

            {/* 💡 상세 모달 렌더링 */}
            {isModalOpen && selectedRequest && (
                <AdminRequestDetailModal
                    request={selectedRequest}
                    statusMap={statusMap}
                    onClose={() => setIsModalOpen(false)}
                    onStatusUpdate={handleStatusUpdate}
                />
            )}
        </div>
    );
};

export default AdminRequestListPage;