package entities.cards;

import entities.Card;

import java.io.Serializable;

public class WildCard extends Card implements Serializable {
    private Type type;
    private Color nextColor;

    public WildCard(int value, Type type) {
        super(value);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setNextColor(Color nextColor) {
        this.nextColor = nextColor;
    }

    public Color getNextColor() {
        return nextColor;
    }

    @Override
    public String toString() {
        return "" + type ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WildCard wildCard = (WildCard) o;

        return type == wildCard.type;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
