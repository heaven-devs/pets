package ga.heaven.controller;

import ga.heaven.model.Breed;
import ga.heaven.service.BreedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/breed")
public class BreedController {
    private final BreedService breedService;

    public BreedController(BreedService breedService) {
        this.breedService = breedService;
    }

    @GetMapping
    public ResponseEntity<List<Breed>> readAll() {
        List<Breed> breeds = breedService.findAll();
        return ResponseEntity.ok(breeds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Breed> readById(@PathVariable Long id) {
        Breed breed = breedService.findById(id);
        if (breed == null) {
            return ResponseEntity.notFound().build();
        }
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
        if (updatedBreed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBreed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Breed> delete(@PathVariable Long id) {
        Breed deletedBreed = breedService.delete(id);
        if (deletedBreed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deletedBreed);
    }
}
