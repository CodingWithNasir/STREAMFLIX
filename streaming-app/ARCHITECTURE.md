# ════════════════════════════════════════════════════════════
# STREAMFLIX — Enterprise Architecture Document
# Java 17 · Spring Boot 3 · MySQL 8 · All 23 GoF Patterns
# ════════════════════════════════════════════════════════════

---

## 1. COMPONENT DIAGRAM (High-Level Architecture)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          STREAMFLIX PLATFORM                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────────────────┐   │
│  │   Frontend    │   │   Frontend    │   │      Frontend             │   │
│  │   (HTML5)     │──▶│   (CSS3/BEM) │──▶│      (Vanilla JS ES2022) │   │
│  └──────┬───────┘   └──────────────┘   └────────────┬─────────────┘   │
│         │              Static Assets                 │                  │
│         ▼                                           ▼                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    streaming-web (Spring MVC)                   │   │
│  │  ┌────────────┐ ┌─────────────┐ ┌──────────────┐ ┌──────────┐ │   │
│  │  │ Controllers│ │   Security  │ │  Interceptors│ │   JWT    │ │   │
│  │  │  (REST)    │ │ Config      │ │ (Chain of    │ │  Filter  │ │   │
│  │  │            │ │             │ │  Responsibility)│ │          │ │   │
│  │  └─────┬──────┘ └──────┬──────┘ └──────┬───────┘ └────┬─────┘ │   │
│  │        │               │               │              │         │   │
│  │  ┌─────▼───────────────▼───────────────▼──────────────▼─────┐  │   │
│  │  │                    Service Layer                          │  │   │
│  │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐  │  │   │
│  │  │  │ Auth     │ │ Video    │ │ Subscript│ │ Payment    │  │  │   │
│  │  │  │ Service  │ │ Service  │ │ ion Svc  │ │ Service    │  │  │   │
│  │  │  └──────────┘ └──────────┘ └──────────┘ └────────────┘  │  │   │
│  │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐  │  │   │
│  │  │  │ Recommend│ │ Notific- │ │ Search   │ │ Chat       │  │  │   │
│  │  │  │ Service  │ │ ationSvc │ │ Service  │ │ Service    │  │  │   │
│  │  │  └──────────┘ └──────────┘ └──────────┘ └────────────┘  │  │   │
│  │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐                 │  │   │
│  │  │  │ Streaming│ │ Player   │ │ Catalog  │                 │  │   │
│  │  │  │ Facade   │ │ Service  │ │ Analytics│                 │  │   │
│  │  │  └──────────┘ └──────────┘ └──────────┘                 │  │   │
│  │  └──────────────────────┬───────────────────────────────────┘  │   │
│  └─────────────────────────┼──────────────────────────────────────┘   │
│                            │                                          │
│  ┌─────────────────────────▼──────────────────────────────────────┐   │
│  │                streaming-persistence (JPA/Hibernate)            │   │
│  │  ┌────────────┐ ┌─────────────┐ ┌──────────────────────────┐  │   │
│  │  │ Repositories│ │   Entities  │ │   Flyway Migrations      │  │   │
│  │  └────────────┘ └─────────────┘ └──────────────────────────┘  │   │
│  └─────────────────────────┬──────────────────────────────────────┘   │
│                            │                                          │
│  ┌─────────────────────────▼──────────────────────────────────────┐   │
│  │                    streaming-domain                             │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │   │
│  │  │ Models   │ │ Patterns │ │  DTOs    │ │     Enums        │  │   │
│  │  │(JPA Ent.)│ │(23 GoF)  │ │          │ │                  │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────────────┘  │   │
│  └────────────────────────────────────────────────────────────────┘   │
│                            │                                          │
│                            ▼                                          │
│  ┌────────────────────────────────────────────────────────────────┐   │
│  │                      MySQL 8 Database                          │   │
│  │  ┌──────┐ ┌────────┐ ┌──────────┐ ┌──────┐ ┌──────┐ ┌──────┐│   │
│  │  │users │ │videos  │ │watchlist │ │watch │ │subscr│ │paymnt││   │
│  │  │      │ │        │ │          │ │_hist │ │iptions│ │      ││   │
│  │  └──────┘ └────────┘ └──────────┘ └──────┘ └──────┘ └──────┘│   │
│  │  ┌──────────────────────┐  ┌──────────────────────────────┐   │   │
│  │  │ vw_active_subscribers│  │ sp_expire_subscriptions      │   │   │
│  │  └──────────────────────┘  └──────────────────────────────┘   │   │
│  └────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 2. ER DIAGRAM (Database Schema)

```
┌─────────────────────┐         ┌─────────────────────────────┐
│       users          │         │          videos              │
├─────────────────────┤         ├─────────────────────────────┤
│ PK user_id BIGINT   │◀───┐   │ PK video_id BIGINT          │
│    name VARCHAR(255) │    │   │    title VARCHAR(255)       │
│    email VARCHAR(255)│    │   │    description TEXT         │
│    password_hash     │    │   │    genre ENUM               │
│    profile_image_url │    │   │    duration_seconds INT     │
│    role ENUM         │    │   │    release_year SMALLINT    │
│    created_at TS     │    │   │    rating DECIMAL(3,1)      │
│    updated_at TS     │    │   │    age_rating ENUM          │
└──────────┬──────────┘    │   │    thumbnail_url VARCHAR    │
           │               │   │    video_url VARCHAR         │
           │               │   │    type ENUM                 │
           │               │   │    created_by BIGINT ────────┤
           │               │   │    is_premium BOOLEAN        │
           │               │   │    created_at TIMESTAMP      │
           │               │   └──────────┬──────────────────┘
           │               │              │
           │               │              │
    ┌──────┴───────┐  ┌────┴────────┐  ┌──┴──────────────┐
    │  watchlist    │  │watch_history│  │   subscriptions  │
    ├──────────────┤  ├─────────────┤  ├─────────────────┤
    │PK watchlist_id│  │PK history_id│  │PK subscription_id│
    │FK user_id ────┤  │FK user_id ──┤  │FK user_id UNIQUE │
    │FK video_id ───┤  │FK video_id ─┤  │    plan ENUM     │
    │   added_at    │  │   progress  │  │    start_date    │
    │UNIQUE(usr,vid)│  │   completed │  │    end_date      │
    └──────────────┘  │   watch_date│  │    status ENUM   │
                      └─────────────┘  └────────┬────────┘
                                                 │
                                        ┌────────▼────────┐
                                        │    payments      │
                                        ├─────────────────┤
                                        │PK payment_id    │
                                        │FK user_id ──────┤
                                        │    amount       │
                                        │    currency     │
                                        │    payment_method│
                                        │    gateway_txn_id│
                                        │    payment_date │
                                        │    status ENUM  │
                                        └─────────────────┘

Relationships:
  users 1────* watchlist
  videos 1────* watchlist
  users 1────* watch_history
  videos 1────* watch_history
  users 1────1 subscriptions
  users 1────* payments
  users 1────* videos (created_by)
```

---

## 3. SEQUENCE DIAGRAM — watchVideo(userId, videoId)

```
 Client          StreamingFacade      AuthService      SubscriptionSvc    VideoService       VideoAccessProxy    PremiumVideo
   │                  │                    │                 │                │                    │                │
   │  watchVideo()    │                    │                 │                │                    │                │
   │─────────────────▶│                    │                 │                │                    │                │
   │                  │                    │                 │                │                    │                │
   │                  │  authenticate()    │                 │                │                    │                │
   │                  │───────────────────▶│                 │                │                    │                │
   │                  │                    │                 │                │                    │                │
   │                  │    userDTO         │                 │                │                    │                │
   │                  │◀───────────────────│                 │                │                    │                │
   │                  │                    │                 │                │                    │                │
   │                  │  checkSubscription()                 │                │                    │                │
   │                  │────────────────────────────────────▶│                │                    │                │
   │                  │                    │                 │                │                    │                │
   │                  │  subscriptionDTO   │                 │                │                    │                │
   │                  │◀────────────────────────────────────│                │                    │                │
   │                  │                    │                 │                │                    │                │
   │                  │  accessVideo(videoId)                             │                    │                │
   │                  │───────────────────────────────────────────────────▶│                    │                │
   │                  │                    │                 │                │                    │                │
   │                  │                    │                 │                │  checkAccess(sub)  │                │
   │                  │                    │                 │                │───────────────────▶│                │
   │                  │                    │                 │                │                    │                │
   │                  │                    │                 │                │                    │  getStream()    │
   │                  │                    │                 │                │                    │───────────────▶│
   │                  │                    │                 │                │                    │                │
   │                  │                    │                 │                │                    │  streamData     │
   │                  │                    │                 │                │                    │◀───────────────│
   │                  │                    │                 │                │  videoDTO          │                │
   │                  │                    │                 │                │◀───────────────────│                │
   │                  │                    │                 │  videoDTO      │                    │                │
   │                  │◀───────────────────────────────────────────────────│                    │                │
   │  videoDTO        │                    │                 │                │                    │                │
   │◀─────────────────│                    │                 │                │                    │                │
```

---

## 4. UML CLASS DIAGRAMS — All 23 GoF Patterns

### ════════════════════════════════════════════════════════════
### CREATIONAL PATTERNS (5)
### ════════════════════════════════════════════════════════════

---

### Pattern 1: SINGLETON — DatabaseConnectionManager & AppConfigManager

```
┌──────────────────────────────────────────────┐
│       DatabaseConnectionManager              │
├──────────────────────────────────────────────┤
│ - static DatabaseConnectionManager instance  │
│ - static Object lock                         │
│ - Connection connection                      │
│ - String url, username, password             │
├──────────────────────────────────────────────┤
│ - DatabaseConnectionManager()                │
│ + static getInstance(): DatabaseConnectionMgr│
│ + getConnection(): Connection                │
│ + closeConnection(): void                    │
│ + executeQuery(sql): ResultSet               │
└──────────────────────────────────────────────┘
         △
         │ uses double-checked locking
         │
┌──────────────────────────────────────────────┐
│        AppConfigManager                      │
├──────────────────────────────────────────────┤
│ - static AppConfigManager instance           │
│ - static final Object lock                  │
│ - Properties properties                     │
├──────────────────────────────────────────────┤
│ - AppConfigManager()                         │
│ + static getInstance(): AppConfigManager     │
│ + getProperty(key): String                   │
│ + getIntProperty(key): int                   │
│ + getBooleanProperty(key): boolean           │
│ + setProperty(key, value): void              │
└──────────────────────────────────────────────┘
```

---

### Pattern 2: FACTORY METHOD — VideoFactory

```
┌───────────────────────────────────────┐
│         «abstract» Video              │
├───────────────────────────────────────┤
│ # title: String                       │
│ # description: String                 │
│ # genre: Genre                        │
│ # durationSeconds: int                │
├───────────────────────────────────────┤
│ + getTitle(): String                  │
│ + getDescription(): String            │
│ + getGenre(): Genre                   │
│ + getDurationSeconds(): int           │
└───────────┬───────────────────────────┘
            △
     ┌──────┴──────┐
     │             │
┌────▼──────┐ ┌───▼──────────┐ ┌──────────────────┐
│   Movie   │ │   TvShow     │ │  Documentary     │
├───────────┤ ├──────────────┤ ├──────────────────┤
│ director  │ │ seasonCount  │ │ narrator         │
│ actors    │ │ episodeCount │ │ subject          │
├───────────┤ ├──────────────┤ ├──────────────────┤
│ +getType() │ │ +getType()   │ │ +getType()       │
│ +getDir()  │ │ +getSeasons()│ │ +getNarrator()   │
└───────────┘ └──────────────┘ └──────────────────┘

┌──────────────────────────────────────┐
│         VideoFactory                 │
├──────────────────────────────────────┤
├──────────────────────────────────────┤
│ + createVideo(type, params): Video   │
└──────────┬───────────────────────────┘
           │ creates
           ▼
     (Movie | TvShow | Documentary)
```

---

### Pattern 3: ABSTRACT FACTORY — UIComponentFactory

```
┌─────────────────────────────────────┐
│     «interface» UIComponentFactory  │
├─────────────────────────────────────┤
│ + createButton(): UIButton           │
│ + createCard(): UICard               │
│ + createNavbar(): UINavbar           │
└──────────┬──────────────────────────┘
      ┌────┴─────────────┐
      │                  │
┌─────▼──────────┐ ┌────▼─────────────┐
│ WebUIFactory   │ │ MobileUIFactory   │
├────────────────┤ ├──────────────────┤
│                │ │                  │
└─────┬──────────┘ └────┬─────────────┘
      │                  │
      ▼                  ▼
┌───────────────┐  ┌───────────────┐
│  WebButton    │  │ MobileButton   │
│  WebCard      │  │ MobileCard     │
│  WebNavbar    │  │ MobileNavbar   │
└───────────────┘  └───────────────┘

┌──────────────────┐  ┌──────────────────┐
│ «interface» UIButton │  │ «interface» UICard   │
├──────────────────┤  ├──────────────────┤
│ + render(): String│  │ + render(): String│
│ + onClick(): void │  │ + getContent(): ..│
└──────────────────┘  └──────────────────┘

┌──────────────────┐
│ «interface» UINavbar│
├──────────────────┤
│ + render(): String│
│ + navigate(url)   │
└──────────────────┘
```

---

### Pattern 4: BUILDER — VideoBuilder

```
┌────────────────────────────────────────────────┐
│               VideoBuilder                     │
├────────────────────────────────────────────────┤
│ - title: String                                │
│ - description: String                          │
│ - genre: Genre                                 │
│ - durationSeconds: int                         │
│ - releaseYear: int                             │
│ - rating: double                               │
│ - ageRating: AgeRating                         │
│ - thumbnailUrl: String                         │
│ - videoUrl: String                             │
│ - type: VideoType                              │
│ - isPremium: boolean                           │
├────────────────────────────────────────────────┤
│ + VideoBuilder(title: String)                  │
│ + withDescription(desc: String): VideoBuilder  │
│ + withGenre(genre: Genre): VideoBuilder        │
│ + withDuration(secs: int): VideoBuilder        │
│ + withReleaseYear(year: int): VideoBuilder     │
│ + withRating(rating: double): VideoBuilder     │
│ + withAgeRating(rating: AgeRating): VideoBuilder│
│ + withThumbnail(url: String): VideoBuilder     │
│ + withVideoUrl(url: String): VideoBuilder      │
│ + withType(type: VideoType): VideoBuilder      │
│ + withPremium(premium: boolean): VideoBuilder  │
│ + build(): Video                               │
└────────────────────────────────────────────────┘
        │ creates via build()
        ▼
┌─────────────────────────────────────┐
│            Video (Model)            │
├─────────────────────────────────────┤
│ + all fields from builder           │
└─────────────────────────────────────┘
```

---

### Pattern 5: PROTOTYPE — VideoPrototypeRegistry

```
┌─────────────────────────────────────────────────┐
│         VideoPrototypeRegistry                  │
├─────────────────────────────────────────────────┤
│ - prototypes: Map<String, Video>                │
├─────────────────────────────────────────────────┤
│ - VideoPrototypeRegistry()                      │
│ + static getInstance(): VideoPrototypeRegistry  │
│ + registerPrototype(key, video): void           │
│ + getPrototype(key): Video                      │
│ + clonePrototype(key): Video                    │
│ + getAllPrototypes(): Map<String, Video>        │
└─────────────────────────────────────────────────┘
          │ clones
          ▼
┌─────────────────────────────────────┐
│         Video (Cloneable)           │
├─────────────────────────────────────┤
│ + clone(): Video (deep copy)        │
│ + all model fields                  │
└─────────────────────────────────────┘
```

---

### ════════════════════════════════════════════════════════════
### STRUCTURAL PATTERNS (7)
### ════════════════════════════════════════════════════════════

---

### Pattern 6: ADAPTER — PaymentGateway

```
┌──────────────────────────────────┐
│   «interface» PaymentGateway     │
├──────────────────────────────────┤
│ + charge(amount, card): Result   │
│ + refund(txnId): Result          │
└────────────┬─────────────────────┘
      ┌──────┴──────────┐
      │                 │
┌─────▼──────────┐ ┌───▼─────────────┐
│ PayPalAdapter   │ │  StripeAdapter   │
├─────────────────┤ ├─────────────────┤
│ - paypalClient  │ │ - stripeClient   │
├─────────────────┤ ├─────────────────┤
│ + charge()      │ │ + charge()       │
│ + refund()      │ │ + refund()       │
└────────┬────────┘ └────────┬────────┘
         │ wraps             │ wraps
         ▼                   ▼
┌─────────────────┐ ┌─────────────────┐
│ PayPalHttpClient│ │ Stripe SDK      │
│ (3rd party)     │ │ (3rd party)     │
└─────────────────┘ └─────────────────┘
```

---

### Pattern 7: BRIDGE — StreamingService & VideoQualityImpl

```
┌──────────────────────────────────────┐
│   «abstract» StreamingService       │
│   (Abstraction)                     │
├──────────────────────────────────────┤
│ # qualityImpl: VideoQualityImpl     │
├──────────────────────────────────────┤
│ + StreamingService(quality)          │
│ + abstract stream(url): StreamResult │
│ + abstract getQuality(): String      │
└────────────┬─────────────────────────┘
             │ implemented by
      ┌──────┴──────────┐
      │                 │
┌─────▼──────────┐ ┌───▼────────────┐
│ MovieStream    │ │ TvShowStream    │
├────────────────┤ ├────────────────┤
│ + stream()     │ │ + stream()      │
│ + getQuality() │ │ + getQuality()  │
└────────────────┘ └────────────────┘

┌──────────────────────────────────────┐
│   «interface» VideoQualityImpl      │
│   (Implementor)                     │
├──────────────────────────────────────┤
│ + encode(data): byte[]              │
│ + getResolution(): String           │
│ + getBitrate(): int                 │
└────────────┬─────────────────────────┘
      ┌──────┴──────────┐
      │       │         │
┌─────▼───┐ ┌▼──────┐ ┌▼──────┐
│ SDImpl  │ │HDImpl │ │FourKImpl│
├─────────┤ ├───────┤ ├───────┤
│480p/1Mbps│ │1080p  │ │2160p  │
│         │ │5Mbps  │ │20Mbps │
└─────────┘ └───────┘ └───────┘
```

---

### Pattern 8: COMPOSITE — ContentComponent

```
┌──────────────────────────────────────┐
│   «abstract» ContentComponent       │
├──────────────────────────────────────┤
│ # name: String                       │
├──────────────────────────────────────┤
│ + getName(): String                  │
│ + abstract getSize(): long           │
│ + abstract getWatchTime(): int       │
│ + add(component): void               │
│ + remove(component): void            │
│ + getChild(index): ContentComponent  │
└────────────┬─────────────────────────┘
      ┌──────┴──────────┐
      │                 │
┌─────▼──────────┐ ┌───▼──────────────┐
│ CategoryComposite│ │   VideoLeaf      │
│  (Composite)    │ │   (Leaf)         │
├─────────────────┤ ├─────────────────┤
│ children: List  │ │ size: long      │
│                 │ │ watchTime: int  │
├─────────────────┤ ├─────────────────┤
│ + add()         │ │ + getSize()     │
│ + remove()      │ │ + getWatchTime()│
│ + getChild()    │ │                 │
│ + getSize()     │ │                 │
│ + getWatchTime()│ │                 │
└─────────────────┘ └─────────────────┘
```

---

### Pattern 9: DECORATOR — VideoStream

```
┌──────────────────────────────────────┐
│   «interface» VideoStream            │
├──────────────────────────────────────┤
│ + play(): StreamData                 │
│ + getMetadata(): StreamMetadata      │
└────────────┬─────────────────────────┘
      ┌──────┴──────────┐
      │                 │
┌─────▼──────────┐ ┌───▼────────────────┐
│  BasicStream   │ │ StreamDecorator     │
│  (Concrete)    │ │  (abstract)         │
├────────────────┤ ├────────────────────┤
│ - videoUrl     │ │ # wrapped: VideoStream│
├────────────────┤ ├────────────────────┤
│ + play()       │ │ + play(): StreamData │
│ + getMetadata()│ │ + getMetadata()      │
└────────────────┘ └────────┬───────────┘
                     ┌──────┴──────────────────┐
              ┌──────┴────────┐    ┌───────────┴──────┐
              │               │    │                   │
   ┌──────────▼─────┐ ┌──────▼────────┐ ┌───────────▼─────┐
   │SubtitleDecorator│ │DRMDecorator   │ │ AdDecorator     │
   ├─────────────────┤ ├───────────────┤ ├─────────────────┤
   │ - language      │ │ - licenseKey  │ │ - adQueue       │
   ├─────────────────┤ ├───────────────┤ ├─────────────────┤
   │ + play()        │ │ + play()      │ │ + play()        │
   │ + getMetadata() │ │ + getMetadata()│ │ + getMetadata() │
   └─────────────────┘ └───────────────┘ └─────────────────┘
```

---

### Pattern 10: FACADE — StreamingFacade

```
┌──────────────────────────────────────────────────────────────┐
│                      StreamingFacade                         │
├──────────────────────────────────────────────────────────────┤
│ - authService: AuthService                                  │
│ - subscriptionService: SubscriptionService                  │
│ - videoService: VideoService                                │
│ - notificationService: NotificationService                  │
│ - watchHistoryService: WatchHistoryService                  │
├──────────────────────────────────────────────────────────────┤
│ + watchVideo(userId, videoId): VideoDTO                     │
│ + addToWatchlist(userId, videoId): void                     │
│ + removeFromWatchlist(userId, videoId): void                │
│ + browseCatalog(genre, page): List<VideoDTO>                │
│ + getRecommendations(userId): List<VideoDTO>                │
│ + startSubscription(userId, plan): SubscriptionDTO          │
└──────────┬───────────────────────────────────────────────────┘
           │ coordinates
     ┌─────┼──────┬──────────────┬────────────────┐
     ▼     ▼      ▼              ▼                ▼
┌────────┐┌──────┐┌────────────┐┌──────────────┐┌──────────┐
│  Auth  ││Subscr││  Video     ││Notification  ││ WatchHist│
│ Service││Svc   ││  Service   ││  Service     ││ Service  │
└────────┘└──────┘└────────────┘└──────────────┘└──────────┘
```

---

### Pattern 11: FLYWEIGHT — ThumbnailCache

```
┌──────────────────────────────────────────────────┐
│               ThumbnailCache                     │
│               (Flyweight Factory)                │
├──────────────────────────────────────────────────┤
│ - cache: Map<String, ThumbnailFlyweight>         │
├──────────────────────────────────────────────────┤
│ - ThumbnailCache()                               │
│ + static getInstance(): ThumbnailCache           │
│ + getThumbnail(url): ThumbnailFlyweight          │
│ + getCacheSize(): int                            │
│ + clearCache(): void                             │
└──────────────────────┬───────────────────────────┘
                       │ creates/manages
                       ▼
┌──────────────────────────────────────────────────┐
│          ThumbnailFlyweight                       │
│          (Flyweight — intrinsic state)           │
├──────────────────────────────────────────────────┤
│ - imageData: byte[]                              │
│ - format: String                                 │
│ - width: int                                     │
│ - height: int                                    │
├──────────────────────────────────────────────────┤
│ + getImageData(): byte[]                         │
│ + getFormat(): String                            │
│ + getDimensions(): int[]                         │
└──────────────────────────────────────────────────┘
           │ used by
           ▼
┌──────────────────────────────────────────────────┐
│           Video (extrinsic state: videoId)       │
│  thumbnail: ThumbnailFlyweight                   │
│  thumbnailUrl: String                            │
└──────────────────────────────────────────────────┘
```

---

### Pattern 12: PROXY — VideoAccessProxy

```
┌──────────────────────────────────────┐
│   «interface» VideoAccess            │
├──────────────────────────────────────┤
│ + getStream(): StreamData            │
│ + getInfo(): VideoDTO               │
└────────────┬─────────────────────────┘
      ┌──────┴──────────┐
      │                 │
┌─────▼──────────┐ ┌───▼────────────┐
│VideoAccessProxy│ │ PremiumVideo    │
│  (Proxy)       │ │  (Real Subject) │
├────────────────┤ ├────────────────┤
│ - realVideo    │ │ - videoData    │
│ - subscription │ │ - streamUrl    │
│   Service      │ │                │
├────────────────┤ ├────────────────┤
│ + getStream()  │ │ + getStream()  │
│   (checks sub  │ │ + getInfo()    │
│    first)      │ │                │
│ + getInfo()    │ │                │
└────────────────┘ └────────────────┘
```

---

### ════════════════════════════════════════════════════════════
### BEHAVIORAL PATTERNS (11)
### ════════════════════════════════════════════════════════════

---

### Pattern 13: CHAIN OF RESPONSIBILITY — RequestHandler

```
┌──────────────────────────────────────────┐
│   «abstract» RequestHandler             │
├──────────────────────────────────────────┤
│ - next: RequestHandler                   │
├──────────────────────────────────────────┤
│ + setNext(handler): RequestHandler       │
│ + handle(request): Response              │
│ # abstract doHandle(request): Response   │
└────────────────┬─────────────────────────┘
                 │
    ┌────────────┼────────────┬──────────────────┐
    ▼            ▼            ▼                  ▼
┌──────────┐┌──────────┐┌──────────────┐┌────────────────┐
│ Auth     ││Subscript.││AgeRestriction││StreamingAccess │
│ Handler  ││Handler   ││Handler       ││Handler         │
├──────────┤├──────────┤├──────────────┤├────────────────┤
│+doHandle()││+doHandle()││+doHandle()  ││+doHandle()     │
└──────────┘└──────────┘└──────────────┘└────────────────┘
  Auth ─────▶ Subscr ─────▶ AgeRestr ─────▶ StreamAccess ─────▶ Response
```

---

### Pattern 14: COMMAND — VideoPlayerCommand

```
┌──────────────────────────────────────┐
│   «interface» VideoPlayerCommand     │
├──────────────────────────────────────┤
│ + execute(): void                    │
│ + undo(): void                       │
│ + getDescription(): String           │
└────────────┬─────────────────────────┘
      ┌──────┼──────────┐
      ▼      ▼          ▼
┌──────────┐┌────────┐┌──────────┐
│PlayCommand││PauseCmd││UndoCommand│
├──────────┤├────────┤├──────────┤
│-player   ││-player ││-invoker  │
│-video    ││-prevSt ││          │
├──────────┤├────────┤├──────────┤
│+execute()││+exec() ││+execute()│
│+undo()   ││+undo() ││+undo()   │
└──────────┘└────────┘└──────────┘

┌──────────────────────────────────────┐
│         UserActionInvoker            │
├──────────────────────────────────────┤
│ - history: Stack<VideoPlayerCommand> │
│ - undoneStack: Stack<...>            │
├──────────────────────────────────────┤
│ + executeCommand(cmd): void          │
│ + undoLastCommand(): void            │
│ + redoLastCommand(): void            │
│ + getHistory(): List<...>            │
└──────────────────────────────────────┘
```

---

### Pattern 15: INTERPRETER — SearchQueryInterpreter

```
┌──────────────────────────────────────┐
│   «interface» SearchExpression       │
├──────────────────────────────────────┤
│ + interpret(context): List<Video>    │
└────────────┬─────────────────────────┘
      ┌──────┼──────────┬─────────────┐
      ▼      ▼          ▼             ▼
┌──────────┐┌────────┐┌──────────┐┌──────────────┐
│GenreExpr ││YearExpr││AndExpr   ││LiteralExpr   │
├──────────┤├────────┤├──────────┤├──────────────┤
│-genre    ││-op     ││-left     ││-keyword      │
│          ││-year   ││-right    ││              │
├──────────┤├────────┤├──────────┤├──────────────┤
│+interpret││+intpr()││+interpret││+interpret()  │
└──────────┘└────────┘└──────────┘└──────────────┘

┌──────────────────────────────────────┐
│    SearchQueryInterpreter            │
│    (Context + Parser)                │
├──────────────────────────────────────┤
│ - input: String                      │
├──────────────────────────────────────┤
│ + parse(query): SearchExpression     │
│ + execute(query): List<Video>        │
│ - parseExpression(): SearchExpression│
└──────────────────────────────────────┘
```

---

### Pattern 16: ITERATOR — CatalogIterator

```
┌──────────────────────────────────────┐
│   «interface» CatalogIterator        │
├──────────────────────────────────────┤
│ + hasNext(): boolean                 │
│ + next(): ContentComponent           │
│ + reset(): void                      │
│ + getCurrent(): ContentComponent     │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│     CatalogTreeIterator              │
├──────────────────────────────────────┤
│ - stack: Deque<ContentComponent>     │
│ - current: ContentComponent          │
├──────────────────────────────────────┤
│ + CatalogTreeIterator(root)          │
│ + hasNext(): boolean                 │
│ + next(): ContentComponent           │
│ + reset(): void                      │
│ + getCurrent(): ContentComponent     │
└──────────────────────────────────────┘
           │ traverses
           ▼
┌──────────────────────────────────────┐
│   Catalog (implements Iterable)      │
├──────────────────────────────────────┤
│ - root: CategoryComposite            │
├──────────────────────────────────────┤
│ + iterator(): CatalogIterator        │
│ + search(query): CatalogIterator     │
└──────────────────────────────────────┘
```

---

### Pattern 17: MEDIATOR — SupportChatMediator

```
┌──────────────────────────────────────┐
│   «interface» ChatMediator           │
├──────────────────────────────────────┤
│ + sendMessage(msg, sender): void     │
│ + addUser(user): void                │
│ + addAgent(agent): void              │
│ + addBot(bot): void                  │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│     SupportChatMediator              │
├──────────────────────────────────────┤
│ - users: List<ChatUser>              │
│ - agents: List<ChatAgent>            │
│ - bots: List<ChatBot>                │
├──────────────────────────────────────┤
│ + sendMessage(msg, sender): void     │
│   (routes to appropriate recipient)  │
└──────────────────────────────────────┘
           │ mediates between
     ┌─────┼──────────┐
     ▼     ▼          ▼
┌────────┐┌────────┐┌────────┐
│  User  ││ Agent  ││ ChatBot│
├────────┤├────────┤├────────┤
│-mediatr││-mediatr││-mediatr│
│-name   ││-name   ││-name   │
├────────┤├────────┤├────────┤
│+send() ││+send() ││+send() │
│+receive││+receive││+receive│
└────────┘└────────┘└────────┘
```

---

### Pattern 18: MEMENTO — PlayerStateMemento

```
┌──────────────────────────────────────┐
│          VideoPlayer                 │
│          (Originator)                │
├──────────────────────────────────────┤
│ - currentTimestamp: int              │
│ - volume: int                        │
│ - quality: String                    │
│ - isPlaying: boolean                 │
├──────────────────────────────────────┤
│ + save(): PlayerStateMemento         │
│ + restore(memento): void             │
│ + getState(): PlayerStateMemento     │
│ + play(), pause(), seek()            │
└────────────┬─────────────────────────┘
             │ saves/restores
             ▼
┌──────────────────────────────────────┐
│      PlayerStateMemento              │
│      (Memento)                       │
├──────────────────────────────────────┤
│ - timestamp: int                     │
│ - volume: int                        │
│ - quality: String                    │
│ - isPlaying: boolean                 │
├──────────────────────────────────────┤
│ + getTimestamp(): int                │
│ + getVolume(): int                   │
│ + getQuality(): String              │
│ + isPlaying(): boolean              │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│      PlayerStateCaretaker            │
│      (Caretaker)                     │
├──────────────────────────────────────┤
│ - mementos: Deque<PlayerStateMemento>│
├──────────────────────────────────────┤
│ + pushMemento(m): void               │
│ + popMemento(): PlayerStateMemento   │
│ + peekMemento(): PlayerStateMemento  │
│ + hasHistory(): boolean              │
└──────────────────────────────────────┘
```

---

### Pattern 19: OBSERVER — NotificationService

```
┌──────────────────────────────────────┐
│   «interface» Observer               │
├──────────────────────────────────────┤
│ + update(event): void                │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│     UserNotificationObserver         │
├──────────────────────────────────────┤
│ - userId: Long                       │
│ - emailService: EmailService         │
├──────────────────────────────────────┤
│ + update(event): void                │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│   «interface» Subject                │
├──────────────────────────────────────┤
│ + subscribe(observer): void          │
│ + unsubscribe(observer): void        │
│ + notifyObservers(): void            │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│     VideoReleaseSubject              │
├──────────────────────────────────────┤
│ - observers: List<Observer>          │
│ - latestRelease: VideoDTO            │
├──────────────────────────────────────┤
│ + subscribe(obs): void               │
│ + unsubscribe(obs): void             │
│ + notifyObservers(): void            │
│ + releaseVideo(video): void          │
└──────────────────────────────────────┘
```

---

### Pattern 20: STATE — SubscriptionState

```
┌──────────────────────────────────────┐
│   «interface» SubscriptionState      │
├──────────────────────────────────────┤
│ + accessContent(): boolean           │
│ + renew(): void                      │
│ + cancel(): void                     │
│ + getStatus(): String                │
└────────────┬─────────────────────────┘
      ┌──────┼──────────┐
      ▼      ▼          ▼
┌──────────┐┌──────────┐┌──────────────┐
│ActiveState││ExpiredSt ││CancelledState│
├──────────┤├──────────┤├──────────────┤
│+access() ││+access() ││+access()     │
│ → true   ││ → false  ││ → false      │
│+renew()  ││+renew()  ││+renew()      │
│→ success ││→ success ││ → fail       │
│+cancel() ││+cancel() ││+cancel()     │
│→ success ││→ fail    ││ → fail       │
└──────────┘└──────────┘└──────────────┘

┌──────────────────────────────────────┐
│        Subscription                   │
│     (Context — owns state)           │
├──────────────────────────────────────┤
│ - state: SubscriptionState           │
│ - user: User                         │
├──────────────────────────────────────┤
│ + setState(state): void              │
│ + getState(): SubscriptionState      │
│ + accessContent(): boolean           │
│ + renew(): void                      │
│ + cancel(): void                     │
└──────────────────────────────────────┘
```

---

### Pattern 21: STRATEGY — RecommendationStrategy

```
┌──────────────────────────────────────────────┐
│   «interface» RecommendationStrategy         │
├──────────────────────────────────────────────┤
│ + getRecommendations(userId, limit): List    │
│ + getName(): String                          │
└────────────┬─────────────────────────────────┘
      ┌──────┼──────────┐
      ▼      ▼          ▼
┌──────────────────┐┌────────────────┐┌────────────────┐
│CollaborativeFilter││ContentBased    ││TrendingStrategy│
│    ingStrategy   ││Strategy        ││                │
├──────────────────┤├────────────────┤├────────────────┤
│-userHistoryRepo  ││-videoRepo      ││-watchHistoryRepo│
│-ratingRepo       ││-genreWeight    ││-timeWindow      │
├──────────────────┤├────────────────┤├────────────────┤
│+getRecommendations││+getRecommend()││+getRecommend() │
│+getName()        ││+getName()      ││+getName()       │
└──────────────────┘└────────────────┘└────────────────┘

┌──────────────────────────────────────────────┐
│        RecommendationEngine                   │
├──────────────────────────────────────────────┤
│ - strategy: RecommendationStrategy           │
├──────────────────────────────────────────────┤
│ + setStrategy(strategy): void                │
│ + getRecommendations(userId, limit): List    │
│ + getActiveStrategy(): String                │
└──────────────────────────────────────────────┘
```

---

### Pattern 22: TEMPLATE METHOD — VideoUploadProcess

```
┌──────────────────────────────────────────────┐
│   «abstract» VideoUploadProcess              │
├──────────────────────────────────────────────┤
├──────────────────────────────────────────────┤
│ + upload(file): UploadResult   [template]    │
│ # abstract validate(file): boolean            │
│ # abstract transcode(file): byte[]            │
│ # abstract generateThumbnail(file): byte[]    │
│ # abstract save(data, meta): Video            │
│ # hook onPreProcess(file): void               │
│ # hook onPostProcess(video): void             │
└────────────┬─────────────────────────────────┘
      ┌──────┴──────────┐
      │                 │
┌─────▼──────────┐ ┌───▼─────────────────┐
│ MovieUpload    │ │ DocumentaryUpload    │
│ Process        │ │ Process              │
├────────────────┤ ├─────────────────────┤
│+validate()     │ │+validate()           │
│+transcode()    │ │+transcode()          │
│+genThumbnail() │ │+genThumbnail()       │
│+save()         │ │+save()               │
│#onPostProcess()│ │#onPreProcess()       │
└────────────────┘ └─────────────────────┘
```

---

### Pattern 23: VISITOR — CatalogAnalyticsVisitor

```
┌──────────────────────────────────────┐
│   «interface» CatalogVisitor         │
├──────────────────────────────────────┤
│ + visitCategory(composite): void     │
│ + visitVideo(leaf): void             │
│ + getResult(): AnalyticsResult       │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│  CatalogAnalyticsVisitor             │
├──────────────────────────────────────┤
│ - totalWatchTime: int                │
│ - totalRevenue: double               │
│ - videoCount: int                    │
│ - categoryStats: Map<String, Stats>  │
├──────────────────────────────────────┤
│ + visitCategory(c): void             │
│ + visitVideo(v): void                │
│ + getResult(): AnalyticsResult       │
└──────────────────────────────────────┘
           │ visits
     ┌─────┴──────────┐
     ▼                ▼
┌──────────────┐ ┌──────────────┐
│CategoryComp. │ │  VideoLeaf   │
│+accept(v)    │ │ +accept(v)   │
└──────────────┘ └──────────────┘

┌──────────────────────────────────────┐
│   «interface» Visitable              │
├──────────────────────────────────────┤
│ + accept(visitor: CatalogVisitor)    │
└──────────────────────────────────────┘
```

---

## 5. PATTERN TRACEABILITY MATRIX

| # | Pattern | Category | Classes / Interfaces | Package | Purpose in App |
|---|---------|----------|---------------------|---------|---------------|
| 1 | Singleton | Creational | `DatabaseConnectionManager`, `AppConfigManager`, `ThumbnailCache` | `com.streaming.domain.patterns.singleton` | Thread-safe DB connection pooling & global config |
| 2 | Factory Method | Creational | `VideoFactory`, `Movie`, `TvShow`, `Documentary` | `com.streaming.domain.patterns.factorymethod` | Creates video subtypes from type discriminator |
| 3 | Abstract Factory | Creational | `UIComponentFactory`, `WebUIFactory`, `MobileUIFactory`, `UIButton/UICard/UINavbar` | `com.streaming.domain.patterns.abstractfactory` | Creates platform-specific UI component families |
| 4 | Builder | Creational | `VideoBuilder` (inner static class) | `com.streaming.domain.patterns.builder` | Fluent API with mandatory field validation |
| 5 | Prototype | Creational | `VideoPrototypeRegistry`, `Cloneable` Video | `com.streaming.domain.patterns.prototype` | Deep-clone video configs for rapid seeding |
| 6 | Adapter | Structural | `PaymentGateway`, `PayPalAdapter`, `StripeAdapter` | `com.streaming.domain.patterns.adapter` | Unified payment interface wrapping PayPal & Stripe SDKs |
| 7 | Bridge | Structural | `StreamingService` (Abstraction), `VideoQualityImpl` (Implementor: SD/HD/4K) | `com.streaming.domain.patterns.bridge` | Decouples streaming logic from quality encoding |
| 8 | Composite | Structural | `ContentComponent`, `CategoryComposite`, `VideoLeaf` | `com.streaming.domain.patterns.composite` | Tree-structured catalog of categories & videos |
| 9 | Decorator | Structural | `VideoStream`, `BasicStream`, `SubtitleDecorator`, `DRMDecorator`, `AdDecorator` | `com.streaming.domain.patterns.decorator` | Dynamically add subtitles, DRM, ads to streams |
| 10 | Facade | Structural | `StreamingFacade` | `com.streaming.domain.patterns.facade` | Simplified `watchVideo()` coordinating 4+ services |
| 11 | Flyweight | Structural | `ThumbnailCache`, `ThumbnailFlyweight` | `com.streaming.domain.patterns.flyweight` | Shared thumbnail image bytes across video instances |
| 12 | Proxy | Structural | `VideoAccessProxy`, `PremiumVideo`, `VideoAccess` | `com.streaming.domain.patterns.proxy` | Access control for premium content |
| 13 | Chain of Responsibility | Behavioral | `RequestHandler`, `AuthHandler`, `SubscriptionHandler`, `AgeRestrictionHandler`, `StreamingAccessHandler` | `com.streaming.domain.patterns.chain` | Spring interceptor chain: Auth→Sub→Age→Stream |
| 14 | Command | Behavioral | `VideoPlayerCommand`, `PlayCommand`, `PauseCommand`, `UndoCommand`, `UserActionInvoker` | `com.streaming.domain.patterns.command` | Player actions with undo/redo history |
| 15 | Interpreter | Behavioral | `SearchExpression`, `GenreExpr`, `YearExpr`, `AndExpr`, `LiteralExpr`, `SearchQueryInterpreter` | `com.streaming.domain.patterns.interpreter` | Parse "genre:action year:>2020" into executable AST |
| 16 | Iterator | Behavioral | `CatalogIterator`, `CatalogTreeIterator`, `Catalog` | `com.streaming.domain.patterns.iterator` | Uniform traversal of composite catalog tree |
| 17 | Mediator | Behavioral | `ChatMediator`, `SupportChatMediator`, `ChatUser`, `ChatAgent`, `ChatBot` | `com.streaming.domain.patterns.mediator` | Support chat without direct component coupling |
| 18 | Memento | Behavioral | `PlayerStateMemento`, `PlayerStateCaretaker`, `VideoPlayer` | `com.streaming.domain.patterns.memento` | Save/restore player state for session resumption |
| 19 | Observer | Behavioral | `Observer`, `Subject`, `VideoReleaseSubject`, `UserNotificationObserver` | `com.streaming.domain.patterns.observer` | Notify users when new episodes drop |
| 20 | State | Behavioral | `SubscriptionState`, `ActiveState`, `ExpiredState`, `CancelledState`, `Subscription` | `com.streaming.domain.patterns.state` | State-specific subscription behavior |
| 21 | Strategy | Behavioral | `RecommendationStrategy`, `CollaborativeFilteringStrategy`, `ContentBasedStrategy`, `TrendingStrategy`, `RecommendationEngine` | `com.streaming.domain.patterns.strategy` | Swappable recommendation algorithms |
| 22 | Template Method | Behavioral | `VideoUploadProcess`, `MovieUploadProcess`, `DocumentaryUploadProcess` | `com.streaming.domain.patterns.templatemethod` | Fixed upload steps with overridable sub-steps |
| 23 | Visitor | Behavioral | `CatalogVisitor`, `CatalogAnalyticsVisitor`, `Visitable` | `com.streaming.domain.patterns.visitor` | Analytics over composite tree without modifying components |

---

## 6. PACKAGE STRUCTURE

```
streaming-app/
├── pom.xml                          (parent POM)
├── schema.sql
├── seed-data.sql
├── ARCHITECTURE.md
│
├── streaming-domain/
│   ├── pom.xml
│   └── src/main/java/com/streaming/domain/
│       ├── model/
│       │   ├── User.java
│       │   ├── Video.java
│       │   ├── Watchlist.java
│       │   ├── WatchHistory.java
│       │   ├── Subscription.java
│       │   └── Payment.java
│       ├── dto/
│       │   ├── UserDTO.java
│       │   ├── VideoDTO.java
│       │   ├── SubscriptionDTO.java
│       │   ├── PaymentDTO.java
│       │   └── AnalyticsResult.java
│       ├── enums/
│       │   ├── Genre.java
│       │   ├── VideoType.java
│       │   ├── AgeRating.java
│       │   ├── UserRole.java
│       │   ├── SubscriptionPlan.java
│       │   ├── SubscriptionStatus.java
│       │   ├── PaymentMethod.java
│       │   └── PaymentStatus.java
│       └── patterns/
│           ├── singleton/
│           ├── factorymethod/
│           ├── abstractfactory/
│           ├── builder/
│           ├── prototype/
│           ├── adapter/
│           ├── bridge/
│           ├── composite/
│           ├── decorator/
│           ├── facade/
│           ├── flyweight/
│           ├── proxy/
│           ├── chain/
│           ├── command/
│           ├── interpreter/
│           ├── iterator/
│           ├── mediator/
│           ├── memento/
│           ├── observer/
│           ├── state/
│           ├── strategy/
│           ├── templatemethod/
│           └── visitor/
│
├── streaming-persistence/
│   ├── pom.xml
│   └── src/main/java/com/streaming/persistence/
│       ├── repository/
│       │   ├── UserRepository.java
│       │   ├── VideoRepository.java
│       │   ├── WatchlistRepository.java
│       │   ├── WatchHistoryRepository.java
│       │   ├── SubscriptionRepository.java
│       │   └── PaymentRepository.java
│       └── entity/
│           └── (JPA entities mapped from domain models)
│
└── streaming-web/
    ├── pom.xml
    └── src/main/
        ├── java/com/streaming/web/
        │   ├── StreamingApp.java
        │   ├── config/
        │   │   ├── SecurityConfig.java
        │   │   └── JwtConfig.java
        │   ├── controller/
        │   │   ├── AuthController.java
        │   │   ├── VideoController.java
        │   │   ├── UserController.java
        │   │   ├── SubscriptionController.java
        │   │   ├── PaymentController.java
        │   │   └── AdminController.java
        │   ├── service/
        │   │   ├── AuthService.java
        │   │   ├── VideoService.java
        │   │   ├── UserService.java
        │   │   ├── SubscriptionService.java
        │   │   ├── PaymentService.java
        │   │   ├── RecommendationService.java
        │   │   ├── NotificationService.java
        │   │   ├── SearchService.java
        │   │   ├── ChatService.java
        │   │   └── CatalogAnalyticsService.java
        │   └── security/
        │       ├── JwtTokenProvider.java
        │       ├── JwtAuthFilter.java
        │       └── UserDetailsServiceImpl.java
        └── resources/
            ├── application.yml
            └── static/
                ├── css/
                │   └── style.css
                ├── js/
                │   └── app.js
                └── html/
                    ├── index.html
                    ├── login.html
                    ├── register.html
                    ├── video.html
                    ├── subscription.html
                    ├── watchlist.html
                    ├── profile.html
                    └── admin.html
```

---

## 7. TECHNOLOGY VERSION MATRIX

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 LTS |
| Framework | Spring Boot | 3.2.4 |
| Security | Spring Security | 6.2.x |
| ORM | Hibernate (Spring Data JPA) | 3.2.x |
| Database | MySQL | 8.x |
| Auth | JWT (jjwt) | 0.12.5 |
| Payments | PayPal REST SDK | 2.0.0 |
| Payments | Stripe Java SDK | 24.22.0 |
| Build | Maven | 3.9+ |
| Frontend | HTML5/CSS3/JS ES2022 | - |
| DB Migration | Flyway | 9.x |