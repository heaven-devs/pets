package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ga.heaven.model.*;
import ga.heaven.repository.NavigationRepository;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NavigationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(NavigationService.class);

    private final NavigationRepository navigationRepository;
    private final CustomerService customerService;
    private final PetService petService;
    private final ReportService reportService;

    private final ShelterService shelterService;

    public NavigationService(NavigationRepository navigationRepository, CustomerService customerService, PetService petService, ReportService reportService, ShelterService shelterService) {
        this.navigationRepository = navigationRepository;
        this.customerService = customerService;
        this.petService = petService;
        this.reportService = reportService;
        this.shelterService = shelterService;
    }

    public List<Navigation> findAll() {
        return navigationRepository.findAll();
    }

    public List<Navigation> findByLevelView(Long id) {
        return navigationRepository.findNavigationsByLevelViewEqualsOrderById(id);
    }

    public Navigation findById(Long id) {
        return navigationRepository.findById(id).orElse(null);
    }

    public MessageTemplate prepareMessageTemplate(Long chatId, Long level) {
        Customer customer = customerService.findCustomerByChatId(chatId);
        CustomerContext context = customer.getCustomerContext();
        MessageTemplate msgTmp = new MessageTemplate();
        msgTmp.setTextMenuCaption(this.findById(level).getText());
        msgTmp.setTextStatus(shelterService.findById(context.getShelterId()).getName());

        this.findByLevelView(level).forEach(button -> {
            JsonLogic jsonLogic = new JsonLogic();
            Boolean enabled;
            try {
                String rulesJson = button.getRules();
                rulesJson = rulesJson == null ? "true" : rulesJson;
                Map<String, String> data = new HashMap<>();
                data.put("shelterId", context.getShelterId().toString());
                data.put("dialogContext", context.getDialogContext().toString());
                enabled = (Boolean) jsonLogic.apply(rulesJson, data);
            } catch (JsonLogicException e) {
                throw new RuntimeException(e);
            }
            if (enabled) {
                msgTmp.getKeyboard().addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
            }
        });

        return msgTmp;
    }

}
