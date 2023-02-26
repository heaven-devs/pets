-- liquibase formatted sql

-- changeset alrepin:1
INSERT INTO public.info
(id, area, instructions)
SELECT 1, 'common_info', 'Common info about bot'
    WHERE
    NOT EXISTS (
        SELECT id FROM public.info WHERE id = 1
    );