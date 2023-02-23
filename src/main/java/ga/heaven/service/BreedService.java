package ga.heaven.service;

import ga.heaven.repository.BreedRepository;
import org.springframework.stereotype.Service;

@Service
public class BreedService {
    private final BreedRepository breedRepository;

    public BreedService(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
    }
}
