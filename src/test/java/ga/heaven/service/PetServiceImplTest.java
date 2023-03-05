package ga.heaven.service;

import ga.heaven.model.Pet;
import ga.heaven.repository.PetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {
    @Mock
    PetRepository pr;
    
    @InjectMocks
    PetServiceImpl ps;
    
    
    static final Long PET_ID = 1L;
    static final String PET_NAME = "Васька";
    static final String PET_NAME_EDITED = "Васька лопоухий";
    static final Integer PET_AGE = 2;
    static final Integer PET_AGE_EDITED = 3;
    static final Integer NUMBER_OF_INVOCATIONS = 1;
    
    static final Pet PET_OBJ = new Pet(PET_ID, null, PET_AGE, PET_NAME, null, null, null, null, null, null, null);
    static final Pet PET_OBJ_EDITED = new Pet(PET_ID, null, PET_AGE_EDITED, PET_NAME_EDITED, null, null, null, null, null, null, null);
    
    
    @Test
    void createPetTest() {
        Mockito.when(pr.save(PET_OBJ)).thenReturn(PET_OBJ);
        Pet actual, expected;
        expected = PET_OBJ;
        actual = ps.create(PET_OBJ);
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getAgeInMonths(), actual.getAgeInMonths());
    }
    
    @Test
    void readPetTest() {
        Mockito.when(pr.findById(PET_ID)).thenReturn(Optional.ofNullable(PET_OBJ));
        Pet actual, expected;
        expected = PET_OBJ;
        actual = ps.read(PET_ID);
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getAgeInMonths(), actual.getAgeInMonths());
    }
    
    @Test
    void updatePetTest() {
        Mockito.when(pr.save(PET_OBJ)).thenReturn(PET_OBJ_EDITED);
        Mockito.when(pr.existsById(PET_ID)).thenReturn(true);
        Pet actual, expected;
        actual = ps.update(PET_ID, PET_OBJ_EDITED);
        expected = PET_OBJ_EDITED;
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getAgeInMonths(), actual.getAgeInMonths());
    }
    
    @Test
    void deletePetTest() {
        Mockito.when(pr.findById(PET_ID)).thenReturn(Optional.ofNullable(PET_OBJ));
        ps.delete(PET_ID);
        verify(pr, times(NUMBER_OF_INVOCATIONS)).deleteById(PET_ID);
    }
    
}