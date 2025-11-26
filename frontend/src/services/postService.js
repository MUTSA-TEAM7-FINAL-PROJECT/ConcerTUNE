import api from "./api"; // axios 인스턴스 (token 포함 가능)를 가정

const postService = {

    /**
     * 게시글 작성 (일반) (POST /api/posts/category/{category})
     * ⭐ Controller 경로 변경 반영
     */
    createPost: async (category, postData) => {
      try {
        const response = await api.post(`/api/posts/category/${category}`, postData);
        return response.data; // PostResponse 반환
      } catch (err) {
        console.error("게시글 등록 실패:", err.response || err);
        throw new Error(err.response?.data?.message || "게시글 등록에 실패했습니다.");
      }
    },

    /**
     * 게시글 작성 (Live ID 포함) (POST /api/posts/live/category/{category})
     * ⭐ Controller 경로 변경 반영 및 메서드 추가
     */
    createPostWithLiveId: async (category, postData) => {
        try {
            const response = await api.post(`/api/posts/live/category/${category}`, postData);
            return response.data; // PostResponse 반환
        } catch (err) {
            console.error("라이브 관련 게시글 등록 실패:", err.response || err);
            throw new Error(err.response?.data?.message || "라이브 관련 게시글 등록에 실패했습니다.");
        }
    },

    /**
     * 카테고리별 게시글 목록 조회 (GET /api/posts/category/{category})
     * ⭐ Controller 경로 변경 반영
     */
    getPostsByCategory: async (category, page = 0, size = 10) => {
      try { 
      // Controller가 Sort=createdAt,desc를 기본으로 하므로, sort는 params에서 제거
      const response = await api.get(`/api/posts/category/${category}`, {
        params: { page, size }
      });
      
      return response.data; // Page<PostResponse> 반환
      } catch (err) {
      console.error(`[${category}] 게시글 목록 조회 실패:`, err.response || err);
      throw new Error("게시글 목록을 불러오는 데 실패했습니다.");
      }
    },

    // Controller에 정의되지 않은 메서드이므로 그대로 유지
    getPostsByConcertAndCategory: async (concertId, category, page, size, sort) => {
        const url = `/api/posts/live/${concertId}/category/${category}`; 
        const response = await api.get(url, {
            params: {page,size,sort,},
        });
        return response.data;
    },

    getPostDetail: async (postId) => {
      try {
        const response = await api.get(`/api/posts/${postId}`);
        return response.data; // PostResponse 반환
      } catch (err) {
        console.error("게시글 상세 조회 실패:", err.response || err);
        throw new Error(err.response?.data?.message || "게시글을 찾을 수 없습니다.");
      }
    },


    updatePost: async (category, postId, updateData) => {
      try {
        const response = await api.put(`/api/posts/category/${category}/postId/${postId}`, updateData);
        return response.data; // PostResponse 반환
      } catch (err) {
        console.error("게시글 수정 실패:", err.response || err);
        throw new Error(err.response?.data?.message || "게시글 수정에 실패했습니다.");
      }
    },

    
    /**
     * 게시글 삭제 (DELETE /api/posts/category/{category}/postId/{postId})
     * ⭐ Controller 경로 변경 반영
     */
    deletePost: async (category, postId) => {
      try {
        await api.delete(`/api/posts/category/${category}/postId/${postId}`);
      } catch (err) {
        console.error("게시글 삭제 실패:", err.response || err);
        throw new Error(err.response?.data?.message || "게시글 삭제에 실패했습니다.");
      }
    },

    likePost: async (postId) => {
      try {
        const response = await api.post(`/api/posts/${postId}/like`);
        return response.data; // PostResponse 반환
      } catch (err) {
        console.error("좋아요 요청 실패:", err.response || err);
        throw new Error("좋아요 처리 중 오류가 발생했습니다.");
      }
    },


    dislikePost: async (postId) => {
      try {
        const response = await api.post(`/api/posts/${postId}/dislike`);
        return response.data; // PostResponse 반환
      } catch (err) {
        console.error("좋아요 취소 요청 실패:", err.response || err);
        throw new Error("좋아요 취소 처리 중 오류가 발생했습니다.");
      }
    },
    
    isPostLiked: async (postId) => {
        try {
          const response = await api.get(`/api/posts/${postId}/like/status`);
          return response.data;
        } catch (err) {
          console.error("게시글 좋아요 상태 조회 실패:", err.response || err);
          throw new Error("게시글 좋아요 상태를 불러오는 데 실패했습니다.");
        }
      },
  
    getTop3WeeklyPosts: async () => {
      try {
        const response = await api.get(`/api/posts/top-weekly`); 
        return response.data; // List<PostResponse> 반환
      } catch (err) {
        console.error("주간 인기 게시글 3개 조회 실패:", err);
        throw new Error(err.response?.data?.message || "주간 인기 게시글을 불러오는 데 실패했습니다.");
      }
    },
    
    /**
     * 게시글 검색 (GET /api/posts?keyword={keyword})
     * ⭐ Controller 엔드포인트에 맞춰 메서드 추가
     */
    searchPosts: async (keyword, page = 0, size = 10) => {
        try {
            const response = await api.get(`/api/posts`, {
                params: { keyword, page, size }
            });
            return response.data; // Page<PostResponse> 반환
        } catch (err) {
            console.error("게시글 검색 실패:", err.response || err);
            throw new Error("게시글 검색에 실패했습니다.");
        }
    },

    /**
     * 자유게시판 베스트 게시글 조회 (GET /api/posts/free/best)
     * ⭐ Controller 엔드포인트에 맞춰 메서드 추가
     */
    getFreeBestPosts: async (size = 10) => {
        try {
            const response = await api.get(`/api/posts/free/best`, { params: { size } });
            return response.data; // List<PostResponse> 반환
        } catch (err) {
            console.error("자유게시판 베스트 게시글 조회 실패:", err);
            throw new Error("자유게시판 베스트 게시글을 불러오는 데 실패했습니다.");
        }
    },

    /**
     * 리뷰 게시판 베스트 게시글 조회 (GET /api/posts/review/best)
     * ⭐ Controller 엔드포인트에 맞춰 메서드 추가
     */
    getReviewBestPosts: async (size = 10) => {
        try {
            const response = await api.get(`/api/posts/review/best`, { params: { size } });
            return response.data; // List<PostResponse> 반환
        } catch (err) {
            console.error("리뷰 게시판 베스트 게시글 조회 실패:", err);
            throw new Error("리뷰 게시판 베스트 게시글을 불러오는 데 실패했습니다.");
        }
    },

    // Controller에 정의되지 않은 메서드이므로 그대로 유지
    getBookmarkedConcertPosts: async () => { 
      try {
          const response = await api.get(`/api/posts/bookmarked`);
          return response.data;
      } catch (err) {
          console.error("북마크된 커뮤니티 글 조회 실패:", err);
          throw new Error(err.response?.data?.message || "북마크된 커뮤니티 글을 불러오는 데 실패했습니다.");
      }
    },
};

export default postService;