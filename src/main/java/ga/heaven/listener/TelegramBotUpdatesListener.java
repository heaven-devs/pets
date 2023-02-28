package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import ga.heaven.model.Customer;
import ga.heaven.repository.CustomerRepository;
import ga.heaven.repository.InfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    
    private final TelegramBot telegramBot;
    
    private final CustomerRepository customerRepository;
    private final InfoRepository infoRepository;

    private final ReportListener reportListener;
    
    public TelegramBotUpdatesListener(TelegramBot telegramBot, CustomerRepository customerRepository, InfoRepository infoRepository, ReportListener reportListener) {
        this.telegramBot = telegramBot;
        this.customerRepository = customerRepository;
        this.infoRepository = infoRepository;
        this.reportListener = reportListener;
    }
    
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    
    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(this::processUpdate);
        } catch (Exception e) {
            
            LOGGER.debug(e.getMessage());
        } finally {
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
        
    }
    
    private void processUpdate(Update update) {
        LOGGER.debug("Processing update: {}", update);
        Long chatId = null;
        String text = null;
        
        
        try {
            text = update.message().text();
            chatId = update.message().chat().id();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }
        if (text == null || text.isBlank()) {
            LOGGER.debug("blank msg from " + chatId);
            return;
        }
        
        if ("/start".equals(text)) {
            if (!this.customerRepository.findCustomerByChatId(chatId).isPresent()) {
                sendMessage(chatId,
                        this.infoRepository
                                .findFirstByAreaContainingIgnoreCase("common_info")
                                .orElseThrow()
                                .getInstructions());
                Customer customerRecord = new Customer();
                customerRecord.setChatId(chatId);
                LOGGER.debug(String.valueOf(chatId));
                customerRecord = customerRepository.save(customerRecord);
                
            }
            SendMessage sendMessage = new SendMessage(chatId, "shelter choice:");
//            SendResponse response;
            InlineKeyboardMarkup kbMarkup = new InlineKeyboardMarkup();
            InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton("shelter1");
            InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton("shelter2");
            keyboardButton1.callbackData(keyboardButton1.text());
            keyboardButton2.callbackData(keyboardButton2.text());
            kbMarkup.addRow(keyboardButton1, keyboardButton2);
//            response =
            telegramBot.execute(sendMessage.replyMarkup(kbMarkup));
            return;
        }
        reportListener.processingReportQueries(update, telegramBot);
        
        
    }
    
    private void sendMessage(Long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}
