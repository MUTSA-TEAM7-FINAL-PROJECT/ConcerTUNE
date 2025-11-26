// src/components/home/UserSpecificSections.jsx (새 컴포넌트)

import React from "react";
import MyUpcomingLives from "./MyUpcomingLives";
import { useAuth } from '../../context/AuthContext'; 
import PersonalizedScheduleSection from './PersonalizedScheduleSection';

const UserSpecificSections = () => {
    const { user } = useAuth();

    if (!user?.id) {
        return null; 
    }
    
    return (
        <div className="space-y-12">
            <PersonalizedScheduleSection userId={user.id} /> 
            <MyUpcomingLives /> 
        </div>
    );
};

export default UserSpecificSections;