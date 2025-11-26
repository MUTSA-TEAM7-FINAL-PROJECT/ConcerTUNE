import React, { useState } from 'react';
import commentService from '../../services/commentService';

const CommentWrite = ({ postId, onCommentCreated, isLoggedIn }) => { 
    const [content, setContent] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!isLoggedIn) {
            alert("댓글 작성은 로그인한 사용자만 가능합니다.");
            return;
        }

        if (!content.trim()) return;

        setIsSubmitting(true);
        try {
            await commentService.createComment(postId, content);
            setContent(''); 
            onCommentCreated();
        } catch (error) {
            alert(error.message);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="mb-8 p-4 border rounded-lg bg-gray-50">
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows="3"
                placeholder={isLoggedIn ? "댓글을 입력해 주세요." : "로그인 후 댓글을 작성할 수 있습니다."}
                className="w-full p-2 border rounded-md focus:ring-indigo-500 focus:border-indigo-500 resize-none"
                disabled={isSubmitting || !isLoggedIn} 
            />
            <div className="flex justify-end mt-2">
                <button
                    type="submit"
                    className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                    disabled={isSubmitting || !content.trim() || !isLoggedIn} 
                >
                    {isSubmitting ? '등록 중...' : '등록'}
                </button>
            </div>
        </form>
    );
};
export default CommentWrite;