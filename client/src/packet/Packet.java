package packet;

import java.io.Serializable;

public class Packet implements Serializable {
    private Object object;

    public Packet(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}
