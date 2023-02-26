package ga.heaven.controller;

import ga.heaven.model.Pet;
import ga.heaven.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static ga.heaven.service.ExceptionsService.statusByException;

@RestController
@RequestMapping("pet")
@Tag(name = "\uD83D\uDC36 Pet store", description = "Pet dependence model CRUD endpoints")
public class PetController {
    private final PetService petService;
    
    public PetController(PetService petService) {
        this.petService = petService;
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        String exceptionMessage = exception.getMessage();
        HttpStatus httpStatus = statusByException(exceptionMessage);
        return ResponseEntity
                .status(httpStatus)
                .body(httpStatus.name() + " (" + exceptionMessage + " -  Exception thrown by " + exception.getClass().toString() + ")");
    }
    
    
    @Operation(
            summary = "[CREATE] Adding a new Pet entity exemplar to db",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "One pet JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
            },
            tags = "\uD83D\uDC36 Pet store"
    )
    @PostMapping()
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        Pet result = petService.create(pet);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    
    @Operation(
            summary = "[READ] Returning serialized Pet entity in JSON view from db by it's ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "One pet JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
            },
            tags = "\uD83D\uDC36 Pet store"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Pet> readPetByID(@PathVariable long id) {
        Pet result = petService.read(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    
    @Operation(
            summary = "[READ] Returning a list of all pets in db",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of all pets",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Pet.class))
                            )
                    ),
                
            },
            tags = "\uD83D\uDC36 Pet store"
    )
    @GetMapping()
    public ResponseEntity<List<Pet>> readAll() {
        List<Pet> result = new ArrayList<>(petService.read());
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    @Operation(
            summary = "[UPDATE] Changing Pet entity exemplar in a db by it's ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "New version Pet's JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
            },
            tags = "\uD83D\uDC36 Pet store"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable long id, @RequestBody Pet pet) {
        Pet result = petService.update(id, pet);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    @Operation(
            summary = "[DELETE] Droping a Pet entity exemplar in a db by it's ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Deleted pet JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
            },
            tags = "\uD83D\uDC36 Pet store"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Pet> deletePet(@PathVariable long id) {
        Pet result = petService.delete(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
}
