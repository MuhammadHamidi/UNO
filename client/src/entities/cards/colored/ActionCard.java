package entities.cards.colored;

import entities.cards.Color;
import entities.cards.ColoredCard;

import java.io.Serializable;

public class ActionCard extends ColoredCard implements Serializable {
    Action action;

    public ActionCard(int value, Color color, Action action) {
        super(value, color);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "" + action + " " +  getColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionCard that = (ActionCard) o;

        if(action == that.action && getColor() == that.getColor())
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return action != null ? action.hashCode() : 0;
    }
}
