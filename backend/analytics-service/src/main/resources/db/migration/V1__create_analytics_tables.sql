-- V1__create_analytics_tables.sql
-- Türkçe Özet:
-- Analytics servisinde kullanılacak aggregate tabloların oluşturulması.

CREATE SCHEMA IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.daily_route_usage (
                                                           usage_date   DATE        NOT NULL PRIMARY KEY,
                                                           route_count  INTEGER     NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS analytics.popular_place_categories (
                                                                  usage_date   DATE        NOT NULL,
                                                                  category     VARCHAR(64) NOT NULL,
    count        INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT pk_popular_place_categories PRIMARY KEY (usage_date, category)
    );

CREATE TABLE IF NOT EXISTS analytics.ai_usage_stats (
                                                        usage_date      DATE    NOT NULL PRIMARY KEY,
                                                        request_count   INTEGER NOT NULL DEFAULT 0,
                                                        accepted_count  INTEGER NOT NULL DEFAULT 0
);
