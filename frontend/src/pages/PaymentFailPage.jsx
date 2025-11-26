import React, { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
// lucide-react 대신 react-icons에서 아이콘을 가져옵니다. (Font Awesome 사용)
import { FaExclamationTriangle, FaClock } from 'react-icons/fa';

const PaymentFailPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    
    // URL에서 오류 정보 추출
    const errorMessage = searchParams.get("message") || "결제 처리 중 알 수 없는 오류가 발생했습니다.";
    const errorCode = searchParams.get("code") || "UNKNOWN_ERROR";
    
    // 카운트다운 상태 (5초 후 이동)
    const [countdown, setCountdown] = useState(5);

    useEffect(() => {
        // 1. 카운트다운 타이머 설정
        const timer = setInterval(() => {
            setCountdown((prevCount) => prevCount - 1);
        }, 1000);

        // 2. 카운트다운이 0이 되면 홈으로 이동
        if (countdown === 0) {
            clearInterval(timer);
            navigate("/", { replace: true });
        }

        // 3. 클린업 함수: 컴포넌트 언마운트 시 타이머 정리
        return () => {
            clearInterval(timer);
        };
    }, [countdown, navigate]); // countdown과 navigate가 변경될 때마다 useEffect 재실행

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50 p-4">
            <div className="w-full max-w-md text-center p-8 bg-white shadow-xl rounded-xl border-t-8 border-red-500 transform transition duration-500 hover:shadow-2xl">
                
                {/* 실패 아이콘: FaExclamationTriangle로 교체 */}
                <FaExclamationTriangle className="w-16 h-16 text-red-500 mx-auto mb-6" />

                <h1 className="text-3xl font-extrabold text-gray-800 mb-2">
                    결제에 실패했습니다.
                </h1>
                
                {/* 오류 상세 정보 */}
                <div className="bg-red-50 p-4 rounded-lg border border-red-200 mt-5 mb-6 text-left">
                    <p className="font-semibold text-sm text-red-700 flex items-center mb-1">
                        {/* 시계 아이콘: FaClock으로 교체 */}
                        <FaClock className="w-4 h-4 mr-2" />
                        오류 원인 (Code: {errorCode})
                    </p>
                    <p className="text-gray-700 text-base font-medium break-words ml-6">
                        {errorMessage}
                    </p>
                </div>
                
                {/* 카운트다운 메시지 */}
                <p className="text-lg text-gray-600 font-semibold mt-6 mb-4">
                    {countdown}초 후 홈 화면으로 자동 이동합니다.
                </p>

                {/* 홈으로 이동 버튼 (수동 이동) */}
                <button
                    onClick={() => navigate("/", { replace: true })}
                    className="w-full py-3 px-4 bg-gray-700 text-white font-bold rounded-full hover:bg-gray-800 transition duration-150 shadow-md hover:shadow-lg focus:outline-none focus:ring-4 focus:ring-gray-300"
                >
                    지금 바로 홈으로 이동
                </button>
            </div>
        </div>
    );
};

export default PaymentFailPage;