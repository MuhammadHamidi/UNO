package impl;

import entities.Card;
import entities.Storage;
import entities.cards.Color;
import entities.cards.ColoredCard;
import entities.cards.Type;
import entities.cards.WildCard;
import entities.cards.colored.Action;
import entities.cards.colored.ActionCard;
import packet.Packet;
import services.GameBuilder;
import users.Bot;
import users.Human;
import users.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameBuilderImpl extends GameBuilder {
    Storage storage = new Storage();
    int turn;
    int turnShifter = 1;
    Boolean punish = false;
    int punishNum = 0;
    Card onBoard;
    Color color;
    boolean changed = false;
    ArrayList<User> users = new ArrayList<>();

    public GameBuilderImpl(int numOfPlayers , Human human) {
        super(numOfPlayers);
        prepareGame(human);
    }

    private void prepareGame(Human human){
        users.add(human);
        for (int i=1 ; i<numOfPlayers ; i++) {
            users.add(new Bot("bot " + i));
        }
        for (int i=0; i<numOfPlayers ; i++) {
            users.get(i).setCards(storage.aSetOf7Cards());
        }
        Random random = new Random();
        turn = random.nextInt(numOfPlayers);
        onBoard = storage.getStart();
        color = ((ColoredCard)onBoard).getColor();
        if(onBoard instanceof ActionCard){
            if(((ActionCard) onBoard).getAction() == Action.REVERSE){
                turnShifter *= -1;
                turn += turnShifter;
            }
            else if(((ActionCard) onBoard).getAction() == Action.SKIP){
                turn += 2*turnShifter;
            }
            else if(((ActionCard) onBoard).getAction() == Action.DRAW2){
                punish = true;
                punishNum += 2;
            }
        }
    }

    @Override
    public void play(ObjectOutputStream out , ObjectInputStream in) {
        turn = turn % numOfPlayers;
        while (turn < 0){
            turn += numOfPlayers;
        }
        turn = turn % numOfPlayers;
        User user = users.get(turn);
        try {
            Packet packet;
            if(user instanceof Human){
                HashMap<String,Object> data = new HashMap<>();
                data.put("type" , "your turn");
                packet = new Packet(data);
            }
            else {
                HashMap<String,Object> data = new HashMap<>();
                data.put("type" , user.getUsername() + " turn");
                packet = new Packet(data);
            }
            out.writeObject(packet);

            HashMap<String,Object> data = new HashMap<>();
            data.put("type" , "game");
            data.put("on board" , onBoard);
            data.put("color" , color);
            HashMap<Integer,Integer> numOfCards = new HashMap<>();
            for (int i=0 ; i<users.size()  ; i++) {
                numOfCards.put(i , users.get(i).getCards().size());
            }
            data.put("num of cards" , numOfCards);
            packet = new Packet(data);
            out.writeObject(packet);
        } catch (IOException e) {
            System.out.println("client suddenly closed the program without logging out");
        }

        ArrayList<String> messages = new ArrayList<>();

        Card played = user.playACard(onBoard , punish , punishNum);
        if(punish && played==null){
            user.addCarts(storage.punish(punishNum));
            punish = false;
            changed = false;
            punishNum = 0;
            if(user instanceof Bot){
                messages.add(user.getUsername() + " punished");
            }
            System.out.println("[punish] " + user.getUsername() + " punished");
            played = user.playACard(onBoard , punish , punishNum);
            if(played != null){
                changed = true;
                onBoard = played;
                if(user instanceof Bot){
                    messages.add(user.getUsername() + " played " + played.toString());
                }
                System.out.println("[play] " + user.getUsername() + " played " + played.toString());
                updateColor();
            }
        }
        else if(punish){
            changed = true;
            onBoard = played;
            if(user instanceof Bot){
                messages.add(user.getUsername() + " played " + played.toString());
            }
            System.out.println("[play] " + user.getUsername() + " played " + played.toString());
            updateColor();
        }
        else if(played == null){
            user.addCarts(storage.punish(1));
            changed = false;
            if(user instanceof Bot){
                messages.add(user.getUsername() + " punished");
            }
            System.out.println("[punish] " + user.getUsername() + " punished");
            played = user.playACard(onBoard , punish , punishNum);
            if(played != null){
                changed = true;
                onBoard = played;
                if(user instanceof Bot){
                    messages.add(user.getUsername() + " played " + played.toString());
                }
                System.out.println("[play] " + user.getUsername() + " played " + played.toString());
                updateColor();
            }
        }
        else {
            changed = true;
            onBoard = played;
            if(user instanceof Bot){
                messages.add(user.getUsername() + " played " + played.toString());
            }
            System.out.println("[play] " + user.getUsername() + " played " + played.toString());
            updateColor();
        }

        if(user instanceof Bot){
            Packet packet = new Packet(messages);
            try {
                out.writeObject(packet);
            } catch (IOException e) {
                System.out.println("client suddenly closed the program without logging out");
            }
        }
        turnShift();
    }

    private void turnShift(){
        if (!changed){
            turn += turnShifter;
        }
        else if(onBoard instanceof ActionCard){
            if(((ActionCard) onBoard).getAction() == Action.REVERSE){
                turnShifter *= -1;
                turn += turnShifter;
            }
            else if(((ActionCard) onBoard).getAction() == Action.SKIP){
                turn += 2*turnShifter;
            }
            else if(((ActionCard) onBoard).getAction() == Action.DRAW2){
                punish = true;
                punishNum += 2;
                turn += turnShifter;
            }
        }
        else if(onBoard instanceof WildCard && ((WildCard) onBoard).getType() == Type.WILD_4){
            punish = true;
            punishNum += 4;
            turn += turnShifter;
        }
        else {
            turn += turnShifter;
        }
    }

    private void updateColor(){
        if(onBoard instanceof ActionCard){
            color = ((ActionCard) onBoard).getColor();
        }
        else if(onBoard instanceof WildCard){
            color = ((WildCard) onBoard).getNextColor();
        }
    }

    public boolean gameIsDone(){
        for (int i=0 ; i<users.size() ; i++) {
            if(users.get(i).getCards().size() == 0){
                return true;
            }
        }
        return false;
    }

    public int[] calculate(){
        int[] scores = new int[users.size()];
        for (int i=0 ; i<users.size() ; i++) {
            for (int j=0 ; j<users.get(i).getCards().size() ; j++) {
                users.get(i).addScore(users.get(i).getCards().get(j).getValue());
            }
            scores[i] = users.get(i).getScore();
        }
        return scores;
    }

    public ArrayList<Card> getHumanCards(){
        return users.get(0).getCards();
    }

    /*public void updateRankingTable(HashMap<String,Integer> sumOfScores , HashMap<String,Integer> numOfGames){
        for (int i=0; i<users.size() ; i++) {
            String username = users.get(i).getUsername();
            if(!numOfGames.containsKey(users.get(i).getUsername())){
                sumOfScores.put(username , 0);
                numOfGames.put(username , 0);
            }
            sumOfScores.put(username , sumOfScores.get(username) + users.get(i).getScore());
            numOfGames.put(username , numOfGames.get(username) + 1);
        }
    }*/
}
