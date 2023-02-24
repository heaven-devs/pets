package ga.heaven.service;

import ga.heaven.model.Breed;
import ga.heaven.repository.BreedRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BreedService {
    private final BreedRepository breedRepository;

    public BreedService(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
    }

    public Collection<Breed> findAll() {
        return breedRepository.findAll();
    }

    public Breed findById(Long id) {
        return breedRepository.findById(id).orElse(null);
    }

    public Breed create(Breed breed) {
        return breedRepository.save(breed);
    }

    public Breed update(Breed breed) {
        return (findById(breed.getId()) != null) ? breedRepository.save(breed) : null;
    }

    public void delete(Long id) {
        breedRepository.deleteById(id);
    }
}
