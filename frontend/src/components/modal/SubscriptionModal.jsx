import React, { useState, useEffect } from "react";
import { FaTimes } from "react-icons/fa";
import subscriptionService from "../../services/subscriptionService";

// 환경변수에서 클라이언트 키 가져오기 (Vite 환경)
const CLIENT_KEY = import.meta.env.VITE_TOSS_CLIENT_KEY;

const SubscriptionModal = ({ isOpen, onClose, artistId, amount, userId }) => {
    const [loading, setLoading] = useState(false);
    // 백엔드에서 받아온 customerKey만 저장
    const [customerKey, setCustomerKey] = useState(null);
    const [error, setError] = useState(null);

    // 1. 모달 열림 -> 백엔드에서 CustomerKey 발급받기
    useEffect(() => {
        if (!isOpen) {
            setCustomerKey(null);
            setError(null);
            return;
        }

        const fetchCustomerKey = async () => {
            setLoading(true);
            try {
                // 백엔드: /init 호출 -> { "customerKey": "CUST-..." } 반환 가정
                const response = await subscriptionService.initSubscription(artistId);
                
                // 백엔드 응답 구조에 따라 아래 줄을 조정하세요.
                // 만약 { customerKey: "..." } 객체로 온다면: response.customerKey
                // 만약 문자열 그대로 온다면: response
                const key = response.customerKey || response; 

                console.log("Customer Key 로드 성공:", key);
                setCustomerKey(key);
            } catch (err) {
                console.error("키 로드 실패:", err);
                setError("결제 정보를 불러오는데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchCustomerKey();
    }, [isOpen, artistId, userId]);

    const handleSubscribe = async () => {
        if (!CLIENT_KEY) {
            alert("환경변수(VITE_TOSS_CLIENT_KEY)가 설정되지 않았습니다.");
            return;
        }

        if (!customerKey) {
            alert("결제 초기화 정보(CustomerKey)가 없습니다. 다시 시도해주세요.");
            return;
        }

        if (!window.TossPayments) {
            alert("토스 결제 SDK가 로드되지 않았습니다. 새로고침 해주세요.");
            return;
        }

        setLoading(true);

        try {
            // --- V2 SDK 로직 시작 ---

            // 1. SDK 초기화 (환경변수의 Client Key 사용)
            const tossPayments = window.TossPayments(CLIENT_KEY);

            // 2. 결제 객체 생성 (백엔드에서 받은 Customer Key 연결)
            const payment = tossPayments.payment({
                customerKey: customerKey
            });


            // 3. 빌링 인증 요청
            await payment.requestBillingAuth({
                method: "CARD",
                
 
                successUrl: `${window.location.origin}/payment/success?orderId=${customerKey}&amount=${amount}`,
                failUrl: `${window.location.origin}/payment/fail`,
                
                customerEmail: "user@example.com", 
                customerName: "후원자",             // (선택) 실제 유저 이름
            });

        } catch (err) {
            console.error("빌링 인증 요청 실패:", err);
            
            if (err.code === "USER_CANCEL") {
                alert("카드 등록이 취소되었습니다.");
            } else {
                alert(`카드 등록 중 오류가 발생했습니다: ${err.message}`);
            }
            setLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
            <div className="bg-white p-6 rounded-lg w-96 relative shadow-2xl">
                <button onClick={onClose} className="absolute top-3 right-3 text-gray-500 hover:text-gray-700">
                    <FaTimes />
                </button>
                
                <h2 className="text-xl font-bold mb-4">정기 후원 구독</h2>
                <p className="mb-4 text-gray-600">
                    월 <span className="font-bold text-indigo-600">{amount.toLocaleString()}원</span>을 후원합니다.
                </p>

                {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
                
                <div className="mb-6 bg-gray-50 p-3 rounded text-sm text-gray-600">
                    <p>✨ 매월 자동으로 결제됩니다.</p>
                    <p>✨ 아래 버튼을 누르면 <strong>토스 카드 등록창</strong>이 열립니다.</p>
                </div>

                <button
                    onClick={handleSubscribe}
                    // 로딩 중이거나 customerKey가 아직 없으면 비활성화
                    disabled={loading || !customerKey}
                    className={`w-full py-3 rounded text-white font-bold transition-colors ${
                        loading || !customerKey
                        ? "bg-gray-400 cursor-not-allowed" 
                        : "bg-indigo-600 hover:bg-indigo-700"
                    }`}
                >
                    {loading ? "처리 중..." : "카드 등록 및 구독 시작"}
                </button>
            </div>
        </div>
    );
};

export default SubscriptionModal;