-- =============================================================================
-- CINERESERVE DATABASE SCHEMA MIGRATION SCRIPTS (PostgreSQL)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. AUTH SERVICE DATABASE (`cinereserve_auth`)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL
);

-- -----------------------------------------------------------------------------
-- 2. BOOKING SERVICE DATABASE (`cinereserve_booking`)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS shows (
    id BIGSERIAL PRIMARY KEY,
    movie_id VARCHAR(255) NOT NULL,
    screen_id VARCHAR(255) NOT NULL,
    theatre_id VARCHAR(255) NOT NULL,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS show_prices (
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    seat_type VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (show_id, seat_type)
);

CREATE TABLE IF NOT EXISTS show_seats (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    seat_number VARCHAR(50) NOT NULL,
    seat_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    locked_by BIGINT,
    lock_time TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER DEFAULT 0,
    CONSTRAINT unique_show_seat UNIQUE (show_id, seat_number)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    total_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS booking_seats (
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    seat_number VARCHAR(50) NOT NULL,
    PRIMARY KEY (booking_id, seat_number)
);

-- -----------------------------------------------------------------------------
-- 3. NOTIFICATION SERVICE DATABASE (`cinereserve_notification`)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    message VARCHAR(1000) NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    sent_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 4. CATALOG SERVICE SCHEMAS (MongoDB - For Reference)
-- -----------------------------------------------------------------------------
/*
  Database: `cinereserve_catalog`
  
  Collection: `movies`
  {
    "_id": ObjectId("..."),
    "title": "Inception",
    "description": "A thief steals corporate secrets...",
    "genres": ["Sci-Fi", "Action"],
    "duration": 148,
    "rating": 8.8,
    "languages": ["English"],
    "releaseDate": ISODate("2010-07-16T00:00:00Z"),
    "poster": "http://...",
    "trailer": "http://...",
    "status": "NOW_SHOWING"
  }

  Collection: `locations`
  {
    "_id": ObjectId("..."),
    "name": "Midtown Center",
    "city": "New York",
    "state": "NY"
  }

  Collection: `theatres`
  {
    "_id": ObjectId("..."),
    "name": "Grand IMAX Midtown",
    "locationId": "location_uuid_string",
    "address": "123 Broad St, NY",
    "status": "APPROVED"
  }

  Collection: `screens`
  {
    "_id": ObjectId("..."),
    "theatreId": "theatre_uuid_string",
    "name": "Screen 01 (Atmos)",
    "seatLayoutId": "layout_uuid_string"
  }

  Collection: `seat_layouts`
  {
    "_id": ObjectId("..."),
    "name": "Standard 100 Grid",
    "rowNames": ["A", "B", "C", "D"],
    "colCount": 12,
    "seats": [
      { "rowName": "A", "colIndex": 1, "seatType": "NORMAL" },
      { "rowName": "A", "colIndex": 2, "seatType": "NORMAL" },
      ...
    ]
  }
*/
