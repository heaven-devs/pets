package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.repository.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*; // when

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

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
    void getCustomers() {
        List<Customer> customers = getInitialTestCustomers();
        when(customerRepository.findAll()).thenReturn(customers);
        List<Customer> expected = customers;
        List<Customer> actual = customerService.getCustomers();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void findCustomerById() {
        Customer customer = getTestCustomer(25L, "Иван", "+71112223333");
        Customer customerWrong = getTestCustomer(35L, "Федор", "+71112224444");

        when(customerRepository.findById(25L)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(35L)).thenReturn(Optional.of(customerWrong));
        Customer expected = customer;
        Customer actual = customerService.findCustomerById(25L);

        Assertions.assertThat(actual).isEqualTo(expected);

        Customer actualWrong = customerService.findCustomerById(35L);
        Assertions.assertThat(actualWrong).isNotEqualTo(expected);
    }

    @Test
    void isPresent() {
        Customer customer = getTestCustomer(25L, "Иван", "+71112223333");
        when(customerRepository.findCustomerByChatId(25L)).thenReturn(Optional.of(customer));
        when(customerRepository.findCustomerByChatId(35L)).thenReturn(Optional.empty());

        Boolean actual = customerService.isPresent(25L);
        Assertions.assertThat(actual).isTrue();

        Boolean actualNegative = customerService.isPresent(35L);
        Assertions.assertThat(actualNegative).isFalse();
    }

    @Test
    void createCustomer() {
        Customer customer = getTestCustomer(25L, "Иван", "+71112223333");
        when(customerRepository.save(customer)).thenReturn(customer);
        Customer expected = customer;
        Customer actual = customerService.createCustomer(customer);
        Assertions.assertThat(actual).isEqualTo(expected);

        Customer customer1 = getTestCustomer(35L, null, null);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);
        expected = customer1;
        actual = customerService.createCustomer(35L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    /*@Test
    void updateCustomer() { // Протестировано в тесте функции findCustomerById
    }*/

    /*@Test
    void deleteCustomerById() {
    }*/

    @Test
    void findCustomerByChatId() {
        Customer customer = getTestCustomer(25L, "Иван", "+71112223333");
        when(customerRepository.findCustomerByChatId(25L)).thenReturn(Optional.of(customer));
        Customer expected = customer;
        Customer actual = customerService.findCustomerByChatId(25L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}