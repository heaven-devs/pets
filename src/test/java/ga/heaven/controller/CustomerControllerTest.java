package ga.heaven.controller;

import ga.heaven.model.Customer;
import ga.heaven.repository.CustomerRepository;
import ga.heaven.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

    @Test
    public void getAllCustomers() throws Exception{
        List<Customer> customers = getInitialTestCustomers();
        when(customerRepository.findAll()).thenReturn(customers);

        MockHttpServletResponse response = mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //System.out.println("response = " + response.getContentAsString());
    }

    private List<Customer> getInitialTestCustomers() {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("customerName1");
        customer1.setPhone("+71111111111");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("customerName2");
        customer2.setPhone("+72222222222");


        List<Customer> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);

        return customers;
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