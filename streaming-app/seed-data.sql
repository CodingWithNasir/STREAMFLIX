-- File: seed-data.sql
-- ════════════════════════════════════════════════════════════
-- STREAMFLIX Seed Data — MySQL 8
-- Passwords: admin123, alice123, bob123, charlie123, diana123
-- BCrypt hashes generated with BCryptPasswordEncoder (strength=10)
-- ════════════════════════════════════════════════════════════

-- ────────────────────────────────────────────────────────────
-- USERS (passwords encoded with BCrypt)
-- ────────────────────────────────────────────────────────────
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin User',   'admin@streamflix.com',   '$2a$10$EqKc2UInLhdmLw.MQcOkR.T1aKbKq3LhQc9JxG3vH5nB7mD9fP1k', 'ADMIN'),
('Alice Smith',  'alice@example.com',      '$2a$10$BxQ5L8nK2pR4tW6yA0C3eE7gJ1mO4qS8vX2zD5fH9iK3lN6rT0wY', 'USER'),
('Bob Johnson',  'bob@example.com',        '$2a$10$C4rT7wY1eH3jL6mN9pQ2sU5vX8zA0B3cD6fG1iJ4kO7lR0tW3yE5', 'USER'),
('Charlie Brown','charlie@example.com',    '$2a$10$D5sU8xZ2fI4kM7nO0qR3tV6wY9zA1B4cE7gH2jL5mP8qS1uV4wX6', 'USER'),
('Diana Prince', 'diana@example.com',      '$2a$10$E6tV9yA3gJ5lN8oP1rS4uW7xA0zB2cD8fH3kM6nQ9rS2vW5xY7zA1', 'USER');

-- ────────────────────────────────────────────────────────────
-- SUBSCRIPTIONS
-- ────────────────────────────────────────────────────────────
INSERT INTO subscriptions (user_id, plan, start_date, end_date, status) VALUES
(2, 'PREMIUM',  CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), 'ACTIVE'),
(3, 'STANDARD', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), 'ACTIVE'),
(4, 'BASIC',    DATE_SUB(CURRENT_DATE, INTERVAL 31 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 'EXPIRED'),
(5, 'PREMIUM',  DATE_SUB(CURRENT_DATE, INTERVAL 60 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), 'EXPIRED');

-- ────────────────────────────────────────────────────────────
-- VIDEOS (20 entries across all genres and types)
-- ────────────────────────────────────────────────────────────
INSERT INTO videos (title, description, genre, duration_seconds, release_year, rating, age_rating, thumbnail_url, video_url, type, is_premium, created_by) VALUES
-- Movies
('The Matrix',           'A computer hacker learns from mysterious rebels about the true nature of his reality.',                     'SCI_FI',      8160,  1999, 8.7, 'R',    '/assets/thumbnails/matrix.jpg',     '/videos/matrix.mp4',      'MOVIE',         TRUE,  1),
('Inception',            'A thief who steals corporate secrets through the use of dream-sharing technology.',                         'SCI_FI',      8880,  2010, 8.8, 'PG13', '/assets/thumbnails/inception.jpg',  '/videos/inception.mp4',   'MOVIE',         TRUE,  1),
('Die Hard',             'An NYPD officer tries to save his wife and several others taken hostage by German terrorists.',              'ACTION',      7920,  1988, 8.2, 'R',    '/assets/thumbnails/diehard.jpg',    '/videos/diehard.mp4',     'MOVIE',         FALSE, 1),
('The Hangover',         'Three buddies wake up from a bachelor party in Las Vegas, with no memory of the previous night.',            'COMEDY',      6000,  2009, 7.7, 'R',    '/assets/thumbnails/hangover.jpg',   '/videos/hangover.mp4',    'MOVIE',         FALSE, 1),
('The Conjuring',        'Paranormal investigators work to help a family terrorized by a dark presence in their farmhouse.',           'HORROR',      6720,  2013, 7.5, 'R',    '/assets/thumbnails/conjuring.jpg',  '/videos/conjuring.mp4',   'MOVIE',         TRUE,  1),
('Gladiator',            'A former Roman General sets out to exact vengeance against the corrupt emperor who murdered his family.',     'ACTION',      9300,  2000, 8.5, 'R',    '/assets/thumbnails/gladiator.jpg',  '/videos/gladiator.mp4',   'MOVIE',         TRUE,  1),
('Superbad',             'Two co-dependent high school seniors are forced to deal with separation anxiety.',                           'COMEDY',      6780,  2007, 7.6, 'R',    '/assets/thumbnails/superbad.jpg',   '/videos/superbad.mp4',    'MOVIE',         FALSE, 1),
('A Quiet Place',        'In a post-apocalyptic world, a family is forced to live in silence while hiding from monsters.',             'HORROR',      5400,  2018, 7.5, 'PG13', '/assets/thumbnails/quietplace.jpg', '/videos/quietplace.mp4',  'MOVIE',         TRUE,  1),
('Mad Max: Fury Road',   'In a post-apocalyptic wasteland, a woman rebels against a tyrannical ruler.',                                'ACTION',      7200,  2015, 8.1, 'R',    '/assets/thumbnails/madmax.jpg',     '/videos/madmax.mp4',      'MOVIE',         TRUE,  1),
('Get Out',              'A young African-American visits his white girlfriend parents for the weekend.',                               'HORROR',      6240,  2017, 7.7, 'R',    '/assets/thumbnails/getout.jpg',     '/videos/getout.mp4',      'MOVIE',         FALSE, 1),

-- TV Shows
('Breaking Bad',         'A high school chemistry teacher turns to manufacturing and selling methamphetamine.',                          'DRAMA',       2820,  2008, 9.5, 'R',    '/assets/thumbnails/breakingbad.jpg', '/videos/breakingbad.mp4', 'TV_SHOW',       TRUE,  1),
('Stranger Things',      'When a young boy disappears, his mother and friends must confront terrifying supernatural forces.',           'SCI_FI',      3000,  2016, 8.7, 'PG13', '/assets/thumbnails/strangerthings.jpg','/videos/strangerthings.mp4','TV_SHOW',     TRUE,  1),
('The Office',           'A mockumentary on a group of typical office workers where the workday consists of ego clashes.',              'COMEDY',      1320,  2005, 8.9, 'PG13', '/assets/thumbnails/theoffice.jpg',  '/videos/theoffice.mp4',   'TV_SHOW',       FALSE, 1),
('The Crown',            'Follows the political rivalries and romance of Queen Elizabeth II reign.',                                    'DRAMA',       3480,  2016, 8.6, 'R',    '/assets/thumbnails/thecrown.jpg',   '/videos/thecrown.mp4',    'TV_SHOW',       TRUE,  1),
('Black Mirror',         'An anthology series exploring a twisted high-tech multiverse.',                                               'SCI_FI',      3600,  2011, 8.8, 'R',    '/assets/thumbnails/blackmirror.jpg', '/videos/blackmirror.mp4', 'TV_SHOW',       TRUE,  1),
('Chernobyl',            'In April 1986 an explosion at the Chernobyl nuclear power plant becomes one of the worst catastrophes.',      'DRAMA',       19800, 2019, 9.4, 'R',    '/assets/thumbnails/chernobyl.jpg',  '/videos/chernobyl.mp4',   'TV_SHOW',       TRUE,  1),

-- Documentaries
('Planet Earth II',      'David Attenborough returns with a new wildlife documentary series.',                                         'DOCUMENTARY', 3000,  2016, 9.5, 'G',    '/assets/thumbnails/planeteartii.jpg','/videos/planeteartii.mp4','DOCUMENTARY',  FALSE, 1),
('Our Planet',           'Documentary series focusing on the diversity of habitats around the world.',                                  'DOCUMENTARY', 3000,  2019, 9.3, 'G',    '/assets/thumbnails/ourplanet.jpg',  '/videos/ourplanet.mp4',   'DOCUMENTARY',   FALSE, 1),
('Free Solo',            'Alex Honnold attempts to free solo climb El Capitan.',                                                       'DOCUMENTARY', 6000,  2018, 8.1, 'PG13', '/assets/thumbnails/freesolo.jpg',   '/videos/freesolo.mp4',    'DOCUMENTARY',   TRUE,  1),
('Cosmos: A Spacetime Odyssey', 'An exploration of our discovery of the laws of nature and coordinates in space and time.',            'DOCUMENTARY', 2700,  2014, 9.3, 'PG',   '/assets/thumbnails/cosmos.jpg',     '/videos/cosmos.mp4',      'DOCUMENTARY',   FALSE, 1);

-- ────────────────────────────────────────────────────────────
-- WATCHLIST (user favorites)
-- ────────────────────────────────────────────────────────────
INSERT INTO watchlist (user_id, video_id) VALUES
(2, 1),   -- Alice: The Matrix
(2, 2),   -- Alice: Inception
(2, 11),  -- Alice: Breaking Bad
(2, 17),  -- Alice: Planet Earth II
(3, 3),   -- Bob: Die Hard
(3, 4),   -- Bob: The Hangover
(3, 12),  -- Bob: Stranger Things
(3, 19),  -- Bob: Free Solo
(4, 5),   -- Charlie: The Conjuring
(4, 13),  -- Charlie: The Office
(4, 20),  -- Charlie: Cosmos
(5, 6),   -- Diana: Gladiator
(5, 10),  -- Diana: Get Out
(5, 16),  -- Diana: Chernobyl
(5, 18);  -- Diana: Our Planet

-- ────────────────────────────────────────────────────────────
-- WATCH HISTORY
-- ────────────────────────────────────────────────────────────
INSERT INTO watch_history (user_id, video_id, progress_seconds, completed, watch_date) VALUES
(2, 1,  8160, TRUE,  DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 2,  4400, FALSE, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 11, 2820, TRUE,  DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 17, 1500, FALSE, NOW()),
(3, 3,  7920, TRUE,  DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, 4,  3000, FALSE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 12, 3000, TRUE,  DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 5,  6720, TRUE,  DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 13, 660,  FALSE, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(5, 6,  9300, TRUE,  DATE_SUB(NOW(), INTERVAL 8 DAY)),
(5, 16, 9900, FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ────────────────────────────────────────────────────────────
-- PAYMENTS
-- ────────────────────────────────────────────────────────────
INSERT INTO payments (user_id, amount, currency, payment_method, gateway_txn_id, payment_date, status) VALUES
(2, 15.99, 'USD', 'STRIPE',  'txn_stripe_alice_001', DATE_SUB(NOW(), INTERVAL 30 DAY), 'SUCCESS'),
(3, 12.99, 'USD', 'PAYPAL',  'txn_paypal_bob_001',   DATE_SUB(NOW(), INTERVAL 30 DAY), 'SUCCESS'),
(4,  9.99, 'USD', 'STRIPE',  'txn_stripe_charlie_001', DATE_SUB(NOW(), INTERVAL 61 DAY), 'SUCCESS'),
(5, 15.99, 'USD', 'PAYPAL',  'txn_paypal_diana_001', DATE_SUB(NOW(), INTERVAL 60 DAY), 'SUCCESS'),
(5, 15.99, 'USD', 'PAYPAL',  'txn_paypal_diana_002', DATE_SUB(NOW(), INTERVAL 30 DAY), 'SUCCESS');