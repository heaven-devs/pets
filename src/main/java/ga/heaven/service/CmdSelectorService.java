package ga.heaven.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            LOGGER.debug("Message\n{}\nsent to: reportSelectorService.switchCmd", inputMessage);
            reportSelectorService.switchCmd(inputMessage);
        }
        
        if ((inputMessage.text() != null)
                && (inputMessage.chat() != null)
                && (inputMessage.chat().id() != null)
        ) {
            if (Pattern.compile("^/(.*)/(.*)[0-9]*").matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", inputMessage);
                
            } else if (Pattern.compile("^/([^/]*)$").matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", inputMessage);
                switch (inputMessage.text()) {
                    
                    case START_CMD:
                        appLogicService.initConversation(inputMessage.chat().id());
                        return;
                    
                    default:
                        break;
                }
                
                petSelectorService.switchCmd(inputMessage);
                volunteerSelectorService.switchCmd(inputMessage);
            }
        }
    }
    
    public void processingCallBackQuery(CallbackQuery cbQuery) {
        msgService.sendCallbackQueryResponse(cbQuery.id());
        
        if ((cbQuery.data() != null)
                && (cbQuery.message() != null)
                && (cbQuery.message().chat() != null)
                && (cbQuery.message().chat().id() != null)
        ) {
            if (Pattern.compile("^/(.*)/(.*)[0-9]*").matcher(cbQuery.data()).matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", cbQuery);
                
            } else if (Pattern.compile("^/([^/]*)$").matcher(cbQuery.data()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", cbQuery);
                petSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
                volunteerSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
            }
        }
    }
}
    
    
