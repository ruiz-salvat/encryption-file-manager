package DataObjetcs;

import java.io.Serializable;

public class DataFileDto implements Serializable {

    public String name;
    public String content;

    public DataFileDto() {
        super();
    }

    public DataFileDto(String name, String content) {
        this.name = name;
        this.content = content;
    }

}
