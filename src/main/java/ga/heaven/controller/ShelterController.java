package ga.heaven.controller;

import ga.heaven.model.Shelter;
import ga.heaven.service.ShelterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("shelter")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public ResponseEntity<Collection<Shelter>> findAll() {
        Collection<Shelter> shelters = shelterService.findAll();
        return ResponseEntity.ok(shelters);
    }

    @GetMapping("{id}")
    public ResponseEntity<Shelter> findById(@PathVariable Long id) {
        Shelter shelter = shelterService.findById(id);
        return ResponseEntity.ok(shelter);
    }

    @PostMapping
    public ResponseEntity<Shelter> create(@RequestBody Shelter shelter) {
        Shelter createdShelter = shelterService.create(shelter);
        return ResponseEntity.ok(createdShelter);
    }

    @PutMapping
    public ResponseEntity<Shelter> update(@RequestBody Shelter shelter) {
        Shelter updatedShelter = shelterService.update(shelter);
        return ResponseEntity.ok(updatedShelter);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        shelterService.delete(id);
    }


}
