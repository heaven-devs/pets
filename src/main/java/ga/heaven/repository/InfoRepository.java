package ga.heaven.repository;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    Optional<Info> findFirstByAreaContainingIgnoreCase(String key);

    @Query(value = "SELECT * FROM Info ORDER BY id", nativeQuery = true)
    List<Info> findAll();

    Optional<Info> findInfoById(long id);
}
