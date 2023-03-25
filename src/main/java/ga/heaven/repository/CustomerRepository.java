package ga.heaven.repository;

import ga.heaven.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByChatId(Long chatId);

    /**
     *
     * @param id value of "id" field
     * @return found record "Customer"
     */
    Optional<Customer> findCustomerById(Long id);
}
