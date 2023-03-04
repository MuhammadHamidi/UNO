package entities.cards.colored;

import entities.cards.Color;
import entities.cards.ColoredCard;

import java.io.Serializable;

public class NumCard extends ColoredCard implements Serializable {
    private int number;

    public NumCard(int value, Color color, int number) {
        super(value, color);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "" + number + " " + getColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumCard numCard = (NumCard) o;

        if(number == numCard.number && getColor() == numCard.getColor())
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
