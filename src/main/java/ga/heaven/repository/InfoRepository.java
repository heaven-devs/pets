package ga.heaven.repository;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InfoRepository extends JpaRepository<Info, String> {
    Optional<Info> findFirstByAreaContainingIgnoreCase(String key);
}
