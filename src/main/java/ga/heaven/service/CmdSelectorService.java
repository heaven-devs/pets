package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.MessageTemplate;
import ga.heaven.model.Shelter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ga.heaven.configuration.Constants.*;

@Service
public class CmdSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSelectorService.class);
    private static final String DYNAMIC_ENDPOINT_REGEXP = "^/(.*)/(.*)[0-9]*";
    private static final String STATIC_ENDPOINT_REGEXP = "^/([^/]*)$";
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
    
    public void processingMsg(Message inputMessage) {
        MessageTemplate messageTemplate;
        if (inputMessage.text() != null || inputMessage.photo() != null) {
            LOGGER.debug("Message\n{}\nsent to: reportSelectorService.switchCmd", inputMessage);
            reportSelectorService.switchCmd(inputMessage);
        }
        
        if ((inputMessage.text() != null)
                && (inputMessage.chat() != null)
                && (inputMessage.chat().id() != null)
        ) {
            final Matcher matcher = Pattern.compile(DYNAMIC_ENDPOINT_REGEXP).matcher(inputMessage.text());
            if (matcher.matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", inputMessage);
                final String ENDPOINT_NAME = matcher.group(1);
                final Long ENDPOINT_VALUE = Long.parseLong(matcher.group(2));
                switch (ENDPOINT_NAME) {
                    case SHELTER_EPT:
                        if (ENDPOINT_VALUE.equals(ENDPOINT_LIST)) {
                            messageTemplate = navigationService.prepareMessageTemplate(inputMessage.chat().id(), 2L);
                            shelterService.findAll().forEach(shelter -> {
                                messageTemplate.getKeyboard().addRow(new InlineKeyboardButton(shelter.getName()).callbackData("/shelter/" + shelter.getId()));
                            });
    
                            msgService.interactiveMsg(inputMessage.chat().id()
                                    ,messageTemplate.getKeyboard()
                                    ,messageTemplate.getText());
                        } else {
                            Shelter selectedShelter = shelterService.findById(Long.valueOf(matcher.group(2)));
                            Customer customer = customerService.findCustomerByChatId(inputMessage.chat().id());
                            CustomerContext context = customer.getCustomerContext();
                            context.setShelterId(selectedShelter.getId());
                            customerService.updateCustomer(customer);
    
                            messageTemplate = navigationService.prepareMessageTemplate(inputMessage.chat().id(), 1L);
                            msgService.interactiveMsg(inputMessage.chat().id()
                                    ,messageTemplate.getKeyboard()
                                    ,messageTemplate.getText());
                        }
                        return;
                }
                
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", inputMessage);
                switch (inputMessage.text()) {
                    
                    case START_CMD:
                        appLogicService.initConversation(inputMessage.chat().id());
                        msgService.deleteMsg(inputMessage.chat().id(), inputMessage.messageId());
                        return;
                        
                    case "/how-adopt":
                        messageTemplate = navigationService.prepareMessageTemplate(inputMessage.chat().id(), 4L);
                        msgService.interactiveMsg(inputMessage.chat().id()
                                ,messageTemplate.getKeyboard()
                                ,messageTemplate.getText());
                        return;
                        
                    case "/shelter":
                        messageTemplate = navigationService.prepareMessageTemplate(inputMessage.chat().id(), 3L);
                        msgService.interactiveMsg(inputMessage.chat().id()
                                ,messageTemplate.getKeyboard()
                                ,messageTemplate.getText());
                        return;
                    
                    case "/main":
                        messageTemplate = navigationService.prepareMessageTemplate(inputMessage.chat().id(), 1L);
                        msgService.interactiveMsg(inputMessage.chat().id()
                                ,messageTemplate.getKeyboard()
                                ,messageTemplate.getText());
                        return;
                    
                    default:
                        break;
                }
                petSelectorService.switchCmd(inputMessage);
                volunteerSelectorService.switchCmd(inputMessage);
            }
        }
    }
    
}
    
    
