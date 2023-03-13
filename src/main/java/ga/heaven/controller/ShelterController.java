package ga.heaven.controller;

import ga.heaven.model.Shelter;
import ga.heaven.service.ShelterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelter")
@Tag(name = "\uD83C\uDFE0 Shelter store", description = "Shelter dependence model CRUD endpoints")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @Operation(
            tags = "\uD83C\uDFE0 Shelter store",
            summary = "search for all shelters in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found shelters",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Shelter.class))
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<Shelter>> findAll() {
        List<Shelter> shelters = shelterService.findAll();
        return ResponseEntity.ok(shelters);
    }

    @Operation(
            tags = "\uD83C\uDFE0 Shelter store",
            summary = "search shelter by its ID in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found shelter",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No shelter found with this id"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Shelter> findById(@Parameter(description = "id of the shelter to find") @PathVariable Long id) {
        Shelter shelter = shelterService.findById(id);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shelter);
    }

    @Operation(
            tags = "\uD83C\uDFE0 Shelter store",
            summary = "add new shelter in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Added shelter",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "the id field is ignored, id is automatically incremented by 1",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class)
                    )
            )
    )
    @PostMapping
    public ResponseEntity<Shelter> create(@RequestBody Shelter shelter) {
        Shelter createdShelter = shelterService.create(shelter);
        return ResponseEntity.ok(createdShelter);
    }

    @Operation(
            tags = "\uD83C\uDFE0 Shelter store",
            summary = "update shelter by its ID in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated shelter",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No shelter found with this id"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "if a non-existent id is entered, a 404 error will be returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class)
                    )
            )
    )
    @PutMapping
    public ResponseEntity<Shelter> update(@RequestBody Shelter shelter) {
        Shelter updatedShelter = shelterService.update(shelter);
        if (updatedShelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedShelter);
    }

    @Operation(
            tags = "\uD83C\uDFE0 Shelter store",
            summary = "delete shelter by its ID in the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Deleted shelter",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No shelter found with this id"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "if a non-existent id is entered, a 404 error will be returned",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class)
                    )
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Shelter> delete(@Parameter(description = "id of the shelter to delete") @PathVariable Long id) {
        Shelter deletedShelter = shelterService.delete(id);
        if (deletedShelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deletedShelter);
    }

}
