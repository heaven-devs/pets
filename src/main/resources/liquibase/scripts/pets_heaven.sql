-- liquibase formatted sql
-- changeset AlexTuraev:1
CREATE TABLE customer (  -- Таблица: Пользователь
                                   id BIGINT,          -- уникальный id
                                   chat_id BIGINT,    -- id Telegram чата
                                   surname VARCHAR(25) , -- фамилия
                                   name VARCHAR(25),    -- имя
                                   second_name VARCHAR(25), -- отчество
                                   phone VARCHAR(12),       -- тлф формата +70000000000
                                   address TEXT             -- адрес
);