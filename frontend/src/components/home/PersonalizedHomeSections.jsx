import React from 'react';
import UpcomingConcerts from './UpcomingConcerts'; 
import FollowFeedSection from './FollowFeedSection'; 
import BookmarkedCommunityFeed from './BookmarkedCommunityFeed'; 

const PersonalizedHomeSections = () => {
  console.log("PersonalizedHomeSections 렌더링");
  return (
    <div className="space-y-12 mt-12">
      
      <UpcomingConcerts /> 
      
     <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <FollowFeedSection /> 
        
        <BookmarkedCommunityFeed /> 
      </div>
    </div>
  );
};

export default PersonalizedHomeSections;