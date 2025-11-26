import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import postService from '../../services/postService';
import commentService from '../../services/commentService'; 
import CommentList from '../comment/CommentList'; 
import CommentWrite from '../comment/CommentWrite'; 
import { useAuth } from '../../context/AuthContext';

const MessageBox = ({ type, message, onConfirm, onCancel, onClose }) => {
    if (!message) return null;

    const isConfirm = type === 'confirm';

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 p-4">
            <div className="bg-white rounded-lg shadow-2xl p-6 w-full max-w-sm transform transition-all">
                <p className="text-gray-700 mb-6 whitespace-pre-wrap">{message}</p>
                <div className={`flex ${isConfirm ? 'justify-end space-x-3' : 'justify-center'}`}>
                    {isConfirm && (
                        <button 
                            onClick={onCancel} 
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition"
                        >
                            취소
                        </button>
                    )}
                    <button 
                        onClick={isConfirm ? onConfirm : onClose} 
                        className={`px-4 py-2 text-sm font-medium rounded-md text-white transition 
                            ${isConfirm ? 'bg-indigo-600 hover:bg-indigo-700' : 'bg-red-500 hover:bg-red-600'}`}
                    >
                        {isConfirm ? '확인' : '닫기'}
                    </button>
                </div>
            </div>
        </div>
    );
};

const getCategoryName = (param) => {
    switch (param) {
        case 'FREE': return '자유게시판';
        case 'REVIEW': return '공연 후기';
        case 'ACCOMPANY': return '동행 구하기';
        default: return '커뮤니티';
    }
};

const PostDetail = () => {
    const { postId, category } = useParams();
    const navigate = useNavigate();
    
    // 💡 로그인 상태 가져오기
    const { isLoggedIn } = useAuth(); 
    
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [comments, setComments] = useState([]);
    
    // Message box state (for non-confirm alerts/errors)
    const [messageBox, setMessageBox] = useState({ type: null, message: null, onConfirm: null, onCancel: null });
    
    // 게시글 상세 정보 및 댓글 목록 불러오기
    const fetchPostAndComments = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const postDetail = await postService.getPostDetail(postId);
            let isLiked = false;
            // 로그인 상태일 때만 좋아요 여부 확인 (서버 호출 비용 절감)
            console.log("isLoggedIn in PostDetail:", isLoggedIn);
            if (isLoggedIn) { 
                isLiked = await postService.isPostLiked(postId);
            }

            setPost({
                ...postDetail,
                isLikedByUser: isLiked 
            });            
            const commentsList = await commentService.getCommentsByPost(postId);
            setComments(commentsList);
            
        } catch (err) {
            console.error("데이터 로드 실패:", err);
            setError(err.message || "게시글 정보를 불러오는 데 실패했습니다.");
        } finally {
            setLoading(false);
        }
    }, [postId, isLoggedIn]); // isLoggedIn을 Dependency List에 추가

    useEffect(() => {
        fetchPostAndComments();
    }, [fetchPostAndComments]);


    const handleLikeToggle = async () => {
        if (!post) return;
        
        // 🚨 로그인 체크 - **alert() 대신 MessageBox 사용**
        if (!isLoggedIn) {
            setMessageBox({
                type: 'alert',
                message: "게시글 좋아요는 로그인한 사용자만 가능합니다.",
                onClose: () => setMessageBox({ type: null, message: null })
            });
            return;
        }

        const isCurrentlyLiked = post.isLikedByUser;
        
        // Optimistic UI Update: 먼저 UI를 변경합니다.
        setPost(prevPost => ({
            ...prevPost,
            isLikedByUser: !isCurrentlyLiked, 
            likeCount: isCurrentlyLiked ? prevPost.likeCount - 1 : prevPost.likeCount + 1,
        }));

        try {
            let response;
            if (isCurrentlyLiked) {
                response = await postService.dislikePost(post.id); 
            } else {
                response = await postService.likePost(post.id); 
            }
            
            // ✅ 서버 응답에서 갱신된 likeCount를 가져와 상태를 최종 갱신합니다.
            const updatedLikeCount = response.likeCount;

            setPost(prevPost => ({
                ...prevPost,
                likeCount: updatedLikeCount, // 서버가 알려준 정확한 값으로 갱신
            }));
            
        } catch (err) {
            // 실패 시 UI 롤백
            setPost(prevPost => ({
                ...prevPost,
                isLikedByUser: isCurrentlyLiked, 
                likeCount: isCurrentlyLiked ? prevPost.likeCount + 1 : prevPost.likeCount - 1, // Count 롤백
            }));
            
            // 오류 메시지 표시 - **alert() 대신 MessageBox 사용**
            setMessageBox({
                type: 'alert',
                message: err.message || (isCurrentlyLiked ? "좋아요 취소 처리 중 오류가 발생했습니다." : "좋아요 처리 중 오류가 발생했습니다."),
                onClose: () => setMessageBox({ type: null, message: null })
            });
            console.error("좋아요/좋아요 취소 처리 실패:", err.response || err);
        }
    };

    const handleDelete = () => {
        // 🚨 window.confirm() 대신 MessageBox를 사용하여 확인 절차를 밟습니다.
        setMessageBox({
            type: 'confirm',
            message: "정말로 게시글을 삭제하시겠습니까?",
            onConfirm: async () => {
                setMessageBox({ type: null, message: null }); // 메시지 박스 닫기
                try {
                    await postService.deletePost(postId);
                    // 삭제 성공 메시지 표시
                    setMessageBox({
                        type: 'alert',
                        message: "게시글이 삭제되었습니다.",
                        onClose: () => {
                            setMessageBox({ type: null, message: null });
                            navigate(`/community/${category}`, { replace: true });
                        }
                    });
                } catch (err) {
                    // 삭제 실패 메시지 표시
                    setMessageBox({
                        type: 'alert',
                        message: err.message || "게시글 삭제에 실패했습니다.",
                        onClose: () => setMessageBox({ type: null, message: null })
                    });
                }
            },
            onCancel: () => setMessageBox({ type: null, message: null })
        });
    };
    
    const handleCommentCreated = () => {
        commentService.getCommentsByPost(postId)
            .then(setComments)
            .catch(err => console.error("댓글 갱신 실패:", err));
        fetchPostAndComments(); // 댓글 작성 시 전체 게시글 정보(댓글 수)도 갱신
    };

    if (loading) {
        return <div className="text-center py-20 text-indigo-600">상세 정보를 불러오는 중입니다...</div>;
    }

    if (error || !post) {
        return <div className="text-center py-20 text-red-500">{error || "게시글을 찾을 수 없습니다."}</div>;
    }

    return (
        <div className="min-h-screen bg-gray-100 p-4 sm:p-8">
            <div className="max-w-4xl mx-auto p-6 bg-white shadow-xl rounded-xl">
                
                {/* 게시글 헤더 및 본문 */}
                <div className="border-b pb-4 mb-6">
                    <h1 className="text-3xl font-extrabold text-gray-900 mb-2">{post.title}</h1>
                    <p className="text-sm text-gray-500 flex flex-wrap items-center">
                        <span className="font-semibold text-indigo-600 mr-4">[{getCategoryName(category)}]</span>
                        작성자: <strong className="mx-1 text-gray-700">{post.writerName}</strong>
                        <span className="mx-2 text-xs">|</span>
                        조회수: {post.viewCount}
                        <span className="mx-2 text-xs">|</span>
                        작성일: {new Date(post.createdAt).toLocaleDateString()}
                    </p>
                </div>
                <div className="prose max-w-none mb-8">
                    {post.imageUrls && post.imageUrls.map((url, index) => (
                        <img 
                            key={index} 
                            src={url} 
                            alt={`첨부 이미지 ${index + 1}`} 
                            className="my-4 rounded-lg shadow-md w-full h-auto object-cover" 
                            style={{ maxHeight: '300px' }} // 이미지 높이 제한 추가
                        />
                    ))}
                    <p className="text-gray-800 whitespace-pre-wrap">{post.content}</p>
                </div>

                {/* 좋아요 및 액션 버튼 */}
                <div className="flex justify-between items-center border-t pt-4">
                    <button
                        onClick={handleLikeToggle}
                        // 💡 비로그인 시 버튼 비활성화 및 스타일 적용
                        disabled={!isLoggedIn} 
                        className={`flex items-center space-x-2 p-3 rounded-full transition duration-200 shadow-md transform active:scale-95
                            ${post.isLikedByUser && isLoggedIn ? 'bg-red-500 text-white hover:bg-red-600' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}
                            ${!isLoggedIn ? 'opacity-50 cursor-not-allowed' : ''}
                        `}
                        title={isLoggedIn ? (post.isLikedByUser ? "좋아요 취소" : "좋아요") : "로그인이 필요합니다"}
                    >
                        {/* 좋아요 아이콘 (lucide-react heart-fill mock) */}
                        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                        <span className="font-bold">{post.likeCount}</span>
                    </button>
                    
                    {/* 작성자만 보이는 수정/삭제 버튼 */}
                    {post.isWriter && ( 
                        <div className="space-x-2">
                            <Link 
                                to={`/community/edit/${category}/${postId}`}
                                className="px-4 py-2 text-sm text-indigo-600 border border-indigo-600 rounded-md hover:bg-indigo-50 transition"
                            >
                                수정
                            </Link>
                            <button
                                onClick={handleDelete}
                                className="px-4 py-2 text-sm text-red-600 border border-red-600 rounded-md hover:bg-red-50 transition"
                            >
                                삭제
                            </button>
                        </div>
                    )}
                </div>

                {/* 댓글 섹션 */}
                <div className="mt-10">
                    <h3 className="text-2xl font-bold text-gray-800 border-b pb-2 mb-4">
                        댓글 <span className="text-indigo-600">({post.commentCount})</span>
                    </h3>
                    
                    {/* 댓글 작성 폼: isLoggedIn 상태에 따라 입력 제한 */}
                    <CommentWrite 
                        postId={post.id} 
                        onCommentCreated={handleCommentCreated}
                        isLoggedIn={isLoggedIn} // 💡 로그인 상태 전달
                    />

                    {/* 댓글 목록 */}
                    <CommentList 
                        comments={comments} 
                        onCommentUpdated={handleCommentCreated} 
                        isLoggedIn={isLoggedIn} // 💡 로그인 상태 전달
                    />
                </div>
            </div>
            
            {/* Custom MessageBox 렌더링 */}
            <MessageBox 
                type={messageBox.type}
                message={messageBox.message}
                onConfirm={messageBox.onConfirm}
                onCancel={messageBox.onCancel}
                onClose={messageBox.onClose}
            />
        </div>
    );
};


export default PostDetail;