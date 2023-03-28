package ga.heaven.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
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

    private final ReportService reportService;
    private final PetService petService;
    private final TelegramBot telegramBot;
    private final ReportPhotoRepository reportPhotoRepository;

    private TgIn in;
    private String responseText;

    public ReportSelectorService(ReportService reportService, PetService petService,
                                 TelegramBot telegramBot, ReportPhotoRepository reportPhotoRepository) {
        this.reportService = reportService;
        this.petService = petService;
        this.telegramBot = telegramBot;
        this.reportPhotoRepository = reportPhotoRepository;
    }

    /**
     * Обрабатываю текстовые сообщения пользователя (которые начинаются не с /) и фото
     *
     * @param in сообщение от пользователя
     */
    public void processingNonCommandMessagesForReport(TgIn in) {
        this.in = in;
        Context context = in.getCustomer().getCustomerContext().getDialogContext();

        switch (context) {
            case WAIT_REPORT:
                responseText = processingMsgWaitReport();
                new TgOut()
                        .tgIn(in)
                        .textBody(responseText)
                        .generateMarkup(5L)
                        .send()
                        .save()
                ;
                break;
            case FREE:
                responseText = addAdditionalPhoto();
                break;
        }
    }

    /**
     * Получил текст/фото от пользователя (не команду). Метод формирует ответ пользоваетлю
     * и записывает данные в БД, когда пользователь отправляет отчет
     *
     * @return текст ответа пользователю
     */
    private String processingMsgWaitReport() {
        Long petId = in.getCustomer().getCustomerContext().getCurrentPetId();
        Pet pet = petService.read(petId);
        Report report = reportService.findTodayReportByPetId(petId);
        report = (null == report) ? new Report() : report;
        report.setPet(pet);
        report.setDate(LocalDateTime.now());
        responseText = REPORT_WAIT_REPORT;

        if (isHavePhotoInReport()) {
            savePhotoToDB(report);
            responseText = REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
        }

        if (isHaveTextInReport(report)) {
            responseText = REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
            report.setPetReport(getReportText());
            reportService.updateReport(report);
        }

        if (isHavePhotoAndTextInReport(report)) {
            responseText = REPORT_ACCEPTED;
            in.getCustomer().getCustomerContext().setDialogContext(FREE);
        }

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
        Pet pet = petService.read(petId);

        in.getCustomer().getCustomerContext().setDialogContext(WAIT_REPORT);
        in.getCustomer().getCustomerContext().setCurrentPetId(petId);

        Report report = reportService.findTodayReportByPetId(petId);
        if (report != null) {
            report.setDate(LocalDateTime.now());
        }

        if (report == null) {
            responseText = REPORT_WAIT_REPORT + "\"" + pet.getName() + "\"";
        } else if (report.getPetReport() != null && isHavePhotoInCurrentReportFromDB(report)) {
            responseText = REPORT_ACCEPTED;
        } else if (report.getPetReport() != null) {
            responseText = REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
        } else {
            responseText = REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
        }

        return responseText;
    }

    /**
     * Имеются ли в базе текст отчета, а в сообщении от пользователя фото,
     * или наоборот в базе фото, а в сообщении от пользователя текст отчета? Т.е. есть и фото и текст отчета.
     *
     * @param report проверяемый отчет
     * @return имеется или нет
     */
    private boolean isHavePhotoAndTextInReport(Report report) {
        return (in.message().photo() != null || (report != null && isHavePhotoInCurrentReportFromDB(report)))
                && (in.message().caption() != null || in.text() != null || (report != null && report.getPetReport() != null));
    }

    /**
     * Имеется ли в текущем сообщении от пользователя картинка (фото)
     *
     * @return имеется или нет
     */
    private boolean isHavePhotoInReport() {
        return in.message().photo() != null;
    }

    /**
     * Имеется ли в текущем сообщении от пользователя фото
     *
     * @param todayReport текущий отчет
     * @return имеется или нет
     */
    private boolean isHaveTextInReport(Report todayReport) {
        return (todayReport == null || todayReport.getPetReport() == null)
                && (in.text() != null || (in.text() == null && in.message().caption() != null));
    }

    /**
     * Имеется ли фото в текущем отчете
     *
     * @param report текущий отчет
     * @return имеется или нет
     */
    private boolean isHavePhotoInCurrentReportFromDB(Report report) {
        return reportPhotoRepository.findFirstByReportId(report.getId()) != null;
    }

    /**
     * Метод выбирает откуда брать текст отчета .text или .caption
     *
     * @return текст отчета
     */
    private String getReportText() {
        return in.text() != null
                ? in.text()
                : in.message().caption();
    }

    /**
     * Метод добавляет к отчету дополнительные фото и возвращает сообщение пользователю об успехе добавления картинки в отчет.
     *
     * @return Сообщение пользователю
     */
    private String addAdditionalPhoto() {
        Long petId = in.getCustomer().getCustomerContext().getCurrentPetId();
        Report todayReport = reportService.findTodayReportByPetId(petId);
        if (in.message().photo() == null || todayReport == null) {
            return "";
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime reportTime = todayReport.getDate();
        long diffTime = ChronoUnit.SECONDS.between(reportTime, currentTime);
        if (diffTime < 180) {
            savePhotoToDB(todayReport);
            responseText = REPORT_PHOTO_ADD_TO_REPORT;
        } else {
            in.getCustomer().getCustomerContext().setDialogContext(FREE);
            in.getCustomer().getCustomerContext().setCurrentPetId(0L);
            responseText = REPORT_UNRECOGNIZED_PHOTO;
        }
        return responseText;
    }

    /**
     * Метод получает фото и записывает его в БД
     */
    private void savePhotoToDB(Report report) {
        reportService.updateReport(report);
        PhotoSize[] photoSizes = in.message().photo();
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
