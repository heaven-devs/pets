package ga.heaven.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class PetShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id
    private String name; // название приюта питомцев
    private String address; // адрес

    // Описание файла со схемой проезда
    private String filePath;
    private long fileSize;
    private String mediaType;
    private byte[] locationMap; // схема проезда

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public byte[] getLocationMap() {
        return locationMap;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setLocationMap(byte[] locationMap) {
        this.locationMap = locationMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetShop petShop = (PetShop) o;
        return id == petShop.id && fileSize == petShop.fileSize && Objects.equals(name, petShop.name) && Objects.equals(address, petShop.address) && Objects.equals(filePath, petShop.filePath) && Objects.equals(mediaType, petShop.mediaType) && Arrays.equals(locationMap, petShop.locationMap);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, address, filePath, fileSize, mediaType);
        result = 31 * result + Arrays.hashCode(locationMap);
        return result;
    }
}
