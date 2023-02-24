package ga.heaven.controller;

import ga.heaven.model.Breed;
import ga.heaven.service.BreedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("breed")
public class BreedController {
    private final BreedService breedService;

    public BreedController(BreedService breedService) {
        this.breedService = breedService;
    }

    @GetMapping
    public ResponseEntity<Collection<Breed>> findAll() {
        Collection<Breed> breeds = breedService.findAll();
        return ResponseEntity.ok(breeds);
    }

    @GetMapping("{id}")
    public ResponseEntity<Breed> findById(@PathVariable Long id) {
        Breed breed = breedService.findById(id);
        return ResponseEntity.ok(breed);
    }

    @PostMapping
    public ResponseEntity<Breed> create(@RequestBody Breed breed) {
        Breed createdBreed = breedService.create(breed);
        return ResponseEntity.ok(createdBreed);
    }

    @PutMapping
    public ResponseEntity<Breed> update(@RequestBody Breed breed) {
        Breed updatedBreed = breedService.update(breed);
        return ResponseEntity.ok(updatedBreed);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        breedService.delete(id);
    }
}
