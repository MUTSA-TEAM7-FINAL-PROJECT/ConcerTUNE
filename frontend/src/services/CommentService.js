import api from "./api";

const commentService = {

    getCommentsByPost: async (postId) => {
        try {
            const response = await api.get(`/api/posts/${postId}/comments`);
            return response.data; // List<CommentResponse>
        } catch (err) {
            console.error("댓글 목록 조회 실패:", err);
            throw new Error("댓글 목록을 불러오는 데 실패했습니다.");
        }
    },

    createComment: async (postId, content, parentCommentId = null) => {
        try {
            // Controller의 DTO(CommentCreateRequest)에 맞춰 content와 parentCommentId 전송
            const response = await api.post(`/api/posts/${postId}/comments`, { content, parentCommentId });
            return response.data; // CommentResponse
        } catch (err) {
            console.error("댓글 등록 실패:", err);
            throw new Error(err.response?.data?.message || "댓글 등록에 실패했습니다.");
        }
    },

    updateComment: async (commentId, content) => {
        try {
            // Controller의 DTO(CommentUpdateRequest)에 맞춰 content 전송
            const response = await api.put(`/api/comments/${commentId}`, { content });
            return response.data; // CommentResponse
        } catch (err) {
            console.error("댓글 수정 실패:", err);
            throw new Error(err.response?.data?.message || "댓글 수정에 실패했습니다.");
        }
    },

    deleteComment: async (commentId) => {
        try {
            await api.delete(`/api/comments/${commentId}`);
        } catch (err) {
            console.error("댓글 삭제 실패:", err);
            throw new Error("댓글 삭제에 실패했습니다.");
        }
    },

    likeComment: async (commentId) => {
        try {
            const response = await api.post(`/api/comments/${commentId}/like`);
            return response.data; // CommentResponse (갱신된 likeCount 포함)
        } catch (err) {
            console.error("댓글 좋아요 요청 실패:", err.response || err);
            throw new Error("좋아요 처리 중 오류가 발생했습니다.");
        }
    },

 
    dislikeComment: async (commentId) => {
        try {
            const response = await api.post(`/api/comments/${commentId}/dislike`);
            return response.data; // CommentResponse (갱신된 likeCount 포함)
        } catch (err) {
            console.error("댓글 좋아요 취소 요청 실패:", err.response || err);
            throw new Error("좋아요 취소 처리 중 오류가 발생했습니다.");
        }
    },
    
    isCommentLiked: async (commentId) => {
        try {
          const response = await api.get(`/api/comments/${commentId}/like/status`);
          return response.data;
        } catch (err) {
          console.error("댓글 좋아요 상태 조회 실패:", err.response || err);
          throw new Error("댓글 좋아요 상태를 불러오는 데 실패했습니다.");
        }
      },
  
};

export default commentService;