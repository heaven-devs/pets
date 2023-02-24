package ga.heaven.controller;

import ga.heaven.model.Customer;
import ga.heaven.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("customer")
@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/get-customers")
    public ResponseEntity getAllCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("{id}")
    public ResponseEntity getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.findCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(customer);
        }
    }

    @PostMapping
    public ResponseEntity createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @PutMapping
    public ResponseEntity updateCustomer(@RequestBody Customer newCustomer) {
        Customer customer = customerService.updateCustomer(newCustomer);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(customer);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity removeCustomer(@PathVariable Long id) {
        Customer customer = customerService.deleteCustomerById(id);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(customer);
    }

}
