import React, { useState, useEffect } from "react";
// import communityService from '../../services/communityService';
import { Link } from "react-router-dom";

const PostList = ({ category }) => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);

  useEffect(() => {
    const fetchPosts = async () => {
        try {
            setLoading(true);
            setError(null);
            
        }
    }
  })
};
return (
  <div className="w-full">
    <div className="flex justify-end mb-4">임시 PostList</div>
  </div>
);

export default PostList;
