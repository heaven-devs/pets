package ga.heaven.controller;

import ga.heaven.model.Breed;
import ga.heaven.model.Shelter;
import ga.heaven.service.BreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
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

    @Operation(
            tags = "Breed",
            summary = "search for all breed in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found breeds",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Breed.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<Breed>> readAll() {
        List<Breed> breeds = breedService.findAll();
        return ResponseEntity.ok(breeds);
    }

    @Operation(
            tags = "Breed",
            summary = "search breed by its ID in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found breed",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Breed.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No breed found with this id"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Breed> readById(@Parameter(description = "id of the breed to find") @PathVariable Long id) {
        Breed breed = breedService.findById(id);
        if (breed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(breed);
    }

    @Operation(
            tags = "Breed",
            summary = "add new breed in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Added breed",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Breed.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "the id field is ignored, id is automatically incremented by 1",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Breed.class)
                    )
            )
    )
    @PostMapping
    public ResponseEntity<Breed> create(@RequestBody Breed breed) {
        Breed createdBreed = breedService.create(breed);
        return ResponseEntity.ok(createdBreed);
    }

    @Operation(
            tags = "Breed",
            summary = "update breed by its ID in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated breed",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Breed.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No breed found with this id"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "if a non-existent id is entered, a 404 error will be returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Breed.class)
                    )
            )
    )
    @PutMapping
    public ResponseEntity<Breed> update(@RequestBody Breed breed) {
        Breed updatedBreed = breedService.update(breed);
        if (updatedBreed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBreed);
    }

    @Operation(
            tags = "Breed",
            summary = "delete breed by its ID in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Deleted breed",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Breed.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No breed found with this id"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "if a non-existent id is entered, a 404 error will be returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Breed.class))
                    )
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Breed> delete(@Parameter(description = "id of the breed to delete") @PathVariable Long id) {
        Breed deletedBreed = breedService.delete(id);
        if (deletedBreed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deletedBreed);
    }
}
