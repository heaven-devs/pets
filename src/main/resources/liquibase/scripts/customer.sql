-- liquibase formatted sql

-- changeset starasov:1

ALTER TABLE customer
ADD status varchar(20);