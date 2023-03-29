package ga.heaven.service;

import ga.heaven.model.Info;
import ga.heaven.model.Navigation;
import ga.heaven.model.TgIn;
import ga.heaven.model.TgOut;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.TgIn.Endpoint.Type.*;
import static ga.heaven.model.CustomerContext.Context.WAIT_REPORT;

@Service
public class CmdSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSelectorService.class);
    private final AppLogicService appLogicService;
    private final VolunteerSelectorService volunteerSelectorService;
    private final ReportSelectorService reportSelectorService;
    
    
    public CmdSelectorService(AppLogicService appLogicService, VolunteerSelectorService volunteerSelectorService, ReportSelectorService reportSelectorService) {
        this.appLogicService = appLogicService;
        this.volunteerSelectorService = volunteerSelectorService;
        this.reportSelectorService = reportSelectorService;
    }
    
    public void processingMsg(TgIn in) {
//        LOGGER.debug("current in: {}", in);
        if (isNonCommandMessages(in)) {
            reportSelectorService.processingNonCommandMessagesForReport(in);
            return;
        }
        
        if ((in.text() != null)
                && (in.chatId() != null)
        ) {
            if (DYNAMIC.equals(in.endpoint().getType())) {

//                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", in);
                switch (in.endpoint().getName()) {
                    case SHELTER_EPT:
                        if (ENDPOINT_LIST.equals(in.endpoint().getValueAsLong())) {
                            appLogicService.initConversation(in);
                        } else {
                            new TgOut()
                                    .tgIn(in)
                                    .setSelectedShelter(in.endpoint().getValueAsLong())
                                    .generateMarkup(1L)
                                    .send()
                                    .save()
                            ;
                        }
                        return;
                    case REPORT_EPT:
                        new TgOut()
                                .tgIn(in)
                                .textBody(reportSelectorService.processingPetChoice(in))
                                .setCustomerContext(WAIT_REPORT)
                                .setCurrentPet(in.endpoint().getValueAsLong())
                                .generateMarkup(5L)
                                .send()
                                .save()
                        ;
                        return;
                }
                
            } else if (STATIC.equals(in.endpoint().getType())) {
            
//                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", in);
                switch (in.endpoint().getName()) {
                    case START_EPT:
                        appLogicService.initConversation(in);
                        return;
                    
                    case "how-adopt":
                        new TgOut()
                                .tgIn(in)
                                .generateMarkup(4L)
                                .send()
                                .save()
                        ;
                        return;
                    
                    case "shelter":
                        new TgOut()
                                .tgIn(in)
                                .generateMarkup(3L)
                                .send()
                                .save()
                        ;
                        return;
                    
                    case "main":
                        new TgOut()
                                .tgIn(in)
                                .generateMarkup(1L)
                                .send()
                                .save()
                        ;
                        return;
                    
                    case "submit_report":
                        new TgOut()
                                .tgIn(in)
                                .setCustomerContext(WAIT_REPORT)
                                .generateMarkup(5L)
                                .send()
                                .save()
                        ;
                        return;
                    
                    default:
                        break;
                }
                informationEndpoints(in);
                //petSelectorService.switchCmd(in);
                volunteerSelectorService.switchCmd(in);
            }
        }
    }
    
    /**
     * Проверяю введена команда или обычный текст/фото
     *
     * @param in сообщение от пользователя
     * @return истина если от пользователя получина не команда, а обычный текст или фото
     */
    private boolean isNonCommandMessages(TgIn in) {
        return (in.text() != null && !in.text().startsWith("/")) || in.message().photo() != null;
    }
    
    private void informationEndpoints(TgIn in) {
        in
                .getInfoList()
                .stream()
                .filter(i -> Optional.ofNullable(Strings.concat("/", i.getArea()))
                        .equals(
                                in.getNavigationList()
                                        .stream()
                                        .filter(n -> n.getEndpoint().equals(Strings.concat("/", in.endpoint().getName())))
                                        .filter(n -> n.getLevelView().equals(in.getCustomer().getCustomerContext().getCurLevel()))
                                        .findFirst()
                                        .map(Navigation::getEndpoint
                                        )
                        )
                )
                .findFirst()
                .map(Info::getInstructions)
                .ifPresent(instructions -> {
                            new TgOut()
                                    .tgIn(in)
                                    .generateMarkup(in.getCustomer().getCustomerContext().getCurLevel())
                                    .textBody(instructions)
                                    .send()
                                    .save()
                            ;
                        }
                )
        ;
    }
    
}
    
    
