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

    /**
     *
     * @return all found records
     * @see ShelterRepository#findAll()
     */
    public List<Shelter> findAll() {
        return shelterRepository.findAll();
    }

    /**
     *
     * @param id value of "id" field
     * @return found record or <b>{@code null}</b> if not found
     * @see ShelterRepository#findById(Object)
     */
    public Shelter findById(Long id) {
        if (id == null) {
            return null;
        }
        return shelterRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param shelter record "shelter" being created
     * @return created record
     * @see ShelterRepository#save(Object)
     */
    public Shelter create(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    /**
     *
     * @param shelter database record being updated
     * @return updated record or <b>{@code null}</b> if not found
     * @see ShelterRepository#save(Object)
     */
    public Shelter update(Shelter shelter) {
        return (findById(shelter.getId()) != null) ? shelterRepository.save(shelter) : null;
    }

    /**
     * 
     * @param id value of "id" field
     * @return deleted record
     * @see ShelterRepository#delete(Object)
     */
    public Shelter delete(Long id) {
        Shelter shelter = findById(id);
        if (shelter != null) {
            shelterRepository.delete(shelter);
        }
        return shelter;
    }
}
