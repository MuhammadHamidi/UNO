package users;

import entities.Card;
import entities.cards.Color;
import entities.cards.Type;
import entities.cards.WildCard;
import entities.cards.colored.Action;
import entities.cards.colored.ActionCard;

import java.util.ArrayList;
import java.util.Random;

public class Bot extends User{

    public Bot(String username) {
        super(username);
    }

    @Override
    public Card playACard(Card card , Boolean punish , int punishNum) {
        ArrayList<Card> valid = validCards(card);
        if(valid.size()==0){
            return null;
        }
        int drawNum = 0;
        for (int i=0 ; i<valid.size() ; i++) {
            if(valid.get(i) instanceof WildCard && ((WildCard) valid.get(i)).getType() == Type.WILD_4){
                drawNum++;
            }
            if(valid.get(i) instanceof ActionCard && ((ActionCard) valid.get(i)).getAction() == Action.DRAW2){
                drawNum++;
            }
        }
        if(punish && drawNum==0){
            return null;
        }
        else if(punish && drawNum > 0){
            for (int i=0 ; i<valid.size() ; i++) {
                if(valid.get(i) instanceof WildCard && ((WildCard) valid.get(i)).getType() == Type.WILD_4 ||
                        valid.get(i) instanceof ActionCard && ((ActionCard) valid.get(i)).getAction() == Action.DRAW2){
                    cards.remove(valid.get(i));
                    Card returnCard = valid.get(i);
                    Random random = new Random();
                    if(returnCard instanceof WildCard){
                        int color = random.nextInt(4);
                        if(color == 0)
                            ((WildCard) returnCard).setNextColor(Color.RED);
                        else if(color == 1){
                            ((WildCard) returnCard).setNextColor(Color.YELLOW);
                        }
                        else if(color == 2){
                            ((WildCard) returnCard).setNextColor(Color.GREEN);
                        }
                        else {
                            ((WildCard) returnCard).setNextColor(Color.BLUE);
                        }
                    }
                    return returnCard;
                }
            }
        }
        else if (!punish){
            Random random = new Random();
            int index = random.nextInt(valid.size());
            Card returnCard = valid.get(index);
            cards.remove(returnCard);
            if(returnCard instanceof WildCard){
                int color = random.nextInt(4);
                if(color == 0)
                    ((WildCard) returnCard).setNextColor(Color.RED);
                else if(color == 1){
                    ((WildCard) returnCard).setNextColor(Color.YELLOW);
                }
                else if(color == 2){
                    ((WildCard) returnCard).setNextColor(Color.GREEN);
                }
                else {
                    ((WildCard) returnCard).setNextColor(Color.BLUE);
                }
            }
            return returnCard;
        }
        return null;
    }
}
