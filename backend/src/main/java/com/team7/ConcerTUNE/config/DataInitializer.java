package com.team7.ConcerTUNE.config;

import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Configuration
public class DataInitializer {

    private static final String DEFAULT_PASSWORD = "password12";

    // Repository 의존성 주입 (게시판 관련 Repository 추가)
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final LiveRepository liveRepository;
    private final LiveArtistRepository liveArtistRepository;
    private final ArtistManagerRepository artistManagerRepository;
    private final GenreRepository genreRepository;
    private final ArtistGenreRepository artistGenreRepository;
    private final ScheduleRepository scheduleRepository;
    private final LiveScheduleRepository liveScheduleRepository;
    private final PostRepository postRepository; // 💡 추가
    private final CommentRepository commentRepository; // 💡 추가
    private final PostLikeRepository postLikeRepository; // 💡 추가
    private final CommentLikeRepository commentLikeRepository; // 💡 추가

    // 포스터 이미지 및 공연 장소 목록 (업데이트된 포스터 URL 목록)
    private static final List<String> POSTER_URLS = Arrays.asList(
            "https://marketplace.canva.com/EAF_4QFDSOo/1/0/1131w/canva-%EB%B3%B4%EB%9D%BC-%ED%95%98%ED%8A%B8%EC%9D%BC%EB%9F%AC%EC%8A%A4%ED%8A%B8-%EC%B9%B4%ED%88%B4-%EC%9D%8C%EC%95%85%EC%BD%98%EC%84%9C%ED%8A%B8-%ED%8F%AC%EC%8A%A4%ED%84%B0-8sYMo2WO3-c.jpg",
            "https://i.pinimg.com/736x/23/c5/d9/23c5d9f7ed15dc479628546e8a025f87.jpg",
            "https://img.freepik.com/free-psd/hand-drawn-music-concert-poster-template_23-2149888576.jpg?semt=ais_hybrid&w=740&q=80",
            "https://m.misulbook.com/web/product/big/20200322/561_shop1_1584814440339.jpg",
            "https://file.newswire.co.kr/data/datafile2/thumb_640/2024/08/2948802425_20240814204613_5839926970.jpg",
            "https://marketplace.canva.com/EAGKCzKE7Tw/2/0/1131w/canva-%ED%8C%8C%EB%9E%91-%ED%95%91%ED%81%AC-%EB%B3%B4%EB%9D%BC%EC%83%89-%EB%8B%A4%EC%9D%B4%EB%82%98%EB%AF%B9-%EA%B0%95%EB%A0%AC%ED%95%9C-%EB%B8%8C%EB%A0%88%EC%9D%B4%ED%82%B9-%EB%8C%84%EC%8A%A4-%EA%B3%B5%EC%97%B0-%ED%99%8D%EB%B3%B4-%ED%8F%AC%EC%8A%A4%ED%84%B0-2tdVRBGg3s0.jpg",
            "https://www.acc.go.kr/webzine/down/image.do?fileNo=8320",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_9EkPfdhkLYcsDhy_HOQ3_Qz0-ChEp8rn5A&s",
            "https://dimg.donga.com/wps/NEWS/IMAGE/2019/09/16/97419640.2.jpg",
            "https://wimg.heraldcorp.com/content/default/2023/09/12/20230912000143_0.jpg"
    );

    private static final List<String> VENUES = Arrays.asList(
            "서울 올림픽 체조경기장 (KSPO DOME)", "고척 스카이돔", "잠실 주경기장",
            "예술의 전당 콘서트홀", "블루스퀘어 마스터카드홀", "YES24 라이브홀",
            "KBS 아레나", "세종문화회관 대극장", "홍대 롤링홀", "부산 벡스코 오디토리움"
    );

    @Bean
    public CommandLineRunner initDefaultUserAndData(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("🚀 [DataInitializer] 초기 데이터 생성을 시작합니다...");
            Random random = new Random();

            // ==========================================
            // 1. 유저 생성 (일반 유저 5명, 매니저 10명 포함)
            // ==========================================
            Map<String, User> userMap = createUsers(passwordEncoder);

            User adminUser = userMap.get("admin@naver.com");
            // 게시글 작성에 사용할 유저 목록 (Admin, Artist, User1, User2, User3, User4, User5)
            List<User> postWriters = new ArrayList<>(Arrays.asList(
                    userMap.get("admin@naver.com"),
                    userMap.get("artist@naver.com"),
                    userMap.get("user1@naver.com"),
                    userMap.get("user2@naver.com"),
                    userMap.get("user3@naver.com")
            ));

            // 매니저 유저 목록
            List<User> managerUsers = IntStream.range(1, 11)
                    .mapToObj(i -> userMap.get("manager" + i + "@company.com"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (adminUser == null || managerUsers.size() < 10) {
                System.err.println("❌ 필수 유저(Admin 또는 Manager)가 충분히 생성되지 않았습니다. 초기화를 중단합니다.");
                return;
            }

            // ==========================================
            // 2. 장르 생성
            // ==========================================
            Map<String, Genre> genreMap = createGenres();

            // ==========================================
            // 3. 아티스트 생성 (약 100명)
            // ==========================================
            List<ArtistData> artistDataList = prepareArtistData();
            List<Artist> savedArtists = new ArrayList<>();
            List<ArtistManager> artistManagersToSave = new ArrayList<>();
            List<ArtistGenre> artistGenresToSave = new ArrayList<>();

            System.out.println("--- 3. 아티스트 " + artistDataList.size() + "명 생성 중... ---");

            for (int i = 0; i < artistDataList.size(); i++) {
                ArtistData data = artistDataList.get(i);

                Artist artist = Artist.builder()
                        .artistName(data.name)
                        .isDomestic(random.nextBoolean())
                        .snsUrl("https://instagram.com/" + data.name.replaceAll("\\s+", "").toLowerCase())
                        .artistImageUrl("https://placehold.co/400x400/333/FFF?text=" + data.name.replaceAll("\\s+", "+"))
                        .build();

                Artist savedArtist = artistRepository.save(artist);
                savedArtists.add(savedArtist);

                for (String genreName : data.genres) {
                    Genre genre = genreMap.get(genreName);
                    if (genre != null) {
                        artistGenresToSave.add(new ArtistGenre(savedArtist, genre));                    }
                }

                if (i < managerUsers.size()) {
                    User currentManager = managerUsers.get(i);

                    ArtistManagerId managerId = new ArtistManagerId(currentManager.getId(), savedArtist.getArtistId());
                    artistManagersToSave.add(ArtistManager.builder()
                            .id(managerId)
                            .user(currentManager)
                            .artist(savedArtist)
                            .assignedAt(LocalDateTime.now())
                            .isOfficial(true)
                            .build());
                }
            }
            artistGenreRepository.saveAll(artistGenresToSave);
            artistManagerRepository.saveAll(artistManagersToSave);
            System.out.println("✅ 아티스트, 장르, 매니저 연결 생성 완료. (총 아티스트: " + savedArtists.size() + "명)");


            // ==========================================
            // 4. 공연(Lives) 및 스케줄 생성 (30개)
            // ==========================================
            System.out.println("--- 4. 공연 및 스케줄 30개 생성 중... ---");

            Map<String, Integer> defaultPrices = Map.of("VIP석", 154000, "R석", 132000, "S석", 110000, "A석", 99000);
            List<LiveSchedule> allLiveSchedules = new ArrayList<>();
            List<LiveArtist> allLiveArtists = new ArrayList<>();
            List<Live> savedLives = new ArrayList<>(); // 게시글 연결을 위해 저장

            for (int i = 1; i <= 30; i++) {
                int concertType = random.nextInt(10);
                List<Artist> selectedArtists = new ArrayList<>();
                String title;
                String venue = VENUES.get(random.nextInt(VENUES.size()));
                String description;
                int durationDays;

                if (concertType < 6) {
                    Artist soloArtist = savedArtists.get(random.nextInt(savedArtists.size()));
                    selectedArtists.add(soloArtist);
                    title = String.format("%s 월드 투어: THE DREAM in %s", soloArtist.getArtistName(), venue.split(" ")[0]);
                    description = soloArtist.getArtistName() + "의 단독 공연입니다. 최고의 무대를 만나보세요.";
                    durationDays = random.nextInt(2) + 1;

                } else if (concertType < 9) {
                    Collections.shuffle(savedArtists);
                    selectedArtists.addAll(savedArtists.subList(0, random.nextInt(3) + 2));
                    String mainArtistName = selectedArtists.stream().limit(3).map(Artist::getArtistName).collect(Collectors.joining(", "));
                    title = String.format("%s 조인트 콘서트", mainArtistName);
                    description = "최고의 아티스트들이 함께하는 특별한 밤! 출연: " + selectedArtists.stream().map(Artist::getArtistName).collect(Collectors.joining(", "));
                    durationDays = 1;

                } else {
                    Collections.shuffle(savedArtists);
                    selectedArtists.addAll(savedArtists.subList(0, random.nextInt(6) + 5));
                    title = "2026 그랜드 뮤직 페스티벌 - DAY " + random.nextInt(3) + 1;
                    description = "도심 속에서 즐기는 음악 축제! Line-up: " + selectedArtists.stream().map(Artist::getArtistName).collect(Collectors.joining(", "));
                    venue = "난지 한강공원";
                    durationDays = 3;
                }

                Live live = Live.builder()
                        .title(title)
                        .description(description)
                        .posterUrl(POSTER_URLS.get(random.nextInt(POSTER_URLS.size())))
                        .ticketUrl("https://ticket.site/booking/" + i)
                        .venue(venue)
                        .price(defaultPrices)
                        .writer(adminUser)
                        .requestStatus(RequestStatus.APPROVED)
                        .ticketDateTime(getRandomFutureTicketDateTime())
                        .build();
                Live savedLive = liveRepository.save(live);
                savedLives.add(savedLive); // 저장된 Live 목록에 추가

                selectedArtists.forEach(artist -> {
                    allLiveArtists.add(LiveArtist.builder().live(savedLive).artist(artist).build());
                });

                LocalDate startDate = LocalDate.now().plusDays(random.nextInt(90) + 10);

                for (int day = 0; day < durationDays; day++) {
                    LocalDate concertDate = startDate.plusDays(day);
                    LocalTime concertTime = (concertDate.getDayOfWeek().getValue() >= 6) ? LocalTime.of(17, 0) : LocalTime.of(19, 30);

                    Schedule schedule = Schedule.builder()
                            .liveDate(concertDate)
                            .liveTime(concertTime)
                            .build();
                    Schedule savedSchedule = scheduleRepository.save(schedule);

                    allLiveSchedules.add(LiveSchedule.builder()
                            .live(savedLive)
                            .schedule(savedSchedule)
                            .build());
                }
            }
            liveArtistRepository.saveAll(allLiveArtists);
            liveScheduleRepository.saveAll(allLiveSchedules);
            System.out.println("✅ 공연 및 스케줄 생성 완료");

            // ==========================================
            // 5. 커뮤니티 게시글 (Post) 생성 (10개)
            // ==========================================
            System.out.println("--- 5. 게시글 10개 및 댓글/좋아요 생성 중... ---");
            List<Post> savedPosts = new ArrayList<>();
            List<Comment> savedComments = new ArrayList<>();
            List<PostLike> postLikesToSave = new ArrayList<>();
            List<CommentLike> commentLikesToSave = new ArrayList<>();

            CommunityCategoryType[] categories = CommunityCategoryType.values();
            String[] postTitles = {
                    "고척돔 콘서트 같이 가실 분!",
                    "어제 홍대 롤링홀 라이브 후기!",
                    "요즘 가장 기대되는 공연이 뭐예요?",
                    "부산 벡스코 주차 팁 공유해요",
                    "티켓팅 성공 후기! VIP석 겟!",
                    "콘서트 굿즈 정리 팁!",
                    "KSPO DOME 3층 시야 궁금합니다.",
                    "세종문화회관 클래식 공연 감동 ㅠㅠ",
                    "좋아하는 밴드 다음 앨범 언제 나올까요?",
                    "멜로망스 공연 같이 보고 저녁 드실 분!"
            };
            String[] postContents = {
                    "다음 주에 열리는 [LIVE TITLE] 고척돔 공연 티켓팅 성공했는데, 혼자 가기 심심해서 동행 구합니다. 20대 여성 분이면 좋겠고, 끝나고 근처에서 간단하게 맥주 한 잔 하실 분 환영해요!",
                    "어제 [ARTIST NAME] 라이브 봤는데, 정말 역대급이었습니다. 특히 앵콜 무대에서 불렀던 곡이 최고였어요. 스탠딩 구역이었는데 생각보다 시야 좋았고, 다음에 또 가고 싶네요.",
                    "개인적으로 [ARTIST NAME]의 단독 콘서트가 제일 기대돼요. 포스터 보니까 이번 컨셉이 역대급일 것 같던데, 혹시 벌써 티켓팅 하신 분 있으신가요?",
                    "부산 벡스코 오디토리움은 주차하기가 좀 까다롭죠. 저는 보통 근처 공영 주차장을 이용하는데, 이번 [LIVE TITLE] 때문에 많이 붐빌 것 같아요. 팁 공유 부탁드립니다!",
                    "드디어 [ARTIST NAME]의 꿈의 무대 티켓팅에 성공했습니다! 손이 덜덜 떨렸지만, 해냈습니다! VIP석이라니 벌써부터 눈물 나네요. 다들 성공하셨나요?",
                    "집에 쌓인 콘서트 굿즈들, 어떻게 정리하세요? 포스터는 액자에 넣고, 슬로건은 벽에 걸었는데, 포토카드가 너무 많네요 ㅠㅠ 보관 팁 알려주세요!",
                    "KSPO DOME 3층 시야가 걱정돼요. [LIVE TITLE]가서 보는데, 너무 멀까봐 불안합니다. 혹시 3층에서 관람해보신 분들 경험 공유 부탁드려요!",
                    "[ARTIST NAME]의 클래식 공연을 처음 봤는데, 정말 압도적이었습니다. 웅장함에 눈물이 났어요. 클래식 입문자에게도 정말 추천합니다.",
                    "요즘 [BAND NAME] 앨범만 돌려 듣는데, 다음 앨범 소식이 너무 궁금해요. 혹시 공식 채널에서 뭔가 언급된 거 있나요? 기다리기 너무 힘듭니다.",
                    "멜로망스 공연 2연석 예매했습니다. 같이 보고 끝나고 근처 맛집에서 저녁 드실 분 구해요. 나이/성별 무관하게 즐겁게 대화하실 분이면 됩니다!"
            };

            for (int i = 0; i < 10; i++) {
                User writer = postWriters.get(random.nextInt(postWriters.size()));
                CommunityCategoryType category = categories[random.nextInt(categories.length)];
                Live live = (i % 3 == 0) ? savedLives.get(random.nextInt(savedLives.size())) : null; // 일부 게시글은 라이브 연결

                String title = postTitles[i];
                String content = postContents[i];

                // [LIVE TITLE] 및 [ARTIST NAME] 치환
                if (live != null) {
                    title = title.replace("[LIVE TITLE]", live.getTitle());
                    content = content.replace("[LIVE TITLE]", live.getTitle());
                }

                String randomArtistName = savedArtists.get(random.nextInt(savedArtists.size())).getArtistName();
                title = title.replace("[ARTIST NAME]", randomArtistName).replace("[BAND NAME]", randomArtistName);
                content = content.replace("[ARTIST NAME]", randomArtistName).replace("[BAND NAME]", randomArtistName);


                Post post = Post.builder()
                        .title(title)
                        .content(content)
                        .writer(writer)
                        .category(category)
                        .viewCount(random.nextInt(1000))
                        .live(live)
                        .build();

                Post savedPost = postRepository.save(post);
                savedPosts.add(savedPost);

                // 댓글 2~5개 생성
                int commentCount = random.nextInt(4) + 2;
                for (int j = 0; j < commentCount; j++) {
                    User commentWriter = postWriters.get(random.nextInt(postWriters.size()));
                    String commentContent = (j == 0) ? "꿀팁 감사합니다!" : (j == 1 ? "저도 같이 가고 싶어요 ㅠㅠ" : "정보 잘 보고 갑니다!");

                    Comment comment = Comment.builder()
                            .content(commentContent)
                            .writer(commentWriter)
                            .post(savedPost)
                            .likeCount(random.nextInt(5))
                            .build();

                    Comment savedComment = commentRepository.save(comment);
                    savedComments.add(savedComment);

                    savedPost.addComment(savedComment); // Post 엔티티의 commentCount 업데이트
                }
                postRepository.save(savedPost); // 카운트 업데이트 반영

                // 게시글 좋아요 1~3개 생성
                List<User> likeUsers = new ArrayList<>(postWriters);
                Collections.shuffle(likeUsers);
                int likeCount = random.nextInt(3) + 1;
                for (int k = 0; k < likeCount; k++) {
                    User likeUser = likeUsers.get(k);
                    PostLike postLike = PostLike.builder().user(likeUser).post(savedPost).build();
                    postLike.setEmbeddedId();
                    postLikesToSave.add(postLike);
                    savedPost.increaseLikeCount(); // Post 엔티티의 likeCount 업데이트
                }
                postRepository.save(savedPost); // 좋아요 카운트 업데이트 반영
            }

            postLikeRepository.saveAll(postLikesToSave);
            System.out.println("✅ 게시글, 댓글, 좋아요 생성 완료");
            System.out.println("-----------------------------------------");
            System.out.println("총 게시글 수: " + postRepository.count());
            System.out.println("총 댓글 수: " + commentRepository.count());
            System.out.println("총 게시글 좋아요 수: " + postLikeRepository.count());
            System.out.println("-----------------------------------------");
        };
    }

    // ---------------- Helper Methods ----------------

    private LocalDateTime getRandomFutureTicketDateTime() {
        return LocalDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(1, 10))
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    private Map<String, User> createUsers(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
        List<User> users = new ArrayList<>();

        // 일반 유저 5명 추가
        for (int i = 1; i <= 5; i++) {
            users.add(User.builder().email("user" + i + "@naver.com").password(encodedPassword).username("일반유저" + i).auth(AuthRole.USER).provider(AuthProvider.LOCAL).enabled(true).build());
        }

        users.add(User.builder().email("artist@naver.com").password(encodedPassword).username("아이유").auth(AuthRole.ARTIST).provider(AuthProvider.LOCAL).enabled(true).build());
        users.add(User.builder().email("admin@naver.com").password(encodedPassword).username("관리자").auth(AuthRole.ADMIN).provider(AuthProvider.LOCAL).enabled(true).build());

        // 10명의 매니저 유저 생성 및 추가
        for (int i = 1; i <= 10; i++) {
            users.add(User.builder().email("manager" + i + "@company.com").password(passwordEncoder.encode("manager" + i)).username("Manager" + i).auth(AuthRole.USER).provider(AuthProvider.LOCAL).enabled(true).build());
        }

        userRepository.saveAll(users);
        System.out.println("기본 유저 및 매니저 유저 총 " + users.size() + "명 생성 완료.");

        return users.stream().collect(Collectors.toMap(User::getEmail, user -> user));
    }

    private Map<String, Genre> createGenres() {
        // ... (기존 createGenres 메서드 유지) ...
        List<String> genreNames = Arrays.asList(
                "팝", "록", "힙합", "알앤비", "재즈", "클래식", "일렉트로닉", "포크", "컨트리", "블루스",
                "케이팝", "인디", "발라드", "메탈", "레게", "앰비언트", "하우스", "테크노", "트랜스",
                "가스펠", "OST/사운드트랙", "오페라", "트로트", "댄스", "펑크", "어쿠스틱", "소울", "디스코", "퓨전", "월드 뮤직", "얼터너티브", "하드록", "사이키델릭", "모던록", "뉴에이지"
        );
        Map<String, Genre> genreMap = new HashMap<>();
        for (String name : genreNames) {
            genreMap.put(name, genreRepository.save(Genre.builder().genreName(name).build()));
        }
        return genreMap;
    }

    private List<ArtistData> prepareArtistData() {
        // ... (기존 prepareArtistData 메서드 유지) ...
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

    private static class ArtistData {
        String name;
        List<String> genres;

        ArtistData(String name, String... genres) {
            this.name = name;
            this.genres = Arrays.asList(genres);
        }
    }
}