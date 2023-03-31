package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.Pet;
import ga.heaven.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {
    private final PetRepository petRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(PetService.class);

    public PetServiceImpl(PetRepository petRepository) {
//        LOGGER.debug("Service wire with Repository");
        this.petRepository = petRepository;
    }

    /**
     * @param pet "Pet" record being created
     * @return created record "Pet"
     * @see PetRepository#save(Object)
     */
    @Override
    public Pet create(Pet pet) {
//        LOGGER.info("Method create was invoked");
        Example<Pet> e = Example.of(pet);
        boolean exists = petRepository.exists(e);
        return petRepository.save(pet);

    }

    /**
     * @param id value of "id" field
     * @return found record
     * @see PetRepository#findById(Object)
     */
    @Override
    public Pet read(Long id) {
//        LOGGER.info("Method read was invoked");
        return petRepository.findById(id).orElse(null);
    }

    /**
     * @return all found records
     * @see PetRepository#findAll()
     */
    @Override
    public List<Pet> read() {
//        LOGGER.info("Method read all was invoked");
        return petRepository.findAll();
    }

    /**
     * @param id  value of "id" field
     * @param pet value of "Pet" being updated
     * @return updated "Pet" record or null if not found
     * @see PetRepository#save(Object)
     */
    public Pet update(Long id, Pet pet) {
        if (petRepository.existsById(id)) {
            pet.setId(id);
            return petRepository.save(pet);
        }
        return null;
    }

    /**
     * @param id value of "id" field
     * @return deleted record or <b>{@code null}</b> if not found
     * @see PetRepository#deleteById(Object)
     */
    public Pet delete(Long id) {
        Pet currentPet = petRepository.findById(id).orElse(null);
        if (currentPet == null) {
            return null;
        } else {
            petRepository.deleteById(id);
            return currentPet;
        }
        /*if (currentPet.isPresent()) {
            try {
                petRepository.deleteAll(currentPet.stream().collect(Collectors.toList()));
            } catch (Exception e) {
                //currentFaculty = Optional.empty();
            } finally {
                return currentPet.orElse(null);
            }
        }
        return null;*/
    }

    /**
     * @param customer record Customer
     * @return list of "Pet" where customer field is <b>{@code customer}</b>
     * @see PetRepository#findPetsByCustomer(Customer)
     */
    public List<Pet> findPetsByCustomer(Customer customer) {
        return petRepository.findPetsByCustomer(customer);
    }

    /**
     * @param customer record Customer
     * @return list of "Pet" where Pet's customer field is <b>{@code customer}</b> sorted by "id"
     */
    public List<Pet> findPetsByCustomerOrderById(Customer customer) {
        return petRepository.findPetsByCustomerOrderById(customer);
    }

    /**
     * Список питомцев, для которых назначен усыновитель
     *
     * @return список питомцев
     */
    @Override
    public List<Pet> findPetsWithCustomer() {
        return petRepository.findPetsByCustomerNotNull();
    }

}
