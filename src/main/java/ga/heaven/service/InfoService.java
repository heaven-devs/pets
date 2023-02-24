package ga.heaven.service;

import ga.heaven.model.Info;
import ga.heaven.repository.InfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoService {
    private final InfoRepository infoRepository;

    public InfoService(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public List<Info> getAll() {
        return infoRepository.findAll();
    }
}
