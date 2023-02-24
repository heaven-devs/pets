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

    @Query(value = "SELECT * FROM Сustomer ORDER BY id", nativeQuery = true)
    List<Customer> findAll();

    Optional<Customer> findCustomerById(Long id);
}
