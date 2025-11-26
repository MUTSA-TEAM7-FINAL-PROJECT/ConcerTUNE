import React, { useState, useEffect } from 'react';
import commentService from '../../services/commentService';
import { useAuth } from '../../context/AuthContext'; // useAuth 훅 임포트

/**
 * 댓글 목록의 개별 항목을 렌더링하는 컴포넌트
 * currentUserId prop 대신 useAuth 훅을 사용하여 현재 로그인 사용자 정보를 가져옵니다.
 */
const CommentItem = ({ comment, onUpdate, isLoggedIn }) => { 
    
    // 💡 useAuth 훅을 사용하여 현재 로그인된 사용자 정보 (currentUser)를 가져옵니다.
    const { user: currentUser } = useAuth();

    // 💡 comment 객체의 likeCount를 관리
    const [currentComment, setCurrentComment] = useState(comment);
    
    // 💡 클라이언트 측에서 좋아요 여부를 추적하는 상태. API 호출로 초기화됩니다.
    const [isClientLiked, setIsClientLiked] = useState(false); 

    const [isEditing, setIsEditing] = useState(false);
    const [editedContent, setEditedContent] = useState(comment.content);
    const [loading, setLoading] = useState(false); // 액션(수정/삭제/토글) 로딩 상태
    const [initialLoading, setInitialLoading] = useState(true); // 초기 좋아요 상태 조회 로딩

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString('ko-KR', {
            year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit',
        });
    };

    // 💡 서버 DTO에 isWriter 필드가 없으므로, writerId와 현재 로그인 유저 ID를 비교하여 판단합니다.
    // currentUser?.id를 사용하여 사용자가 로그인 상태이고 ID가 있을 때만 비교합니다.
    const isCurrentUserWriter = isLoggedIn && currentUser?.id === currentComment.writerId;

    // 🚀 컴포넌트 마운트 시, 좋아요 상태 API로 확인
    useEffect(() => {
        // 로그인 상태가 아니면 좋아요 상태를 확인할 필요가 없음
        if (!isLoggedIn) {
            setIsClientLiked(false);
            setInitialLoading(false);
            return;
        }

        const fetchLikeStatus = async () => {
            setInitialLoading(true);
            try {
                // commentService.isCommentLiked는 boolean을 반환한다고 가정
                const isLiked = await commentService.isCommentLiked(currentComment.id);
                setIsClientLiked(isLiked);
            } catch (error) {
                console.error(`댓글 ID ${currentComment.id} 좋아요 상태 초기 조회 실패:`, error);
                // 실패 시, 기본값인 false 유지
            } finally {
                setInitialLoading(false);
            }
        };

        fetchLikeStatus();
    // 의존성 배열: comment.id와 isLoggedIn이 변경될 때만 실행
    }, [currentComment.id, isLoggedIn]); 


    // 댓글 수정 처리 (로직은 기존과 동일)
    const handleEditSubmit = async () => {
        if (!editedContent.trim() || editedContent.trim() === currentComment.content.trim()) {
            setIsEditing(false);
            return;
        }

        setLoading(true);
        try {
            const updatedComment = await commentService.updateComment(currentComment.id, editedContent);
            setCurrentComment(updatedComment);
            setIsEditing(false);
            onUpdate();
        } catch (error) {
            alert(`댓글 수정 실패: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    // 댓글 삭제 처리 (로직은 기존과 동일)
    const handleDelete = async () => {
        // [IMPORTANT] alert 대신 custom modal을 사용해야 하지만, 현재 코드 흐름 유지를 위해 window.confirm 유지
        if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
        
        setLoading(true);
        try {
            await commentService.deleteComment(currentComment.id);
            onUpdate(); 
        } catch (error) {
            alert(`댓글 삭제 실패: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };
    
    /**
     * 댓글 좋아요 토글 처리 (like / dislike 명시적 호출)
     */
    const handleLikeToggle = async () => {
        if (!isLoggedIn) {
            alert("좋아요는 로그인한 사용자만 가능합니다.");
            return;
        }

        const wasLiked = isClientLiked;
        
        // 1. Optimistic Update: 좋아요 여부(isClientLiked)를 먼저 토글
        setIsClientLiked(prev => !prev);
        setLoading(true);
        
        try {
            let response;
            
            if (wasLiked) {
                // 현재 좋아요 상태였으므로 -> 좋아요 취소 (dislike) 요청
                response = await commentService.dislikeComment(currentComment.id); 
            } else {
                // 현재 좋아요 상태가 아니었으므로 -> 좋아요 (like) 요청
                response = await commentService.likeComment(currentComment.id); 
            }
            
            // 2. Final Update: 서버 응답에서 갱신된 likeCount만 받아서 최종 갱신
            const updatedLikeCount = response.likeCount; 
            
            setCurrentComment(prev => ({
                ...prev,
                likeCount: updatedLikeCount,
            }));
            
        } catch (error) {
            // 3. Rollback: 요청 실패 시, 클라이언트 상태를 원래대로 되돌립니다.
            setIsClientLiked(wasLiked);
            alert(error.message || (wasLiked ? "좋아요 취소 처리 중 오류가 발생했습니다." : "좋아요 처리 중 오류가 발생했습니다."));
        } finally {
            setLoading(false);
        }
    };
    
    // 좋아요/수정/삭제 등 모든 액션과 초기 로딩 시 버튼 비활성화
    const isAnyLoading = loading || initialLoading;

    return (
        <div className="border-t p-4 first:border-t-0 hover:bg-gray-50 transition duration-100">
            <div className="flex justify-between items-start mb-2">
                <div className="flex items-center space-x-3">
                    {/* DTO에 writerName 필드를 사용 */}
                    <span className="font-bold text-gray-800">{currentComment.writerName}</span>
                    <span className="text-xs text-gray-500">{formatDate(currentComment.createdAt)}</span>
                </div>
                
                {/* 액션 버튼: 현재 사용자가 작성자인지 확인 (서버 DTO에 isWriter 없음) */}
                {isCurrentUserWriter && (
                    <div className="text-sm space-x-2">
                        {isEditing ? (
                            <div className="space-x-2">
                                <button onClick={handleEditSubmit} disabled={isAnyLoading} className="text-indigo-600 hover:text-indigo-800 font-semibold disabled:opacity-50">{loading ? '저장 중...' : '저장'}</button>
                                <button onClick={() => { setIsEditing(false); setEditedContent(currentComment.content); }} className="text-gray-500 hover:text-gray-700 disabled:opacity-50" disabled={isAnyLoading}>취소</button>
                            </div>
                        ) : (
                            <div className="space-x-2">
                                <button onClick={() => setIsEditing(true)} className="text-gray-500 hover:text-gray-700" disabled={isAnyLoading}>수정</button>
                                <button onClick={handleDelete} className="text-red-500 hover:text-red-700" disabled={isAnyLoading}>삭제</button>
                            </div>
                        )}
                    </div>
                )}
            </div>

            {/* 댓글 내용 */}
            {isEditing ? (
                <textarea
                    value={editedContent}
                    onChange={(e) => setEditedContent(e.target.value)}
                    rows="3"
                    className="w-full p-2 border rounded-md focus:ring-indigo-500 focus:border-indigo-500 resize-none text-sm"
                    disabled={isAnyLoading}
                />
            ) : (
                <p className="text-gray-700 whitespace-pre-wrap text-sm mb-2">
                    {currentComment.content}
                </p>
            )}

            {/* 좋아요 버튼: 로그인 상태일 때만 활성화 */}
            <div className="flex justify-end">
                <button
                    onClick={isLoggedIn ? handleLikeToggle : () => alert("좋아요는 로그인한 사용자만 가능합니다.")}
                    disabled={isAnyLoading || !isLoggedIn}
                    className={`flex items-center space-x-1 text-sm p-1 rounded transition disabled:opacity-50 ${
                        isClientLiked && isLoggedIn ? 'text-red-500 font-bold' : 'text-gray-500 hover:text-red-500'
                    }`}
                    title={isLoggedIn ? (isClientLiked ? "좋아요 취소" : "좋아요") : "로그인 필요"}
                >
                    {/* 하트 아이콘 */}
                    {initialLoading ? (
                        <div className="w-4 h-4 flex items-center justify-center">
                            {/* 초기 로딩 시 스피너 표시 */}
                            <svg className="animate-spin h-3 w-3 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                        </div>
                    ) : (
                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" /></svg>
                    )}
                    <span>{currentComment.likeCount}</span>
                </button>
            </div>
        </div>
    );
};

export default CommentItem;