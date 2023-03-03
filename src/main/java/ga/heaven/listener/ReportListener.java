package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import ga.heaven.model.Customer;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.service.CustomerService;
import ga.heaven.service.PetService;
import ga.heaven.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ga.heaven.constants.ReportConstants.*;


@Service
public class ReportListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReportListener.class);

    private final ReportService reportService;
    private final CustomerService customerService;
    private final PetService petService;

    private Update update;
    private TelegramBot telegramBot;
    private long chatId;
    private String userMessage;
    private Customer customer;
    private String dialogStatus;
    private long petId;
    private String responseText;
    private PhotoSize[] photos;
    private String caption;

    public ReportListener(ReportService reportService, CustomerService customerService, PetService petService) {
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
    }

    /**
     * Обработка команд связанных с отчетами.
     * @param update
     * @param telegramBot
     */
    public void processingReportQueries(Update update, TelegramBot telegramBot) {
        LOGGER.debug("");
        this.update = update;
        this.telegramBot = telegramBot;
        chatId = update.message().chat().id();
        customer = customerService.findCustomerByChatId(chatId);
        dialogStatus = customer.getDialogStatus();
        userMessage = update.message().text();
        analysisDialogStatus();
        photos =  update.message().photo();
        if (photos != null) {
            caption = update.message().caption();
        }

        processingSubmitReport();
    }

    /**
     * диалог пользователя с ботом при вводе команды "/submit_report"
     */
    private void processingSubmitReport() {
        LOGGER.debug("dialogStatus = " + dialogStatus);
        LOGGER.debug("userMessage = " + userMessage);



        if (userMessage != null && userMessage.equals(COMMAND_SUBMIT_REPORT)) {
            responseText = commandSubmitReportStep1();
        } else if (dialogStatus.equals(STATUS_WAIT_PET_ID)) {
            responseText = commandSubmitReportStep2();
        } else if (dialogStatus.equals(STATUS_WAIT_REPORT)) {
            responseText = commandSubmitReportStep3();
        }
        SendMessage message = new SendMessage(chatId, responseText)
                .parseMode(ParseMode.HTML);
        telegramBot.execute(message);
    }

    /**
     * Обработка команды боту /submit_report
     * @return
     */
    private String commandSubmitReportStep1() {
        LOGGER.debug("STEP 1: Run");
        List<Pet> customerPetList = petService.findPetsByCustomerOrderById(customer);
        responseText = ANSWER_DONT_HAVE_PETS;
        LocalDate localDate = LocalDate.now();
        LocalDateTime startTime = localDate.atStartOfDay();
        LocalDateTime finishTime = LocalTime.MAX.atDate(localDate);

        // Нужен запрос, который выдаст список животных, по которым сегодня не сдавался отчет
        List<Pet> petsWithoutReport = petService.findCustomerPetsWithoutReportsToday(customer.getChatId(), startTime, finishTime);
        LOGGER.debug("petsWithoutReport" + petsWithoutReport.toString());

/*        Collection<Report> petsWithoutReportToday = reportService.findAllByPetReportIsNullOrPhotoIsNullAndDateBetween(startTime, finishTime);
        System.out.println("petsWithoutReportToday = " + petsWithoutReportToday);*/

        for (Pet pet : customerPetList) {
            LOGGER.warn("petId: " + pet.getId() +
                    ", startTime: " + startTime +
                    ", finishTime: " + finishTime);

            Report report = reportService.findReportByPetIdAndDateBetween(pet.getId(), startTime, finishTime);
            LOGGER.warn("Нашел: " + report.getId() + ", " + report.getPet().getName());
        }

        if (customerPetList.size() == 1) {
            responseText = ANSWER_ONE_PET;
            customer.setDialogStatus(STATUS_WAIT_REPORT + DELIMITER + "0");
            customerService.updateCustomer(customer);
        } else if (customerPetList.size() > 1) {
            StringBuilder sb = new StringBuilder(ANSWER_ENTER_PET_ID + CARRIAGE_RETURN);
            for (Pet pet : customerPetList) {
                long petId = pet.getId();
                String petName = pet.getName();
                sb.append(petId).append(". ").append(petName).append(CARRIAGE_RETURN);
            }
            responseText = sb.toString();
            customer.setDialogStatus(STATUS_WAIT_PET_ID);
            customerService.updateCustomer(customer);
        }

        LOGGER.debug("STEP 1: " + responseText);
        return responseText;
    }

    /**
     * Обработка сообщения боту, если статус диалога с покупателем "wait_pet_id"
     * @return
     */
    private String commandSubmitReportStep2() {
        LOGGER.debug("STEP 2: Run");
        responseText = ANSWER_NON_EXISTENT_PET;
        List<String> validIdList = petService.findPetsByCustomerOrderById(customer).stream()
                .map(Pet::getId)
                .map(Object::toString)
                .collect(Collectors.toList());

        if (validIdList.contains(userMessage)) {
            responseText = ANSWER_SEND_REPORT_FOR_PET_WITH_ID + userMessage;
            customer.setDialogStatus(STATUS_WAIT_REPORT + ":" + userMessage);
            customerService.updateCustomer(customer);
        }
        LOGGER.debug("STEP 2: " + responseText);
        return responseText;
    }

    /**
     * Обработка сообщения боту, если статус диалога с покупателем
     *      "wait_report" или "wait_report:X", где X - id животного
     * @return
     */
    private String commandSubmitReportStep3() {
        LOGGER.debug("STEP 3: Run");
        LOGGER.debug("petId = " + petId);

        // todo: Логика получения отчета
        // Если в базе уже есть запись по этому отчету, то
        //  - если есть текст, нужно фото,
        //  - если есть фото, нужно текст
        // как определить, что этот отчет уже есть в базе? по дате конечно.
        // если сегодня уже сдавался отчет, то выдать "Вы уже отправляли отчет сегодня"
        // если есть часть отчета, то выдать, что нужно отправить вторую часть

        if (photos != null && caption != null) {
            LOGGER.debug("Есть картинка и текст");
            responseText = ANSWER_REPORT_ACCEPTED;
            customer.setDialogStatus(STATUS_FREE);
            customerService.updateCustomer(customer);
//            savePhotoToDB(telegramBot);
        } else if (photos != null) {
            LOGGER.debug("Есть картинка без текста");
            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
//            savePhotoToDB(telegramBot);
        } else if (userMessage != null) {
            LOGGER.debug("Есть текст без картинки");
            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
        }

        LOGGER.debug("STEP 3: " + responseText);
        return responseText;
    }

    /**
     * Анализ текущего статуса диалога, поиск в нём id животного
     */
    private void analysisDialogStatus() {
        if (dialogStatus.contains(":")) {
            String[] dialogStatusArray = dialogStatus.split(DELIMITER);
            int dialogStatusArrayCount = dialogStatusArray.length;
            if (dialogStatusArrayCount > 2) {
                StringBuilder sb = new StringBuilder(dialogStatusArray[0]);
                for (int i = 1; i < dialogStatusArrayCount - 2; i++) {
                    sb.append(DELIMITER).append(dialogStatusArray[i]);
                }
                dialogStatusArray[0] = sb.toString();
            }
            petId = Integer.parseInt(dialogStatusArray[dialogStatusArray.length-1]);
            dialogStatus = dialogStatusArray[0];
        }
    }

    /**
     * Получение картинки с сервера Telegram и запись ее в базу
     * @param telegramBot
     */
    private void savePhotoToDB(TelegramBot telegramBot) {

        // todo: Запись картинки в БД
        // https://www.baeldung.com/java-download-file

        String photoId = Arrays.stream(photos)
                .filter(e -> e.width() == 800)
                .map(e -> e.fileId())
                .findAny()
                .get();
        GetFile getFileRequest = new GetFile(photoId);
        GetFileResponse getFileResponse = telegramBot.execute(getFileRequest);
        File file = getFileResponse.file();

        String fullPath = telegramBot.getFullFilePath(file);

        LOGGER.debug("photoId: " + photoId);
        LOGGER.debug("PHOTO1: " + file.filePath());
        LOGGER.debug("PHOTO2: " + fullPath);
    }
}
