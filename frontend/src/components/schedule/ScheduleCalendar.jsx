import React, { useState, useEffect } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import { Link } from 'react-router-dom';

// prop 이름을 currentViewDate로 변경하여 Container와 일치시킵니다.
function ScheduleCalendar({
    schedules,
    isLoading,
    error,
    onActiveStartDateChange,
    currentViewDate // activeStartDate -> currentViewDate로 변경
}) {
    const validSchedules = Array.isArray(schedules) ? schedules : [];

    // selectedDate의 초기값은 상위에서 내려온 현재 월의 시작 날짜를 사용합니다.
    const [selectedDate, setSelectedDate] = useState(currentViewDate || new Date());

    // 💡 핵심 수정: currentViewDate(즉, 월)가 바뀔 때마다 selectedDate를 해당 월의 시작일로 초기화합니다.
    useEffect(() => {
        // currentViewDate가 바뀌면 selectedDate도 새 월의 시작일로 설정하여 UI를 동기화합니다.
        setSelectedDate(currentViewDate);
    }, [currentViewDate]);


    // 날짜 포맷팅 유틸리티 함수들
    const getCustomDateString = (date) => {
        // ⭐ 오류 방지 로직 추가: date가 유효한 Date 객체인지 확인합니다.
        if (!date || isNaN(date.getTime())) {
            return "날짜 정보 없음";
        }

        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const days = ['일', '월', '화', '수', '목', '금', '토'];
        const dayOfWeek = days[date.getDay()];
        return `${year}년 ${month}월 ${day}일 (${dayOfWeek})의 일정`;
    };

    // 달력 셀의 날짜를 숫자만 표시하도록 포맷하는 함수 (formatDay prop 사용)
    const formatDay = (locale, date) => date.getDate().toString();

    const tileContent = ({ date, view }) => {
        if (view === 'month') {
            const daySchedules = validSchedules.filter(schedule => {
                // liveDate 문자열을 Date 객체로 변환할 때, 시간대 문제를 방지하기 위해 UTC를 사용합니다.
                // 'YYYY-MM-DD' 형식에 'T00:00:00'을 추가하여 UTC 자정으로 해석되도록 합니다.
                const scheduleDate = new Date(schedule.liveDate + 'T00:00:00');
                return scheduleDate.toDateString() === date.toDateString();
            });

            if (daySchedules.length > 0) {
                return (
                    <div className="schedule-indicator" style={{ textAlign: 'center', marginTop: '3px' }}>
                        {/* 스케줄 표시점 (북마크 여부에 따라 색상 다름) */}
                        <div style={{
                            height: '6px',
                            width: '6px',
                            // Tailwind 색상으로 변경: 북마크됨: #4c51bf (indigo-700 계열), 아님: #dc2626 (red-600 계열)
                            backgroundColor: daySchedules[0].isBookmarked ? '#4c51bf' : '#dc2626',
                            borderRadius: '50%',
                            margin: '3px auto'
                        }} />
                        {/* 스케줄 제목 간략 표시 */}
                        <small className="block text-xs text-gray-700 mt-1 truncate max-w-full">
                            {daySchedules[0].liveTitle}
                            {daySchedules.length > 1 ? ` 외 ${daySchedules.length - 1}개` : ''}
                        </small>
                    </div>
                );
            }
        }
    };

    // 선택된 날짜의 상세 스케줄 목록
    const selectedDayDetails = validSchedules.filter(schedule =>
        // selectedDate에 방어 로직 추가 및 liveDate 처리
        selectedDate && new Date(schedule.liveDate + 'T00:00:00').toDateString() === selectedDate.toDateString()
    ).sort((a, b) => (a.liveTime || '').localeCompare(b.liveTime || ''));

    if (isLoading)
        return (
            <div className="schedule-container p-4">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* 로딩 중 UI */}
                    <div className="calendar-section bg-gray-50 p-4 rounded-lg shadow-xl text-center py-10 text-gray-500 col-span-2">
                        스케줄 로드 중...
                    </div>
                </div>
            </div>
        );
    if (error)
        return <div className="text-center py-10 text-red-500">❌ {error}</div>;

    return (
        <div className="schedule-container p-4">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* 달력 섹션 */}
                <div className="calendar-section bg-gray-50 p-4 rounded-lg shadow-xl">
                    <Calendar
                        onChange={setSelectedDate}
                        onActiveStartDateChange={onActiveStartDateChange}
                        // ⭐ activeStartDate prop을 currentViewDate로 전달 (현재 월 표시)
                        activeStartDate={currentViewDate}
                        // ⭐ value prop도 selectedDate로 유지 (선택한 날짜 표시)
                        value={selectedDate}
                        tileContent={tileContent}
                        className="w-full border-0 shadow-lg p-2"
                        formatDay={formatDay}
                    />
                </div>

                {/* 상세 목록 섹션 */}
                <div className="selected-day-details p-4 bg-white rounded-lg shadow-xl border border-gray-100">
                    <h4 className="text-2xl font-bold text-gray-800 mb-4 border-b pb-2">
                        {getCustomDateString(selectedDate)}
                    </h4>

                    <div className="space-y-3 max-h-96 overflow-y-auto pr-2">
                        {selectedDayDetails.length > 0 ? (
                            <ul className="divide-y divide-gray-200">
                                {selectedDayDetails.map((schedule, index) => (
                                    <li
                                        key={schedule.liveId + (schedule.liveTime || index)}
                                        className="py-3 px-2 hover:bg-indigo-50 transition duration-150 rounded-md"
                                    >
                                        <Link to={`/concerts/${schedule.liveId}`} className="block">
                                            <div className="flex justify-between items-start">
                                                <p className="flex items-center text-lg font-semibold text-gray-800">
                                                    <span className="text-indigo-600 mr-2">{schedule.liveTime}</span>
                                                    {schedule.liveTitle}
                                                </p>
                                                {schedule.isBookmarked && (
                                                    <span className="text-yellow-500 text-xl" title="북마크됨">★</span>
                                                )}
                                            </div>
                                            <p className="text-sm text-gray-500 mt-1">장소: {schedule.venue}</p>
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p className="text-gray-500 text-center py-5 bg-gray-50 rounded-md border">
                                선택하신 날짜에는 예정된 스케줄이 없습니다.
                            </p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ScheduleCalendar;