package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;

@Service
public class CmdSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSelectorService.class);
    private final MsgService msgService;
    private final AppLogicService appLogicService;
    private final PetSelectorService petSelectorService;
    private final VolunteerSelectorService volunteerSelectorService;
    
    private final ReportSelectorService reportSelectorService;
    
    public CmdSelectorService(MsgService msgService, AppLogicService appLogicService, PetSelectorService petSelectorService, VolunteerSelectorService volunteerSelectorService, ReportSelectorService reportSelectorService) {
        this.msgService = msgService;
        this.appLogicService = appLogicService;
        this.petSelectorService = petSelectorService;
        this.volunteerSelectorService = volunteerSelectorService;
        this.reportSelectorService = reportSelectorService;
    }
    
    public void processingMsg(Message inputMessage) {
        if (inputMessage.text() != null || inputMessage.photo() != null) {
            reportSelectorService.switchCmd(inputMessage);
        }

        if (inputMessage.text() != null) {
            petSelectorService.switchCmd(inputMessage);
            volunteerSelectorService.switchCmd(inputMessage);

            switch (inputMessage.text()) {
                
                // peripheral commands
                
                case START_CMD:
                    appLogicService.initConversation(inputMessage.chat().id());
                    break;
                
                default:
                    break;
            }
        }
    }
}
    
    
