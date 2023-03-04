package impl;

import packet.Packet;
import services.Authentication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;

public class AuthenticationImpl extends Authentication implements Serializable {
    HashMap<String,String> users = new HashMap<>();

    @Override
    public void authorize(ObjectInputStream in, ObjectOutputStream out) {
        try {
            while (true) {
                Packet packet = (Packet) in.readObject();
                HashMap<String,String> data = (HashMap<String, String>) packet.getObject();
                String type = data.get("type");
                if (type.equals("login")) {
                    String username = data.get("username");
                    String password = data.get("password");
                    String message;
                    if(!users.containsKey(username)){
                        message = "this username doesn't exist!";
                        System.out.println("[log in] unsuccessful log in . username doesn't exist");
                    }
                    else if(!users.get(username).equals(password)){
                        message = "wrong password!";
                        System.out.println("[log in] unsuccessful log in . password is not match by username");
                    }
                    else {
                        message = "welcome back to your account.";
                        System.out.println("[log in] successful log in");
                    }
                    Packet send = new Packet(message);
                    out.writeObject(send);
                    if(message.equals("welcome back to your account.")){
                        break;
                    }
                } else if (type.equals("sign up")) {
                    String username = data.get("username");
                    String password = data.get("password");
                    String message;
                    if(users.containsKey(username)){
                        message = "this username already exist!";
                        System.out.println("[sign up] unsuccessful sign up . username already exist");
                    }
                    else {
                        users.put(username , password);
                        message = "account successfully created.";
                        System.out.println("[sign up] successful sign up");
                    }
                    Packet send = new Packet(message);
                    out.writeObject(send);
                    if(message.equals("account successfully created.")){
                        break;
                    }
                } else {
                    System.out.println("invalid input!");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("client suddenly closed the program without logging out");
        }
    }
}
