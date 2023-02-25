package ga.heaven.service;

import ga.heaven.model.Volunteer;
import ga.heaven.repository.VolunteerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public List<Volunteer> findAllVolunteers(){
        return volunteerRepository.findAll();
    }

    public Volunteer findVolunteerById (long id){
        return volunteerRepository.findById(id).orElse(null);
    }

    public Volunteer createVolunteer(Volunteer volunteer){
        return volunteerRepository.save(volunteer);
    }

    public Volunteer updateVolunteer(Volunteer volunteer){
        return volunteerRepository.save(volunteer);
    }

    public void deleteVolunteer(long id){
        volunteerRepository.deleteById(id);
    }
}
