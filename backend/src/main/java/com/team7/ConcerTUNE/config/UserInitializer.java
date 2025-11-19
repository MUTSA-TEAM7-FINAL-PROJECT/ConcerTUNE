package com.team7.ConcerTUNE.config;

import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.repository.*;
import com.team7.ConcerTUNE.temp.repository.ArtistGenreRepository;
import com.team7.ConcerTUNE.temp.repository.GenreRepository;
import com.team7.ConcerTUNE.temp.repository.LiveSchedulesRepository;
import com.team7.ConcerTUNE.temp.repository.SchedulesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Configuration
public class UserInitializer {
    private static final String DEFAULT_PASSWORD = "password12";
    private static final String DEFAULT_USERNAME = "Initial User";

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final LivesRepository livesRepository;
    private final LiveArtistRepository liveArtistRepository;
    private final ArtistManagerRepository artistManagerRepository;
    private final GenreRepository genreRepository;
    private final ArtistGenreRepository artistGenreRepository;

    // 💡 추가된 Repository
    private final SchedulesRepository schedulesRepository;
    private final LiveSchedulesRepository liveSchedulesRepository;

    // 사용할 포스터 이미지 URL 목록
    private static final List<String> POSTER_URLS = Arrays.asList(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsP2JWoHzrD-LEgtz89wfwJM_-RWfHEW45Tg&s",
            "https://file.newswire.co.kr/data/datafile2/thumb_640/2020/10/2948802425_20201030102144_7402605803.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8jjfaX2dNaQAZ4YvPBUdgkFewvbhidCAj8g&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRcxMsDE1ip_nbnq_opEhqQjTaYGY38ZjCHlA&s"
    );

    // 20명의 아티스트 데이터 정의 (장르 포함)
    private static final List<Map.Entry<String, List<String>>> ARTIST_GENRE_MAP = Arrays.asList(
            Map.entry("ConcertUNE 공식 아티스트", Arrays.asList("팝", "케이팝", "댄스")),
            Map.entry("인디 밴드: 에코", Arrays.asList("인디", "록", "포크")),
            Map.entry("클래식 피아니스트: 제인", Arrays.asList("클래식", "재즈", "앰비언트")),
            Map.entry("랩퍼: 다이나믹 K", Arrays.asList("힙합", "알앤비")),
            Map.entry("트로트 여왕: 송가요", Arrays.asList("트로트", "발라드")),
            Map.entry("메탈 밴드: 스틸레인", Arrays.asList("메탈", "록")),
            Map.entry("포크 싱어: 김나무", Arrays.asList("포크", "어쿠스틱")),
            Map.entry("일렉트로닉 DJ: 퓨처B", Arrays.asList("일렉트로닉", "하우스")),
            Map.entry("퓨전 국악단: 아리랑", Arrays.asList("퓨전", "월드 뮤직")),
            Map.entry("댄스 그룹: 스파크", Arrays.asList("댄스", "팝")),
            Map.entry("R&B 보컬: 리오", Arrays.asList("알앤비", "소울")),
            Map.entry("앰비언트 사운드: 이터널", Arrays.asList("앰비언트", "일렉트로닉")),
            Map.entry("테크노 프로듀서: 제로", Arrays.asList("테크노", "트랜스")),
            Map.entry("가스펠 코러스: 헤븐스", Arrays.asList("가스펠")),
            Map.entry("OST 마스터: 사운드맨", Arrays.asList("OST/사운드트랙")),
            Map.entry("오페라 바리톤: 강철", Arrays.asList("오페라", "클래식")),
            Map.entry("컨트리 듀오: 더 로드", Arrays.asList("컨트리", "포크")),
            Map.entry("블루스 기타: 찰리", Arrays.asList("블루스", "재즈")),
            Map.entry("레게 뮤지션: 자메이카맨", Arrays.asList("레게")),
            Map.entry("뉴에이지 밴드: 미스트", Arrays.asList("앰비언트", "클래식"))
    );


    @Bean
    public CommandLineRunner initDefaultUser(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("--- 초기 유저 설정 ---");
            String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
            Random random = new Random();

            // ------------------------------------
            // 1. 유저 생성 및 저장 (유지)
            // ------------------------------------
            User defaultUser1 = User.builder()
                    .email("user1@naver.com")
                    .password(encodedPassword)
                    .username(DEFAULT_USERNAME + "1")
                    .auth(AuthRole.USER)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();

            User defaultUser2 = User.builder()
                    .email("user2@naver.com")
                    .password(encodedPassword)
                    .username(DEFAULT_USERNAME + "2")
                    .auth(AuthRole.ARTIST)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();

            User defaultUser3 = User.builder()
                    .email("user3@naver.com")
                    .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .username(DEFAULT_USERNAME + "3")
                    .auth(AuthRole.ADMIN)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();

            userRepository.save(defaultUser1);
            userRepository.save(defaultUser2);
            userRepository.save(defaultUser3);

            User artistManagerUser = User.builder()
                    .email("manager@company.com")
                    .password(passwordEncoder.encode("supermanager"))
                    .username("SuperManager")
                    .auth(AuthRole.USER)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();
            userRepository.save(artistManagerUser);
            System.out.println("--- 1-1. 단일 매니저 유저 생성 완료 ---");


            // ------------------------------------
            // 2. 장르 생성 및 저장 (유지)
            // ------------------------------------
            System.out.println("--- 2. 장르 데이터 설정 ---");
            List<String> genreNames = Arrays.asList(
                    "팝", "록", "힙합", "알앤비", "재즈", "클래식",
                    "일렉트로닉", "포크", "컨트리", "블루스", "케이팝",
                    "인디", "발라드", "메탈", "레게", "앰비언트",
                    "하우스", "테크노", "트랜스", "가스펠", "OST/사운드트랙",
                    "오페라", "트로트", "댄스", "펑크", "어쿠스틱",
                    "소울", "디스코", "퓨전", "월드 뮤직"
            );

            Map<String, Genre> genreMap = new HashMap<>();
            genreNames.forEach(name -> {
                Genre genre = Genre.builder().genreName(name).build();
                Genre savedGenre = genreRepository.save(genre);
                genreMap.put(name, savedGenre);
            });
            System.out.println("장르 " + genreNames.size() + "개 저장 완료.");


            // ------------------------------------
            // 3. 아티스트 생성 및 저장 (유지)
            // ------------------------------------
            System.out.println("--- 3. 아티스트 데이터 설정 (20명) ---");
            List<Artist> artists = new ArrayList<>();
            List<ArtistManager> managerLinks = new ArrayList<>();

            for (int i = 0; i < ARTIST_GENRE_MAP.size(); i++) {
                Map.Entry<String, List<String>> entry = ARTIST_GENRE_MAP.get(i);
                String artistName = entry.getKey();

                Artist newArtist = Artist.builder()
                        .artistName(artistName)
                        .isDomestic(i % 3 != 2)
                        .snsUrl("https://sns.url/" + artistName.replace(" ", "").toLowerCase())
                        .artistImageUrl("https://image.url/artist" + (i + 1) + ".jpg")
                        .build();

                Artist savedArtist = artistRepository.save(newArtist);
                artists.add(savedArtist);

                ArtistManagerId managerId = ArtistManagerId.builder()
                        .userId(artistManagerUser.getId())
                        .artistId(savedArtist.getArtistId())
                        .build();
                ArtistManager managerLink = ArtistManager.builder()
                        .id(managerId)
                        .user(artistManagerUser)
                        .artist(savedArtist)
                        .assignedAt(LocalDateTime.now())
                        .isOfficial(true)
                        .build();
                managerLinks.add(managerLink);
            }
            artistManagerRepository.saveAll(managerLinks);
            System.out.println("아티스트-매니저 연결 " + managerLinks.size() + "개 저장 완료.");


            // 3-1. 아티스트에 장르 연결 (유지)
            System.out.println("--- 3-1. 아티스트-장르 연결 ---");
            List<ArtistGenre> artistGenres = new ArrayList<>();

            for (int i = 0; i < ARTIST_GENRE_MAP.size(); i++) {
                Artist artist = artists.get(i);
                List<String> genresToAssign = ARTIST_GENRE_MAP.get(i).getValue();

                for (String genreName : genresToAssign) {
                    Genre genre = genreMap.get(genreName);
                    if (genre != null) {
                        artistGenres.add(new ArtistGenre(artist, genre));
                    }
                }
            }
            artistGenreRepository.saveAll(artistGenres);
            System.out.println("아티스트-장르 연결 " + artistGenres.size() + "개 저장 완료.");


            // ------------------------------------
            // 4. 공연 (Lives) 10개 생성, 아티스트, 그리고 일정 연결 (수정 완료)
            // ------------------------------------
            System.out.println("--- 4. 공연 데이터 설정 (10개) ---");

            // 공통 좌석 가격 설정
            Map<String, Integer> seatPrices = new HashMap<>();
            seatPrices.put("VIP석", 120000);
            seatPrices.put("R석", 99000);
            seatPrices.put("S석", 77000);
            seatPrices.put("A석", 55000);

            List<String> liveTitles = Arrays.asList(
                    "[Official] ConcerTUNE 데뷔 라이브",
                    "에코 밴드 단독 콘서트 - The Sound of Echo",
                    "제인 피아노 리사이틀: 쇼팽 야상곡",
                    "케이팝 올스타 대전: K-Wave Festa",
                    "힙합 나이트: 언더그라운드 잼",
                    "재즈 & 블루스 스페셜 세션",
                    "록 페스티벌: 메탈리카 헌정",
                    "발라드 가든: 겨울 이야기",
                    "일렉트로닉 댄스 파티: Future Beats",
                    "트로트 대향연: 국민 가요제"
            );

            // 모든 스케줄 링크를 모으는 리스트
            List<LiveSchedules> allLiveSchedulesLinks = new ArrayList<>();
            int totalSchedules = 0;

            for (int i = 0; i < 10; i++) {
                String posterUrl = POSTER_URLS.get(random.nextInt(POSTER_URLS.size()));
                Artist assignedArtist = artists.get(random.nextInt(artists.size()));

                // 4-1. Lives 생성 및 저장
                Lives live = Lives.builder()
                        .title(liveTitles.get(i % liveTitles.size()))
                        .description(assignedArtist.getArtistName() + "의 " + liveTitles.get(i % liveTitles.size()) + " 공연입니다. 이 공연은 테스트용으로 " + (i+1) + "회차 공연을 포함합니다.")
                        .posterUrl(posterUrl)
                        .ticketUrl("https://ticket.url/live" + (i + 1))
                        .venue(i % 2 == 0 ? "서울 올림픽 경기장" : "부산 벡스코")
                        .seatPrices(seatPrices)
                        .build();

                Lives savedLive = livesRepository.save(live);

                // 4-2. 공연과 아티스트 연동 (LiveArtist 생성)
                LiveArtist liveArtistLink = LiveArtist.builder()
                        .live(savedLive)
                        .artist(assignedArtist)
                        .build();

                liveArtistRepository.save(liveArtistLink);

                // 💡 4-3. 공연 일정(Schedules) 및 연동 (LiveSchedules 생성)
                LocalDateTime baseDateTime = LocalDateTime.now().plusDays(i * 5 + 10); // 미래 날짜로 설정

                // 각 공연마다 3개의 스케줄 생성
                for (int j = 0; j < 3; j++) {
                    LocalDateTime scheduleDateTime = baseDateTime
                            .plusDays(j) // 날짜 차이
                            .withHour(19)
                            .withMinute(j * 10)
                            .withSecond(0).withNano(0);

                    // Schedules 엔티티 생성 및 저장 (LocalDateTime 필드 사용 가정)
                    Schedules scheduleEntity = Schedules.builder()
                            // 💡 LocalDateTime에서 날짜만 추출하여 저장
                            .liveDate(scheduleDateTime.toLocalDate())
                            // 💡 LocalDateTime에서 시간만 추출하여 저장
                            .liveTime(scheduleDateTime.toLocalTime())
                            .build();

                    Schedules savedSchedule = schedulesRepository.save(scheduleEntity);

                    // LiveSchedules 연결 엔티티 생성
                    LiveSchedules liveScheduleLink = LiveSchedules.builder()
                            .live(savedLive)
                            .schedule(savedSchedule)
                            .build();

                    allLiveSchedulesLinks.add(liveScheduleLink);
                    totalSchedules++;
                }
            }

            // 모든 LiveSchedules 일괄 저장
            liveSchedulesRepository.saveAll(allLiveSchedulesLinks);


            System.out.println("--- 초기 데이터 설정 완료 ---");
            System.out.println("총 유저 수: " + userRepository.count() + "명");
            System.out.println("총 아티스트 수: " + artistRepository.count() + "명");
            System.out.println("총 공연 수: " + livesRepository.count() + "개");
            System.out.println("총 일정 수 (Schedules): " + schedulesRepository.count() + "개"); // 💡 스케줄 수 확인
            System.out.println("총 Live-Schedule 연결 수: " + liveSchedulesRepository.count() + "개"); // 💡 연결 수 확인
        };
    }
}