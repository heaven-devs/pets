package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.Pet;

import java.util.List;

public interface PetService {
    
    Pet create(Pet pet);
    
    Pet read(Long id);
    
    List<Pet> read();
    
    Pet update(Long id, Pet pet);
    
    Pet delete(Long id);

    List<Pet> findPetsByCustomerOrderById(Customer customer);
    
}
