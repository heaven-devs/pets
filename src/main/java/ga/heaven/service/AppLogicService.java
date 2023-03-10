package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import com.pengrad.telegrambot.model.Message;
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
    private final CustomerContextService customerContextService;
    
    private final MsgService msgService;
    
    
    public AppLogicService(InfoService infoService, CustomerService customerService, CustomerContextService customerContextService, MsgService msgService) {
        this.infoService = infoService;
        this.customerService = customerService;
        this.customerContextService = customerContextService;
        this.msgService = msgService;
    }
    
    public void sendDatingRules(Long chatId) {
        Info info = infoService.findInfoByArea(DATING_RULES_FIELD);
        if (info == null) {
            msgService.sendMsg(chatId, DATING_RULES_NOT_FOUND);
        } else {
            msgService.sendMsg(chatId, info.getInstructions());
        }
    }
    
    public void initConversation(Long chatId) {
        if (!customerService.isPresent(chatId)) {
            msgService.sendMsg(chatId,infoService.findInfoByArea(COMMON_INFO_FIELD).getInstructions());
            createNewCustomer(chatId);
        }
        msgService.sendMsg(chatId, SHELTER_CHOOSE_MSG, msgService.selectShelter());
    }

    private void createNewCustomer(Long chatId) {
        Customer customer = customerService.createCustomer(chatId);
        CustomerContext customerContext = customerContextService.create(customer);
        customer.setCustomerContext(customerContext);
        customerService.updateCustomer(customer);
    }

    public void volunteerRequest(Message inputMessage) {

    }

    public void sendDocuments(Long chatId) {
        Info info = infoService.findInfoByArea(DOCUMENTS_FIELD);
        if (info == null) {
            msgService.sendMsg(chatId, DOCUMENTS_NOT_FOUND);
        } else {
            msgService.sendMsg(chatId, info.getInstructions());
        }
    }
}
