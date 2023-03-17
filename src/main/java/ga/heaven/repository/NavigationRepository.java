package ga.heaven.repository;

import ga.heaven.model.Customer;
import ga.heaven.model.Navigation;
import ga.heaven.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NavigationRepository extends JpaRepository<Navigation, Long> {
    List<Navigation> findNavigationsByParentIdEquals(Long id);
}
