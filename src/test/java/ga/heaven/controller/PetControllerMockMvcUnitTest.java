package ga.heaven.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ga.heaven.model.Pet;
import ga.heaven.repository.PetRepository;
import ga.heaven.service.PetServiceImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PetController.class)
class PetControllerMockMvcUnitTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PetRepository pr;
    
    @SpyBean
    private PetServiceImpl ps;
    
    @InjectMocks
    private PetController pc;
    
    private final String URL_ENDPOINT = "/pet";
    private final JSONObject pj = new JSONObject();
    private static final Long PET_ID_ONE = 1L;
    private static final Long PET_ID_TWO = 2L;
    private static final Long PET_ID_NOT_EXISTED = 3L;
    private static final String PET_NAME_ONE = "Васька";
    private static final String PET_NAME_TWO = "Черныш";
    private static final String PET_NAME_ONE_EDITED = "Васька лопоухий";
    private static final Integer PET_AGE_ONE = 2;
    private static final Integer PET_AGE_TWO = 5;
    private static final Integer PET_AGE_ONE_EDITED = 3;
    
    private static final Pet PET_OBJ_ONE = new Pet(PET_ID_ONE, null, PET_AGE_ONE, PET_NAME_ONE, null, null, null, null, null, null, null);
    static final Pet PET_OBJ_TWO = new Pet(PET_ID_TWO, null, PET_AGE_TWO, PET_NAME_TWO, null, null, null, null, null, null, null);
    static final Pet PET_OBJ_ONE_EDITED = new Pet(PET_ID_ONE, null, PET_AGE_ONE_EDITED, PET_NAME_ONE_EDITED, null, null, null, null, null, null, null);
    
    static final List<Pet> LIST_OF_TWO_PETS = List.of(PET_OBJ_ONE, PET_OBJ_TWO);
    
    private static ResultMatcher fieldMatcher(Pet pet) {
        return ResultMatcher.matchAll(
                jsonPath("$.id").value(pet.getId()),
                jsonPath("$.name").value(pet.getName()),
                jsonPath("$.ageInMonths").value(pet.getAgeInMonths())
        );
    }
    
    private static Object deserializeActualWithTypeAsExpected(String content,
                                                              Object expected) throws JsonProcessingException {
        return new ObjectMapper().readValue(content, expected.getClass());
    }
    
    private static List<?> deserializeActualWithTypeAsExpected(String content, Class objClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, objClass));
    }
    
    @BeforeEach
    void setUp() throws JSONException {
        pj.put("id", PET_ID_ONE);
        pj.put("name", PET_NAME_ONE);
        pj.put("ageInMonths", PET_AGE_ONE);
    }
    
    @Test
    void createPetTest() throws Exception {
        Pet expected = PET_OBJ_ONE;
        when(pr.save(any(Pet.class))).thenReturn(expected);
        String response =
                mockMvc.perform(MockMvcRequestBuilders
                                .post(URL_ENDPOINT)
                                .content(pj.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(HttpStatus.OK.value()))
                        .andExpect(fieldMatcher(expected))
                        .andReturn().getResponse().getContentAsString();
        Pet actual = (Pet) deserializeActualWithTypeAsExpected(response, expected);
        assertEquals(expected, actual);
    }
    
    @Test
    void readPetByIDPositiveTest() throws Exception {
        Pet expected = PET_OBJ_ONE;
        when(pr.findById(any(Long.class))).thenReturn(Optional.of(expected));
        String response =
                mockMvc.perform(MockMvcRequestBuilders
                                .get(URL_ENDPOINT + "/" + PET_ID_ONE)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(fieldMatcher(expected))
                        .andReturn().getResponse().getContentAsString();
        Pet actual = (Pet) deserializeActualWithTypeAsExpected(response, expected);
        assertEquals(expected, actual);
    }
    
    @Test
    void readPetByIDNegativeTest() throws Exception {
        int expected = HttpStatus.NOT_FOUND.value();
        when(pr.findById(ArgumentMatchers.eq(PET_ID_NOT_EXISTED)))
                .thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_ENDPOINT + "/" + PET_ID_NOT_EXISTED)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expected));
    }
    
    @Test
    void readAllPetsPositiveTest() throws Exception {
        List<Pet> expected, actual;
        expected = LIST_OF_TWO_PETS;
        when(pr.findAll()).thenReturn(expected);
        
        String response =
                mockMvc.perform(MockMvcRequestBuilders
                                .get(URL_ENDPOINT)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        actual = (List<Pet>) deserializeActualWithTypeAsExpected(response, Pet.class);
        assertEquals(expected, actual);
    }
    
    @Test
    void updatePetPositiveTest() throws Exception {
        Pet actual, expected;
        expected = PET_OBJ_ONE_EDITED;
        when(pr.save(any(Pet.class))).thenReturn(expected);
        when(pr.existsById(PET_ID_ONE)).thenReturn(true);
        
        String response =
                mockMvc.perform(MockMvcRequestBuilders
                                .put(URL_ENDPOINT + "/" + PET_ID_ONE)
                                .content(pj.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(HttpStatus.OK.value()))
                        .andExpect(fieldMatcher(expected))
                        .andReturn().getResponse().getContentAsString();
        actual = (Pet) deserializeActualWithTypeAsExpected(response, expected);
        assertEquals(expected, actual);
    }
    
    @Test
    void deletePetPositiveTest() throws Exception {
        when(pr.findById(any(Long.class))).thenReturn(Optional.of(PET_OBJ_ONE));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL_ENDPOINT + "/" + PET_ID_ONE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    
}