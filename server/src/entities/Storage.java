package entities;

import entities.cards.Color;
import entities.cards.Type;
import entities.cards.WildCard;
import entities.cards.colored.Action;
import entities.cards.colored.ActionCard;
import entities.cards.colored.NumCard;

import java.util.ArrayList;
import java.util.Random;

public class Storage {
    private ArrayList<Card> cards ;
    private Card start;
    private Random random = new Random();

    {
        cards = new ArrayList<>();

        cards.add(new NumCard(0 , Color.RED , 0));
        cards.add(new NumCard(0 , Color.YELLOW , 0));
        cards.add(new NumCard(0 , Color.GREEN , 0));
        cards.add(new NumCard(0 , Color.BLUE , 0));
        for (int i=1 ; i<=9 ; i++) {
            for (int j=0 ; j<2 ; j++) {
                cards.add(new NumCard(i , Color.RED , i));
                cards.add(new NumCard(i , Color.YELLOW , i));
                cards.add(new NumCard(i , Color.GREEN , i));
                cards.add(new NumCard(i , Color.BLUE , i));
            }
        }
        for (int i=0 ; i<2 ; i++) {
            cards.add(new ActionCard(20 , Color.RED , Action.SKIP));
            cards.add(new ActionCard(20 , Color.YELLOW , Action.SKIP));
            cards.add(new ActionCard(20 , Color.GREEN , Action.SKIP));
            cards.add(new ActionCard(20 , Color.BLUE , Action.SKIP));
            cards.add(new ActionCard(20 , Color.RED , Action.REVERSE));
            cards.add(new ActionCard(20 , Color.YELLOW , Action.REVERSE));
            cards.add(new ActionCard(20 , Color.GREEN , Action.REVERSE));
            cards.add(new ActionCard(20 , Color.BLUE , Action.REVERSE));
            cards.add(new ActionCard(20 , Color.RED , Action.DRAW2));
            cards.add(new ActionCard(20 , Color.YELLOW , Action.DRAW2));
            cards.add(new ActionCard(20 , Color.GREEN , Action.DRAW2));
            cards.add(new ActionCard(20 , Color.BLUE , Action.DRAW2));
        }
        for (int i=0 ; i<4 ; i++) {
            cards.add(new WildCard(50 , Type.WILD_4));
            cards.add(new WildCard(50 , Type.WILD_COLOR));
        }

        setStart();
    }

    private void setStart(){
        int index = random.nextInt(108);
        while (cards.get(index) instanceof WildCard) {
            index = random.nextInt(108);
        }
        start = cards.get(index);
        cards.remove(index);
    }

    public ArrayList<Card> aSetOf7Cards(){
        ArrayList<Card> cartSet = new ArrayList<>();
        for (int i=0 ; i<7 ; i++) {
            int index = random.nextInt(cards.size());
            cartSet.add(cards.get(index));
            cards.remove(index);
        }
        return cartSet;
    }

    public ArrayList<Card> punish(int num){
        ArrayList<Card> punish = new ArrayList<>();
        for (int i=0 ; i<num ; i++) {
            int index = random.nextInt(cards.size());
            punish.add(cards.get(index));
            cards.remove(index);
        }
        return punish;
    }

    public Card getStart() {
        return start;
    }
}
