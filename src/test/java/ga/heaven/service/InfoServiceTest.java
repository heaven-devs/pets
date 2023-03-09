package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.repository.InfoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfoServiceTest {
    @InjectMocks
    private InfoService infoService;
    @Mock
    private InfoRepository infoRepositoryMock;


    private List<Info> getInitialTestInfoList() {
        Info info1 = getTestInfo(1L, "area1", "instructions1");
        Info info2 = getTestInfo(2L, "area2", "instructions2");

        List<Info> infoList = new ArrayList<>();
        infoList.add(info1);
        infoList.add(info2);

        return infoList;
    }

    private Info getTestInfo(long id, String area, String instructions) {
        Info info = new Info();
        info.setId(id);
        info.setArea(area);
        info.setInstructions(instructions);
        return info;
    }
    @Test
    void getAll() {
        List<Info> infoList = getInitialTestInfoList();
        when(infoRepositoryMock.findAll()).thenReturn(infoList);
        List<Info> expected = infoList;
        List<Info> actual = infoService.getAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void findInfoById() {
        Info info = getTestInfo(1L, "area1", "instructions1");
        Info infoWrong = getTestInfo(35L, "area35", "instructions35");

        when(infoRepositoryMock.findById(1L)).thenReturn(Optional.of(info));
        when(infoRepositoryMock.findById(35L)).thenReturn(Optional.of(infoWrong));

        Info expected = info;
        Info actual = infoService.findInfoById(1L);

        Assertions.assertThat(actual).isEqualTo(expected);

        Info actualWrong = infoService.findInfoById(35L);
        Assertions.assertThat(actualWrong).isNotEqualTo(expected);
    }

    @Test
    void createInfo() {
        Info info = getTestInfo(1L, "area1", "instructions1");
        when(infoRepositoryMock.save(info)).thenReturn(info);

        Info expected = info;
        Info actual = infoService.createInfo(info);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateInfo() {
        Info infoNotFound = getTestInfo(35L, "area35", "instructions35");
        when(infoRepositoryMock.findById(35L)).thenReturn(Optional.empty());
        Info expected = null;
        Info actual = infoService.updateInfo(infoNotFound);
        Assertions.assertThat(actual).isEqualTo(expected);

        Info info = getTestInfo(1L, "area35", "instructions35");
        when(infoRepositoryMock.findById(1L)).thenReturn(Optional.of(info));
        when(infoRepositoryMock.save(info)).thenReturn(info);
        expected = info;
        actual = infoService.updateInfo(info);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteInfoById() {
        when(infoRepositoryMock.findById(35L)).thenReturn(Optional.empty());
        Info expected = null;
        Info actual = infoService.deleteInfoById(35L);
        Assertions.assertThat(actual).isEqualTo(expected);

        Info info = getTestInfo(1L, "area35", "instructions35");
        when(infoRepositoryMock.findById(1L)).thenReturn(Optional.of(info));

        expected = info;
        actual = infoService.deleteInfoById(1L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findInfoByArea() {
        Info infoMotFound = getTestInfo(35L, "area35", "instructions35");
        when(infoRepositoryMock.findFirstByAreaContainingIgnoreCase("area35")).thenReturn(Optional.empty());
        Assertions.assertThat(infoService.findInfoByArea("area35")).isNull();

        Info info = getTestInfo(1L, "area1", "instructions1");
        when(infoRepositoryMock.findFirstByAreaContainingIgnoreCase("area1")).thenReturn(Optional.of(info));
        Assertions.assertThat(infoService.findInfoByArea("area1")).isEqualTo(info);
    }
}