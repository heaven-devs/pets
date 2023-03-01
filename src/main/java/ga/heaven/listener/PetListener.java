package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import ga.heaven.model.Info;
import ga.heaven.service.InfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PetListener {
    private final String DATING_RULES = "dating_rules";
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final InfoService infoService;

    public PetListener(TelegramBot telegramBot, InfoService infoService) {
        this.telegramBot = telegramBot;
        this.infoService = infoService;
    }

    /**
     * Метод processUpdate обрабатывает команду пользователя из чат бота
     * @param update - объект пользовательского сообщения из Telegram-чата
     */
    public void processUpdate(Update update) {
        LOGGER.debug("Processing update: {}", update);
        Long chatId=null;
        String text;

        try {
            chatId = update.message().chat().id();
            text = update.message().text();
            LOGGER.debug("Отправленное сообщение: Hello! Section: dating-rules");

            switch (text){
                case "/dating_rules":
                    getDatingRules(chatId);
                    break;
            }
        } catch (RuntimeException e) {
            LOGGER.debug(e.getMessage());
        }
    }

    private void getDatingRules(Long chatId) {
        Info info = infoService.findInfoByArea(DATING_RULES);
        String text;
        if (info == null) {
            text = "Информация по обращению с питомцами не найдена. Обратитесь к администрации";
        } else {
            text = info.getInstructions();
        }
        SendMessage message = new SendMessage(chatId, text);
        telegramBot.execute(message);
    }
}
