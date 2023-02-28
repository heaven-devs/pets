package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import ga.heaven.model.Customer;
import ga.heaven.model.Pet;
import ga.heaven.service.CustomerService;
import ga.heaven.service.PetService;
import ga.heaven.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReportListener.class);

    private final ReportService reportService;
    private final CustomerService customerService;
    private final PetService petService;

    public ReportListener(ReportService reportService, CustomerService customerService, PetService petService) {
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
    }

    public void processingReportQueries(Update update, TelegramBot telegramBot) {
        processingSubmitReport(update, telegramBot);
    }

    private void processingSubmitReport(Update update, TelegramBot telegramBot) {
        long chatId = update.message().chat().id();
        Customer customer = customerService.findCustomerByChatId(chatId);
        String dialogStatus = customer.getStatus();
        String userMessage = update.message().text();
        String messageText = "";

        LOGGER.debug("dialogStatus = " + dialogStatus);
        LOGGER.debug("userMessage = " + userMessage);

        if (userMessage.equals("/submit_report")) {
            List<Pet> customerPetList = petService.findPetsByCustomerOrderById(customer);
            messageText = "У вас нет питомцев";
            if (customerPetList.size() == 1) {
                messageText = "У вас один питомец, отправьте отчет";
                customer.setStatus("wait_report:0");
                customerService.updateCustomer(customer);
            } else if (customerPetList.size() > 1) {
                StringBuilder sb = new StringBuilder("Введите id питомца:" + "\r\n");
                for (Pet pet : customerPetList) {
                    long petId = pet.getId();
                    String petName = pet.getName();
                    sb.append(petId).append(". ").append(petName).append("\r\n");
                }
                messageText = sb.toString();
                customer.setStatus("wait_pet_id");
                customerService.updateCustomer(customer);
            }


        } else if (dialogStatus.equals("wait_pet_id")) {
            List<String> validIdList = petService.findPetsByCustomerOrderById(customer).stream()
                    .map(Pet::getId)
                    .map(Object::toString)
                    .collect(Collectors.toList());

            LOGGER.debug("validList = " + validIdList);

            if (validIdList.contains(userMessage)) {
                messageText = "Отправьте отчет о питомце с id = " + userMessage;
                customer.setStatus("wait_report:" + userMessage);
                customerService.updateCustomer(customer);
            } else {
                messageText = "Некорректные данные, введите id питомца из списка выше.";
            }
        }
        SendMessage message = new SendMessage(chatId, messageText);
        SendResponse response = telegramBot.execute(message);
    }
}
