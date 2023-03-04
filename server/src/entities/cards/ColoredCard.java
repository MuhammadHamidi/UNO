package entities.cards;

import entities.Card;

import java.io.Serializable;

public abstract class ColoredCard extends Card implements Serializable {
    private Color color;

    public ColoredCard(int value, Color color) {
        super(value);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
