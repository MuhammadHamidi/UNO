package impl;

import entities.Card;
import entities.cards.Color;
import entities.cards.ColoredCard;
import entities.cards.Type;
import entities.cards.WildCard;
import entities.cards.colored.Action;
import entities.cards.colored.ActionCard;
import entities.cards.colored.NumCard;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Functions {
    private Scanner input = new Scanner(System.in);

    public ArrayList<Card> validCards(ArrayList<Card> cards , Card card){
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

    public Card playACard(ArrayList<Card> cards , Card card , Boolean punish) {
        ArrayList<Card> valid = validCards(cards , card);
        if(valid.size()==0){
            return null;
        }
        ArrayList<Card> draws = new ArrayList<>();
        for (int i=0 ; i<valid.size() ; i++) {
            if(valid.get(i) instanceof WildCard && ((WildCard) valid.get(i)).getType() == Type.WILD_4){
                draws.add(valid.get(i));
            }
            if(valid.get(i) instanceof ActionCard && ((ActionCard) valid.get(i)).getAction() == Action.DRAW2){
                draws.add(valid.get(i));
            }
        }
        if(punish && draws.size()==0){
            return null;
        }
        else if(punish){
            System.out.println("valid cards {");
            for (int i=0 ; i<draws.size() ; i++) {
                System.out.println("\tcart " + (i) + " :" + draws.get(i));
            }
            System.out.println("}\n");
            int index;
            while (true){
                System.out.println("please choose one of indexes :");
                String in = input.nextLine();
                if(in.length() == 0){
                    System.out.println("type at least one character");
                    continue;
                }
                index = Integer.parseInt(in);
                if(index >= draws.size() || index<0){
                    System.out.println("invalid input!");
                    continue;
                }
                break;
            }
            if (draws.get(index) instanceof WildCard){
                int color;
                while (true){
                    System.out.println("please choose the next color by index :");
                    System.out.println("0:RED     1:YELLOW     2:GREEN     3:BLUE");
                    String in = input.nextLine();
                    if(in.length() == 0){
                        System.out.println("type at least one character");
                        continue;
                    }
                    color = Integer.parseInt(in);
                    if(color > 3 || color < 0){
                        System.out.println("invalid input!");
                        continue;
                    }
                    break;
                }
                if(color == 0)
                    ((WildCard) draws.get(index)).setNextColor(Color.RED);
                else if(color == 1){
                    ((WildCard) draws.get(index)).setNextColor(Color.YELLOW);
                }
                else if(color == 2){
                    ((WildCard) draws.get(index)).setNextColor(Color.GREEN);
                }
                else {
                    ((WildCard) draws.get(index)).setNextColor(Color.BLUE);
                }
            }
            return draws.get(index);
        }
        else {
            System.out.println("valid cards {");
            for (int i=0 ; i<valid.size() ; i++) {
                System.out.println("\tcart " + (i) + " :" + valid.get(i));
            }
            System.out.println("}\n");
            int index;
            while (true){
                System.out.println("please choose one of indexes :");
                String in = input.nextLine();
                if(in.length() == 0){
                    System.out.println("type at least one character");
                    continue;
                }
                index = Integer.parseInt(in);
                if(index >= valid.size() || index<0){
                    System.out.println("invalid input!");
                    continue;
                }
                break;
            }
            if (valid.get(index) instanceof WildCard){
                int color;
                while (true){
                    System.out.println("please choose the next color by index :");
                    System.out.println("0:RED     1:YELLOW     2:GREEN     3:BLUE");
                    String in = input.nextLine();
                    if(in.length() == 0){
                        System.out.println("type at least one character");
                        continue;
                    }
                    color = Integer.parseInt(in);
                    if(color > 3 || color < 0){
                        System.out.println("invalid input!");
                        continue;
                    }
                    break;
                }
                if(color == 0)
                    ((WildCard) valid.get(index)).setNextColor(Color.RED);
                else if(color == 1){
                    ((WildCard) valid.get(index)).setNextColor(Color.YELLOW);
                }
                else if(color == 2){
                    ((WildCard) valid.get(index)).setNextColor(Color.GREEN);
                }
                else {
                    ((WildCard) valid.get(index)).setNextColor(Color.BLUE);
                }
            }
            return valid.get(index);
        }
    }
}
