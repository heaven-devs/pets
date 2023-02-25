package ga.heaven.controller;

import ga.heaven.model.Volunteer;
import ga.heaven.service.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping
    public ResponseEntity<List<Volunteer>> findAllVolunteers(){
        return ResponseEntity.ok(volunteerService.findAllVolunteers());
    }

    @GetMapping("/{volunteerId}")
        public ResponseEntity<Volunteer> findVolunteerById(@PathVariable  long id) {
        Volunteer volunteer = volunteerService.findVolunteerById(id);
        if (volunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(volunteer);
    }

    @PostMapping
    public ResponseEntity<Volunteer> createVolunteer(@RequestBody Volunteer volunteer){
        Volunteer createVolunteer = volunteerService.createVolunteer(volunteer);
        return ResponseEntity.ok(createVolunteer);
    }

    @PutMapping
    public ResponseEntity<Volunteer> updateVolunteer(@RequestBody Volunteer volunteer){
        Volunteer update = volunteerService.updateVolunteer(volunteer);
        if(update==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{volunteerId}")
    public ResponseEntity<Volunteer> deleteVolunteer(@PathVariable long id){
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.ok().build();
    }

}

