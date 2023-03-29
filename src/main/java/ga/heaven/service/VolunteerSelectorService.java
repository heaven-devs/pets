package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import ga.heaven.model.TgIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.DATING_RULES_CMD;
import static ga.heaven.configuration.Constants.VOLUNTEER_REQUEST_CMD;

@Service
public class VolunteerSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VolunteerSelectorService.class);
    private final AppLogicService appLogicService;

    public VolunteerSelectorService(AppLogicService appLogicService) {
        this.appLogicService = appLogicService;
    }
    
    public void switchCmd(TgIn in) {
        switch (in.endpoint().getName()) {
            case VOLUNTEER_REQUEST_CMD:
                appLogicService.volunteerRequest(in);
                break;
        }
    }
}
