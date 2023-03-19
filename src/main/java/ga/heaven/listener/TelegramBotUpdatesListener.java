package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import ga.heaven.service.CmdSelectorService;
import ga.heaven.service.MsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    
    private final TelegramBot telegramBot;
    
    private final CmdSelectorService cmdSelectorService;
    
    private final MsgService msgService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, CmdSelectorService cmdSelectorService, MsgService msgService) {
        this.telegramBot = telegramBot;
        this.cmdSelectorService = cmdSelectorService;
        this.msgService = msgService;
    }
    
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            LOGGER.info("Processing update: {}", update);
            Message msg = msgService.msgExtractor(update);
            cmdSelectorService.processingMsg(msg);
            //cmdSelectorService.processingCallBackQuery(cbQuery);
            
//            Message msg = update.message();
/*            if (msg != null) {
                cmdSelectorService.processingMsg(msg);
            }
            CallbackQuery cbQuery = update.callbackQuery();
            if (cbQuery != null) {
                cmdSelectorService.processingCallBackQuery(cbQuery);
            }*/
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
