package ga.heaven.service;

import ga.heaven.model.Report;
import ga.heaven.model.Volunteer;
import ga.heaven.repository.VolunteerRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    /**
     * Search for all volunteers from the database. (Table - Volunteer)
     * The repository method is used{@link JpaRepository#findAll()} 
     * @return - found volunteers
     */
    public List<Volunteer> findAllVolunteers(){
        return volunteerRepository.findAll();
    }

    /**
     * Search for a volunteer by its ID in the database. (Table - Volunteer)
     * The repository method is used{@link JpaRepository#findById(Object)}
     *
     * @param id -  ID of the volunteer we are looking for.
     * @return - found volunteers.
     */
    public Volunteer findVolunteerById (long id){
        return volunteerRepository.findById(id).orElse(null);
    }

    /**
     *Create a volunteer and add it to the database. (Table - Volunteer)
     * The repository method is used{@link JpaRepository#save(Object)}
     * @param volunteer - The entity of the volunteer we want to create.
     * @return - created volunteer.
     */
    public Volunteer createVolunteer(Volunteer volunteer){
        return volunteerRepository.save(volunteer);
    }

    /**
     * Update an existing volunteer in the database.
     * The repository method is used{@link JpaRepository#save(Object)}
     * @param volunteer - ID of the volunteer we want to update.
     * @return - updated volunteer.
     */
    public Volunteer updateVolunteer(Volunteer volunteer) {
        if (findVolunteerById(volunteer.getId()) != null) {
            return volunteerRepository.save(volunteer);
        }
        return null;

//        return volunteerRepository.save(volunteer);
    }

    /**
     * Deleting  for a volunteer by its ID in the database.(Table - Volunteer)
     * The repository method is used{@link JpaRepository#deleteById(Object)}
     *
     * @param id - ID of the volunteer we want to delete.
     * @return - deleted volunteer
     */
    public Volunteer deleteVolunteer(long id){
        Volunteer volunteer = findVolunteerById(id);
        if (volunteer != null) {
            volunteerRepository.delete(volunteer);
        }
        return volunteer;

    }
}
