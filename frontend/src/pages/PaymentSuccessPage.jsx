import React, { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import subscriptionService from "../services/subscriptionService"; 

const PaymentSuccessPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    let isMounted = true; 

    const confirmPayment = async () => {
      const orderId = searchParams.get("orderId");
      const authKey = searchParams.get("authKey"); 
      const amountStr = searchParams.get("amount"); 

      if (!authKey || !orderId || !amountStr) {
        if (isMounted) {
            alert("필수 결제 정보가 누락되었습니다.");
            navigate("/payment/fail", { replace: true });
        }
        return;
      }
      
      const amount = parseInt(amountStr);

      try {

        const response = await subscriptionService.confirmSubscription({ 
            orderId, 
            amount, 
            authKey 
        });

        if (isMounted) {
            alert("구독이 성공적으로 시작되었습니다!");
            navigate(`/subscriptions/${response.artistId}`, { replace: true }); 
        }
      } catch (error) {
        console.error("결제 승인 실패:", error);
        if (isMounted) {
            const backendError = error.response?.data?.message || "처리 중 알 수 없는 오류 발생";
            navigate(`/payment/fail?message=${encodeURIComponent(backendError)}&code=BACKEND_ERROR`, { replace: true });
        }
      } finally {
        if (isMounted) {
            setIsProcessing(false);
        }
      }
    };
    
    confirmPayment();
    
    return () => {
        isMounted = false;
    };
  }, [searchParams, navigate]);

  return (
    <div className="flex justify-center items-center h-screen bg-gray-50">
      <div className="text-center p-8 bg-white shadow-lg rounded-lg">
          {isProcessing ? (
              <>
                  <svg className="animate-spin h-10 w-10 text-indigo-600 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p className="text-xl font-semibold text-gray-700">구독 승인을 처리하고 있습니다...</p>
                  <p className="text-sm text-gray-500 mt-2">잠시만 기다려 주세요.</p>
              </>
          ) : (
              <p className="text-lg text-gray-600">처리 완료. 잠시 후 페이지가 전환됩니다.</p>
          )}
      </div>
    </div>
  );
};

export default PaymentSuccessPage;