import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import artistManagerRequestService from "../services/artistManagerRequestService";
import artistService from "../services/artistService";

const ArtistManagerRequestPage = () => {
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();

  const [artists, setArtists] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [showResults, setShowResults] = useState(false);

  const [requestData, setRequestData] = useState({
    selectedArtist: null,
    reason: "",
    isOfficial: false,
    proofDocumentUrl: "", // [추가] 증빙 서류 링크
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchLoading, setSearchLoading] = useState(false);

  const fetchArtists = useCallback(async (name) => {
    if (!name) {
      setArtists([]);
      return;
    }
    setSearchLoading(true);
    setError(null);
    try {
      const response = await artistService.getArtists(name, {
        page: 0,
        size: 5,
      });
      setArtists(response.content || []);
    } catch (error) {
      console.error("Failed to fetch artists:", error);
      setArtists([]);
    } finally {
      setSearchLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!isLoggedIn) {
      alert("로그인 후 아티스트 관리자 요청을 할 수 있습니다.");
      navigate("/login");
    }
  }, [isLoggedIn, navigate]);

  useEffect(() => {
    if (requestData.selectedArtist) {
      setShowResults(false);
      return;
    }

    if (searchTerm.trim().length > 0) {
      fetchArtists(searchTerm.trim());
      setShowResults(true);
    } else {
      setArtists([]);
      setShowResults(false);
    }
  }, [searchTerm, fetchArtists, requestData.selectedArtist]);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    const inputValue = type === "checkbox" ? checked : value;
    setRequestData((prev) => ({ ...prev, [name]: inputValue }));
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleArtistSelection = (artist) => {
    if (artist) {
      setRequestData((prev) => ({
        ...prev,
        selectedArtist: {
          artistId: artist.artistId,
          artistName: artist.artistName,
        },
      }));
      setSearchTerm("");
      setShowResults(false);
      setArtists([]);
    }
  };

  const removeArtist = () => {
    setRequestData((prev) => ({
      ...prev,
      selectedArtist: null,
    }));
    setShowResults(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!requestData.selectedArtist) {
      alert("요청 대상 아티스트를 반드시 선택해야 합니다.");
      return;
    }
    if (!requestData.reason.trim()) {
      alert("관리자 권한 요청 이유를 입력해야 합니다.");
      return;
    }
    // [추가] 증빙 서류 URL 유효성 검사
    if (
      !requestData.proofDocumentUrl.trim() ||
      !isValidUrl(requestData.proofDocumentUrl)
    ) {
      alert("올바른 형식의 증빙 서류(포트폴리오) URL을 입력해주세요.");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const requestDto = {
        artistId: requestData.selectedArtist.artistId,
        artistName: requestData.selectedArtist.artistName,
        description: requestData.reason,
        reason: requestData.reason,
        isOfficial: requestData.isOfficial,
        proofDocumentUrl: requestData.proofDocumentUrl, // [추가] DTO 포함
      };

      await artistManagerRequestService.submitManagerRequest(requestDto);

      alert(
        `'${requestData.selectedArtist.artistName}' 아티스트의 관리자 요청이 성공적으로 등록되었습니다. 운영진의 검토를 기다려주세요.`
      );
      navigate("/artist-manager/requests-list");
    } catch (err) {
      console.error("Manager request submission failed:", err);
      const message =
        err.response?.data?.message ||
        "관리자 요청 등록에 실패했습니다. 입력 정보를 확인해주세요.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  // URL 검증 헬퍼 함수
  const isValidUrl = (string) => {
    try {
      new URL(string);
      return true;
    } catch (_) {
      return false;
    }
  };

  if (!isLoggedIn) return null;

  return (
    <div className="w-full max-w-xl mx-auto p-6 md:p-10 bg-white shadow-2xl rounded-xl my-10">
      <h1 className="text-3xl font-bold mb-8 text-purple-700">
        ✍️ 아티스트 관리 권한 요청
      </h1>

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* 1. 요청 대상 아티스트 선택 (기존 코드 유지) */}
        <div className="p-4 border border-purple-200 rounded-lg bg-purple-50 relative">
          <label className="block text-xl font-bold text-gray-800 mb-3">
            요청 대상 아티스트 <span className="text-red-500">*</span>
          </label>

          {requestData.selectedArtist ? (
            <div className="flex items-center gap-2">
              <span className="flex items-center gap-1 rounded-full px-4 py-2 text-md font-semibold bg-purple-100 text-purple-700">
                {requestData.selectedArtist.artistName} (선택 완료)
              </span>
              <button
                type="button"
                onClick={removeArtist}
                className="text-red-500 hover:text-red-700 text-2xl font-bold p-1"
              >
                &times;
              </button>
            </div>
          ) : (
            <div className="relative">
              <input
                type="text"
                placeholder="관리 권한을 요청할 아티스트 이름 검색..."
                value={searchTerm}
                onChange={handleSearchChange}
                className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-purple-500 focus:ring-purple-500"
              />
              {/* 검색 결과 목록 (기존 코드 유지) */}
              {showResults && searchTerm.length > 0 && (
                <div className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-40 overflow-y-auto">
                  {searchLoading ? (
                    <div className="p-3 text-sm text-gray-500 text-center">
                      검색 중...
                    </div>
                  ) : artists.length > 0 ? (
                    artists.map((artist) => (
                      <div
                        key={artist.artistId}
                        className="p-3 cursor-pointer hover:bg-purple-50 border-b last:border-b-0 text-gray-800"
                        onClick={() => handleArtistSelection(artist)}
                      >
                        {artist.artistName}{" "}
                        <span className="text-xs text-purple-500 ml-2">
                          [선택]
                        </span>
                      </div>
                    ))
                  ) : (
                    <div className="p-3 text-sm text-gray-500">
                      일치하는 아티스트가 없습니다.
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
        </div>

        <hr className="border-gray-200" />

        {/* 2. 요청 상세 정보 */}
        <div className="space-y-6">
          <div className="space-y-3 p-4 border border-gray-200 rounded-lg bg-gray-50">
            {/* 관리자 권한 요청 이유 (기존 코드 유지) */}
            <div>
              <label
                htmlFor="reason"
                className="block text-lg font-bold text-gray-700 mb-2"
              >
                관리자 권한 요청 이유{" "}
                <span className="text-red-500 text-xl ml-0.5">*</span>
              </label>
              <textarea
                id="reason"
                name="reason"
                rows="3"
                value={requestData.reason}
                onChange={handleInputChange}
                required
                placeholder="요청 상세 이유를 입력해주세요."
                className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-purple-500 focus:ring-purple-500 resize-none"
              />
            </div>

            {/* [추가] 증빙 서류 URL 입력 필드 */}
            <div>
              <label
                htmlFor="proofDocumentUrl"
                className="block text-lg font-bold text-gray-700 mb-2"
              >
                증빙 자료 링크 (Google Drive 등){" "}
                <span className="text-red-500 text-xl ml-0.5">*</span>
              </label>
              <input
                type="url"
                id="proofDocumentUrl"
                name="proofDocumentUrl"
                value={requestData.proofDocumentUrl}
                onChange={handleInputChange}
                required
                placeholder="https://example.com/portfolio"
                className="w-full border border-gray-300 rounded-lg p-3 text-gray-900 focus:border-purple-500 focus:ring-purple-500"
              />
              <p className="text-sm text-gray-500 mt-1">
                * 포트폴리오, 재직 증명서, 버스킹 허가증 등을 확인할 수 있는
                URL을 입력해주세요.
              </p>
            </div>

            <div className="flex items-center mt-4">
              <input
                type="checkbox"
                id="isOfficial"
                name="isOfficial"
                checked={requestData.isOfficial}
                onChange={handleInputChange}
                className="h-5 w-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500 cursor-pointer"
              />
              <label
                htmlFor="isOfficial"
                className="ml-3 block text-base font-bold text-gray-800 cursor-pointer"
              >
                저는 해당 아티스트의{" "}
                <span className="text-purple-700">
                  공식 관계자(소속사, 매니저 등)입니다.
                </span>
              </label>
            </div>

            <div className="p-3 bg-white border-l-4 border-red-500 text-sm text-gray-600 shadow-sm mt-2">
              <p className="font-semibold text-red-600">🚨 엄격한 검증 안내</p>
              <p className="mt-1">
                제출하신 증빙 자료는 운영진이 직접 확인하며, 허위 사실 기재 시
                계정이 영구 정지될 수 있습니다.
              </p>
            </div>
          </div>
        </div>

        <button
          type="submit"
          disabled={
            loading ||
            !requestData.selectedArtist ||
            !requestData.reason.trim() ||
            !requestData.proofDocumentUrl.trim()
          }
          className="w-full bg-purple-700 text-white font-bold py-4 rounded-xl hover:bg-purple-800 transition-colors text-xl disabled:bg-gray-400"
        >
          {loading ? "요청 등록 중..." : "✅ 관리자 권한 요청 제출하기"}
        </button>

        {error && (
          <p className="text-red-500 text-center mt-4 font-medium">{error}</p>
        )}
      </form>
    </div>
  );
};

export default ArtistManagerRequestPage;
