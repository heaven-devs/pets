package ga.heaven.repository;

import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerContextRepository extends JpaRepository<CustomerContext, Long> {
    CustomerContext findCustomerContextByCustomer(Customer customer);
}
