package ga.heaven.controller;

import ga.heaven.model.Volunteer;
import ga.heaven.repository.VolunteerRepository;
import ga.heaven.service.VolunteerService;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = VolunteerController.class)
public class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VolunteerRepository volunteerRepository;

    @SpyBean
    private VolunteerService volunteerService;

    @InjectMocks
    private VolunteerController volunteerController;


    @Test
    public void testFindAllVolunteers() throws Exception {
        List <Volunteer> volunteers = volunteersForTest();
        when(volunteerRepository.findAll()).thenReturn(volunteers);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/volunteer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }


    @Test
    public  void testFindVolunteerById(){
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer()));
        when(volunteerRepository.findById(3L)).thenReturn(Optional.of(testVolunteerWrong()));
        Volunteer expected = testVolunteer();
        Volunteer actual = volunteerService.findVolunteerById(1L);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getId()).isEqualTo(expected.getId());

        Volunteer actualWrong= volunteerService.findVolunteerById(3L);
        Assertions.assertThat(actualWrong.getId()).isNotNull();
        Assertions.assertThat(actualWrong.getId()).isNotEqualTo(expected.getId());
    }

    @Test
    public void testCreateVolunteer() throws Exception {
        Long id = 1L;
        Long chatId = 123L;
        String surname = "Blink";
        String name = "Amanda";
        String secondName = "-";
        String phone = "12345";
        String address = "123 Second Creek Rd, #1";

        JSONObject volunteerObj = new JSONObject();
        volunteerObj.put("id", id);
        volunteerObj.put("chatId", chatId);
        volunteerObj.put("surname", surname);
        volunteerObj.put("name", name);
        volunteerObj.put("secondName", secondName);
        volunteerObj.put("phone", phone);
        volunteerObj.put("address", address);

        Volunteer volunteer = new Volunteer();
        volunteer.setId(id);
        volunteer.setChatId(chatId);
        volunteer.setSurname(surname);
        volunteer.setName(name);
        volunteer.setSecondName(secondName);
        volunteer.setPhone(phone);
        volunteer.setAddress(address);
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
        when(volunteerRepository.findById(id)).thenReturn(Optional.of(volunteer));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/volunteer")
                        .content(volunteerObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.chatId").value(chatId))
                .andExpect(jsonPath("$.surname").value(surname))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.secondName").value(secondName))
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.address").value(address));
    }


    @Test
    public void updateVolunteer() throws Exception{
        Long id = 1L;
        Long oldChatId = 123L;
        String oldSurname = "Blink";
        String oldName = "Amanda";
        String oldSecondName = "-";
        String oldPhone = "12345";
        String oldAddress = "123 Second Creek Rd, #1";


        Long newChatId = 123L;
        String newSurname = "Smith";
        String newName = "Amanda";
        String newSecondName = "-";
        String newPhone = "23124";
        String newAddress = "678 S Padre, #1";

    JSONObject volunteerObj = new JSONObject();
        volunteerObj.put("id", id);
        volunteerObj.put("chatId", newChatId);
        volunteerObj.put("surname", newSurname);
        volunteerObj.put("name", newName);
        volunteerObj.put("secondName", newSecondName);
        volunteerObj.put("phone", newPhone);
        volunteerObj.put("address", newAddress);

    Volunteer volunteer = new Volunteer();
        volunteer.setId(id);
        volunteer.setChatId(oldChatId);
        volunteer.setSurname(oldSurname);
        volunteer.setName(oldName);
        volunteer.setSecondName(oldSecondName);
        volunteer.setPhone(oldPhone);
        volunteer.setAddress(oldAddress);

    Volunteer updatedVolunteer = new Volunteer();
        updatedVolunteer.setId(id);
        updatedVolunteer.setChatId(newChatId);
        updatedVolunteer.setSurname(newSurname);
        updatedVolunteer.setName(newName);
        updatedVolunteer.setSecondName(newSecondName);
        updatedVolunteer.setPhone(newPhone);
        updatedVolunteer.setAddress(newAddress);

    when(volunteerRepository.findById(id)).thenReturn(Optional.of(volunteer));
    when(volunteerRepository.save(any(Volunteer.class))).thenReturn(updatedVolunteer);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/volunteer")
                .content(volunteerObj.toString())
            .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.chatId").value(newChatId))
                .andExpect(jsonPath("$.surname").value(newSurname))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.secondName").value(newSecondName))
                .andExpect(jsonPath("$.phone").value(newPhone))
                .andExpect(jsonPath("$.address").value(newAddress));
    }

    @Test
    public void deleteVolunteer() throws Exception{
        Long id = 1L;
        Long chatId = 123L;
        String surname = "Blink";
        String name = "Amanda";
        String secondName = "-";
        String phone = "12345";
        String address = "123 Second Creek Rd, #1";

    Volunteer volunteer = new Volunteer();
        volunteer.setId(id);
        volunteer.setChatId(chatId);
        volunteer.setSurname(surname);
        volunteer.setName(name);
        volunteer.setSecondName(secondName);
        volunteer.setPhone(phone);
        volunteer.setAddress(address);

        when(volunteerRepository.findById(id)).thenReturn(Optional.of(volunteer));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/volunteer/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    verify(volunteerRepository, atLeastOnce()).deleteById(id);
}
    private List<Volunteer> volunteersForTest() {

        Volunteer volunteer1 = createTestVolunteer(1L, 123L,"Blink", "Amanda", "-", "12345",  "123 Second Creek Rd, #1");
        Volunteer volunteer2 = createTestVolunteer(2L, 124L,"Brouni", "Sandra", "-", "67890",  "145 Avery Ranch Rd, #2");

        List<Volunteer> volunteers = new ArrayList<>();
        volunteers.add(volunteer1);
        volunteers.add(volunteer2);

        return volunteers;
    }

    private Volunteer createTestVolunteer(Long id, Long chatId, String surname, String name, String secondName, String phone, String address) {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(id);
        volunteer.setChatId(chatId);
        volunteer.setSurname(surname);
        volunteer.setName(name);
        volunteer.setSecondName(secondName);
        volunteer.setPhone(phone);
        volunteer.setAddress(address);
        return (volunteer);
    }

    public static Volunteer testVolunteer () {
        Volunteer v1 = new Volunteer();
        v1.setId(1L);
        v1.setChatId(123L);
        v1.setSurname("Blink");
        v1.setName("Amanda");
        v1.setSecondName("-");
        v1.setPhone("12345");
        v1.setAddress("123 Second Creek Rd, #1");
        return v1;

    }
        public static Volunteer testVolunteerWrong () {
            Volunteer v2 = new Volunteer();
            v2.setId(3L);
            v2.setChatId(789L);
            v2.setSurname("Faber");
            v2.setName("Susan");
            v2.setSecondName("-");
            v2.setPhone("09876");
            v2.setAddress("453 Parmer Ln, #3");
            return v2;

    }

}
