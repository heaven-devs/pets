
-- id животных, по которым сегодня сданы отчеты
SELECT pet.id
from pet LEFT JOIN report on pet.id = report.id_pet
where (date between '2023-03-02 00:00:00' AND '2023-03-02 23:59:59');




-- Нужен запрос, который выдаст список животных, по которым сегодня не сдавался отчет
-- SELECT pet.* FROM pet LEFT JOIN customer c on pet.id_customer = c.id where c.chat_id = 440401693 and pet.id NOT IN (SELECT pet.id from pet LEFT JOIN report on pet.id = report.id_pet where (date between '2023-03-02 00:00:00' AND '2023-03-02 23:59:59'));

SELECT pet.*
FROM pet
         LEFT JOIN customer c on pet.id_customer = c.id
where c.chat_id = 440401693
  and pet.id NOT IN (SELECT pet.id
                     from pet
                              LEFT JOIN report on pet.id = report.id_pet
                     where (date between '2023-03-02 00:00:00' AND '2023-03-02 23:59:59'));



-- SELECT
--     COUNT(wxa43_virtuemart_products.virtuemart_product_id)
-- FROM
--     wxa43_virtuemart_products
-- WHERE
--         published = 1 AND
--         wxa43_virtuemart_products.virtuemart_product_id NOT IN (
--         SELECT virtuemart_product_id
--         FROM wxa43_virtuemart_product_customfields
--         WHERE virtuemart_custom_id = 54
--     )