package ga.heaven.service;

import ga.heaven.model.Breed;
import ga.heaven.repository.BreedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BreedServiceTest {

    @InjectMocks
    private BreedService breedService;

    @Mock
    private BreedRepository breedRepository;

    private List<Breed> expectedBreedList;
    private Breed expectedBreed;

    @BeforeEach
    private void getInitialTestBreeds() {
        expectedBreedList = List.of(
                new Breed(1, "Sheepdog", "Puppy recommendation", "Recommendation for an adult dog", 10),
                new Breed(2, "Poodle", "Puppy recommendation", "Recommendation for an adult dog", 12),
                new Breed(3, "Collie", "Puppy recommendation", "Recommendation for an adult dog", 20)
        );
        expectedBreed = expectedBreedList.get(0);
    }

    @Test
    void findAllBreeds() {
        when(breedRepository.findAll()).thenReturn(expectedBreedList);
        List<Breed> actual = breedService.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expectedBreedList);
    }

    @Test
    void findBreedByIdPositive() {
        Long testId = expectedBreed.getId();
        when(breedRepository.findById(testId)).thenReturn(Optional.of(expectedBreed));
        Breed actual = breedService.findById(testId);
        Assertions.assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void createBreed() {
        when(breedRepository.save(expectedBreed)).thenReturn(expectedBreed);
        Breed actual = breedService.create(expectedBreed);
        Assertions.assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void updateBreed() {
        Long testId = expectedBreed.getId();
        when(breedRepository.findById(testId)).thenReturn(Optional.ofNullable(expectedBreed));
        when(breedRepository.save(expectedBreed)).thenReturn(expectedBreed);
        Breed actual = breedService.update(expectedBreed);
        assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void deleteBreed() {
        Long testId = expectedBreed.getId();
        when(breedRepository.findById(testId)).thenReturn(Optional.ofNullable(expectedBreed));
        Breed actual = breedService.delete(testId);
        assertThat(actual).isEqualTo(expectedBreed);
    }


}
