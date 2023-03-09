package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.DATING_RULES_CMD;
import static ga.heaven.configuration.Constants.VOLUNTEER_REQUEST_CMD;

@Service
public class VolunteerSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VolunteerSelectorService.class);
    private final AppLogicService appLogicService;
    
    private final MsgService msgService;
    
    public VolunteerSelectorService(AppLogicService appLogicService, MsgService msgService) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
    }
    
    public void switchCmd(Message inputMessage) {
        
        switch (inputMessage.text()) {
            case VOLUNTEER_REQUEST_CMD:
                appLogicService.volunteerRequest(inputMessage);
                break;
        }
    }
    
    
}
