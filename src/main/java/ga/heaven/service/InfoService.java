package ga.heaven.service;

import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.repository.InfoRepository;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

@Service
public class InfoService {
    private final InfoRepository infoRepository;

    public InfoService(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public List<Info> getAll() {
        return infoRepository.findAll();
    }

    public Info findInfoById(long id) {
        return infoRepository.findById(id).orElse(null);
    }

    public Info createInfo(Info info) {
        return infoRepository.save(info);
    }

    private Info getInfoById(Long id) {
        return infoRepository.findInfoById(id).orElse(null);
    }
    public Info updateInfo(Info info) {
        if (getInfoById(info.getId()) == null) {
            return null;
        } else {
            return infoRepository.save(info);
        }
    }

    public Info deleteInfoById(Long id) {
        Info info = getInfoById(id);
        if (info == null) {
            return null;
        } else {
            infoRepository.deleteById(id);
            return info;
        }
    }

    public Info findInfoByArea(String area) {
        Info info = infoRepository.findInfoByArea(area).orElse(null);
        return info;
    }
}
