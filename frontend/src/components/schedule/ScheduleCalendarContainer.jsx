import React, { useState, useEffect, useCallback } from 'react';
import ScheduleCalendar from './ScheduleCalendar'; // 경로는 가정
import concertService from '../../services/concertService'; 

function ScheduleCalendarContainer() {
    const [schedules, setSchedules] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    
    // 캘린더의 현재 보기(월)를 추적하는 상태
    const [currentViewDate, setCurrentViewDate] = useState(new Date()); 

    /**
     * 특정 월의 라이브 일정을 API에서 가져오는 함수
     * @param {Date} date - 조회할 월을 포함하는 Date 객체
     */
    const fetchSchedulesForMonth = useCallback(async (date) => {
        const year = date.getFullYear();
        // JavaScript getMonth()는 0부터 시작하므로 +1
        const month = date.getMonth() + 1; 

        // 💡 디버깅: API 요청이 나가는지 확인
        console.log(`[API Call] Fetching schedules for: ${year}년 ${month}월`);

        // 데이터 가져오기 전 상태 초기화
        setIsLoading(true);
        setError(null);
        setSchedules([]);

        try {
            // API 호출: liveSummaries가 LiveSummaryDto 리스트를 반환한다고 가정
            const liveSummaries = await concertService.getLivesByYearAndMonth(year, month);
            
            // 데이터 변환 및 평탄화
            const transformedSchedules = liveSummaries.flatMap(summary => {
                // summary 객체가 venue 정보를 가지고 있다고 가정하고, 없다면 빈 문자열 처리
                const venueName = summary.venueName || '장소 정보 없음'; 
                
                return summary.schedules.map(scheduleDto => ({
                    liveId: summary.id,
                    liveTitle: summary.title,
                    isBookmarked: summary.isBookmarked,
                    liveDate: scheduleDto.liveDate, // YYYY-MM-DD 형식 문자열을 기대
                    liveTime: scheduleDto.liveStartTime || '시간 미정', // liveStartTime이 null일 수 있으므로 처리
                    venue: venueName // summary에서 가져온 장소 정보 사용
                }));
            });
            
            setSchedules(transformedSchedules);
            console.log(`[API Result] Successfully fetched ${transformedSchedules.length} schedules.`);

        } catch (err) {
            console.error("월별 스케줄 조회 실패:", err);
            // 사용자에게 표시할 에러 메시지 설정
            setError("월별 라이브 일정을 불러오는 데 실패했습니다. 잠시 후 다시 시도해 주세요.");
        } finally {
            setIsLoading(false);
        }
    }, []); // 의존성 배열 비어있음: 초기 마운트 시 한 번만 생성되므로 안전함

    // currentViewDate가 변경될 때마다 데이터를 다시 불러옴
    useEffect(() => {
        fetchSchedulesForMonth(currentViewDate);
    }, [currentViewDate, fetchSchedulesForMonth]);

    /**
     * 캘린더에서 표시되는 월이 변경될 때 호출되는 핸들러
     * prop 이름을 onActiveStartDateChange (react-calendar 표준)에 맞게 변경했습니다.
     * @param {object} detail - react-calendar의 activeStartDate 속성을 포함
     */
    const handleActiveStartDateChange = useCallback(({ activeStartDate }) => {
        // 💡 디버깅: 캘린더 이동 이벤트가 호출되는지 확인
        console.log(`[View Change] Active start date updated to: ${activeStartDate}`);
        // activeStartDate는 해당 월의 첫 번째 날짜입니다.
        setCurrentViewDate(activeStartDate);
    }, []);

    return (
        <div className="schedule-page-wrapper p-8 bg-white shadow-lg rounded-xl max-w-7xl mx-auto my-10">
            <h2 className="text-4xl font-extrabold text-gray-900 mb-8 border-b pb-4">
                전체 라이브 스케줄
            </h2>
            
            {/* 로딩 및 에러 상태 표시 */}
            {error && (
                <div className="text-red-600 bg-red-100 p-3 rounded-md mb-4 font-medium">
                    오류: {error}
                </div>
            )}
            {isLoading && (
                <div className="text-blue-600 p-3 mb-4 font-medium flex items-center justify-center">
                    <svg className="animate-spin h-5 w-5 mr-3 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                    일정을 불러오는 중...
                </div>
            )}

            {/* ScheduleCalendar 컴포넌트 렌더링 */}
            <ScheduleCalendar
                schedules={schedules}
                isLoading={isLoading} // 로딩 상태를 캘린더 내부에서도 활용할 수 있도록 전달
                error={error}
                // prop 이름을 onActiveStartDateChange로 변경
                onActiveStartDateChange={handleActiveStartDateChange} 
                currentViewDate={currentViewDate}    
            />
        </div>
    );
}

export default ScheduleCalendarContainer;