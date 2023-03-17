package ga.heaven.service;

import ga.heaven.model.Navigation;
import ga.heaven.repository.NavigationRepository;
import ga.heaven.repository.ShelterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NavigationService {
    private final NavigationRepository navigationRepository;

    public NavigationService(ShelterRepository shelterRepository, NavigationRepository navigationRepository) {
        this.navigationRepository = navigationRepository;
    }

    public List<Navigation> findAll() {
        return navigationRepository.findAll();
    }
    
    public List<Navigation> findByParentId(Long id) {
        return navigationRepository.findNavigationsByParentIdEquals(id);
    }
    
    
    public Navigation findById(Long id) {
        return navigationRepository.findById(id).orElse(null);
    }

}
