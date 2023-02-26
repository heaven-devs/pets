package ga.heaven.service;

import ga.heaven.model.Shelter;
import ga.heaven.repository.ShelterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public List<Shelter> findAll() {
        return shelterRepository.findAll();
    }

    public Shelter findById(Long id) {
        return shelterRepository.findById(id).orElse(null);
    }

    public Shelter create(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    public Shelter update(Shelter shelter) {
        return (findById(shelter.getId()) != null) ? shelterRepository.save(shelter) : null;
    }

    public Shelter delete(Long id) {
        Shelter shelter = findById(id);
        if (shelter != null) {
            shelterRepository.delete(shelter);
        }
        return shelter;
    }
}
