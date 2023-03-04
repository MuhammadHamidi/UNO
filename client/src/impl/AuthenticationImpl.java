package impl;

import packet.Packet;
import services.Authentication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Scanner;

public class AuthenticationImpl extends Authentication {
    private Scanner input = new Scanner(System.in);
    private String username;

    public AuthenticationImpl(ObjectInputStream in, ObjectOutputStream out) {
        super(in, out);
    }

    @Override
    public void authorize() {
        try {
            while (true) {
                System.out.println("--------------------");
                System.out.println("1.login\n2.create account\nplease choose an option by its number :");
                String str = input.nextLine();
                if (str.equals("1")) {
                    Packet packet = authorizePacket("login");
                    out.writeObject(packet);
                    Packet receive = (Packet) in.readObject();
                    String message = (String) receive.getObject();
                    System.out.println(message);
                    if(message.equals("welcome back to your account.")){
                        this.username = username;
                        break;
                    }
                } else if (str.equals("2")) {
                    Packet packet = authorizePacket("sign up");
                    out.writeObject(packet);
                    Packet receive = (Packet) in.readObject();
                    String message = (String) receive.getObject();
                    System.out.println(message);
                    if(message.equals("account successfully created.")){
                        this.username = username;
                        break;
                    }
                } else {
                    System.out.println("invalid input!");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("server ran out");
        }
    }

    public String getUsername() {
        return username;
    }

    private Packet authorizePacket(String type){
        System.out.println("enter your username :");
        String username = input.nextLine();
        System.out.println("enter your password :");
        String password = input.nextLine();
        HashMap<String, String> data = new HashMap<>();
        data.put("type", type);
        data.put("username", username);
        data.put("password", password);
        return new Packet(data);
    }
}
