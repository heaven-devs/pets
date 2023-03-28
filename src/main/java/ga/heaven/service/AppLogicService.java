package ga.heaven.service;

import ga.heaven.model.*;
import ga.heaven.model.CustomerContext.Context;
import ga.heaven.repository.ShelterRepository;
import ga.heaven.repository.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;

@Service
public class AppLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogicService.class);
    
    private final List<TgIn> ins = new ArrayList<>();
    
    private final InfoService infoService;
    private final MsgService msgService;
    private final ShelterRepository shelterRepository;
    
    public AppLogicService(InfoService infoService, MsgService msgService, ShelterRepository shelterRepository) {
        this.infoService = infoService;
        this.msgService = msgService;
        this.shelterRepository = shelterRepository;
    }
    
    public void addInputInstance(TgIn inObj) {
        this.ins.add(inObj);
    }
    
    public TgIn getInputInstance(Long chatId) {
        return this.ins.stream().filter(i -> chatId.equals(i.chatId()))
                //.map(Navigation::getText)
                .findFirst()
                .orElse(null);
    }
    
    public void removeInputInstance(Long chatId) {
        
        TgIn in = getInputInstance(chatId);
        this.ins.remove(in);
        //in = null;
    }
    
    public void removeInputInstance(TgIn in) {
        this.ins.remove(in);
        //in = null;
    }
    
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
    
    public void volunteerRequest(Long chatId) {
        //msgService.sendMsg(inputMessage.chat().id(), "ok");
//        volunteerRepository.findById(3L).ifPresent(volunteer -> msgService.sendMsg(inputMessage.chat().id(), volunteer.getShelters().toString()));
        /*msgService.sendMsg(inputMessage.chat().id(),shelterRepository.findById(1L).ifPresent(shelter -> shelter.getVolunteers().forEach(v -> v.getName())));*/
        Shelter s = shelterRepository.findById(1L).orElse(null);
        msgService.sendMsg(chatId, s.getVolunteers().stream()
                .map(v -> v.getName())
                .collect(Collectors.toList()).toString());
        
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
    
    protected void sendMultipurpose(Long chatId, String areaField, String notFoundMsg) {
        Info info = infoService.findInfoByArea(areaField);
//        MessageTemplate tmp = navigationService.prepareMessageTemplate(chatId, 4L);
        TgIn in = this.getInputInstance(chatId);
        TgOut out = new TgOut();
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
                .save();
        
    }
    
}
