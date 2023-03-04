package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.repository.CustomerContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerContextService {
    private final CustomerContextRepository customerContextRepository;

    public CustomerContextService(CustomerContextRepository customerContextRepository) {
        this.customerContextRepository = customerContextRepository;
    }

    public List<CustomerContext> findAll() {
        return customerContextRepository.findAll();
    }

    public CustomerContext findById(Long id) {
        return customerContextRepository.findById(id).orElse(null);
    }

    public CustomerContext findCustomerContextByCustomer(Customer customer) {
        return customerContextRepository.findCustomerContextByCustomer(customer);
    }

    public CustomerContext create(CustomerContext customerContext) {
        return customerContextRepository.save(customerContext);
    }

    public CustomerContext create(Customer customer) {
        CustomerContext customerContext = new CustomerContext();
        customerContext.setDialogContext("start");
        customerContext.setCustomer(customer);
        return customerContextRepository.save(customerContext);
    }

    public CustomerContext update(CustomerContext customerContext) {
        return (findById(customerContext.getId()) != null) ? customerContextRepository.save(customerContext) : null;
    }

    public CustomerContext delete(Long id) {
        CustomerContext customerContext = findById(id);
        if (customerContext != null) {
            customerContextRepository.delete(customerContext);
        }
        return customerContext;
    }
}
