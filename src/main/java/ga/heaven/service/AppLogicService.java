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
    
    /*public void sendDatingRules(Long chatId) {
        Info info = infoService.findInfoByArea(DATING_RULES_FIELD);
        if (info == null) {
            msgService.sendMsg(chatId, DATING_RULES_NOT_FOUND);
        } else {
            msgService.sendMsg(chatId, info.getInstructions());
        }
    }*/
    
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

    public void sendDatingRules(Long chatId) {
        sendMultipurpose(chatId, DATING_RULES_FIELD, DATING_RULES_NOT_FOUND);
    }
    public void sendDocuments(Long chatId) {
        sendMultipurpose(chatId, DOCUMENTS_FIELD, DOCUMENTS_NOT_FOUND);
    }

    public void sendTransportRules(Long chatId) {
        sendMultipurpose(chatId, TRANSPORT_FIELD, TRANSPORT_NOT_FOUND);
    }
    public void sendComfortPet(Long chatId) {
        sendMultipurpose(chatId, COMFORT_PET_FIELD, COMFORT_PET_NOT_FOUND);
    }

    public void sendComfortDog(Long chatId) {
        sendMultipurpose(chatId, COMFORT_DOG_FIELD, COMFORT_DOG_NOT_FOUND);
    }
    public void sendComfortHandicapped(Long chatId) {
        sendMultipurpose(chatId, COMFORT_HANDICAPPED_FIELD, COMFORT_HANDICAPPED_NOT_FOUND);
    }

    public void sendCynologistAdvice(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGIST_ADVICE_FIELD, CYNOLOGIST_ADVICE_NOT_FOUND);
    }

    public void sendCynologistsList(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGISTS_LIST_FIELD, CYNOLOGIST_LIST_NOT_FOUND);
    }

    public void sendReasonsRefusal(Long chatId) {
        sendMultipurpose(chatId, REASONS_REFUSAL_FIELD, REASONS_REFUSAL_NOT_FOUND);
    }



    private void sendMultipurpose(Long chatId, String areaField, String notFoundMsg) {
        Info info = infoService.findInfoByArea(areaField);
        if (info == null) {
            msgService.sendMsg(chatId, notFoundMsg);
        } else {
            msgService.sendMsg(chatId, info.getInstructions());
        }
    }
}
