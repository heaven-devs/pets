package ga.heaven.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ga.heaven.model.Customer;
import ga.heaven.repository.CustomerRepository;
import ga.heaven.service.*;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ExtendWith(MockitoExtension.class)
@WebMvcTest
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CustomerController customerController;

    @SpyBean
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private BreedService breedService;
    @MockBean
    private InfoService infoService;
    @MockBean
    private PeripheralService peripheralService;

    @MockBean
    private PetService petService;
    @MockBean
    private ReportService reportService;
    @MockBean
    private ShelterService shelterService;

    @MockBean
    private VolunteerService volunteerService;

    private List<Customer> getInitialTestCustomers() {
        Customer customer1 = getTestCustomer(1L, "customerName1", "+71111111111");
        Customer customer2 = getTestCustomer(2L, "customerName2", "+72222222222");

        List<Customer> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);

        return customers;
    }

    private Customer getTestCustomer(long id, String name, String phoneNumber) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setPhone(phoneNumber);
        return customer;
    }

    @Test
    public void getAllCustomers() throws Exception{
        List<Customer> customers = getInitialTestCustomers();
        when(customerRepository.findAll()).thenReturn(customers);

        MockHttpServletResponse response = mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //System.out.println("response = " + response.getContentAsString());
        // Полезная информация https://www.baeldung.com/gson-list

        Type listOfMyClassObject = new TypeToken<ArrayList<Customer>>() {}.getType();

        Gson gson = new Gson();
        List<Customer> actual = gson.fromJson(response.getContentAsString(), listOfMyClassObject);
        /*System.out.println("actual = " + actual);
        System.out.println("customers = " + customers);*/
        assertThat(actual).containsExactlyInAnyOrderElementsOf(customers);
        assertThat(actual).isEqualTo(customers);
    }

    @Test
    void getCustomerById() throws Exception{

    }

    @Test
    void createCustomer() throws Exception{
    }

    @Test
    void updateCustomer() throws Exception{
    }

    @Test
    void removeCustomer() throws Exception{
    }
}