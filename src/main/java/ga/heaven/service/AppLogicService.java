package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.model.*;
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
    private final MsgService msgService;

    public AppLogicService(InfoService infoService, MsgService msgService) {
        this.infoService = infoService;
        this.msgService = msgService;
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
        TgOut out = new TgOut();
        out.tgIn(in)
                .generateMarkup(SHELTERS_MENU_LEVEL);

        if (!Objects.nonNull(out.getIn().lastInQueryMessageId())) {
            out.textBody(infoService.findInfoByArea(COMMON_INFO_FIELD).getInstructions());
        }

        out.send().save();
//        LOGGER.debug("TgOut: {}", t);
    }

    public void sendContact(TgIn in, Long recipientChatId, String text) {
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
                .map(Volunteer::getName)
                .collect(Collectors.toList()).toString();

        String shelter = in.currentShelter(in.getCustomer().getCustomerContext().getShelterId()).getName();

        String text = "This person want to consult with volunteer supervised " + shelter;


        in
                .currentShelter(in.getCustomer().getCustomerContext().getShelterId())
                .getVolunteers()
                .forEach(v -> sendContact(in, v.getChatId(), text))
        ;

        TgOut out = new TgOut();
        out
                .tgIn(in)
                .generateMarkup(in.getCustomer().getCustomerContext().getCurLevel())
                .textBody("Your request has been accepted.\n" +
                        "You will be contacted by the first free volunteer from the list\n\n " + shelterSupervisors)
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

}
