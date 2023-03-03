package ga.heaven.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import ga.heaven.listener.TelegramBotUpdatesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.SHELTER1_CMD;
import static ga.heaven.configuration.Constants.SHELTER2_CMD;

@Service
public class MsgService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    
    private final TelegramBot telegramBot;
    
    
    public MsgService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;

    }
    
    
    public ReplyKeyboardMarkup selectShelter() {
        LOGGER.info("Shelters keyboard viewed");
        return new ReplyKeyboardMarkup(
                SHELTER1_CMD, SHELTER2_CMD)
                .resizeKeyboard(true)
                .selective(true);
    }
    
    public void sendMsg(Long chatId, String inputMessage) {
        sendMsg(chatId, inputMessage, null);
    }
    
    public void sendMsg(Long chatId, String inputMessage, Keyboard keyboard) {
        SendMessage outputMessage = new SendMessage(chatId, inputMessage);
        if (keyboard != null) {
            outputMessage.replyMarkup(keyboard);
        }
        try {
            telegramBot.execute(outputMessage);
        } catch (Exception e) {
            LOGGER.info("Exception was thrown in sendMessage method with keyboard ");
            e.printStackTrace();
        }
    }
}
