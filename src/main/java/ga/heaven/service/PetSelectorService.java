package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;

@Service
public class PetSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetSelectorService.class);
    private final AppLogicService appLogicService;
    
    private final MsgService msgService;
    
    public PetSelectorService(AppLogicService appLogicService, MsgService msgService) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
    }
    
    public void switchCmd(Message inputMessage) {
        
        switch (inputMessage.text()) {
            case DATING_RULES_CMD:
                appLogicService.sendDatingRules(inputMessage.chat().id());
                break;
            case DOCUMENTS_CMD:
                appLogicService.sendDocuments(inputMessage.chat().id());
        }
    }
    
    
}
