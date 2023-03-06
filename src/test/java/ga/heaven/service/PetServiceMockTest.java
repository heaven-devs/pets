package ga.heaven.service;

import ga.heaven.model.Pet;
import ga.heaven.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceMockTest {
    @Mock
    PetRepository pr;
    
    @InjectMocks
    PetServiceImpl ps;
    
    
    static final Long PET_ID_ONE = 1L;
    static final Long PET_ID_TWO = 2L;
    static final String PET_NAME_ONE = "Васька";
    static final String PET_NAME_TWO = "Черныш";
    static final String PET_NAME_ONE_EDITED = "Васька лопоухий";
    static final Integer PET_AGE_ONE = 2;
    static final Integer PET_AGE_TWO = 5;
    static final Integer PET_AGE_ONE_EDITED = 3;
    static final Integer NUMBER_OF_INVOCATIONS = 1;
    
    static final Pet PET_OBJ_ONE = new Pet(PET_ID_ONE, null, PET_AGE_ONE, PET_NAME_ONE, null, null, null, null, null, null, null);
    static final Pet PET_OBJ_TWO = new Pet(PET_ID_TWO, null, PET_AGE_TWO, PET_NAME_TWO, null, null, null, null, null, null, null);
    static final Pet PET_OBJ_ONE_EDITED = new Pet(PET_ID_ONE, null, PET_AGE_ONE_EDITED, PET_NAME_ONE_EDITED, null, null, null, null, null, null, null);
    
    static final List<Pet> LIST_OF_TWO_PETS = List.of(PET_OBJ_ONE, PET_OBJ_TWO);
    
    @Test
    void createPetTest() {
        when(pr.save(PET_OBJ_ONE)).thenReturn(PET_OBJ_ONE);
        Pet actual, expected;
        expected = PET_OBJ_ONE;
        actual = ps.create(PET_OBJ_ONE);
        assertEquals(expected, actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAgeInMonths(), actual.getAgeInMonths());
    }
    
    @Test
    void readPetTest() {
        when(pr.findById(PET_ID_ONE)).thenReturn(Optional.ofNullable(PET_OBJ_ONE));
        Pet actual, expected;
        expected = PET_OBJ_ONE;
        actual = ps.read(PET_ID_ONE);
        assertEquals(expected, actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAgeInMonths(), actual.getAgeInMonths());
    }
    
    @Test
    void readAllPetTest() {
        when(pr.findAll()).thenReturn(LIST_OF_TWO_PETS);
        Collection<Pet> actual, expected;
        expected = LIST_OF_TWO_PETS;
        actual = ps.read();
        assertEquals(expected, actual);
    }
    
    @Test
    void updatePetTest() {
        when(pr.save(PET_OBJ_ONE)).thenReturn(PET_OBJ_ONE_EDITED);
        when(pr.existsById(PET_ID_ONE)).thenReturn(true);
        Pet actual, expected;
        actual = ps.update(PET_ID_ONE, PET_OBJ_ONE_EDITED);
        expected = PET_OBJ_ONE_EDITED;
        assertEquals(expected, actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAgeInMonths(), actual.getAgeInMonths());
    }
    
    @Test
    void deletePetTest() {
        when(pr.findById(PET_ID_ONE)).thenReturn(Optional.ofNullable(PET_OBJ_ONE));
        ps.delete(PET_ID_ONE);
        verify(pr, times(NUMBER_OF_INVOCATIONS)).deleteById(PET_ID_ONE);
    }
    
}