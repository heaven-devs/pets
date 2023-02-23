-- liquibase formatted sql
-- changeset AlexTuraev:1
CREATE TABLE customer (  -- Таблица: Пользователь
                                   id BIGINT PRIMARY KEY,          -- уникальный id
                                   chat_id BIGINT,    -- id Telegram чата
                                   surname VARCHAR(25) , -- фамилия
                                   name VARCHAR(25),    -- имя
                                   second_name VARCHAR(25), -- отчество
                                   phone VARCHAR(12),       -- тлф формата +70000000000
                                   address TEXT             -- адрес
);
-- changeset AlexTuraev:2
CREATE TABLE volunteer (  -- Таблица: Волонтер
                          id BIGINT,          -- уникальный id
                          chat_id BIGINT,    -- id Telegram чата
                          surname VARCHAR(25) , -- фамилия
                          name VARCHAR(25),    -- имя
                          second_name VARCHAR(25), -- отчество
                          phone VARCHAR(12),       -- тлф формата +70000000000
                          address TEXT             -- адрес
);
-- changeset AlexTuraev:3
CREATE TABLE pets_care_recommendations (  -- Таблица: Рекомендации по уходу за питомцем
                          breed VARCHAR(30) PRIMARY KEY, -- порода питомца (key field)
                          recommendations_child TEXT,    -- рекомендации по уходу за щенком
                          recommendations_adult TEXT,    -- рекомендации по уходу за взрослой собакой
                          child_age INTEGER              -- ≥ возраст взрослой собаки
);