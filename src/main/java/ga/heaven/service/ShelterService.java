package ga.heaven.service;

import ga.heaven.repository.ShelterRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

}
