package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ga.heaven.model.*;
import ga.heaven.repository.NavigationRepository;
import ga.heaven.repository.ShelterRepository;
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
    public NavigationService(ShelterRepository shelterRepository, NavigationRepository navigationRepository, CustomerService customerService, PetService petService, ReportService reportService, ShelterService shelterService) {
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
        LOGGER.error("findLevel " + level + ": " + findByLevelView(level).stream().map(Navigation::getText).collect(Collectors.toList()).toString());

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

    /**
     * Метод возвращает меню с кнопками, на которых перечислены питомцы пользователя.
     * @param chatId чат айди текущего пользователя
     * @param level уровень меню
     * @return меню с кнопками
     */
    public MessageTemplate prepareMessagePetChoice(Long chatId, Long level) {
        Customer customer = customerService.findCustomerByChatId(chatId);
        CustomerContext context = customer.getCustomerContext();
        MessageTemplate msgTmp = new MessageTemplate();
        msgTmp.setTextMenuCaption(this.findById(level).getText());
        msgTmp.setTextStatus(shelterService.findById(context.getShelterId()).getName());

        LOGGER.error("findLevel " + level + ": " + findByLevelView(level).stream().map(Navigation::getText).collect(Collectors.toList()).toString());
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

        this.generateNavigationForPetsReports(customer).forEach(button -> msgTmp.getKeyboard().addRow(new InlineKeyboardButton(button.getText()).callbackData(button.getEndpoint())));
        return msgTmp;
    }

    /**
     * Метод генерирует кнопки с питомцами пользователя, для которых сегодня не сдавались отчеты
     * @param customer текущий пользователь
     * @return список кнопок с питомцами
     */
    List<Navigation> generateNavigationForPetsReports(Customer customer) {
        long level = 5L;
        List<Navigation> buttons = new ArrayList<>();
        getPetsWithoutTodayReport(customer).forEach(pet -> {
            String endpoint = "/submit_report/" + pet.getId();
            String text = pet.getName();
            buttons.add(new Navigation(level, level, endpoint, text, null));
        });
        return buttons;
    }

    /**
     * Метод ищет питомцев пользователя, для которых сегодня не был сдан отчет.
     * @return список питомцев
     */
    private List<Pet> getPetsWithoutTodayReport(Customer customer) {
        List<Pet> petWithoutReportList = new ArrayList<>();
        for (Pet pet : petService.findPetsByCustomer(customer)) {
            Report report = reportService.findTodayCompletedReportsByPetId(pet.getId());
            if (null == report) {
                petWithoutReportList.add(pet);
            }
        }
        return petWithoutReportList;
    }
}
