package ga.heaven.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
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
    private final ShelterSelectorService shelterSelectorService;

    public CmdSelectorService(MsgService msgService, AppLogicService appLogicService, PetSelectorService petSelectorService, VolunteerSelectorService volunteerSelectorService, ReportSelectorService reportSelectorService, ShelterService shelterService, CustomerService customerService, ShelterSelectorService shelterSelectorService, NavigationService navigationService) {
        this.msgService = msgService;
        this.appLogicService = appLogicService;
        this.petSelectorService = petSelectorService;
        this.volunteerSelectorService = volunteerSelectorService;
        this.reportSelectorService = reportSelectorService;
        this.shelterService = shelterService;
        this.customerService = customerService;
        this.shelterSelectorService = shelterSelectorService;
        this.navigationService = navigationService;
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
            if (Pattern.compile(DYNAMIC_ENDPOINT_REGEXP).matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", inputMessage);
                
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", inputMessage);
                switch (inputMessage.text()) {
                    
                    case START_CMD:
                        appLogicService.initConversation(inputMessage.chat().id());
                        msgService.deleteMsg(inputMessage.chat().id(), inputMessage.messageId());
                        return;
                    
                    default:
                        break;
                }

                shelterSelectorService.switchCmd(inputMessage);
                petSelectorService.switchCmd(inputMessage);
                volunteerSelectorService.switchCmd(inputMessage);
            } else {
                LOGGER.debug("Constant not command message\n{}\nsent to: switchText methods", inputMessage);
                shelterSelectorService.switchText(inputMessage);

            }
        }
    }
    
    public void processingCallBackQuery(CallbackQuery cbQuery) {
        msgService.sendCallbackQueryResponse(cbQuery.id());
        InlineKeyboardMarkup kbMarkup;
        if ((cbQuery.data() != null)
                && (cbQuery.message() != null)
                && (cbQuery.message().chat() != null)
                && (cbQuery.message().chat().id() != null)
        ) {
            final Matcher matcher = Pattern.compile(DYNAMIC_ENDPOINT_REGEXP).matcher(cbQuery.data());
            if (matcher.matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", cbQuery);
                if (matcher.group(1).equals("shelter")) {
                    Shelter selectedShelter = shelterService.findById(Long.valueOf(matcher.group(2)));
                    Customer customer = customerService.findCustomerByChatId(cbQuery.message().chat().id());
                    CustomerContext context = customer.getCustomerContext();
                    context.setShelterId(selectedShelter.getId());
                    customerService.updateCustomer(customer);

                    kbMarkup = new InlineKeyboardMarkup();

                    navigationService.findByParentId(1L).forEach(button -> {
                        kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                    });


                    msgService.interactiveMsg(cbQuery.message().chat().id(),
                            kbMarkup,
                            "" + selectedShelter.getName()
                                    + " selected.");
                }
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(cbQuery.data()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", cbQuery);

                switch (cbQuery.data()) {
                    case "/shelters":
                        kbMarkup = new InlineKeyboardMarkup();
                        shelterService.findAll().forEach(shelter -> {
                            kbMarkup.addRow(new InlineKeyboardButton(shelter.getName()).callbackData("/shelter/" + shelter.getId()));
                        });
                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                null);
                        return;
                    case "/how-adopt":
                        kbMarkup = new InlineKeyboardMarkup();
                        Customer customer = customerService.findCustomerByChatId(cbQuery.message().chat().id());
                        CustomerContext context = customer.getCustomerContext();

                        navigationService.findByParentId(4L).forEach(button -> {
                            if((button.getShelterId() == null) ||  (button.getShelterId().getId() == context.getShelterId())) {
                                kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                            }
                        });

                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                null);
                        return;
                    case "/shelter":
                        kbMarkup = new InlineKeyboardMarkup();

                        navigationService.findByParentId(3L).forEach(button -> {
//                            button.getShelterId()
                            kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                        });
                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                null);
                        return;

                    case "/main":
                        kbMarkup = new InlineKeyboardMarkup();

                        navigationService.findByParentId(1L).forEach(button -> {
                            kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                        });
                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                null);
                        return;
                }


                petSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
                volunteerSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
            }
        }
    }
}
    
    
