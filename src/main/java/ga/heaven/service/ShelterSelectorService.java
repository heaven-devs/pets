package ga.heaven.service;

import ga.heaven.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.CustomerContext.Context.*;

@Service
public class ShelterSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShelterSelectorService.class);

    private final ShelterService shelterService;

    private TgIn in;

    public ShelterSelectorService(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    /**
     * Обрабатываю текстовые сообщения пользователя (которые начинаются не с /) и фото
     *
     * @param in сообщение от пользователя
     */
    public void processingNonCommandMessagesForShelter(TgIn in) {
        this.in = in;
        CustomerContext.Context context = in.getCustomer().getCustomerContext().getDialogContext();
        switch (context) {
            case WAIT_CUSTOMER_NAME:
                in.getCustomer().getCustomerContext().setDialogContext(WAIT_CUSTOMER_PHONE);
                formatFieldName(in.text());
                generateMenu(SHELTER_SEND_PHONE_MSG, SHELTER_INFO_MENU_LEVEL);
                break;
            case WAIT_CUSTOMER_PHONE:
                in.getCustomer().getCustomerContext().setDialogContext(WAIT_CUSTOMER_ADDRESS);
                in.getCustomer().setPhone(truncate(formatFieldPhone(in.text()), 11));
                generateMenu(SHELTER_SEND_ADDRESS_MSG, SHELTER_INFO_MENU_LEVEL);
                break;
            case WAIT_CUSTOMER_ADDRESS:
                in.getCustomer().getCustomerContext().setDialogContext(FREE);
                in.getCustomer().setAddress(in.text());
                generateMenu(SHELTER_CONTACT_SAVED, SHELTER_INFO_MENU_LEVEL);
                break;
        }
    }

    /**
     * Метод создает набор кнопок меню и подставляет переданный текст в сообщение бота
     *
     * @param text текст в тело сообщения
     */
    private void generateMenu(String text, Long level) {
        new TgOut()
                .tgIn(in)
                .textBody(text)
                .generateMarkup(level)
                .send()
                .save()
        ;
    }

    /**
     * Метод определяет какая команда была введена
     *
     * @param in входящий массив от бота
     */
    public void switchCmd(TgIn in) {
        this.in = in;
        Shelter shelter = shelterService.findById(in.getCustomer().getCustomerContext().getShelterId());

        String text;
        switch (in.endpoint().getName()) {
            case SHELTER_INFO_EPT:
                text = sendShelterInformation(shelter.getDescription(), SHELTER_INFO_NOT_FOUND);
                generateMenu(text, SHELTER_INFO_MENU_LEVEL);
                break;
            case SHELTER_ADDRESS_EPT:
                text = sendShelterInformation(shelter.getAddress(), SHELTER_ADDRESS_NOT_FOUND);
                generateMenu(text, SHELTER_INFO_MENU_LEVEL);
                break;
            case SHELTER_SAFETY_EPT:
                text = sendShelterInformation(shelter.getRules(), SHELTER_RULES_NOT_FOUND);
                generateMenu(text, SHELTER_INFO_MENU_LEVEL);
                break;
            case SHELTER_LEAVE_CONTACT_EPT:
                in.getCustomer().getCustomerContext().setDialogContext(WAIT_CUSTOMER_NAME);
                generateMenu(SHELTER_SEND_NAME_MSG, SHELTER_INFO_MENU_LEVEL);
                break;
        }
    }

    /**
     * Метод отправляет боту сообщение
     *
     * @param areaField   текст сообщения
     * @param notFoundMsg текст ошибки, если сообщение не найдено
     */
    private String sendShelterInformation(String areaField, String notFoundMsg) {
        return areaField != null ? areaField : notFoundMsg;
    }

    /**
     * Метод форматирует поле ФИО в зависимости от введенных данных
     *
     * @param text введенные пользователем данные
     */
    private void formatFieldName(String text) {
        String[] nameParts = text.split(" ");
        if (nameParts.length == 1) {
            in.getCustomer().setName(truncate(nameParts[0], 25));
        } else {
            if (nameParts.length >= 2) {
                in.getCustomer().setSurname(truncate(nameParts[0], 25));
                in.getCustomer().setName(truncate(nameParts[1], 25));
            }
            if (nameParts.length >= 3) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < nameParts.length; i++) {
                    sb.append(nameParts[i] + " ");
                }
                in.getCustomer().setSecondName(truncate(sb.toString(), 25));
            }
        }
//        customerService.updateCustomer(customer);
    }

    /**
     * Метод обрезает текст до указанного количества символов
     *
     * @param text   текст, который обрезаем
     * @param length длина, которую нужно оставить
     * @return возвращаю обрезанный текст
     */
    private String truncate(String text, int length) {
        if (text.length() <= length) {
            return text;
        }
        return text.substring(0, length);
    }

    /**
     * Метод оставляет в строке только цифры
     *
     * @param text входящее сообщение от бота
     * @return отформатированная строка
     */
    private String formatFieldPhone(String text) {
        return text.replaceAll("\\D+", "");
    }

}
