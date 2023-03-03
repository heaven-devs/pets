package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;

@Service
public class ReportSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSelectorService.class);
    private final AppLogicService appLogicService;

    private final MsgService msgService;

    public ReportSelectorService(AppLogicService appLogicService, MsgService msgService) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
    }

    public void switchCmd(Message inputMessage) {
        switch (inputMessage.text()){
            case REPORT_SUBMIT_CMD:
                msgService.sendMsg(inputMessage.chat().id(),
                        appLogicService.processingSubmitReport());
                break;
        }
    }


}
