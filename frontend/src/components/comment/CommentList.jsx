import React from 'react';
import CommentItem from './CommentItem'; 

const CommentList = ({ comments, onCommentUpdated, isLoggedIn }) => {
    return (
        <div className="space-y-4">
            {comments.length === 0 ? (
                <p className="text-center text-gray-500 py-4">아직 등록된 댓글이 없습니다.</p>
            ) : (
                comments.map((comment) => (
                    <CommentItem 
                        key={comment.id} 
                        comment={comment} 
                        onUpdate={onCommentUpdated} 
                        isLoggedIn={isLoggedIn}
                    />
                ))
            )}
        </div>
    );
};
export default CommentList;