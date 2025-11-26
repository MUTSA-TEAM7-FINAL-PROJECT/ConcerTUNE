import React, { useState, useEffect, useRef } from 'react';
import { Map, MapMarker, useKakaoLoader } from 'react-kakao-maps-sdk';

const KAKAO_MAP_API_KEY = import.meta.env.VITE_KAKAO_MAP_KEY;

const VenueSelector = ({ onSelectLocation }) => {
    // 💡 useKakaoLoader 로딩 상태 추출 (배열 형태로)
    const [isLoading] = useKakaoLoader({
        appkey: KAKAO_MAP_API_KEY,
        libraries: ["services"],
    });

    const isLoaded = !isLoading;
    
    const initialCenter = { lat: 37.5665, lng: 126.9780 };
    
    const [searchKeyword, setSearchKeyword] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [selectedPlace, setSelectedPlace] = useState(null); 
    const [mapCenter, setMapCenter] = useState(initialCenter);
    const mapRef = useRef(null); 

    const searchPlaces = () => {
        if (!isLoaded || !searchKeyword.trim()) return;

        const ps = new window.kakao.maps.services.Places();
        
        ps.keywordSearch(searchKeyword, (data, status) => {
            if (status === window.kakao.maps.services.Status.OK) {
                setSearchResults(data);
                
                const firstPlace = data[0];
                const newCenter = { 
                    lat: parseFloat(firstPlace.y), 
                    lng: parseFloat(firstPlace.x) 
                };
                
                setMapCenter(newCenter);
                setSelectedPlace(newCenter);
                
            } else if (status === window.kakao.maps.services.Status.ZERO_RESULT) {
                setSearchResults([]);
                alert('검색 결과가 없습니다.');
            } else {
                alert('장소 검색 중 오류가 발생했습니다.');
            }
        });
    };

    const handleResultClick = (place) => {
        const newLocation = {
            venueName: place.place_name,
            address: place.road_address_name || place.address_name,
            latitude: parseFloat(place.y),
            longitude: parseFloat(place.x),
        };

        setMapCenter({ lat: newLocation.latitude, lng: newLocation.longitude });
        setSelectedPlace({ lat: newLocation.latitude, lng: newLocation.longitude });
        
        onSelectLocation(newLocation);
        
        setSearchResults([]);
        setSearchKeyword(place.place_name);
    };
    
    const handleMarkerDragEnd = (marker) => {
        if (!isLoaded) return;

        const latlng = marker.getPosition();
        const geocoder = new window.kakao.maps.services.Geocoder(); 

        geocoder.coord2Address(latlng.getLng(), latlng.getLat(), (result, status) => {
            let displayAddress = '좌표를 드래그한 위치 (주소 변환 중...)';
            
            if (status === window.kakao.maps.services.Status.OK && result[0]) {
                displayAddress = result[0].road_address ? 
                                 result[0].road_address.address_name : 
                                 result[0].address.address_name;
            }

            const newLocation = {
                venueName: `[지도 드래그] ${displayAddress}`,
                address: displayAddress, 
                latitude: latlng.getLat(),
                longitude: latlng.getLng(),
            };
            
            setSelectedPlace({ lat: newLocation.latitude, lng: newLocation.longitude });
            onSelectLocation(newLocation);
        });
    }

    // 💡 추가된 부분: 지도 클릭 시 마커 이동 및 주소 역변환 처리
    const handleMapClick = (map, mouseEvent) => {
        if (!isLoaded) return;
        console.log("지도 클릭 이벤트 발생!"); // 💡 이 코드가 콘솔에 찍히는지 확인
        const latlng = mouseEvent.latLng;
        
        setSelectedPlace({ lat: latlng.getLat(), lng: latlng.getLng() });
        setMapCenter({ lat: latlng.getLat(), lng: latlng.getLng() });
        
        const geocoder = new window.kakao.maps.services.Geocoder();

        geocoder.coord2Address(latlng.getLng(), latlng.getLat(), (result, status) => {
            let displayAddress = '좌표를 클릭한 위치 (주소 변환 중...)';
            
            if (status === window.kakao.maps.services.Status.OK && result[0]) {
                displayAddress = result[0].road_address ? 
                                 result[0].road_address.address_name : 
                                 result[0].address.address_name;
            }

            const newLocation = {
                venueName: `${displayAddress}`,
                address: displayAddress, 
                latitude: latlng.getLat(),
                longitude: latlng.getLng(),
            };
            
            onSelectLocation(newLocation);
        });
    };
    
    // 로딩 중일 때 로딩 UI를 표시합니다.
    if (isLoading) {
        return <div className="p-4 text-center text-indigo-600">지도 API 로딩 중...</div>;
    }

    return (
        <div className="space-y-4">
            <div className="flex gap-2">
                <input
                    type="text"
                    placeholder="공연장 또는 장소 이름 검색"
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    onKeyUp={(e) => { if (e.key === 'Enter') searchPlaces(); }}
                    className="flex-1 border border-gray-300 rounded-lg p-3 focus:border-indigo-500 focus:ring-indigo-500"
                />
                <button
                    type="button"
                    onClick={searchPlaces}
                    className="bg-indigo-600 text-white px-5 rounded-lg hover:bg-indigo-700 transition-colors whitespace-nowrap"
                >
                    검색
                </button>
            </div>

            {searchResults.length > 0 && (
                <div className="border border-gray-300 rounded-lg max-h-40 overflow-y-auto bg-white shadow-lg">
                    {searchResults.map((place, index) => (
                        <div
                            key={index}
                            className="p-3 cursor-pointer hover:bg-indigo-50 border-b last:border-b-0"
                            onClick={() => handleResultClick(place)}
                        >
                            <div className="font-semibold text-gray-800">{place.place_name}</div>
                            <div className="text-sm text-gray-500">{place.road_address_name || place.address_name}</div>
                        </div>
                    ))}
                </div>
            )}

            <div className="border border-gray-300 rounded-lg overflow-hidden">
                <Map
                    center={mapCenter}
                    style={{ width: '100%', height: '400px' }}
                    level={4} 
                    ref={mapRef}
                    onClick={handleMapClick} // 💡 지도 클릭 이벤트 연결
                >
                    {selectedPlace && (
                        <MapMarker
                            position={selectedPlace}
                            draggable={true} 
                            onDragEnd={handleMarkerDragEnd} 
                        />
                    )}
                </Map>
            </div>
        </div>
    );
};

export default VenueSelector;