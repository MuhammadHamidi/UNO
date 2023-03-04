package users;

import entities.Card;
import entities.cards.Color;
import entities.cards.Type;
import entities.cards.WildCard;
import entities.cards.colored.Action;
import entities.cards.colored.ActionCard;
import packet.Packet;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Human extends User{
    ObjectInputStream in;
    ObjectOutputStream out;

    public Human(String username , ObjectInputStream in ,ObjectOutputStream out) {
        super(username);
        this.in = in;
        this.out = out;
    }


    @Override
    public Card playACard(Card card , Boolean punish , int punishNum) {
        HashMap<String,Object> data = new HashMap<>();
        data.put("type" , "play");
        data.put("punish" , "" + punish);
        Packet packet = new Packet(data);
        try {
            out.writeObject(packet);
            Packet receive = (Packet) in.readObject();
            HashMap <String,Card> hashMap = (HashMap<String, Card>) receive.getObject();
            Card returnCard = hashMap.get("play");
            if (returnCard == null){
                return null;
            }
            Iterator<Card> iterator = cards.iterator();
            while (iterator.hasNext()){
                Card itCard = iterator.next();
                if(itCard.equals(returnCard)){
                    iterator.remove();
                    break;
                }
            }

            return returnCard;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("client suddenly closed the program without logging out");
        }
        return null;
    }

    @Override
    public void addCarts(ArrayList<Card> cards) {
        super.addCarts(cards);
        HashMap<String,Object> data = new HashMap<>();
        data.put("type" , "add");
        data.put("add" , cards);
        Packet packet = new Packet(data);
        try {
            out.writeObject(packet);
        } catch (IOException e) {
            System.out.println("client suddenly closed the program without logging out");
        }
    }
}
