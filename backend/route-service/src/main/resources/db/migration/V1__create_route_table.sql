CREATE TABLE IF NOT EXISTS route (
                                     id               VARCHAR(36) PRIMARY KEY,
    from_lat         DOUBLE PRECISION NOT NULL,
    from_lng         DOUBLE PRECISION NOT NULL,
    to_lat           DOUBLE PRECISION NOT NULL,
    to_lng           DOUBLE PRECISION NOT NULL,
    distance_km      DOUBLE PRECISION NOT NULL,
    duration_minutes DOUBLE PRECISION NOT NULL,
    geometry         TEXT NOT NULL
    );
