import api from "./api"; 

const userService = {

    getFollowArtistFeeds: async () => { 
        try {
            const response = await api.get(`/api/users/feeds/follow`); 
            return response.data; 
        } catch (err) {
            console.error("팔로우 아티스트 피드 조회 실패:", err);
            throw new Error(err.response?.data?.message || "팔로우 피드를 불러오는 데 실패했습니다.");
        }
    },

};

export default userService;