package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.MessageTemplate;
import ga.heaven.model.Navigation;
import ga.heaven.repository.NavigationRepository;
import ga.heaven.repository.ShelterRepository;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NavigationService {
    private final NavigationRepository navigationRepository;
    private final CustomerService customerService;
    
    private final ShelterService shelterService;
    public NavigationService(ShelterRepository shelterRepository, NavigationRepository navigationRepository, CustomerService customerService, ShelterService shelterService) {
        this.navigationRepository = navigationRepository;
        this.customerService = customerService;
        this.shelterService = shelterService;
    }

    public List<Navigation> findAll() {
        return navigationRepository.findAll();
    }
    
    public List<Navigation> findByLevelView(Long id) {
        return navigationRepository.findNavigationsByLevelViewEquals(id);
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
                rulesJson = rulesJson==null ? "true" : rulesJson;
                /*ObjectMapper mapper = new ObjectMapper();
                String data = mapper.writeValueAsString(context);*/
                Map<String, String> data = new HashMap<>();
                data.put("shelterId", context.getShelterId().toString());
                data.put("dialogContext", context.getDialogContext().toString());
                enabled = (Boolean) jsonLogic.apply(rulesJson,data);
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
