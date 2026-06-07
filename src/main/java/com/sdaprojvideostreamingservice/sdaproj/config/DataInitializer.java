package com.sdaprojvideostreamingservice.sdaproj.config;

import com.sdaprojvideostreamingservice.sdaproj.model.AgeRating;
import com.sdaprojvideostreamingservice.sdaproj.model.PaymentMethod;
import com.sdaprojvideostreamingservice.sdaproj.model.PaymentStatus;
import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionPlan;
import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionStatus;
import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.UserRole;
import com.sdaprojvideostreamingservice.sdaproj.model.VideoGenre;
import com.sdaprojvideostreamingservice.sdaproj.model.VideoType;
import com.sdaprojvideostreamingservice.sdaproj.model.WatchHistory;
import com.sdaprojvideostreamingservice.sdaproj.model.payment;
import com.sdaprojvideostreamingservice.sdaproj.model.subscription;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;
import com.sdaprojvideostreamingservice.sdaproj.model.watchlist;
import com.sdaprojvideostreamingservice.sdaproj.repository.PaymentRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.SubscriptionRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.UserRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.VideoRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchHistoryRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchlistRespository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String SAMPLE_STREAM = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final WatchlistRespository watchlistRepository;
    private final WatchHistoryRepository historyRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, VideoRepository videoRepository,
                           WatchlistRespository watchlistRepository, WatchHistoryRepository historyRepository,
                           SubscriptionRepository subscriptionRepository, PaymentRepository paymentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.watchlistRepository = watchlistRepository;
        this.historyRepository = historyRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User admin = userRepository.save(User.builder()
                .name("Admin User").email("admin@streamflix.com")
                .passwordHash(passwordEncoder.encode("admin123")).role(UserRole.ADMIN).build());
        User alice = userRepository.save(User.builder()
                .name("Alice Smith").email("alice@example.com")
                .passwordHash(passwordEncoder.encode("alice123")).role(UserRole.USER).build());
        User bob = userRepository.save(User.builder()
                .name("Bob Johnson").email("bob@example.com")
                .passwordHash(passwordEncoder.encode("bob123")).role(UserRole.USER).build());

        videoentity matrix = saveVideo("The Matrix", "A computer hacker learns about the true nature of his reality.",
                VideoGenre.SCI_FI, 8160, 1999, new BigDecimal("8.7"), AgeRating.R, VideoType.MOVIE, true, admin.getUserId());
        videoentity inception = saveVideo("Inception", "A thief who steals corporate secrets through dream-sharing technology.",
                VideoGenre.SCI_FI, 8880, 2010, new BigDecimal("8.8"), AgeRating.PG13, VideoType.MOVIE, true, admin.getUserId());
        videoentity dieHard = saveVideo("Die Hard", "An NYPD officer saves hostages from terrorists in a Los Angeles skyscraper.",
                VideoGenre.ACTION, 7920, 1988, new BigDecimal("8.2"), AgeRating.R, VideoType.MOVIE, false, admin.getUserId());
        videoentity hangover = saveVideo("The Hangover", "Three buddies wake up in Las Vegas with no memory of the night before.",
                VideoGenre.COMEDY, 6000, 2009, new BigDecimal("7.7"), AgeRating.R, VideoType.MOVIE, false, admin.getUserId());
        videoentity conjuring = saveVideo("The Conjuring", "Paranormal investigators help a family terrorized by a dark presence.",
                VideoGenre.HORROR, 6720, 2013, new BigDecimal("7.5"), AgeRating.R, VideoType.MOVIE, true, admin.getUserId());
        videoentity breakingBad = saveVideo("Breaking Bad", "A chemistry teacher turns to manufacturing methamphetamine.",
                VideoGenre.DRAMA, 2820, 2008, new BigDecimal("9.5"), AgeRating.R, VideoType.TV_SHOW, true, admin.getUserId());
        videoentity planetEarth = saveVideo("Planet Earth II", "Wildlife documentary exploring habitats around the world.",
                VideoGenre.DOCUMENTARY, 3000, 2016, new BigDecimal("9.5"), AgeRating.G, VideoType.DOCUMENTARY, false, admin.getUserId());
        videoentity strangerThings = saveVideo("Stranger Things", "A group confronts terrifying supernatural forces.",
                VideoGenre.SCI_FI, 3000, 2016, new BigDecimal("8.7"), AgeRating.PG13, VideoType.TV_SHOW, true, admin.getUserId());

        subscriptionRepository.save(subscription.builder().userId(alice.getUserId())
                .plan(SubscriptionPlan.PREMIUM).startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30)).status(SubscriptionStatus.ACTIVE).build());
        subscriptionRepository.save(subscription.builder().userId(bob.getUserId())
                .plan(SubscriptionPlan.STANDARD).startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30)).status(SubscriptionStatus.ACTIVE).build());

        watchlistRepository.save(watchlist.builder().userId(alice.getUserId()).videoId(matrix.getVideoId()).build());
        watchlistRepository.save(watchlist.builder().userId(alice.getUserId()).videoId(inception.getVideoId()).build());
        watchlistRepository.save(watchlist.builder().userId(bob.getUserId()).videoId(dieHard.getVideoId()).build());

        historyRepository.save(WatchHistory.builder().userId(alice.getUserId()).videoId(matrix.getVideoId())
                .progressSeconds(8160).completed(true).watchDate(Instant.now().minus(5, ChronoUnit.DAYS)).build());
        historyRepository.save(WatchHistory.builder().userId(alice.getUserId()).videoId(inception.getVideoId())
                .progressSeconds(4400).completed(false).watchDate(Instant.now().minus(3, ChronoUnit.DAYS)).build());

        paymentRepository.save(payment.builder().userId(alice.getUserId()).amount(new BigDecimal("15.99"))
                .paymentMethod(PaymentMethod.STRIPE).gatewayTxnId("txn_stripe_alice_001")
                .status(PaymentStatus.SUCCESS).build());
        paymentRepository.save(payment.builder().userId(bob.getUserId()).amount(new BigDecimal("12.99"))
                .paymentMethod(PaymentMethod.PAYPAL).gatewayTxnId("txn_paypal_bob_001")
                .status(PaymentStatus.SUCCESS).build());
    }

    private videoentity saveVideo(String title, String desc, VideoGenre genre, int duration, int year,
                                  BigDecimal rating, AgeRating age, VideoType type, boolean premium, Long createdBy) {
        String slug = title.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        return videoRepository.save(videoentity.builder()
                .title(title).description(desc).genre(genre).durationSeconds(duration)
                .releaseYear(year).rating(rating).ageRating(age)
                .thumbnailUrl("/assets/thumbnails/default.svg")
                .videoUrl(SAMPLE_STREAM).type(type).isPremium(premium).createdBy(createdBy).build());
    }
}
