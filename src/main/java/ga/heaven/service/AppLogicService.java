package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.model.*;
import ga.heaven.model.CustomerContext.*;
import ga.heaven.repository.ShelterRepository;
import ga.heaven.repository.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;

@Service
public class AppLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogicService.class);
    
    private final InfoService infoService;
    private final CustomerService customerService;
    private final MsgService msgService;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final ShelterService shelterService;
    private final NavigationService navigationService;
    
    public AppLogicService(InfoService infoService, CustomerService customerService, MsgService msgService, VolunteerRepository volunteerRepository, ShelterRepository shelterRepository, ShelterService shelterService, NavigationService navigationService) {
        this.infoService = infoService;
        this.customerService = customerService;
        this.msgService = msgService;
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
        this.shelterService = shelterService;
        this.navigationService = navigationService;
    }
    
    public void initConversation(Long chatId) {
        if (!customerService.isPresent(chatId)) {
            msgService.sendMsg(chatId, infoService.findInfoByArea(COMMON_INFO_FIELD).getInstructions());
            customerService.createCustomer(chatId);
        }
        
        InlineKeyboardMarkup kbMarkup = new InlineKeyboardMarkup();
        shelterService.findAll().forEach(shelter -> {
            kbMarkup.addRow(new InlineKeyboardButton(shelter.getName()).callbackData("/shelter/" + shelter.getId()));
        });
        msgService.sendMsg(chatId, SHELTER_CHOOSE_MSG + " \n", kbMarkup);
        
    }
    
    public void volunteerRequest(Long chatId) {
        //msgService.sendMsg(inputMessage.chat().id(), "ok");
//        volunteerRepository.findById(3L).ifPresent(volunteer -> msgService.sendMsg(inputMessage.chat().id(), volunteer.getShelters().toString()));
        /*msgService.sendMsg(inputMessage.chat().id(),shelterRepository.findById(1L).ifPresent(shelter -> shelter.getVolunteers().forEach(v -> v.getName())));*/
        Shelter s = shelterRepository.findById(1L).orElse(null);
        msgService.sendMsg(chatId, s.getVolunteers().stream()
                .map(v -> v.getName())
                .collect(Collectors.toList()).toString());
        
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendDatingRules(Long chatId) {
        sendMultipurpose(chatId, DATING_RULES_FIELD, DATING_RULES_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendDocuments(Long chatId) {
        sendMultipurpose(chatId, DOCUMENTS_FIELD, DOCUMENTS_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendTransportRules(Long chatId) {
        sendMultipurpose(chatId, TRANSPORT_FIELD, TRANSPORT_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendComfortPet(Long chatId) {
        sendMultipurpose(chatId, COMFORT_PET_FIELD, COMFORT_PET_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendComfortDog(Long chatId) {
        sendMultipurpose(chatId, COMFORT_DOG_FIELD, COMFORT_DOG_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendComfortHandicapped(Long chatId) {
        sendMultipurpose(chatId, COMFORT_HANDICAPPED_FIELD, COMFORT_HANDICAPPED_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendCynologistAdvice(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGIST_ADVICE_FIELD, CYNOLOGIST_ADVICE_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendCynologistsList(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGISTS_LIST_FIELD, CYNOLOGIST_LIST_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendReasonsRefusal(Long chatId) {
        sendMultipurpose(chatId, REASONS_REFUSAL_FIELD, REASONS_REFUSAL_NOT_FOUND);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @param areaField column "area" in Data Base Table "Info"
     * @param notFoundMsg a message sent to the Telegram chat when there is no record with the areaField value
     */
    protected void sendMultipurpose(Long chatId, String areaField, String notFoundMsg) {
        Info info = infoService.findInfoByArea(areaField);
        MessageTemplate tmp = navigationService.prepareMessageTemplate(chatId, 4L);
        if (info == null) {
            tmp.setTextBody(notFoundMsg);
        } else {
            tmp.setTextBody(info.getInstructions());
        }
        
        msgService.interactiveMsg(chatId, null, tmp.getText());
    }
    
    /**
     * Метод обновляет значения полей "context" и "petId"
     *
     * @param customer текущий пользователь
     * @param context  новое значение поля "context"
     * @param petId    новое значение поля "petId"
     */
    void updateCustomerContext(Customer customer, Context context, long petId) {
        CustomerContext customerContext = customer.getCustomerContext();
        customerContext.setCurrentPetId(petId);
        customerService.updateCustomer(customer);
        updateCustomerContext(customer, context);
    }
    
    /**
     * Метод обновляет значения полей "context"
     *
     * @param customer текущий пользователь
     * @param context  новое значение поля "context"
     */
    void updateCustomerContext(Customer customer, Context context) {
        CustomerContext customerContext = customer.getCustomerContext();
        customerContext.setDialogContext(context);
        customerService.updateCustomer(customer);
    }
    
}
