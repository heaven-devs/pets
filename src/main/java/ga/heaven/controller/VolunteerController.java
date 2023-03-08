package ga.heaven.controller;

import ga.heaven.model.Report;
import ga.heaven.model.Volunteer;
import ga.heaven.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer")
@Tag(name = "\uD83D\uDC68\u200D⚕️ Volunteer store", description = "Volunteer dependence model CRUD endpoints")
public class VolunteerController {
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @Operation(
            summary = "Search for all volunteers from the database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found volunteerss",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = List.class)
                            )
                    ),

            },
            tags = "\uD83D\uDC68\u200D⚕️ Volunteer store"
    )
    @GetMapping
    public ResponseEntity<List<Volunteer>> findAllVolunteers(){
        return ResponseEntity.ok(volunteerService.findAllVolunteers());
    }
    
    @Operation(
            summary = " Search for a volunteer by its ID in the database.",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Volunteer JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report[].class)
                    )
            )
    })
    @GetMapping("/{id}")
        public ResponseEntity<Volunteer> findVolunteerById(@PathVariable  long id) {
        Volunteer volunteer = volunteerService.findVolunteerById(id);
        if (volunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(volunteer);
    }
    
    @Operation(
            summary = "Create a volunteer and add it to the database. ",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Volunteer JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report[].class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Volunteer> createVolunteer(@RequestBody Volunteer volunteer){
        Volunteer createVolunteer = volunteerService.createVolunteer(volunteer);
        return ResponseEntity.ok(createVolunteer);
    }
    
    @Operation(
            summary = "Edits volunteers",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Volunteer JSON",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report[].class)
                            )
                    )
            })
    @PutMapping
    public ResponseEntity<Volunteer> updateVolunteer(@RequestBody Volunteer volunteer){
        Volunteer update = volunteerService.updateVolunteer(volunteer);
        if(update==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(update);
    }
    
    @Operation(
            summary = "Deleting  for a volunteer by its ID in the database.(Table - Volunteer)",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Volunteer JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report[].class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Volunteer> deleteVolunteer(@PathVariable long id){
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.ok().build();
    }

}

