package ga.heaven.service;

import ga.heaven.model.Volunteer;
import ga.heaven.repository.VolunteerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceTest {
    
    @Mock
    VolunteerRepository vr;
    
    @InjectMocks
    VolunteerService vs;
    
    
    static final Long VOLUNTEER_ID_ONE = 1L;
    static final Long VOLUNTEER_ID_TWO = 2L;
    static final String VOLUNTEER_NAME_ONE = "Иван";
    static final String VOLUNTEER_NAME_TWO = "Петр";
    static final String VOLUNTEER_SURNAME_ONE = "иванов";
    static final String VOLUNTEER_SURNAME_TWO = "Петров";
    static final String VOLUNTEER_SECOND_NAME_ONE = "Иванович";
    static final String VOLUNTEER_SECOND_NAME_TWO = "Петрович";
    static final Integer NUMBER_OF_INVOCATIONS = 1;
    
    static final Volunteer VOLUNTEER_OBJ_ONE = new Volunteer(VOLUNTEER_ID_ONE, null,VOLUNTEER_SURNAME_ONE, VOLUNTEER_NAME_ONE, VOLUNTEER_SECOND_NAME_ONE,null, null, null);
    static final Volunteer VOLUNTEER_OBJ_TWO = new Volunteer(VOLUNTEER_ID_TWO, null,VOLUNTEER_SURNAME_TWO, VOLUNTEER_NAME_TWO, VOLUNTEER_SECOND_NAME_TWO,null, null, null);
    static final Volunteer VOLUNTEER_OBJ_ONE_EDITED = new Volunteer(VOLUNTEER_ID_ONE, null,VOLUNTEER_SURNAME_ONE, VOLUNTEER_NAME_ONE, VOLUNTEER_SECOND_NAME_ONE,null, null, null);
    
    static final List<Volunteer> LIST_OF_TWO_VOLUNTEERS = List.of(VOLUNTEER_OBJ_ONE, VOLUNTEER_OBJ_TWO);
    
    @Test
    void createVolunteerTest() {
        when(vr.save(VOLUNTEER_OBJ_ONE)).thenReturn(VOLUNTEER_OBJ_ONE);
        Volunteer actual, expected;
        expected = VOLUNTEER_OBJ_ONE;
        actual = vs.createVolunteer(VOLUNTEER_OBJ_ONE);
        assertEquals(expected, actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getId(), actual.getId());
    }
    
    @Test
    void readVolunteerTest() {
        when(vr.findById(VOLUNTEER_ID_ONE)).thenReturn(Optional.ofNullable(VOLUNTEER_OBJ_ONE));
        Volunteer actual, expected;
        expected = VOLUNTEER_OBJ_ONE;
        actual = vs.findVolunteerById(VOLUNTEER_ID_ONE);
        assertEquals(expected, actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getId(), actual.getId());
    }
    
    @Test
    void readAllVolunteerTest() {
        when(vr.findAll()).thenReturn(LIST_OF_TWO_VOLUNTEERS);
        Collection<Volunteer> actual, expected;
        expected = LIST_OF_TWO_VOLUNTEERS;
        actual = vs.findAllVolunteers();
        assertEquals(expected, actual);
    }
    
    @Test
    void updateVolunteerTest() {
        when(vr.save(VOLUNTEER_OBJ_ONE)).thenReturn(VOLUNTEER_OBJ_ONE_EDITED);
        Volunteer actual, expected;
        actual = vs.updateVolunteer(VOLUNTEER_OBJ_ONE_EDITED);
        expected = VOLUNTEER_OBJ_ONE_EDITED;
        assertEquals(expected, actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getId(), actual.getId());
    }
    
    @Test
    void deleteVolunteerTest() {
        vs.deleteVolunteer(VOLUNTEER_ID_ONE);
        verify(vr, times(NUMBER_OF_INVOCATIONS)).deleteById(VOLUNTEER_ID_ONE);
    }
}