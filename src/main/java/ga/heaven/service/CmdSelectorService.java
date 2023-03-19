package ga.heaven.service;

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

import static ga.heaven.configuration.Constants.START_CMD;

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
        InlineKeyboardMarkup kbMarkup;
        String caption;
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
                if (Long.parseLong(matcher.group(2)) == 0) {
                    switch (matcher.group(1)) {
                        case "shelter":
                            kbMarkup = new InlineKeyboardMarkup();
                            shelterService.findAll().forEach(shelter -> {
                                kbMarkup.addRow(new InlineKeyboardButton(shelter.getName()).callbackData("/shelter/" + shelter.getId()));
                            });
                            
                            caption = navigationService.findById(2L).getText() + ":";
                            
                            msgService.interactiveMsg(inputMessage.chat().id(),
                                    kbMarkup,
                                    caption);
                            return;
                    }
                } else {
                    switch (matcher.group(1)) {
                        case "shelter":
                            Shelter selectedShelter = shelterService.findById(Long.valueOf(matcher.group(2)));
                            Customer customer = customerService.findCustomerByChatId(inputMessage.chat().id());
                            CustomerContext context = customer.getCustomerContext();
                            context.setShelterId(selectedShelter.getId());
                            customerService.updateCustomer(customer);
                            
                            kbMarkup = new InlineKeyboardMarkup();
                            
                            navigationService.findByLevelView(1L).forEach(button -> {
                                kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                            });
                            caption = navigationService.findById(1L).getText() + ":";
                            
                            msgService.interactiveMsg(inputMessage.chat().id(),
                                    kbMarkup,
                                    caption);
                            return;
                    }
                }
                
                
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", inputMessage);
                switch (inputMessage.text()) {
                    
                    case START_CMD:
                        appLogicService.initConversation(inputMessage.chat().id());
                        msgService.deleteMsg(inputMessage.chat().id(), inputMessage.messageId());
                        return;
                    case "/how-adopt":
                        /*kbMarkup = new InlineKeyboardMarkup();
                        Customer customer = customerService.findCustomerByChatId(inputMessage.chat().id());
                        CustomerContext context = customer.getCustomerContext();*/
                        caption = navigationService.findById(4L).getText() + ":";
                        /*navigationService.findByLevelView(4L).forEach(button -> {
                            if ((button.getShelterId() == null) || (button.getShelterId().getId() == context.getShelterId())) {
                                kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                            }
                        });*/
                        
                        msgService.interactiveMsg(inputMessage.chat().id(),
                                navigationService.getButtons(inputMessage.chat().id(), 4L),
                                caption);
                        return;
                    case "/shelter":
                        kbMarkup = new InlineKeyboardMarkup();
                        
                        caption = navigationService.findById(3L).getText() + ":";
                        
                        navigationService.findByLevelView(3L).forEach(button -> {
//                            button.getShelterId()
                            kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                        });
                        msgService.interactiveMsg(inputMessage.chat().id(),
                                kbMarkup,
                                caption);
                        return;
                    
                    case "/main":
                        kbMarkup = new InlineKeyboardMarkup();
                        caption = navigationService.findById(1L).getText() + ":";
                        navigationService.findByLevelView(1L).forEach(button -> {
                            kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                        });
                        msgService.interactiveMsg(inputMessage.chat().id(),
                                kbMarkup,
                                caption);
                        return;
                    default:
                        break;
                }
                
                petSelectorService.switchCmd(inputMessage);
                volunteerSelectorService.switchCmd(inputMessage);
            }
        }
    }
    
    /*public void processingCallBackQuery(CallbackQuery cbQuery) {
        //msgService.sendCallbackQueryResponse(cbQuery.id());
        InlineKeyboardMarkup kbMarkup;
        String caption;
        if ((cbQuery.data() != null)
                && (cbQuery.message() != null)
                && (cbQuery.message().chat() != null)
                && (cbQuery.message().chat().id() != null)
        ) {
            final Matcher matcher = Pattern.compile(DYNAMIC_ENDPOINT_REGEXP).matcher(cbQuery.data());
            if (matcher.matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", cbQuery);
                if (Long.parseLong(matcher.group(2)) == 0) {
                    switch (matcher.group(1)) {
                        case "shelter":
                            kbMarkup = new InlineKeyboardMarkup();
                            shelterService.findAll().forEach(shelter -> {
                                kbMarkup.addRow(new InlineKeyboardButton(shelter.getName()).callbackData("/shelter/" + shelter.getId()));
                            });
                            
                            caption = navigationService.findById(2L).getText() + ":";
                            
                            msgService.interactiveMsg(cbQuery.message().chat().id(),
                                    kbMarkup,
                                    caption);
                            return;
                    }
                } else {
                    
                    
                    
                    
                    switch (matcher.group(1)) {
                        case "shelter":
                            Shelter selectedShelter = shelterService.findById(Long.valueOf(matcher.group(2)));
                            Customer customer = customerService.findCustomerByChatId(cbQuery.message().chat().id());
                            CustomerContext context = customer.getCustomerContext();
                            context.setShelterId(selectedShelter.getId());
                            customerService.updateCustomer(customer);
                            
                            kbMarkup = new InlineKeyboardMarkup();
                            
                            navigationService.findByParentId(1L).forEach(button -> {
                                kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                            });
                            caption = navigationService.findById(1L).getText() + ":";
                            
                            msgService.interactiveMsg(cbQuery.message().chat().id(),
                                    kbMarkup,
                                    caption);
                            return;
                    }
                }
*//*                if ((matcher.group(1).equals("shelter")) && (Long.parseLong(matcher.group(2)) > 0)) {
                
                }*//*
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(cbQuery.data()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", cbQuery);
                
                switch (cbQuery.data()) {
                    
                    case "/how-adopt":
                        kbMarkup = new InlineKeyboardMarkup();
                        Customer customer = customerService.findCustomerByChatId(cbQuery.message().chat().id());
                        CustomerContext context = customer.getCustomerContext();
                        caption = navigationService.findById(4L).getText() + ":";
                        navigationService.findByParentId(4L).forEach(button -> {
                            if ((button.getShelterId() == null) || (button.getShelterId().getId() == context.getShelterId())) {
                                kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                            }
                        });
                        
                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                caption);
                        return;
                    case "/shelter":
                        kbMarkup = new InlineKeyboardMarkup();
                        
                        caption = navigationService.findById(3L).getText() + ":";
                        
                        navigationService.findByParentId(3L).forEach(button -> {
//                            button.getShelterId()
                            kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                        });
                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                caption);
                        return;
                    
                    case "/main":
                        kbMarkup = new InlineKeyboardMarkup();
                        caption = navigationService.findById(1L).getText() + ":";
                        navigationService.findByParentId(1L).forEach(button -> {
                            kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
                        });
                        msgService.interactiveMsg(cbQuery.message().chat().id(),
                                kbMarkup,
                                caption);
                        return;
                }
                
                
                petSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
                volunteerSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
            }
        }
    }*/
}
    
    
