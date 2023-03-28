package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import ga.heaven.model.Customer;
import ga.heaven.model.Shelter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.CustomerContext.Context.*;

@Service
public class ShelterSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShelterSelectorService.class);

    private final AppLogicService appLogicService;
    private final MsgService msgService;
    private final ShelterService shelterService;
    private final CustomerService customerService;

    private Message inputMessage;
    private Shelter currentShelter;
    private Long chatId;
    private Customer customer;

    public ShelterSelectorService(AppLogicService appLogicService, MsgService msgService, ShelterService shelterService, CustomerService customerService) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
        this.shelterService = shelterService;
        this.customerService = customerService;
    }

    /**
     * Метод определяет какая команда была введена
     * @param inputMessage входящий массив от бота
     */
    public void switchCmd(Message inputMessage) {
        this.inputMessage = inputMessage;
        chatId = inputMessage.chat().id();
        customer = customerService.findCustomerByChatId(chatId);
        currentShelter = shelterService.findById(customer.getCustomerContext().getShelterId());
        if (currentShelter == null) {
            msgService.sendMsg(chatId, SHELTER_NOT_FOUND);
            return;
        }

        String command = inputMessage.text();
        switch (command) {
            case SHELTER_INFO_CMD:
                sendShelterInformation(currentShelter.getDescription(), SHELTER_INFO_NOT_FOUND);
                break;
            case SHELTER_ADDRESS_CMD:
                sendShelterInformation(currentShelter.getAddress(), SHELTER_ADDRESS_NOT_FOUND);
                break;
            case SHELTER_SAFETY_CMD:
                sendShelterInformation(currentShelter.getRules(), SHELTER_RULES_NOT_FOUND);
                break;
            case SHELTER_LEAVE_CONTACT_CMD:
                appLogicService.updateCustomerContext(customer, WAIT_CUSTOMER_NAME);
                msgService.sendMsg(chatId, SHELTER_SEND_NAME_MSG);
                break;
        }
    }

    /**
     * Метод отправляет боту сообщение
     * @param areaField текст сообщения
     * @param notFoundMsg текст ошибки, если сообщение не найдено
     */
    private void sendShelterInformation(String areaField, String notFoundMsg) {
        if (areaField != null) {
            msgService.sendMsg(chatId, areaField);
        } else {
            msgService.sendMsg(chatId, notFoundMsg);
        }
    }

    /**
     * Метод выполняется если введена не команда, а простой текст. Метод выбирает что делать с данными, в зависимости от контекста
     * @param inputMessage входящее сообщение от бота
     */
    public void switchText(Message inputMessage) {
        this.inputMessage = inputMessage;
        chatId = inputMessage.chat().id();
        customer = customerService.findCustomerByChatId(chatId);

        String text = inputMessage.text();
        switch (customer.getCustomerContext().getDialogContext()) {

            case WAIT_CUSTOMER_NAME:
                appLogicService.updateCustomerContext(customer, WAIT_CUSTOMER_PHONE);
                formatFieldName(text);
                msgService.sendMsg(chatId, SHELTER_SEND_PHONE_MSG);
                break;

            case WAIT_CUSTOMER_PHONE:
                appLogicService.updateCustomerContext(customer, WAIT_CUSTOMER_ADDRESS);
                customer.setPhone(truncate(formatFieldPhone(text), 11));
                customerService.updateCustomer(customer);
                msgService.sendMsg(chatId, SHELTER_SEND_ADDRESS_MSG);
                break;

            case WAIT_CUSTOMER_ADDRESS:
                appLogicService.updateCustomerContext(customer, FREE);
                customer.setAddress(text);
                customerService.updateCustomer(customer);
                msgService.sendMsg(chatId, SHELTER_CONTACT_SAVED);
                break;
        }
    }

    /**
     * Метод форматирует поле ФИО в зависимости от введенных данных
     * @param text введенные пользователем данные
     */
    private void formatFieldName(String text) {
        String[] nameParts = text.split(" ");
        if (nameParts.length == 1) {
            customer.setName(truncate(nameParts[0], 25));
        } else {
            if (nameParts.length >= 2) {
                customer.setSurname(truncate(nameParts[0], 25));
                customer.setName(truncate(nameParts[1], 25));
            }
            if (nameParts.length >= 3) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < nameParts.length; i++) {
                    sb.append(nameParts[i] + " ");
                }
                customer.setSecondName(truncate(sb.toString(), 25));
            }
        }
        customerService.updateCustomer(customer);
    }

    /**
     * Метод обрезает текст до указанного количества символов
     * @param text текст, который обрезаем
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
     * @param text входящее сообщение от бота
     * @return отформатированная строка
     */
    private String formatFieldPhone(String text) {
        return text.replaceAll("\\D+", "");
    }
}
