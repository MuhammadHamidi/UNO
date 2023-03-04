package impl;

import entities.Card;
import entities.cards.Color;
import entities.cards.ColoredCard;
import entities.cards.Type;
import entities.cards.WildCard;
import entities.cards.colored.Action;
import entities.cards.colored.ActionCard;
import entities.cards.colored.NumCard;
import packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class GamePlay {
    private Scanner input = new Scanner(System.in);
    private ArrayList<Card> cards = new ArrayList<>();
    private Functions functions = new Functions();

    public int getNumOfPlayers(){
        while (true){
            System.out.println("--------------------");
            System.out.println("please enter num of players :");
            String str = input.nextLine();
            boolean flag = false;
            for (int i=0 ; i<str.length() ; i++) {
                if(str.charAt(i) > 57 || str.charAt(i) < 48){
                    System.out.println("invalid input!");
                    flag = true;
                    break;
                }
            }
            if (flag){
                continue;
            }
            int num = Integer.parseInt(str);
            if(num < 2){
                System.out.println("invalid input!");
                continue;
            }
            return num;
        }
    }

    public void play(ObjectInputStream in , ObjectOutputStream out){
        System.out.println("game start");
        try {
            Packet packet = (Packet) in.readObject();
            HashMap<String,Object> firstData = (HashMap<String, Object>) packet.getObject();
            cards = (ArrayList<Card>) firstData.get("your cards");
            while (true) {
                System.out.println("--------------------");
                packet = (Packet) in.readObject();
                HashMap<String,Object> receive = (HashMap<String, Object>) packet.getObject();
                String turn = (String) receive.get("type");
                System.out.println(turn);

                if(turn.equals("done")){
                    int[] scores = (int[]) receive.get("scores");
                    int min = 0;
                    for (int i=1 ; i<scores.length ; i++){
                        if(scores[i] < scores[min]){
                            min = i;
                        }
                    }
                    if(min == 0){
                        System.out.println("you won :D");
                    }
                    else {
                        System.out.println("bot " + min + " won");
                    }
                    System.out.println("scores {");
                    for (int i=0 ; i<scores.length ; i++) {
                        if(i == 0){
                            System.out.println("\tyour score : " + scores[0]);
                        }
                        else {
                            System.out.println("\tBot " + i + " score : " + scores[i]);
                        }
                    }
                    System.out.println("}");
                    break;
                }

                packet = (Packet) in.readObject();
                HashMap<String,Object> data = (HashMap<String, Object>) packet.getObject();
                Card onBoard = (Card) data.get("on board");
                Color color = (Color) data.get("color");
                HashMap<Integer,Integer> numOfCards = (HashMap<Integer, Integer>) data.get("num of cards");

                System.out.println();
                System.out.println("card on board : ");
                ArrayList<Card> onBoardToPrint = new ArrayList<>();
                onBoardToPrint.add(onBoard);
                print(onBoardToPrint);
                System.out.println("color to play next : " + color);
                System.out.println("num of cards {");
                System.out.println("\tyour cards :" + cards.size());
                for (int i=1 ; i<numOfCards.size() ; i++) {
                    System.out.println("\tBot " + i + " cards :" + numOfCards.get(i));
                }
                System.out.println("}\n");

                if(turn.equals("your turn")){
                    packet = (Packet) in.readObject();
                    data = (HashMap<String, Object>) packet.getObject();
                    String type = (String) data.get("type");
                    String punish = (String) data.get("punish");
                    print(cards);
                    Card played = functions.playACard(cards , onBoard , punish.equals("true"));
                    if(played == null){
                        data = new HashMap<>();
                        data.put("play" , null);
                        packet = new Packet(data);
                        out.writeObject(packet);

                        System.out.println("get punished but still its your turn");;

                        packet = (Packet) in.readObject();
                        data = (HashMap<String, Object>) packet.getObject();
                        ArrayList<Card> add = (ArrayList<Card>) data.get("add");
                        cards.addAll(add);

                        System.out.println("after punish");
                        print(cards);

                        packet = (Packet) in.readObject();
                        data = (HashMap<String, Object>) packet.getObject();
                        punish = (String) data.get("punish");
                        played = functions.playACard(cards , onBoard , punish.equals("true"));

                        if(played == null) {
                            data = new HashMap<>();
                            data.put("play" , null);
                            packet = new Packet(data);
                            out.writeObject(packet);
                            System.out.println("you can't play because you have no valid cards.");
                        }
                        else {
                            data = new HashMap<>();
                            data.put("play" , played);
                            packet = new Packet(data);
                            out.writeObject(packet);
                            Iterator<Card> iterator = cards.iterator();
                            while (iterator.hasNext()){
                                Card itCard = iterator.next();
                                if(itCard.equals(played)){
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        data = new HashMap<>();
                        data.put("play" , played);
                        packet = new Packet(data);
                        out.writeObject(packet);
                        Iterator<Card> iterator = cards.iterator();
                        while (iterator.hasNext()){
                            Card itCard = iterator.next();
                            if(itCard.equals(played)){
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
                else {
                    packet = (Packet) in.readObject();
                    ArrayList<String> messages = (ArrayList<String>) packet.getObject();
                    for (int i=0 ; i<messages.size() ; i++) {
                        System.out.println(messages.get(i));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("server ran out");
        }
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    private void print(ArrayList<Card> cards){
        HashMap<Action,String> print = new HashMap<>();
        print.put(Action.DRAW2 , "++2");
        print.put(Action.REVERSE , "rev");
        print.put(Action.SKIP , "skp");
        HashMap<Type,String> types = new HashMap<>();
        types.put(Type.WILD_4 , "++4");
        types.put(Type.WILD_COLOR , "clr");
        for (int i=0 ; i<cards.size() ; i++) {
            Card card = cards.get(i);
            if(card instanceof ColoredCard){
                if(((ColoredCard) card).getColor() == Color.BLUE){
                    System.out.print(ANSI_BLUE + "*******" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.GREEN){
                    System.out.print(ANSI_GREEN + "*******" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.RED){
                    System.out.print(ANSI_RED + "*******" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.YELLOW){
                    System.out.print(ANSI_YELLOW + "*******" + ANSI_RESET + " ");
                }
            }
            else {
                System.out.print("******* ");
            }
        }
        System.out.println();

        for (int i=0 ; i<cards.size() ; i++) {
            Card card = cards.get(i);
            if(card instanceof ColoredCard){
                if(((ColoredCard) card).getColor() == Color.BLUE){
                    System.out.print(ANSI_BLUE + "*     *" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.GREEN){
                    System.out.print(ANSI_GREEN + "*     *" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.RED){
                    System.out.print(ANSI_RED + "*     *" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.YELLOW){
                    System.out.print(ANSI_YELLOW + "*     *" + ANSI_RESET + " ");
                }
            }
            else {
                System.out.print("*     * ");
            }
        }
        System.out.println();

        for (int i=0 ; i<cards.size() ; i++) {
            Card card = cards.get(i);
            if(card instanceof NumCard){
                if(((NumCard) card).getColor() == Color.BLUE){
                    System.out.print(ANSI_BLUE + "*  " + ((NumCard) card).getNumber() + "  *" + ANSI_RESET + " ");
                }
                else if(((NumCard) card).getColor() == Color.GREEN){
                    System.out.print(ANSI_GREEN + "*  " + ((NumCard) card).getNumber() + "  *" + ANSI_RESET + " ");
                }
                else if(((NumCard) card).getColor() == Color.RED){
                    System.out.print(ANSI_RED + "*  " + ((NumCard) card).getNumber() + "  *" + ANSI_RESET + " ");
                }
                else if(((NumCard) card).getColor() == Color.YELLOW){
                    System.out.print(ANSI_YELLOW + "*  " + ((NumCard) card).getNumber() + "  *" + ANSI_RESET + " ");
                }
            }
            else if(card instanceof ActionCard){
                if(((ActionCard) card).getColor() == Color.BLUE){
                    System.out.print(ANSI_BLUE + "* " + print.get(((ActionCard) card).getAction()) + " *" + ANSI_RESET + " ");
                }
                else if(((ActionCard) card).getColor() == Color.GREEN){
                    System.out.print(ANSI_GREEN + "* " + print.get(((ActionCard) card).getAction()) + " *" + ANSI_RESET + " ");
                }
                else if(((ActionCard) card).getColor() == Color.RED){
                    System.out.print(ANSI_RED + "* " + print.get(((ActionCard) card).getAction()) + " *" + ANSI_RESET + " ");
                }
                else if(((ActionCard) card).getColor() == Color.YELLOW){
                    System.out.print(ANSI_YELLOW + "* " + print.get(((ActionCard) card).getAction()) + " *" + ANSI_RESET + " ");
                }
            }
            else if(card instanceof WildCard) {
                System.out.print("* " + types.get(((WildCard) card).getType()) + " * ");
            }
        }
        System.out.println();

        for (int i=0 ; i<cards.size() ; i++) {
            Card card = cards.get(i);
            if(card instanceof ColoredCard){
                if(((ColoredCard) card).getColor() == Color.BLUE){
                    System.out.print(ANSI_BLUE + "*     *" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.GREEN){
                    System.out.print(ANSI_GREEN + "*     *" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.RED){
                    System.out.print(ANSI_RED + "*     *" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.YELLOW){
                    System.out.print(ANSI_YELLOW + "*     *" + ANSI_RESET + " ");
                }
            }
            else {
                System.out.print("*     * ");
            }
        }
        System.out.println();

        for (int i=0 ; i<cards.size() ; i++) {
            Card card = cards.get(i);
            if(card instanceof ColoredCard){
                if(((ColoredCard) card).getColor() == Color.BLUE){
                    System.out.print(ANSI_BLUE + "*******" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.GREEN){
                    System.out.print(ANSI_GREEN + "*******" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.RED){
                    System.out.print(ANSI_RED + "*******" + ANSI_RESET + " ");
                }
                else if(((ColoredCard) card).getColor() == Color.YELLOW){
                    System.out.print(ANSI_YELLOW + "*******" + ANSI_RESET + " ");
                }
            }
            else {
                System.out.print("******* ");
            }
        }
        System.out.println();
    }
}
