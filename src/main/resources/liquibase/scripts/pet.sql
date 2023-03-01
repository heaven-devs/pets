-- liquibase formatted sql

-- changeset alrepin:3
INSERT INTO public.pet
    (age, name)
SELECT 5, 'кот Васька'
WHERE NOT EXISTS(
        SELECT id, name FROM public.pet WHERE name = 'кот Васька'
    );

-- changeset alrepin:4
INSERT INTO public.pet
(age, name)
SELECT 1, 'котенок Мурзик'
WHERE NOT EXISTS(
        SELECT id, name FROM public.pet WHERE name = 'котенок Мурзик'
    );

INSERT INTO public.pet
(age, name)
SELECT 24, 'пес Барбос'
WHERE NOT EXISTS(
        SELECT id, name FROM public.pet WHERE name = 'пес Барбос'
    );

INSERT INTO public.pet
(age, name)
SELECT 24, 'щенок Гав'
WHERE NOT EXISTS(
        SELECT id, name FROM public.pet WHERE name = 'щенок Гав'
    );

-- changeset Alex Turaev:4
INSERT INTO public.info (id, area, instructions)
VALUES (2, 'dating_rules', 'Обращайтесь с питомцами хорошо');