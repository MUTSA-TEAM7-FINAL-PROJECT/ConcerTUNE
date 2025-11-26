// SubscriptionDetailPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { FaArrowLeft } from 'react-icons/fa';
import subscriptionService from '../services/subscriptionService';

const SubscriptionDetailPage = () => {
    const { artistId } = useParams();
    const [subscription, setSubscription] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchSubscriptionDetail = async () => {
            setIsLoading(true);
            try {
                const data = await subscriptionService.getSubscriptionDetail(artistId);
                console.log("구독 상세 정보:", data);
                setSubscription(data);
            } catch (err) {
                console.error(err);
                setSubscription(null);
            } finally {
                setIsLoading(false);
            }
        };
        fetchSubscriptionDetail();
    }, [artistId]);

    if (isLoading) return <p className="text-center mt-10">구독 정보를 불러오는 중...</p>;
    if (!subscription) return <p className="text-center mt-10 text-red-500">구독 정보를 불러오지 못했습니다.</p>;

    return (
        <div className="container mx-auto p-6">
            <button onClick={() => window.history.back()} className="mb-4 flex items-center text-indigo-600 hover:underline">
                <FaArrowLeft className="mr-2"/> 뒤로가기
            </button>

            {/* 구독 아티스트 정보 */}
            <div className="flex items-center bg-white p-6 rounded-xl shadow-md mb-6">
                <img src={subscription.artistImageUrl} alt={subscription.artistName} className="w-24 h-24 rounded-full object-cover mr-6"/>
                <div>
                    <h2 className="text-2xl font-bold">{subscription.artistName}</h2>
                    <p className="mt-1 text-gray-600">구독 상태: <span className="font-semibold">{subscription.status}</span></p>
                    <p className="mt-1 text-gray-600">월 구독 금액: <span className="font-semibold">{subscription.amount}원</span></p>
                    <p className="mt-1 text-gray-600">구독 시작일: {new Date(subscription.subscribedAt).toLocaleDateString()}</p>
                    <p className="mt-1 text-gray-600">다음 결제일:  
                        {subscription.nextPaymentDate 
                            ? new Date(subscription.nextPaymentDate).toLocaleDateString() 
                            : '-'}
                    </p>
                </div>
            </div>
            <div className="flex justify-end mt-4">
                <button
                    onClick={async () => {
                        if (!window.confirm("정말 구독을 취소하시겠습니까?")) return;

                        try {
                            await subscriptionService.cancelSubscription(artistId);
                            alert("구독이 취소되었습니다.");
                            window.history.back();
                        } catch (err) {
                            console.error(err);
                            alert("구독 취소 중 오류가 발생했습니다.");
                        }
                    }}
                    className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600"
                >
                    구독 취소
                </button>
            </div>

            {/* 결제 내역 */}
            <h3 className="text-xl font-bold mb-3">결제 내역</h3>
            {subscription.paymentHistory?.length > 0 ? (
                <table className="w-full text-left border rounded-lg shadow overflow-hidden">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="px-4 py-2">결제일</th>
                            <th className="px-4 py-2">금액</th>
                            <th className="px-4 py-2">결제 상태</th>
                            <th className="px-4 py-2">영수증</th>
                        </tr>
                    </thead>
                    <tbody>
                        {subscription.paymentHistory.map(history => (
                            <tr key={history.orderId} className="border-t">
                                <td className="px-4 py-2">{new Date(history.paymentDate).toLocaleDateString()}</td>
                                <td className="px-4 py-2">{history.amount}원</td>
                                <td className="px-4 py-2">{history.status}</td>
                                <td className="px-4 py-2">
                                    {history.tossTransaction?.receipt?.url ? (
                                        <a 
                                            href={history.tossTransaction.receipt.url} 
                                            target="_blank" 
                                            rel="noreferrer" 
                                            className="text-indigo-600 hover:underline"
                                        >
                                            영수증 보기
                                        </a>
                                    ) : '-'}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p className="text-gray-500 mt-3">결제 내역이 없습니다.</p>
            )}
        </div>
    );
};

export default SubscriptionDetailPage;
