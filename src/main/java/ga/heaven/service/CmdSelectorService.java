package ga.heaven.service;

import ga.heaven.model.TgIn;
import ga.heaven.model.TgOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.CustomerContext.Context.WAIT_REPORT;
import static ga.heaven.model.TgIn.Endpoint.Type.*;

@Service
public class CmdSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSelectorService.class);
    private final AppLogicService appLogicService;
    private final PetSelectorService petSelectorService;
    private final VolunteerSelectorService volunteerSelectorService;
    private final ReportSelectorService reportSelectorService;
    private final ShelterSelectorService shelterSelectorService;


    public CmdSelectorService(AppLogicService appLogicService, PetSelectorService petSelectorService,
                              VolunteerSelectorService volunteerSelectorService, ReportSelectorService reportSelectorService,
                              ShelterSelectorService shelterSelectorService) {
        this.appLogicService = appLogicService;
        this.petSelectorService = petSelectorService;
        this.volunteerSelectorService = volunteerSelectorService;
        this.reportSelectorService = reportSelectorService;
        this.shelterSelectorService = shelterSelectorService;
    }

    public void processingMsg(TgIn in) {
//        LOGGER.debug("current in: {}", in);
        if (isNonCommandMessages(in)) {
            reportSelectorService.processingNonCommandMessagesForReport(in);
            shelterSelectorService.processingNonCommandMessagesForShelter(in);
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
                    case START_CMD:
                        appLogicService.initConversation(in);
                        return;

                    case "/how-adopt":
                        new TgOut()
                                .tgIn(in)
                                .generateMarkup(4L)
                                .send()
                                .save()
                        ;
                        return;

                    case "/shelter":
                        new TgOut()
                                .tgIn(in)
                                .generateMarkup(3L)
                                .send()
                                .save()
                        ;
                        return;

                    case "/main":
                        new TgOut()
                                .tgIn(in)
                                .generateMarkup(1L)
                                .send()
                                .save()
                        ;
                        return;

                    case "/submit_report":
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
                petSelectorService.switchCmd(in.message());
                volunteerSelectorService.switchCmd(in.message());
                shelterSelectorService.switchCmd(in);
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

}
    
    
