package ga.heaven.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
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

    public NavigationService(ShelterRepository shelterRepository, NavigationRepository navigationRepository, CustomerService customerService) {
        this.navigationRepository = navigationRepository;
        this.customerService = customerService;
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
    
    
    public Long getMenuLevel(String endpoint) {
        return navigationRepository.getFirstByEndpointIs(endpoint).getLevelReference();
    }
    
    public InlineKeyboardMarkup getButtons(Long chatId, Long level) {
        Customer customer = customerService.findCustomerByChatId(chatId);
        CustomerContext context = customer.getCustomerContext();
        
        
        InlineKeyboardMarkup kbMarkup;
        kbMarkup = new InlineKeyboardMarkup();
        this.findByLevelView(level).forEach(button -> {
//            if ((button.getShelterId() == null) || (button.getShelterId().getId() == context.getShelterId())) {
            JsonLogic jsonLogic = new JsonLogic();
            //boolean enabled = true;
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
                //enabled = (boolean) jsonLogic.apply(button.getRules(),context);
            } catch (JsonLogicException e) {
                throw new RuntimeException(e);
            }
            if (enabled) {
                kbMarkup.addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint()));
            }
        });
        return kbMarkup;
    }
    
}
