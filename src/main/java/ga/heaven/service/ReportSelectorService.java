package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import ga.heaven.model.Customer;
import ga.heaven.model.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.configuration.ReportConstants.*;

@Service
public class ReportSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSelectorService.class);

    private final MsgService msgService;

    private final ReportService reportService;
    private final CustomerService customerService;
    private final PetService petService;

    private Message inputMessage;
    private Customer customer;
    private String responseText;

    public ReportSelectorService(MsgService msgService, ReportService reportService, CustomerService customerService, PetService petService) {
        this.msgService = msgService;
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
    }

    public void switchCmd(Message inputMessage) {
        this.inputMessage = inputMessage;
        this.customer = customerService.findCustomerByChatId(inputMessage.chat().id());
        if (inputMessage.text() != null && inputMessage.text().equals(REPORT_SUBMIT_CMD)) {
            msgService.sendMsg(inputMessage.chat().id(), processingSubmitReport());
        } else {
            msgService.sendMsg(inputMessage.chat().id(), processingUserMessages(inputMessage));
        }
    }

    /**
     * диалог пользователя с ботом при вводе команды "/submit_report"
     */
    private String processingSubmitReport() {
        // todo: сделать проверку, сдавал ли пользователь сегодня отчет?

        List<Pet> customerPetList = petService.findPetsByCustomerOrderById(customer);
        responseText = ANSWER_DONT_HAVE_PETS;

        if (customerPetList.size() == 1) {
            responseText = ANSWER_ONE_PET;
            updateCustomerContext(STATUS_WAIT_REPORT, customerPetList.get(0).getId());
        } else if (customerPetList.size() > 1) {
            responseText = generateListOfCustomersPets(customerPetList);
            updateCustomerContext(STATUS_WAIT_PET_ID, 0);
        }
        return responseText;
    }

    private void updateCustomerContext(String context, long petId) {
        customer.getCustomerContext().setPetId(petId);
        updateCustomerContext(context);
    }

    private void updateCustomerContext(String context) {
        customer.getCustomerContext().setDialogContext(context);
        customerService.updateCustomer(customer);
    }

    private String generateListOfCustomersPets(List<Pet> customerPetList) {
        StringBuilder sb = new StringBuilder(ANSWER_ENTER_PET_ID + CARRIAGE_RETURN);
        for (Pet pet : customerPetList) {
            sb.append(pet.getId()).append(". ").append(pet.getName()).append(CARRIAGE_RETURN);
        }
        return sb.toString();
    }

    private String processingUserMessages(Message inputMessage) {
        String context = customer.getCustomerContext().getDialogContext();
        switch (context) {
            case STATUS_WAIT_PET_ID: responseText = processingMsgWaitPetId(); break;
            case STATUS_WAIT_REPORT: responseText = processingMsgWaitReport(); break;
        }
        return responseText;
    }

    /**
     * Обработка сообщения боту, если статус диалога с покупателем "wait_pet_id"
     * @return текст ответа
     */
    private String processingMsgWaitPetId() {
        responseText = ANSWER_NON_EXISTENT_PET;
        List<String> validIdList = petService.findPetsByCustomerOrderById(customer).stream()
                .map(Pet::getId)
                .map(Object::toString)
                .collect(Collectors.toList());

        if (validIdList.contains(inputMessage.text())) {
            responseText = ANSWER_SEND_REPORT_FOR_PET_WITH_ID + inputMessage.text();
            updateCustomerContext(STATUS_WAIT_REPORT, Long.parseLong(inputMessage.text()));
        }
        return responseText;
    }

    /**
     * Обработка сообщения боту, если статус диалога с покупателем
     *      "wait_report" или "wait_report:X", где X - id животного
     * @return текст ответа
     */
    private String processingMsgWaitReport() {

        // todo: реализовать запись данных в таблицу report

        if (inputMessage.photo() != null && inputMessage.caption() != null) {
            responseText = ANSWER_REPORT_ACCEPTED;
            updateCustomerContext(STATUS_FREE, 0);
            savePhotoToDB();

        } else if (inputMessage.photo() != null) {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
            updateCustomerContext(STATUS_WAIT_REPORT);
            savePhotoToDB();

        } else if (inputMessage.text() != null) {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
            updateCustomerContext(STATUS_WAIT_REPORT);
        }

        return responseText;
    }

    private void savePhotoToDB() {
        // todo: получить фото от бота, разложить на байты, записать в базу. https://www.baeldung.com/java-download-file
    }

}
