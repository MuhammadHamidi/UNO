package users;

import entities.Card;
import entities.cards.ColoredCard;
import entities.cards.WildCard;
import entities.cards.colored.ActionCard;
import entities.cards.colored.NumCard;

import java.util.ArrayList;

public abstract class User {
    ArrayList<Card> cards ;
    String username;
    int score = 0;

    public User(String username) {
        this.username = username;
    }

    public abstract Card playACard(Card card , Boolean punish , int punishNum);

    public void setCards(ArrayList<Card> cards){
        this.cards = cards;
    }

    public void addCarts(ArrayList<Card> cards){
        this.cards.addAll(cards);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Card> validCards(Card card){
        ArrayList<Card> valid = new ArrayList<>();
        if(card instanceof NumCard){
            for (int i=0 ; i<cards.size() ; i++) {
                if(cards.get(i) instanceof NumCard && (((NumCard) cards.get(i)).getNumber() == ((NumCard) card).getNumber() || ((NumCard) cards.get(i)).getColor() == ((NumCard) card).getColor())){
                    valid.add(cards.get(i));
                }
                else if(cards.get(i) instanceof ActionCard && ((ActionCard) cards.get(i)).getColor() == ((NumCard) card).getColor()){
                    valid.add(cards.get(i));
                }
            }
        }
        else if(card instanceof ActionCard){
            for (int i=0 ; i<cards.size() ; i++) {
                if(cards.get(i) instanceof NumCard && ((NumCard) cards.get(i)).getColor() == ((ActionCard) card).getColor()){
                    valid.add(cards.get(i));
                }
                else if(cards.get(i) instanceof ActionCard && (((ActionCard) cards.get(i)).getColor() == ((ActionCard) card).getColor() || ((ActionCard) cards.get(i)).getAction() == ((ActionCard) card).getAction())){
                    valid.add(cards.get(i));
                }
            }
        }
        else if(card instanceof WildCard){
            for (int i=0 ; i<cards.size() ; i++) {
                if(cards.get(i) instanceof ColoredCard && ((ColoredCard) cards.get(i)).getColor() == ((WildCard) card).getNextColor()){
                    valid.add(cards.get(i));
                }
            }
        }
        if(valid.size() == 0){
            for (int i=0 ; i<cards.size() ; i++) {
                if(cards.get(i) instanceof WildCard ){
                    valid.add(cards.get(i));
                }
            }
        }
        return valid;
    }

    public String getUsername() {
        return username;
    }

    public void addScore(int score){
        this.score += score;
    }

    public int getScore() {
        return score;
    }
}
