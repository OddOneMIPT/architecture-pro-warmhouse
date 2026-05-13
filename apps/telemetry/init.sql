CREATE DATABASE IF NOT EXISTS telemetry;

-- Таблица истории показаний
CREATE TABLE IF NOT EXISTS telemetry.temperature_timeseries
(
    device_id String,
    timestamp DateTime64(3, 'UTC'),
    value     Float64,
    location  String
)
ENGINE = MergeTree()
PARTITION BY toDate(timestamp)
ORDER BY (device_id, timestamp);

-- Таблица для последнего значения по устройству
CREATE TABLE IF NOT EXISTS telemetry.temperature_last
(
    device_id  String,
    value      Float64,
    updated_at DateTime64(3, 'UTC')
)
ENGINE = ReplacingMergeTree(updated_at)
ORDER BY device_id;

-- Материализованное представление, обновляющее таблицу temperature_last
CREATE MATERIALIZED VIEW IF NOT EXISTS telemetry.temperature_last_mv
TO telemetry.temperature_last
AS
SELECT
    device_id,
    anyLast(value)  AS value,
    max(timestamp)  AS updated_at
FROM telemetry.temperature_timeseries
GROUP BY device_id;
