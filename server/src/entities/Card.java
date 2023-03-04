package entities;

import java.io.Serializable;

public abstract class Card implements Serializable {
    private int value ;

    public Card(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
