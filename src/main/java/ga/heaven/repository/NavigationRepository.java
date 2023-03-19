package ga.heaven.repository;

import ga.heaven.model.Navigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NavigationRepository extends JpaRepository<Navigation, Long> {
    List<Navigation> findNavigationsByLevelViewEquals(Long id);
    Navigation getFirstByEndpointIs(String endpoint);
}
