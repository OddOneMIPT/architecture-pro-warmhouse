-- Создаём схему данных для devices-service
-- БД: devices

-- Таблица домов
CREATE TABLE IF NOT EXISTS houses (
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    address    TEXT,
    created_at TIMESTAMPTZ NOT NULL
);

-- Таблица комнат
CREATE TABLE IF NOT EXISTS rooms (
    id         UUID PRIMARY KEY,
    house_id   UUID NOT NULL REFERENCES houses(id),
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

-- Таблица типов устройств
CREATE TABLE IF NOT EXISTS device_types (
    id   UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Таблица моделей устройств
CREATE TABLE IF NOT EXISTS device_models (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type_id     UUID NOT NULL REFERENCES device_types(id),
    description TEXT
);

-- Таблица модулей (комплектов устройств)
CREATE TABLE IF NOT EXISTS modules (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

-- Таблица конфигурации модулей:
-- какие модели устройств и в каком количестве входят в модуль
CREATE TABLE IF NOT EXISTS module_device_models (
    id              UUID PRIMARY KEY,
    module_id       UUID NOT NULL REFERENCES modules(id),
    device_model_id UUID NOT NULL REFERENCES device_models(id),
    quantity        INTEGER NOT NULL
);

-- Таблица устройств, созданных у пользователя
CREATE TABLE IF NOT EXISTS devices (
    id                 UUID PRIMARY KEY,
    room_id            UUID NOT NULL REFERENCES rooms(id),
    model_id           UUID NOT NULL REFERENCES device_models(id),
    name               VARCHAR(100) NOT NULL,
    status             VARCHAR(20) NOT NULL,
    sensor_external_id INTEGER,
    created_at         TIMESTAMPTZ NOT NULL
);

-- Индексы (по минимуму)

CREATE INDEX IF NOT EXISTS idx_rooms_house_id ON rooms(house_id);
CREATE INDEX IF NOT EXISTS idx_devices_room_id ON devices(room_id);
CREATE INDEX IF NOT EXISTS idx_devices_model_id ON devices(model_id);


-- Наполнение БД devices начальными данными для MVP

-- 1. Дом

INSERT INTO houses (id, name, address, created_at)
VALUES (
    'd8e643fb-2bd6-47f3-8b69-e036cac9f2bb',
    'Загородный дом',
    'Тульская область, деревня Сосны, д. 3',
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 2. Комнаты в доме

INSERT INTO rooms (id, house_id, name, created_at) VALUES
(
    '98d83aa5-5f66-40c0-b070-391787e20760',
    'd8e643fb-2bd6-47f3-8b69-e036cac9f2bb',
    'Living Room',
    NOW()
),
(
    'd5034b5a-d6d7-463c-b707-2a62f3beb8a7',
    'd8e643fb-2bd6-47f3-8b69-e036cac9f2bb',
    'Bedroom',
    NOW()
),
(
    '54ce50e3-b761-40a8-966b-41e326583f1b',
    'd8e643fb-2bd6-47f3-8b69-e036cac9f2bb',
    'Kitchen',
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 3. Типы устройств

INSERT INTO device_types (id, name) VALUES
(
    'b1a6f1e5-f765-412e-b68b-752ae8f69a14',
    'HEATING_RELAY'
),
(
    '318c9fc9-2fee-4109-b37d-c8ebf9a66cc0',
    'TEMPERATURE_SENSOR'
)
ON CONFLICT (id) DO NOTHING;

-- 4. Модели устройств

INSERT INTO device_models (id, name, type_id, description) VALUES
(
    '7bcd2f74-f2ef-441a-9f29-bb1d69c2bdd5',
    'Реле отопления',
    'b1a6f1e5-f765-412e-b68b-752ae8f69a14',
    'Умное реле для управления котлом/радиаторами'
),
(
    '21ad97b7-b43a-4514-9766-23ce47b455ea',
    'Датчик температуры',
    '318c9fc9-2fee-4109-b37d-c8ebf9a66cc0',
    'Датчик температуры воздуха в помещении'
)
ON CONFLICT (id) DO NOTHING;

-- 5. Модуль (комплект устройств)

INSERT INTO modules (id, name, description) VALUES
(
    '019ad4ca-a014-7033-8809-f0302e52796f',
    'Комплект отопления (реле + датчик)',
    'Готовый комплект для управления отоплением в одной комнате'
)
ON CONFLICT (id) DO NOTHING;

-- 6. Конфигурация модуля:
-- 1 x датчик температуры + 1 x реле отопления

INSERT INTO module_device_models (id, module_id, device_model_id, quantity) VALUES
(
    'fddbf147-9a66-40c0-868b-124a7425f237',
    '019ad4ca-a014-7033-8809-f0302e52796f',
    '21ad97b7-b43a-4514-9766-23ce47b455ea', -- Датчик температуры
    1
),
(
    '4c07cc0e-848c-4f42-a89f-e754b8d77621',
    '019ad4ca-a014-7033-8809-f0302e52796f',
    '7bcd2f74-f2ef-441a-9f29-bb1d69c2bdd5', -- Реле отопления
    1
)
ON CONFLICT (id) DO NOTHING;
