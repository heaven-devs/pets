package ga.heaven.service;

import ga.heaven.model.Pet;
import ga.heaven.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {
    private final PetRepository petRepository;
    
    private final Logger LOGGER = LoggerFactory.getLogger(PetService.class);
    
    public PetServiceImpl(PetRepository petRepository) {
        LOGGER.debug("Service wire with Repository");
        this.petRepository = petRepository;
    }
    
    @Override
    public Pet create(Pet pet) {
        LOGGER.info("Method create was invoked");
        Example<Pet> e = Example.of(pet);
        boolean exists = petRepository.exists(e);
        return petRepository.save(pet);
    
    }
    
    @Override
    public Pet read(Long id) {
        LOGGER.info("Method read was invoked");
        return petRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<Pet> read() {
        LOGGER.info("Method read all was invoked");
        return petRepository.findAll();
    }
    
    public Pet update(Long id, Pet pet) {
        if (petRepository.existsById(id)) {
            pet.setId(id);
            return petRepository.save(pet);
        }
        return null;
    }
    
    public Pet delete(Long id) {
        Optional<Pet> currentPet = petRepository.findById(id);
        if (currentPet.isPresent()) {
            try {
                petRepository.deleteAll(currentPet.stream().collect(Collectors.toList()));
            } catch (Exception e) {
                //currentFaculty = Optional.empty();
            } finally {
                return currentPet.orElse(null);
            }
        }
        return null;
    }
}
