package ga.heaven.service;

import ga.heaven.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;

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

}