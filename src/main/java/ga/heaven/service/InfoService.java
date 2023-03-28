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

    /**
     *
     * @return all records in database table "Info"
     * @see InfoRepository#findAll()
     * @see Info
     */
    public List<Info> getAll() {
        return infoRepository.findAll();
    }

    /**
     *
     * @param id value of "id" field
     * @return found Info
     * @see InfoRepository#findById(Object) 
     * @see Info
     */
    public Info findInfoById(long id) {
        return infoRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param info the value of Info record being created
     * @return created Info
     * @see InfoRepository#save(Object)
     * @see Info
     */
    public Info createInfo(Info info) {
        return infoRepository.save(info);
    }

    /*private Info getInfoById(Long id) {
        return infoRepository.findInfoById(id).orElse(null);
    }*/

    /**
     *
     * @param info updated record
     * @return updated (corrected) database record "Info". If record {@code <b>info</b>} not found returns {@code null}
     * @see InfoRepository#save(Object)
     * @see Info
     */
    public Info updateInfo(Info info) {
        //if (getInfoById(info.getId()) == null) {
        if (findInfoById(info.getId()) == null) {
            return null;
        } else {
            return infoRepository.save(info);
        }
    }

    /**
     *
     * @param id value "id" field in database table "Info"
     * @return deleted database record (entity Info)
     * @see InfoRepository#deleteById(Object) 
     * @see Info
     */
    public Info deleteInfoById(Long id) {
        Info info = findInfoById(id);
        if (info == null) {
            return null;
        } else {
            infoRepository.deleteById(id);
            return info;
        }
    }

    /**
     *
     * @param area column Area in data base table Info
     * @return an entry in the database table with the specified field "area"
     * @see InfoRepository#findFirstByAreaContainingIgnoreCase(String)
     */
    public Info findInfoByArea(String area) {
        return infoRepository.findFirstByAreaContainingIgnoreCase(area).orElse(null);
    }
}
