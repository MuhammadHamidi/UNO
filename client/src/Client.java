import impl.AuthenticationImpl;
import impl.GamePlay;
import packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    private String ip;
    private int port;

    public Client(String ip , int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start(){
        try {
            Socket socket = new Socket(ip , port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner input = new Scanner(System.in);

            AuthenticationImpl authentication = new AuthenticationImpl(in , out);
            authentication.authorize();

            while (true){
                String index;
                while (true){
                    System.out.println("--------------------");
                    System.out.println("choose an option by its index:");
                    System.out.println("1-new game\n2-exit");
                    index = input.nextLine();
                    if(index.equals("1") || index.equals("2")){
                        break;
                    }
                    else {
                        System.out.println("invalid input!");
                    }
                }
                Packet packet;
                if(index.equals("2")){
                    packet = new Packet("exit");
                    out.writeObject(packet);
                    break;
                }
                else {
                    packet = new Packet("new game");
                    out.writeObject(packet);
                }

                GamePlay gamePlay = new GamePlay();
                int num = gamePlay.getNumOfPlayers();
                HashMap<String,Object> data = new HashMap<>();
                data.put("username" , authentication.getUsername());
                data.put("num" , num);
                packet = new Packet(data);
                out.writeObject(packet);

                gamePlay.play(in , out);
            }

        } catch (IOException e) {
            System.out.println("server ran out");
        }
    }
}
