package ga.heaven.service;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ga.heaven.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.constants.ReportConstants.*;

@Service
public class AppLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogicService.class);
    
    private final InfoService infoService;
    
    private final CustomerService customerService;
    
    private final MsgService msgService;
    
    
    public AppLogicService(InfoService infoService, CustomerService customerService, MsgService msgService) {
        this.infoService = infoService;
        this.customerService = customerService;
        this.msgService = msgService;
    }
    
    public String getDatingRules() {
        Info info = infoService.findInfoByArea(DATING_RULES_FIELD);
        if (info == null) {
            return("Информация по обращению с питомцами не найдена. Обратитесь к администрации");
        } else {
            return(info.getInstructions());
        }
    }
    
    public void initConversation(Long chatId) {
        if (!customerService.isPresent(chatId)) {
            msgService.sendMsg(chatId,infoService.findInfoByArea(COMMON_INFO_FIELD).getInstructions());
            customerService.createCustomer(chatId);
        }
        msgService.sendMsg(chatId, SHELTER_CHOOSE_MSG, msgService.selectShelter());
    }

    public String processingSubmitReport() {
        LOGGER.debug("submit send");
        return "submit report";
    }
//        LOGGER.debug("dialogStatus = " + dialogStatus);
/*        LOGGER.debug("userMessage = " + userMessage);



        if (userMessage != null && userMessage.equals(COMMAND_SUBMIT_REPORT)) {
            responseText = commandSubmitReportStep1();
        } else if (dialogStatus.equals(STATUS_WAIT_PET_ID)) {
            responseText = commandSubmitReportStep2();
        } else if (dialogStatus.equals(STATUS_WAIT_REPORT)) {
            responseText = commandSubmitReportStep3();
        }
        SendMessage message = new SendMessage(chatId, responseText)
                .parseMode(ParseMode.HTML);
        telegramBot.execute(message);
    }

 */
}
