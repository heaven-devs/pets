package ga.heaven.service;

import ga.heaven.model.TgIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.VOLUNTEER_REQUEST_EPT;

@Service
public class VolunteerSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VolunteerSelectorService.class);
    private final AppLogicService appLogicService;

    public VolunteerSelectorService(AppLogicService appLogicService) {
        this.appLogicService = appLogicService;
    }
    
    public void switchCmd(TgIn in) {
        switch (in.endpoint().getName()) {
            case VOLUNTEER_REQUEST_EPT:
                appLogicService.volunteerRequest(in);
                break;
        }
    }
}
