package ga.heaven.listener;

import ga.heaven.model.Info;
import ga.heaven.service.InfoService;
import ga.heaven.service.PetSelectorService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetSelectorServiceTest {
    @InjectMocks
    PetSelectorService petListener;
    @Mock
    private InfoService infoService;

    @Test
    public void createTest() {
        String areaTest1 = "Группа правил 1";
        String instructionsTest1 = "Инструкция 1";

        Info info = new Info();
        info.setId(1L);
        info.setArea(areaTest1);
        info.setInstructions(instructionsTest1);

        when(infoService.findInfoByArea(anyString())).thenReturn(info);

        //Assertions.assertThat(petListener.getDatingRules()).isEqualTo("Инструкция 1");

        when(infoService.findInfoByArea(anyString())).thenReturn(null);
        //assertThat(petListener.getDatingRules()).isEqualTo("Информация по обращению с питомцами не найдена. Обратитесь к администрации");
    }
}