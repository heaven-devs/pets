package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import ga.heaven.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ReportListener {
    private final static Logger logger = LoggerFactory.getLogger(ReportListener.class);

    private final ReportRepository reportRepository;
    private Update update;
    private TelegramBot telegramBot;

    public ReportListener(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void processingReportQueries(Update update, TelegramBot telegramBot) {
        if (this.update == null) {
            this.update = update;
        }
        if (this.telegramBot == null) {
            this.telegramBot = telegramBot;
        }
        processingSubmitReport();
    }

    private void processingSubmitReport() {
        if (update.message().text().equals("/submit_report")) {
            long chatId = update.message().chat().id();
            logger.info(String.valueOf(chatId));
            String messageText = "проверка";
            SendMessage message = new SendMessage(chatId, messageText);
            SendResponse response = telegramBot.execute(message);
        }
    }
}
