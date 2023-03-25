package ga.heaven.service;

import ga.heaven.model.Breed;
import ga.heaven.repository.BreedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BreedService {
    private final BreedRepository breedRepository;

    public BreedService(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
    }

    /**
     *
     * @return all database table entries
     * @see BreedRepository#findAll() 
     */
    public List<Breed> findAll() {
        return breedRepository.findAll();
    }

    /**
     * 
     * @param id value of "id" field
     * @return found "Breed"
     * @see BreedRepository#findById(Object)
     */
    public Breed findById(Long id) {
        return breedRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param breed database record "Breed" being created
     * @return created record
     * @see BreedRepository#save(Object) 
     */
    public Breed create(Breed breed) {
        return breedRepository.save(breed);
    }

    /**
     * 
     * @param breed updated database record
     * @return updated database record or null, if record "breed" not found
     * @see BreedRepository#save(Object) 
     */
    public Breed update(Breed breed) {
        return (findById(breed.getId()) != null) ? breedRepository.save(breed) : null;
    }

    /**
     * 
     * @param id value of "id" field
     * @return deleted record "Breed"
     * @see BreedRepository#delete(Object)
     */
    public Breed delete(Long id) {
        Breed breed = findById(id);
        if (breed != null) {
            breedRepository.delete(breed);
        }
        return breed;
    }
}
