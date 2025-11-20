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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final SchedulesRepository schedulesRepository;
    private final LiveSchedulesRepository liveSchedulesRepository;

    // 포스터 이미지 (랜덤 배정용)
    private static final List<String> POSTER_URLS = Arrays.asList(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsP2JWoHzrD-LEgtz89wfwJM_-RWfHEW45Tg&s", // 콘서트 1
            "https://file.newswire.co.kr/data/datafile2/thumb_640/2020/10/2948802425_20201030102144_7402605803.jpg", // 콘서트 2
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8jjfaX2dNaQAZ4YvPBUdgkFewvbhidCAj8g&s", // 콘서트 3
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRcxMsDE1ip_nbnq_opEhqQjTaYGY38ZjCHlA&s", // 콘서트 4
            "https://i.pinimg.com/736x/a3/6b/72/a36b72697834393d82233d27d4415671.jpg", // 페스티벌 느낌
            "https://tickets.interpark.com/contents/_next/image?url=https%3A%2F%2Fticketimage.interpark.com%2FPlay%2Fimage%2Flarge%2F23%2F23006712_p.gif&w=3840&q=75" // 클래식/발라드
    );

    // 공연 장소 목록
    private static final List<String> VENUES = Arrays.asList(
            "서울 올림픽 체조경기장 (KSPO DOME)", "고척 스카이돔", "잠실 주경기장",
            "예술의 전당 콘서트홀", "블루스퀘어 마스터카드홀", "YES24 라이브홀",
            "KBS 아레나", "세종문화회관 대극장", "홍대 롤링홀", "부산 벡스코 오디토리움"
    );

    @Bean
    public CommandLineRunner initDefaultUser(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("🚀 [UserInitializer] 초기 데이터 생성을 시작합니다...");
            Random random = new Random();

            // ==========================================
            // 1. 유저 생성 (기존 유지)
            // ==========================================
            if (userRepository.count() == 0) {
                createUsers(passwordEncoder);
            }

            // ==========================================
            // 2. 장르 생성 (기존 유지)
            // ==========================================
            Map<String, Genre> genreMap = createGenres();

            // ==========================================
            // 3. 아티스트 생성 (약 100명)
            // ==========================================
            // 아티스트 데이터 준비 (이름, 장르)
            List<ArtistData> artistDataList = prepareArtistData();
            List<Artist> savedArtists = new ArrayList<>();

            // 매니저 유저 (아티스트 연결용)
            User managerUser = userRepository.findByEmail("manager@company.com").orElse(null);

            System.out.println("--- 3. 아티스트 " + artistDataList.size() + "명 생성 중... ---");

            for (int i = 0; i < artistDataList.size(); i++) {
                ArtistData data = artistDataList.get(i);

                // 아티스트 저장
                Artist artist = Artist.builder()
                        .artistName(data.name)
                        .isDomestic(random.nextBoolean()) // 국내/해외 랜덤
                        .snsUrl("https://instagram.com/" + data.name.replaceAll("\\s+", "").toLowerCase())
                        .artistImageUrl("https://placehold.co/400x400/333/FFF?text=" + data.name.replaceAll("\\s+", "+"))
                        .isOfficial(true)
                        .build();

                Artist savedArtist = artistRepository.save(artist);
                savedArtists.add(savedArtist);

                // 장르 연결
                List<ArtistGenre> artistGenres = new ArrayList<>();
                for (String genreName : data.genres) {
                    Genre genre = genreMap.get(genreName);
                    if (genre != null) {
                        artistGenres.add(new ArtistGenre(savedArtist, genre));
                    }
                }
                artistGenreRepository.saveAll(artistGenres);

                // 앞쪽 10명만 매니저 연결 (테스트용)
                if (i < 10 && managerUser != null) {
                    ArtistManagerId managerId = new ArtistManagerId(managerUser.getId(), savedArtist.getArtistId());
                    ArtistManager manager = ArtistManager.builder()
                            .id(managerId)
                            .user(managerUser)
                            .artist(savedArtist)
                            .assignedAt(LocalDateTime.now())
                            .isOfficial(true)
                            .build();
                    artistManagerRepository.save(manager);
                }
            }
            System.out.println("✅ 아티스트 생성 완료");


            // ==========================================
            // 4. 공연(Lives) 및 스케줄 생성 (30개)
            // ==========================================
            System.out.println("--- 4. 공연 및 스케줄 30개 생성 중... ---");

            // 공통 좌석 가격
            Map<String, Integer> defaultPrices = new HashMap<>();
            defaultPrices.put("VIP석", 154000);
            defaultPrices.put("R석", 132000);
            defaultPrices.put("S석", 110000);
            defaultPrices.put("A석", 99000);

            List<LiveSchedules> allLiveSchedules = new ArrayList<>();

            for (int i = 1; i <= 30; i++) {
                // 공연 타입 결정 (0: 단독, 1: 합동, 2: 페스티벌)
                int concertType = random.nextInt(10); // 0~5:단독(60%), 6~8:합동(30%), 9:페스티벌(10%)

                List<Artist> selectedArtists = new ArrayList<>();
                String title;
                String venue = VENUES.get(random.nextInt(VENUES.size()));
                String description;
                int durationDays; // 공연 기간 (1~3일)

                if (concertType < 6) {
                    // [단독 공연]
                    Artist soloArtist = savedArtists.get(random.nextInt(savedArtists.size()));
                    selectedArtists.add(soloArtist);
                    title = String.format("%s 월드 투어: THE DREAM", soloArtist.getArtistName());
                    description = soloArtist.getArtistName() + "의 단독 내한 공연입니다. 최고의 무대를 만나보세요.";
                    durationDays = random.nextInt(2) + 1; // 1 or 2일

                } else if (concertType < 9) {
                    // [합동 공연] - 2~3팀
                    Collections.shuffle(savedArtists);
                    selectedArtists.addAll(savedArtists.subList(0, random.nextInt(2) + 2));
                    String mainArtistName = selectedArtists.get(0).getArtistName();
                    title = String.format("%s & Friends 조인트 콘서트", mainArtistName);
                    description = "최고의 아티스트들이 함께하는 특별한 밤! " + selectedArtists.stream().map(Artist::getArtistName).collect(Collectors.joining(", ")) + " 출연.";
                    durationDays = 1; // 보통 하루

                } else {
                    // [페스티벌] - 5~8팀
                    Collections.shuffle(savedArtists);
                    selectedArtists.addAll(savedArtists.subList(0, random.nextInt(4) + 5));
                    title = "2025 그랜드 민트 뮤직 페스티벌";
                    description = "도심 속에서 즐기는 음악 축제! 역대급 라인업을 공개합니다.\nLine-up: " + selectedArtists.stream().map(Artist::getArtistName).collect(Collectors.joining(", "));
                    venue = "난지 한강공원"; // 페스티벌은 야외
                    durationDays = 3; // 3일
                }

                // Lives 저장
                Lives live = Lives.builder()
                        .title(title)
                        .description(description)
                        .posterUrl(POSTER_URLS.get(random.nextInt(POSTER_URLS.size())))
                        .ticketUrl("https://ticket.site/booking/" + i)
                        .venue(venue)
                        .seatPrices(defaultPrices)
                        .build();
                Lives savedLive = livesRepository.save(live);

                // LiveArtist 연결
                for (Artist artist : selectedArtists) {
                    liveArtistRepository.save(LiveArtist.builder()
                            .live(savedLive)
                            .artist(artist)
                            .build());
                }

                // 스케줄 생성 (durationDays 만큼)
                LocalDate startDate = LocalDate.now().plusDays(random.nextInt(90) + 10); // 10일 ~ 100일 뒤

                for (int day = 0; day < durationDays; day++) {
                    LocalDate concertDate = startDate.plusDays(day);
                    LocalTime concertTime = (concertDate.getDayOfWeek().getValue() >= 6) ? LocalTime.of(17, 0) : LocalTime.of(19, 30); // 주말 5시, 평일 7시 반

                    Schedules schedule = Schedules.builder()
                            .liveDate(concertDate)
                            .liveTime(concertTime)
                            .build();
                    Schedules savedSchedule = schedulesRepository.save(schedule);

                    // Live - Schedule 연결
                    allLiveSchedules.add(LiveSchedules.builder()
                            .live(savedLive)
                            .schedule(savedSchedule)
                            .build());
                }
            }
            liveSchedulesRepository.saveAll(allLiveSchedules);

            System.out.println("✅ 공연 및 스케줄 생성 완료");
            System.out.println("-----------------------------------------");
            System.out.println("총 아티스트 수: " + artistRepository.count());
            System.out.println("총 공연 수: " + livesRepository.count());
            System.out.println("총 스케줄 수: " + schedulesRepository.count());
            System.out.println("-----------------------------------------");
        };
    }

    // ---------------- Helper Methods ----------------

    private void createUsers(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        userRepository.save(User.builder().email("user1@naver.com").password(encodedPassword).username("김철수").auth(AuthRole.USER).provider(AuthProvider.LOCAL).enabled(true).build());
        userRepository.save(User.builder().email("artist@naver.com").password(encodedPassword).username("아이유").auth(AuthRole.ARTIST).provider(AuthProvider.LOCAL).enabled(true).build());
        userRepository.save(User.builder().email("admin@naver.com").password(encodedPassword).username("관리자").auth(AuthRole.ADMIN).provider(AuthProvider.LOCAL).enabled(true).build());
        userRepository.save(User.builder().email("manager@company.com").password(passwordEncoder.encode("supermanager")).username("SuperManager").auth(AuthRole.USER).provider(AuthProvider.LOCAL).enabled(true).build());

        System.out.println("기본 유저 4명 생성 완료.");
    }

    private Map<String, Genre> createGenres() {
        List<String> genreNames = Arrays.asList(
                "팝", "록", "힙합", "알앤비", "재즈", "클래식", "일렉트로닉", "포크", "컨트리", "블루스",
                "케이팝", "인디", "발라드", "메탈", "레게", "앰비언트", "하우스", "테크노", "트랜스",
                "가스펠", "OST/사운드트랙", "오페라", "트로트", "댄스", "펑크", "어쿠스틱", "소울", "디스코", "퓨전", "월드 뮤직"
        );
        Map<String, Genre> genreMap = new HashMap<>();
        for (String name : genreNames) {
                genreMap.put(name, genreRepository.save(Genre.builder().genreName(name).build()));
        }
        return genreMap;
    }

    // 약 100명의 아티스트 데이터를 생성하여 반환
    private List<ArtistData> prepareArtistData() {
        List<ArtistData> list = new ArrayList<>();

        // K-Pop (20)
        list.add(new ArtistData("BTS", "케이팝", "팝"));
        list.add(new ArtistData("BLACKPINK", "케이팝", "댄스"));
        list.add(new ArtistData("NewJeans", "케이팝", "팝"));
        list.add(new ArtistData("IVE", "케이팝", "댄스"));
        list.add(new ArtistData("SEVENTEEN", "케이팝", "팝"));
        list.add(new ArtistData("LE SSERAFIM", "케이팝", "댄스"));
        list.add(new ArtistData("Stray Kids", "케이팝", "힙합"));
        list.add(new ArtistData("TWICE", "케이팝", "댄스"));
        list.add(new ArtistData("EXO", "케이팝", "알앤비"));
        list.add(new ArtistData("NCT 127", "케이팝", "힙합"));
        list.add(new ArtistData("Red Velvet", "케이팝", "알앤비"));
        list.add(new ArtistData("aespa", "케이팝", "일렉트로닉"));
        list.add(new ArtistData("TXT", "케이팝", "록"));
        list.add(new ArtistData("ENHYPEN", "케이팝", "팝"));
        list.add(new ArtistData("ATEEZ", "케이팝", "힙합"));
        list.add(new ArtistData("ITZY", "케이팝", "댄스"));
        list.add(new ArtistData("MAMAMOO", "케이팝", "소울"));
        list.add(new ArtistData("Taeyeon", "케이팝", "발라드"));
        list.add(new ArtistData("IU", "케이팝", "발라드", "어쿠스틱"));
        list.add(new ArtistData("Psy", "케이팝", "댄스"));

        // Ballad & R&B (15)
        list.add(new ArtistData("박효신", "발라드", "소울"));
        list.add(new ArtistData("성시경", "발라드"));
        list.add(new ArtistData("Crush", "알앤비", "힙합"));
        list.add(new ArtistData("Heize", "알앤비", "힙합"));
        list.add(new ArtistData("Zion.T", "알앤비", "힙합"));
        list.add(new ArtistData("폴킴", "발라드", "어쿠스틱"));
        list.add(new ArtistData("10CM", "인디", "어쿠스틱"));
        list.add(new ArtistData("멜로망스", "발라드", "인디"));
        list.add(new ArtistData("볼빨간사춘기", "인디", "팝"));
        list.add(new ArtistData("AKMU", "케이팝", "포크"));
        list.add(new ArtistData("백예린", "알앤비", "인디"));
        list.add(new ArtistData("Dean", "알앤비", "힙합"));
        list.add(new ArtistData("이하이", "알앤비", "소울"));
        list.add(new ArtistData("김동률", "발라드"));
        list.add(new ArtistData("이적", "발라드", "록"));

        // Rock & Indie (20)
        list.add(new ArtistData("자우림", "록", "얼터너티브"));
        list.add(new ArtistData("YB", "록", "하드록"));
        list.add(new ArtistData("국카스텐", "록", "사이키델릭"));
        list.add(new ArtistData("잔나비", "인디", "록"));
        list.add(new ArtistData("새소년", "인디", "록"));
        list.add(new ArtistData("혁오", "인디", "록"));
        list.add(new ArtistData("검정치마", "인디", "록"));
        list.add(new ArtistData("카더가든", "인디", "록"));
        list.add(new ArtistData("실리카겔", "인디", "록"));
        list.add(new ArtistData("NELL", "록", "모던록"));
        list.add(new ArtistData("DAY6", "케이팝", "록"));
        list.add(new ArtistData("N.Flying", "케이팝", "록"));
        list.add(new ArtistData("LUCY", "케이팝", "인디"));
        list.add(new ArtistData("쏜애플", "인디", "록"));
        list.add(new ArtistData("브로콜리너마저", "인디", "포크"));
        list.add(new ArtistData("언니네이발관", "인디", "모던록"));
        list.add(new ArtistData("장기하와 얼굴들", "인디", "록"));
        list.add(new ArtistData("노브레인", "펑크", "록"));
        list.add(new ArtistData("크라잉넛", "펑크", "록"));
        list.add(new ArtistData("부활", "록"));

        // Hip-hop (15)
        list.add(new ArtistData("Jay Park", "힙합", "알앤비"));
        list.add(new ArtistData("Zico", "힙합", "케이팝"));
        list.add(new ArtistData("Epik High", "힙합"));
        list.add(new ArtistData("Dynamic Duo", "힙합"));
        list.add(new ArtistData("Loco", "힙합"));
        list.add(new ArtistData("Gray", "힙합", "알앤비"));
        list.add(new ArtistData("Simon Dominic", "힙합"));
        list.add(new ArtistData("E-Sens", "힙합"));
        list.add(new ArtistData("Beenzino", "힙합"));
        list.add(new ArtistData("Changmo", "힙합"));
        list.add(new ArtistData("Giriboy", "힙합"));
        list.add(new ArtistData("Kid Milli", "힙합"));
        list.add(new ArtistData("Justhis", "힙합"));
        list.add(new ArtistData("Superbee", "힙합"));
        list.add(new ArtistData("Ash Island", "힙합"));

        // Classic, Jazz, Others (15)
        list.add(new ArtistData("조성진", "클래식"));
        list.add(new ArtistData("임윤찬", "클래식"));
        list.add(new ArtistData("손열음", "클래식"));
        list.add(new ArtistData("이루마", "뉴에이지", "클래식"));
        list.add(new ArtistData("나윤선", "재즈"));
        list.add(new ArtistData("웅산", "재즈"));
        list.add(new ArtistData("Winterplay", "재즈", "팝"));
        list.add(new ArtistData("송가인", "트로트"));
        list.add(new ArtistData("임영웅", "트로트", "발라드"));
        list.add(new ArtistData("영탁", "트로트"));
        list.add(new ArtistData("이찬원", "트로트"));
        list.add(new ArtistData("장윤정", "트로트"));
        list.add(new ArtistData("홍진영", "트로트"));
        list.add(new ArtistData("나훈아", "트로트"));
        list.add(new ArtistData("조용필", "록", "발라드")); // 레전드

        // Foreign Artists (Mock for variety) (15)
        list.add(new ArtistData("Coldplay", "록", "팝"));
        list.add(new ArtistData("Taylor Swift", "팝", "컨트리"));
        list.add(new ArtistData("Bruno Mars", "팝", "알앤비"));
        list.add(new ArtistData("Ed Sheeran", "팝", "어쿠스틱"));
        list.add(new ArtistData("Adele", "팝", "소울"));
        list.add(new ArtistData("Justin Bieber", "팝", "알앤비"));
        list.add(new ArtistData("The Weeknd", "알앤비", "일렉트로닉"));
        list.add(new ArtistData("Dua Lipa", "팝", "댄스"));
        list.add(new ArtistData("Billie Eilish", "팝", "얼터너티브"));
        list.add(new ArtistData("Imagine Dragons", "록"));
        list.add(new ArtistData("Maroon 5", "팝", "록"));
        list.add(new ArtistData("Charlie Puth", "팝"));
        list.add(new ArtistData("Post Malone", "힙합", "록"));
        list.add(new ArtistData("Drake", "힙합", "알앤비"));
        list.add(new ArtistData("Eminem", "힙합"));

        return list;
    }

    // Helper DTO Class
    private static class ArtistData {
        String name;
        List<String> genres;

        ArtistData(String name, String... genres) {
            this.name = name;
            this.genres = Arrays.asList(genres);
        }
    }
}