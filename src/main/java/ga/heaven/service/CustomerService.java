package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static ga.heaven.model.CustomerContext.Context.FREE;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     *
     * @return all database table entries
     * @see CustomerRepository#findAll()
     */
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    /**
     *
     * @param id value of "id" field
     * @return found record "Customer" or {@code null}, if record not found
     * @see CustomerRepository#findById(Object)
     */
    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param chatId Telegram chat id
     * @return <b>{@code true}</b> if the database record is found, <b>{@code false}</b> otherwise
     * @see CustomerRepository#findCustomerByChatId(Long)
     */
    public Boolean isPresent(Long chatId) {
        return this.customerRepository.findCustomerByChatId(chatId).isPresent();
    }

    /**
     *
     * @param customer the value of the record "Customer" being created
     * @return created Customer
     * @see CustomerRepository#save(Object) 
     */
    public Customer createCustomer(Customer customer) {
        if (null == customer.getCustomerContext()) {
            CustomerContext customerContext = new CustomerContext();
            customerContext.setDialogContext(FREE);
            customer.setCustomerContext(customerContext);
        }
        return customerRepository.save(customer);
    }

    /**
     * 
     * @param chatId Telegram chat id
     * @return created "Customer"
     * @see CustomerRepository#save(Object) 
     */
    public Customer createCustomer(Long chatId) {
        Customer customerRecord = new Customer();
        customerRecord.setChatId(chatId);
        CustomerContext customerContext = new CustomerContext();
        customerContext.setDialogContext(FREE);
        customerRecord.setCustomerContext(customerContext);
        return customerRepository.save(customerRecord);
    }

    /**
     * 
     * @param customer updated database record
     * @return updated database record or null, if record "customer" not found
     * @see CustomerRepository#save(Object)
     */
    public Customer updateCustomer(Customer customer) {
        if (findCustomerById(customer.getId()) == null) {
            return null;
        } else {
            return customerRepository.save(customer);
        }
    }

    /**
     *
     * @param id value of "id" field
     * @return deleted record "Customer"
     */
    public Customer deleteCustomerById(Long id) {
        Customer customer = findCustomerById(id);
        if (customer == null) {
            return null;
        } else {
            customerRepository.deleteById(id);
            return customer;
        }
    }

    /**
     *
     * @param id Telegram chat id
     * @return found Customer or <b>{@code null}</b> if not found
     * @see CustomerRepository#findCustomerByChatId(Long)
     */
    public Customer findCustomerByChatId(Long id) {
        return customerRepository.findCustomerByChatId(id).orElse(null);
    }

}
