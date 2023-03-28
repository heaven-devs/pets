package ga.heaven.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import ga.heaven.model.*;
import ga.heaven.model.CustomerContext.Context;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.repository.ReportPhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ga.heaven.configuration.Constants.*;
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
    private final NavigationService navigationService;
    private final ReportPhotoRepository reportPhotoRepository;

    private TgIn in;
    private Customer customer;
    private String responseText;

    public ReportSelectorService(AppLogicService appLogicService, MsgService msgService, ReportService reportService, CustomerService customerService, PetService petService,
                                 TelegramBot telegramBot, ReportPhotoRepository reportPhotoRepository, NavigationService navigationService) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
        this.telegramBot = telegramBot;
        this.reportPhotoRepository = reportPhotoRepository;
        this.navigationService = navigationService;
    }

    /**
     * Обрабатываю текстовые сообщения пользователя (которые начинаются не с /) и фото
     *
     * @param in сообщение от пользователя
     */
    public void processingNonCommandMessagesForReport(TgIn in) {
        this.in = in;
        Context context = in.getCustomer().getCustomerContext().getDialogContext();
        LOGGER.error("context: " + context);

        switch (context) {
            case WAIT_REPORT: responseText = processingMsgWaitReport(); break;
            case FREE: responseText = addAdditionalPhoto(); break;
        }

        new TgOut()
                .tgIn(in)
                .setTextMessage(responseText)
                .generateMarkup(5L)
                .send()
                .save()
        ;
    }

    /**
     * Получил текст/фото от пользователя (не команду). Метод формирует ответ пользоваетлю
     * и записывает данные в БД, когда пользователь отправляет отчет
     *
     * @return текст ответа пользователю
     */
    private String processingMsgWaitReport() {
        LOGGER.error("processingMsgWaitReport-start");
        Long petId = in.getCustomer().getCustomerContext().getCurrentPetId();
        Pet pet = petService.read(petId);
        Report todayReport = reportService.findTodayReportByPetId(petId);
        todayReport = (null == todayReport) ? new Report() : todayReport;
        todayReport.setPet(pet);
        todayReport.setDate(LocalDateTime.now());
        responseText = ANSWER_WAIT_REPORT;

        if (isHavePhotoInReport()) {
            LOGGER.error("isHavePhotoInReport");
            savePhotoToDB(todayReport);
            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
        }

        if (isHaveTextInReport(todayReport)) {
            LOGGER.error("isHaveTextInReport");
            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
            todayReport.setPetReport(getReportText());
            reportService.updateReport(todayReport);
        }

        if (isHavePhotoAndTextInReport(todayReport)) {
            LOGGER.error("isHavePhotoAndTextInReport");
            responseText = ANSWER_REPORT_ACCEPTED;
            appLogicService.updateCustomerContext(customer, FREE);
        }
        LOGGER.error("processingMsgWaitReport-exit");

        return responseText;
    }

    /**
     * Обработка нажатия кнопки меню с одним из питомцев.
     *
     * @param in сообщение от пользователя (нажатие кнопки)
     * @return ответ бота
     */
    public String processingPetChoice(TgIn in) {
        this.in = in;
        long petId = in.endpoint().getValueAsLong();
        customer = customerService.findCustomerByChatId(in.chatId());
        customer.getCustomerContext().setDialogContext(WAIT_REPORT);
        customer.getCustomerContext().setCurrentPetId(petId);
        customerService.updateCustomer(customer);

        Pet pet = petService.read(petId);

        Report todayReport = reportService.findTodayReportByPetId(petId);
        if (todayReport != null) {
            todayReport.setDate(LocalDateTime.now());
        }

        if (todayReport == null) {
//            responseText = ANSWER_WAIT_REPORT + "\"" + pet.getName() + "\"";
            responseText = ANSWER_WAIT_REPORT + "\"" + pet.getName() + "\"";
        } else if (todayReport.getPetReport() != null && isHavePhotoInCurrentReportFromDB(todayReport)) {
            responseText = ANSWER_REPORT_ACCEPTED;
        } else if (todayReport.getPetReport() != null) {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
        } else {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
        }

        return responseText;
    }

    /**
     * Имеются ли в базе текст отчета, а в сообщении от пользователя фото,
     * или наоборот в базе фото, а в сообщении от пользователя текст отчета? Т.е. есть и фото и текст отчета.
     * @param report проверяемый отчет
     * @return имеется или нет
     */
    private boolean isHavePhotoAndTextInReport(Report report) {
        return (in.photo() != null || (report != null && isHavePhotoInCurrentReportFromDB(report)))
                && (in.message().caption() != null || in.text() != null || (report != null && report.getPetReport() != null));
    }

    /**
     * Имеется ли в текущем сообщении от пользователя картинка (фото)
     * @return имеется или нет
     */
    private boolean isHavePhotoInReport() {
        return in.photo() != null;
    }

    /**
     * Имеется ли в текущем сообщении от пользователя фото
     * @param todayReport текущий отчет
     * @return имеется или нет
     */
    private boolean isHaveTextInReport(Report todayReport) {
        return (todayReport == null || todayReport.getPetReport() == null)
                && (in.text() != null || (in.text() == null && in.message().caption() != null));
    }

    /**
     * Имеется ли фото в текущем отчете
     * @param report текущий отчет
     * @return имеется или нет
     */
    private boolean isHavePhotoInCurrentReportFromDB(Report report) {
        return reportPhotoRepository.findFirstByReportId(report.getId()) != null;
    }

    /**
     * Метод выбирает откуда брать текст отчета .text или .caption
     * @return текст отчета
     */
    private String getReportText() {
        return in.text() != null
                ? in.text()
                : in.message().caption();
    }

    /**
     * Метод добавляет к отчету дополнительные фото и возвращает сообщение пользователю об успехе добавления картинки в отчет.
     * @return Сообщение пользователю
     */
    private String addAdditionalPhoto() {
        LOGGER.error("addAdditionalPhoto");
        Long petId = customer.getCustomerContext().getCurrentPetId();
        Report todayReport = reportService.findTodayReportByPetId(petId);
        if (in.photo() == null || todayReport == null) {
            return "";
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime reportTime = todayReport.getDate();
        long diffTime = ChronoUnit.SECONDS.between(reportTime, currentTime);
        if (diffTime < 180) {
            savePhotoToDB(todayReport);
            responseText = ANSWER_PHOTO_ADD_TO_REPORT;
        } else {
            appLogicService.updateCustomerContext(customer, FREE, 0);
            responseText = ANSWER_UNRECOGNIZED_PHOTO;
        }
        return responseText;
    }

    /**
     * Метод получает фото и записывает его в БД
     */
    private void savePhotoToDB(Report report) {
        reportService.updateReport(report);

        PhotoSize[] photoSizes = in.photo();
        PhotoSize photoSize = Arrays.stream(photoSizes)
                .max(Comparator.comparing(PhotoSize::fileSize))
                .orElseThrow(RuntimeException::new);

        ReportPhoto reportPhoto = new ReportPhoto();
        reportPhoto.setReport(report);
        GetFile getFile = new GetFile(photoSize.fileId());
        GetFileResponse getFileResponse = telegramBot.execute(getFile);
        if (getFileResponse.isOk()) {
            File file = getFileResponse.file();
            String extension = StringUtils.getFilenameExtension(file.filePath());
            reportPhoto.setMediaType(extension);
            try {
                byte[] image = telegramBot.getFileContent(file);
                reportPhoto.setPhoto(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        reportPhotoRepository.save(reportPhoto);
    }
}
