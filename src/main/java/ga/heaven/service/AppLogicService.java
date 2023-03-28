package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.model.*;
import ga.heaven.model.CustomerContext.Context;
import ga.heaven.repository.ShelterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;

@Service
public class AppLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogicService.class);
    
    //private final List<TgIn> ins = new ArrayList<>();
    
    private final InfoService infoService;
    private final CustomerService customerService;
    private final MsgService msgService;
    private final ShelterRepository shelterRepository;
    
    public AppLogicService(InfoService infoService, CustomerService customerService, MsgService msgService, ShelterRepository shelterRepository) {
        this.infoService = infoService;
        this.customerService = customerService;
        this.msgService = msgService;
        this.shelterRepository = shelterRepository;
    }
    
   /* public void addInputInstance(TgIn inObj) {
        this.ins.add(inObj);
    }*/
    
    /*public TgIn getInputInstance(Long chatId) {
        return this.ins.stream().filter(i -> chatId.equals(i.chatId()))
                //.map(Navigation::getText)
                .findFirst()
                .orElse(null);
    }*/
    
   /* public void removeInputInstance(Long chatId) {
        
        TgIn in = getInputInstance(chatId);
        this.ins.remove(in);
        //in = null;
    }*/
    
    /*public void removeInputInstance(TgIn in) {
        this.ins.remove(in);
        //in = null;
    }*/
    
    public void initConversation(TgIn in) {
        TgOut t = new TgOut();
        t
                .tgIn(in)
                .generateMarkup(SHELTERS_MENU_LEVEL);
        
        if (!Objects.nonNull(t.getIn().lastInQueryMessageId())) {
            t.textBody(infoService.findInfoByArea(COMMON_INFO_FIELD).getInstructions());
        }
        
        t
                .send()
                .save()
        ;
        LOGGER.debug("TgOut: {}", t);
    }
    
    public void sendContact(TgIn in,Long recipientChatId ,String text) {

        String name = in.message().chat().username();
        
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton(name + "'s profile "
        ).url("tg://user?id=" + in.chatId());
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(keyboardButton);
        msgService.sendMsg(recipientChatId, text, keyboardMarkup);
    }
    
    
    public void volunteerRequest(TgIn in) {
        
        
        //msgService.sendMsg(inputMessage.chat().id(), "ok");
//        volunteerRepository.findById(3L).ifPresent(volunteer -> msgService.sendMsg(inputMessage.chat().id(), volunteer.getShelters().toString()));
        /*msgService.sendMsg(inputMessage.chat().id(),shelterRepository.findById(1L).ifPresent(shelter -> shelter.getVolunteers().forEach(v -> v.getName())));*/
        /*Shelter s = shelterRepository.findById(1L).orElse(null);
        msgService.sendMsg(in.chatId(), s.getVolunteers().stream()
                .map(v -> v.getName())
                .collect(Collectors.toList()).toString());*/
        
        String shelterSupervisors = in
                .currentShelter(in.getCustomer().getCustomerContext().getShelterId())
                .getVolunteers().stream()
                .map(v -> v.getName())
                .collect(Collectors.toList()).toString();
    
        String shelter = in.currentShelter(in.getCustomer().getCustomerContext().getShelterId()).getName();
        
        String text = "This person want to consult with volunteer supervised " + shelter;
        
        
        in
        .currentShelter(in.getCustomer().getCustomerContext().getShelterId())
                .getVolunteers()
                .forEach(v -> sendContact(in,v.getChatId(), text))
                
        ;
    
        TgOut out = new TgOut();
        out
                .tgIn(in)
                .generateMarkup(in.getCustomer().getCustomerContext().getCurLevel())
                .textBody("Your request has been accepted.\n" +
                        "You will be contacted by the first free volunteer from the list\n\n " + shelterSupervisors )
                .send()
                .save();
                
        
        
        
    }
    
    /*public void volunteerRequest(Long chatId) {
        TgIn in = this.getInputInstance(chatId);
        this.getInputInstance(chatId)
                .currentShelter(in.getCustomer().getCustomerContext().getShelterId())
                .getVolunteers()
                .forEach(v -> sendContact(v.getChatId(), chatId));
        
    }*/
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendDatingRules(Long chatId) {
        sendMultipurpose(chatId, DATING_RULES_FIELD, DATING_RULES_NOT_FOUND);
    }
    public void sendDatingRules(TgIn in) {
        sendMultipurpose(in, DATING_RULES_FIELD, DATING_RULES_NOT_FOUND);
    }
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendDocuments(Long chatId) {
        sendMultipurpose(chatId, DOCUMENTS_FIELD, DOCUMENTS_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendTransportRules(Long chatId) {
        sendMultipurpose(chatId, TRANSPORT_FIELD, TRANSPORT_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendComfortPet(Long chatId) {
        sendMultipurpose(chatId, COMFORT_PET_FIELD, COMFORT_PET_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendComfortDog(Long chatId) {
        sendMultipurpose(chatId, COMFORT_DOG_FIELD, COMFORT_DOG_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendComfortHandicapped(Long chatId) {
        sendMultipurpose(chatId, COMFORT_HANDICAPPED_FIELD, COMFORT_HANDICAPPED_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendCynologistAdvice(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGIST_ADVICE_FIELD, CYNOLOGIST_ADVICE_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendCynologistsList(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGISTS_LIST_FIELD, CYNOLOGIST_LIST_NOT_FOUND);
    }
    
    /**
     * @param chatId Telegram chat id
     * @see #sendMultipurpose(Long, String, String)
     */
    public void sendReasonsRefusal(Long chatId) {
        sendMultipurpose(chatId, REASONS_REFUSAL_FIELD, REASONS_REFUSAL_NOT_FOUND);
    }
    
    /**
     * @param chatId      Telegram chat id
     * @param areaField   column "area" in Data Base Table "Info"
     * @param notFoundMsg a message sent to the Telegram chat when there is no record with the areaField value
     */
    protected void sendMultipurpose(Long chatId, String areaField, String notFoundMsg) {
        Info info = infoService.findInfoByArea(areaField);
//        MessageTemplate tmp = navigationService.prepareMessageTemplate(chatId, 4L);
       
       /* TgOut out = new TgOut();
        out
                .tgIn(in)
                //.inlineMarkup(in.inlineMarkup());
                .generateMarkup(in.getCustomer().getCustomerContext().getCurLevel());
        if (info == null) {
            out.textBody(notFoundMsg);
        } else {
            out.textBody(info.getInstructions());
        }
        out
                .send()
                .save();*/
        
    }
    protected void sendMultipurpose(TgIn in, String areaField, String notFoundMsg) {
        Info info = infoService.findInfoByArea(areaField);
        TgOut out = new TgOut();
        out
                .tgIn(in)
                .generateMarkup(in.getCustomer().getCustomerContext().getCurLevel());
        if (info == null) {
        out
            .textBody(notFoundMsg);
        } else {
        out
            .textBody(info.getInstructions());
        }
        out
                .send()
                .save();
        
    }

    // todo: deprecated
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
