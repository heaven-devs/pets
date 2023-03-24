package ga.heaven.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext.Context;
import ga.heaven.model.Pet;
import ga.heaven.model.Photo;
import ga.heaven.model.Report;
import ga.heaven.repository.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.configuration.ReportConstants.*;
import static ga.heaven.model.CustomerContext.Context.*;

@Service
public class ReportSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSelectorService.class);

    private final AppLogicService appLogicService;
    private final MsgService msgService;
    private final ReportService reportService;
    private final CustomerService customerService;
    private final PetService petService;
    private final TelegramBot telegramBot;
    private final PhotoRepository photoRepository;

    private Message inputMessage;
    private Customer customer;
    private String responseText;

    public ReportSelectorService(AppLogicService appLogicService, MsgService msgService, ReportService reportService, CustomerService customerService, PetService petService,
                                 TelegramBot telegramBot, PhotoRepository photoRepository) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
        this.telegramBot = telegramBot;
        this.photoRepository = photoRepository;
    }

    /**
     * метод проверяет были ли вызваны команды по работе с отчетом, или было отправлено сообщение с текстом/фото
     * @param inputMessage сообщение полученное от пользователя
     */
    public void switchCmd(Message inputMessage) {
        this.inputMessage = inputMessage;
        customer = customerService.findCustomerByChatId(inputMessage.chat().id());

        if (customer != null && inputMessage.text() != null
                && inputMessage.text().equals(REPORT_SUBMIT_CMD)) {
            msgService.sendMsg(inputMessage.chat().id(), processingSubmitReport());
        } else if (customer != null && inputMessage.text() == null || !inputMessage.text().startsWith("/") ) {
            msgService.sendMsg(inputMessage.chat().id(), processingUserMessages());
        }
    }

    /**
     * метод запускается, если пользователь отправил команду "/submit_report" и отправляет ответ пользователю
     * в зависимости от того сколько питомцев у пользователя, и по каким из них не сдан сегодня отчет.
     */
    private String processingSubmitReport() {
        List<Pet> customerPetList = petService.findPetsByCustomerOrderById(customer);
        System.out.println("customerPetList = " + customerPetList);
        if (customerPetList.isEmpty()) {
            appLogicService.updateCustomerContext(customer, FREE);
            return ANSWER_DONT_HAVE_PETS;
        }

        List<Pet> customerPetListWithoutTodayReport = getPetsWithoutTodayReport();
        if (customerPetListWithoutTodayReport.isEmpty()) {
            responseText = ANSWER_NO_NEED_TO_REPORT;
            appLogicService.updateCustomerContext(customer, FREE);
        } else if (customerPetListWithoutTodayReport.size() == 1) {
            Pet pet = customerPetListWithoutTodayReport.get(0);
            responseText = ANSWER_ONE_PET + "\"" + pet.getName() + "\"";
            appLogicService.updateCustomerContext(customer, WAIT_REPORT, pet.getId());
        } else {
            responseText = generateListOfCustomersPets(customerPetListWithoutTodayReport);
            appLogicService.updateCustomerContext(customer, WAIT_PET_ID, 0);
        }
        return responseText;
    }

    /**
     * Метод ищет питомцев пользователя, для которых сегодня не был сдан отчет.
     * @return список питомцев
     */
    private List<Pet> getPetsWithoutTodayReport() {
        List<Pet> currentCustomerPetList = petService.findPetsByCustomer(customer);
        List<Pet> petWithoutReportList = new ArrayList<>();
        for (Pet pet : currentCustomerPetList) {
            Report report = reportService.findTodayCompletedReportsByPetId(pet.getId());
            if (null == report) {
                petWithoutReportList.add(pet);
            }
        }
        return petWithoutReportList;
    }

    /**
     * Метод формирует сообщение пользователю, со списком его питомцев
     * @param customerPetList Список питомцев, на поручении у пользователя
     * @return список питомцев
     */
    private String generateListOfCustomersPets(List<Pet> customerPetList) {
        StringBuilder sb = new StringBuilder(ANSWER_ENTER_PET_ID + CR);
        for (Pet pet : customerPetList) {
            sb.append(pet.getId()).append(". ").append(pet.getName()).append(CR);
        }
        return sb.toString();
    }

    /**
     * Метод выбирает нужный для запуска метод в зависимости от контекста диалога с пользователем
     * @return текст ответа пользователю
     */
    private String processingUserMessages() {
        responseText = "";
        Context context = customer.getCustomerContext().getDialogContext();
        switch (context) {
            case WAIT_PET_ID: responseText = processingMsgWaitPetId(); break;
            case WAIT_REPORT: responseText = processingMsgWaitReport(); break;
            case FREE: addAdditionalPhoto(); break;
        }

        return responseText;
    }

    /**
     * Метод формирует сообщение пользователю, когда пользователь выбирает для какого животного хочет сдать отчет
     * @return текст ответа пользователю
     */
    private String processingMsgWaitPetId() {
        responseText = ANSWER_NON_EXISTENT_PET;
        List<String> validIdList = petService.findPetsByCustomerOrderById(customer).stream()
                .map(Pet::getId)
                .map(Object::toString)
                .collect(Collectors.toList());

        if (validIdList.contains(inputMessage.text())) {
            Report report = reportService.findTodayReportsByPetId(Long.parseLong(inputMessage.text()));
            if (report == null) {
                responseText = ANSWER_SEND_REPORT_FOR_PET_WITH_ID + inputMessage.text();
            } else if (report.getPetReport() == null) {
                responseText = ANSWER_PHOTO_REPORT_ACCEPTED;
            } else if (photoRepository.findAnyPhotoByReportId(report.getId()) == null) {
                responseText = ANSWER_TEST_REPORT_ACCEPTED;
            }
            appLogicService.updateCustomerContext(customer, WAIT_REPORT, Long.parseLong(inputMessage.text()));
        }
        return responseText;
    }

    /**
     * Метод формирует ответ пользоваетлю и записывает данные в БД, когда пользователь отправляет отчет
     * @return текст ответа пользователю
     */
    private String processingMsgWaitReport() {
        Long petId = customer.getCustomerContext().getCurrentPetId();
        Pet pet = petService.read(petId);
        Report todayReport = reportService.findTodayReportsByPetId(petId);
        todayReport = (null == todayReport) ? new Report() : todayReport;
        todayReport.setPet(pet);
        todayReport.setDate(LocalDateTime.now());
        responseText = ANSWER_WAIT_REPORT;

        if (isHavePhotoInReport()) {
            savePhotoToDB(todayReport);
            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
        }

        if (isHaveTextInReport(todayReport)) {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
            todayReport.setPetReport(getReportText());
            reportService.updateReport(todayReport);
        }

        if (isHavePhotoAndTextInReport(todayReport)) {
            responseText = ANSWER_REPORT_ACCEPTED;
            appLogicService.updateCustomerContext(customer, FREE);
        }

        return responseText;
    }

    public String processingWaitReport(Message inputMessage, long petId) {
        this.inputMessage = inputMessage;
        Pet pet = petService.read(petId);
        Report todayReport = reportService.findTodayReportsByPetId(petId);
        todayReport = (null == todayReport) ? new Report() : todayReport;
        todayReport.setPet(pet);
        todayReport.setDate(LocalDateTime.now());
        responseText = ANSWER_WAIT_REPORT;

        if (isCommand()) {
//            Report report = reportService.findTodayReportsByPetId(petId);
  //          appLogicService.updateCustomerContext(customer, WAIT_REPORT, Long.parseLong(inputMessage.text()))
        } else {
            LOGGER.error("введена не команда");
            return inputMessage.text() != null ? inputMessage.text() :
                    (inputMessage.photo() != null ? inputMessage.photo().toString() : "");
        }




//        if (isHavePhotoInReport()) {
//            savePhotoToDB(todayReport);
//            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
//        }
//
//        if (isHaveTextInReport(todayReport)) {
//            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
//            todayReport.setPetReport(getReportText());
//            reportService.updateReport(todayReport);
//        }
//
//        if (isHavePhotoAndTextInReport(todayReport)) {
//            responseText = ANSWER_REPORT_ACCEPTED;
//            appLogicService.updateCustomerContext(customer, FREE);
//        }

        return responseText;
    }

    private boolean isCommand() {
        return Pattern.compile(DYNAMIC_ENDPOINT_REGEXP).matcher(inputMessage.text()).matches();
    }

    /**
     * Имеются ли в базе текст отчета, а в сообщении от пользователя фото,
     * или наоборот в базе фото, а в сообщении от пользователя текст отчета? Т.е. есть и фото и текст отчета.
     * @param report проверяемый отчет
     * @return имеется или нет
     */
    private boolean isHavePhotoAndTextInReport(Report report) {
        return (inputMessage.photo() != null || (report != null && photoRepository.findAnyPhotoByReportId(report.getId()) != null))
                && (inputMessage.caption() != null || inputMessage.text() != null || (report != null && report.getPetReport() != null));
    }

    /**
     * Имеется ли в текущем сообщении от пользователя картинка (фото)
     * @return имеется или нет
     */
    private boolean isHavePhotoInReport() {
        return inputMessage.photo() != null;
    }

    /**
     * Имеется ли в текущем сообщении от пользователя фото
     * @param todayReport текущий отчет
     * @return имеется или нет
     */
    private boolean isHaveTextInReport(Report todayReport) {
        return (todayReport == null || todayReport.getPetReport() == null)
                && (inputMessage.text() != null || (inputMessage.text() == null && inputMessage.caption() != null));
    }

    /**
     * Метод выбирает откуда брать текст отчета .text или .caption
     * @return текст отчета
     */
    private String getReportText() {
        return inputMessage.text() != null
                ? inputMessage.text()
                : inputMessage.caption();
    }

    /**
     * Метод добавляет к отчету дополнительные фото
     */
    private void addAdditionalPhoto() {
        Long petId = customer.getCustomerContext().getCurrentPetId();
        Report todayReport = reportService.findTodayReportsByPetId(petId);
        if (inputMessage.photo() == null || todayReport == null) {
            return;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime reportTime = todayReport.getDate();
        long diffTime = ChronoUnit.SECONDS.between(reportTime, currentTime);
        if (diffTime < 180) {
            savePhotoToDB(todayReport);
        } else {
            appLogicService.updateCustomerContext(customer, FREE, 0);
        }
    }

    /**
     * Метод получает фото и записывает его в БД
     */
    private void savePhotoToDB(Report report) {
        reportService.updateReport(report);

        PhotoSize[] photoSizes = inputMessage.photo();
        PhotoSize photoSize = Arrays.stream(photoSizes)
                .max(Comparator.comparing(PhotoSize::fileSize))
                .orElseThrow(RuntimeException::new);

        Photo photo = new Photo();
        photo.setReport(report);
        GetFile getFile = new GetFile(photoSize.fileId());
        GetFileResponse getFileResponse = telegramBot.execute(getFile);
        if (getFileResponse.isOk()) {
            File file = getFileResponse.file();
            String extension = StringUtils.getFilenameExtension(file.filePath());
            photo.setMediaType(extension);
            try {
                byte[] image = telegramBot.getFileContent(file);
                photo.setPhoto(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        photoRepository.save(photo);
    }

    public void processingNonCommandMessagesForReport(Message inputMessage) {
        this.inputMessage = inputMessage;
        customer = customerService.findCustomerByChatId(inputMessage.chat().id());

        if (customer != null && inputMessage.text() != null
                && inputMessage.text().equals(REPORT_SUBMIT_CMD)) {
            msgService.sendMsg(inputMessage.chat().id(), processingSubmitReport());
        } else if (customer != null && inputMessage.text() == null || !inputMessage.text().startsWith("/") ) {
            msgService.sendMsg(inputMessage.chat().id(), processingUserMessages());
        }
    }
}
