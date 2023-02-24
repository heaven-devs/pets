package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    private Customer getCustomerById(Long id) {
        return customerRepository.findCustomerById(id).orElse(null);
    }
    public Customer updateCustomer(Customer customer) {
        if (getCustomerById(customer.getId()) == null) {
            return null;
        } else {
            return customerRepository.save(customer);
        }
    }

    public Customer deleteCustomerById(Long id) {
        Customer customer = getCustomerById(id);
        if (customer == null) {
            return null;
        } else {
            customerRepository.deleteById(id);
            return customer;
        }
    }
}
