package DataObjetcs;

import java.io.Serializable;

public class PrivateKeyDto implements Serializable {

    public String name;
    public String privateKey;

    public PrivateKeyDto() {
        super();
    }

    public PrivateKeyDto(String name, String privateKey) {
        this.name = name;
        this.privateKey = privateKey;
    }
}
