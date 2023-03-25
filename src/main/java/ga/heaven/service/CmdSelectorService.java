package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ga.heaven.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.TgIn.Endpoint.Type.*;

@Service
public class CmdSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSelectorService.class);
    private final MsgService msgService;
    private final AppLogicService appLogicService;
    private final PetSelectorService petSelectorService;
    private final VolunteerSelectorService volunteerSelectorService;
    private final ReportSelectorService reportSelectorService;
    private final ShelterService shelterService;
    private final CustomerService customerService;
    private final NavigationService navigationService;
    
    
    public CmdSelectorService(MsgService msgService, AppLogicService appLogicService, PetSelectorService petSelectorService, VolunteerSelectorService volunteerSelectorService, ReportSelectorService reportSelectorService, ShelterService shelterService, CustomerService customerService, NavigationService navigationService) {
        this.msgService = msgService;
        this.appLogicService = appLogicService;
        this.petSelectorService = petSelectorService;
        this.volunteerSelectorService = volunteerSelectorService;
        this.reportSelectorService = reportSelectorService;
        this.shelterService = shelterService;
        this.customerService = customerService;
        this.navigationService = navigationService;
    }
    
    
    public void processingMsg(TgIn in) {
        Optional.ofNullable(in.getCallbackQueryId())
                .ifPresent(lId -> msgService.sendCallbackQueryResponse(lId.toString()));
        in.setCustomer(customerService.findCustomerByChatId(in.chatId()));
        
        MessageTemplate messageTemplate;
        if (in.text() != null || in.photo() != null) {
            LOGGER.debug("Message\n{}\nsent to: reportSelectorService.switchCmd", in);
            reportSelectorService.switchCmd(in.message());
        }
        
        if ((in.text() != null)
                && (in.chatId() != null)
        ) {
            if (DYNAMIC.equals(in.endpoint().getType())) {
                
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", in);
                switch (in.endpoint().getName()) {
                    case SHELTER_EPT:
                        if (ENDPOINT_LIST.equals(in.endpoint().getValueAsLong())) {
                            messageTemplate = navigationService.prepareMessageTemplate(in.chatId(), 2L);
                            in.getShelterList()
                                    .forEach(shelter -> messageTemplate.getKeyboard()
                                            .addRow(new InlineKeyboardButton(shelter.getName())
                                                    .callbackData("/" + SHELTER_EPT + "/" + shelter.getId())));
                            
                            msgService.interactiveMsg(in.chatId()
                                    , messageTemplate.getKeyboard()
                                    , messageTemplate.getText());
                        } else {
                            Shelter selectedShelter = in.currentShelter(in.endpoint().getValueAsLong());
                            Customer customer = in.getCustomer();
                            CustomerContext context = customer.getCustomerContext();
                            context.setShelterId(selectedShelter.getId());
                            customerService.updateCustomer(customer);
                            
                            messageTemplate = navigationService.prepareMessageTemplate(in.chatId(), 1L);
                            msgService.interactiveMsg(in.chatId()
                                    , messageTemplate.getKeyboard()
                                    , messageTemplate.getText());
                        }
                        return;
                }
                
            } else if (STATIC.equals(in.endpoint().getType())) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", in);
                switch (in.endpoint().getName()) {
                    case START_CMD:
                        appLogicService.initConversation(in.chatId());
                        Integer id = in.messageId();
                        LOGGER.debug(String.valueOf(id));
                        msgService.deleteMsg(in.chatId(), in.messageId());
                        return;
                    
                    case "/how-adopt":
                        messageTemplate = navigationService.prepareMessageTemplate(in.chatId(), 4L);
                        msgService.interactiveMsg(in.chatId()
                                , messageTemplate.getKeyboard()
                                , messageTemplate.getText());
                        return;
                    
                    case "/shelter":
                        messageTemplate = navigationService.prepareMessageTemplate(in.chatId(), 3L);
                        msgService.interactiveMsg(in.chatId()
                                , messageTemplate.getKeyboard()
                                , messageTemplate.getText());
                        return;
                    
                    case "/main":
                        messageTemplate = navigationService.prepareMessageTemplate(in.chatId(), 1L);
                        msgService.interactiveMsg(in.chatId()
                                , messageTemplate.getKeyboard()
                                , messageTemplate.getText());
                        return;
                    
                    default:
                        break;
                }
                petSelectorService.switchCmd(in.message());
                volunteerSelectorService.switchCmd(in.message());
            }
        }
    }
    
}
    
    
