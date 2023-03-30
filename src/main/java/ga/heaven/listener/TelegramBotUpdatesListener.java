package ga.heaven.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import ga.heaven.model.TgIn;
import ga.heaven.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    
    private final TelegramBot telegramBot;
    
    private final CmdSelectorService cmdSelectorService;
    
    private final InfoService infoService;
    private final MsgService msgService;
    private final CustomerService customerService;
    private final AppLogicService appLogicService;
    private final ReportService reportService;
    
    private static TgIn tgInGlobal;
    
    private static long updateCounter;
    
    public TelegramBotUpdatesListener(TelegramBot telegramBot, CmdSelectorService cmdSelectorService, NavigationService navigationService,
                                      ShelterService shelterService, InfoService infoService, MsgService msgService, CustomerService customerService, AppLogicService appLogicService, ReportService reportService) {
        this.telegramBot = telegramBot;
        this.cmdSelectorService = cmdSelectorService;
        this.infoService = infoService;
        this.msgService = msgService;
        this.customerService = customerService;
        this.appLogicService = appLogicService;
        this.reportService = reportService;
        
        
        tgInGlobal = new TgIn();
        tgInGlobal
                .injectServices(msgService, customerService, appLogicService, reportService)
                .setNavigationList(navigationService.findAll())
                .setShelterList(shelterService.findAll())
                .setInfoList(infoService.findAll())
        ;
    }
    
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            updateCounter++;
            LOGGER.debug(">>>>>>>>>>>>>>>>>>> [" +
                    updateCounter + "] >>>>>>>>>>>>>>>>>>");
//            LOGGER.debug("Processing update: {}", update);
            TgIn in = tgInGlobal
                    .newInstance()
                    .update(update)
                    .initMsgInstanceEnvironment();
            if (Objects.nonNull(in.chatId())) {
                LOGGER.debug(">>>>>>>>>>>>>>>>>>> [" +
                        updateCounter + "] input with chatId {} redirected to cmdSelectorService >>>>>>>>>>>>>>>>>>",in.chatId());
                cmdSelectorService.processingMsg(in);
            }
            //LOGGER.debug("current in: {}", in);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    
}
