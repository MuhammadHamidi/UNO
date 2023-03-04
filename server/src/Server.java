import impl.AuthenticationImpl;
import impl.GameBuilderImpl;
import packet.Packet;
import users.Human;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Serializable{
    int port;
    AuthenticationImpl authentication = null;
    transient ArrayList<GameManager> games = new ArrayList<>();

    public Server(int port) {
        this.port = port;
        try (FileInputStream fis = new FileInputStream("DB\\Authentication.bin") ; ObjectInputStream in = new ObjectInputStream(fis)){
            authentication = (AuthenticationImpl) in.readObject();
        } catch (Exception e) {
            System.out.println("no previous file is available!\ncreating new file...");
        }
        if(authentication == null) {
            authentication = new AuthenticationImpl();
        }
    }

    public void startServer(){
        games = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("server is running on port :" + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new GameManager(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GameManager implements Runnable{
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public GameManager(Socket socket) {
            try {
                this.socket = socket;
                this.out = new ObjectOutputStream(socket.getOutputStream());
                this.in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("invalid socket!");
            }
        }

        @Override
        public void run() {
            games.add(this);
            while (true) {
                authentication.authorize(in, out);
                System.out.println("[authorize] authorize completed");

                HashMap<String,Integer> sumOfScores = new HashMap<>();
                HashMap<String,Integer> numOfGames = new HashMap<>();

                String exitStatus = null;
                try {
                    while (true) {
                        Packet packet = (Packet) in.readObject();
                        String state = (String) packet.getObject();
                        if (state.equals("exit") || state.equals("log out")) {
                            System.out.println("[authorize] user log out");
                            exitStatus = state;
                            break;
                        }
                        /*else if(state.equals("rank")){
                            HashMap<String,Object> data = new HashMap<>();
                            data.put("type" , "rank");
                            data.put("scores" , sumOfScores);
                            data.put("games" , numOfGames);
                            Packet send = new Packet(data);
                            out.writeObject(send);
                        }*/
                        packet = (Packet) in.readObject();
                        HashMap<String, Object> data = (HashMap<String, Object>) packet.getObject();
                        String username = (String) data.get("username");
                        int num = (Integer) data.get("num");
                        GameBuilderImpl gameBuilder = new GameBuilderImpl(num, new Human(username, in, out));
                        System.out.println("[game] game created for " + username + " with " + num + " players");

                        data.clear();
                        data.put("type", "first cards");
                        data.put("your cards", gameBuilder.getHumanCards());
                        packet = new Packet(data);
                        out.writeObject(packet);

                        while (!gameBuilder.gameIsDone()) {
                            gameBuilder.play(out, in);
                        }
                        int[] scores = gameBuilder.calculate();
                        HashMap<String, Object> send = new HashMap<>();
                        send.put("type", "done");
                        send.put("scores", scores);
                        packet = new Packet(send);
                        out.writeObject(packet);
                        System.out.println("[game] game finished for " + username);
                        //gameBuilder.updateRankingTable(sumOfScores , numOfGames);
                        System.out.println("[update] ranking table updated");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("client suddenly closed the program without logging out");
                }
                if(exitStatus == null || exitStatus.equals("exit")){
                    break;
                }
            }
            update();
            System.out.println("[update] server updated");
            games.remove(this);
        }
    }

    synchronized public void update(){
        try (FileOutputStream fos = new FileOutputStream("DB\\Server.bin"); ObjectOutputStream out = new ObjectOutputStream(fos)){
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (FileOutputStream fos = new FileOutputStream("DB\\Authentication.bin"); ObjectOutputStream out = new ObjectOutputStream(fos)){
            out.writeObject(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
