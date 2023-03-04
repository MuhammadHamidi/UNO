import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Main {

    public static void main(String[] args) {
        Server server = null;
        try (FileInputStream fis = new FileInputStream("DB\\Server.bin") ; ObjectInputStream in = new ObjectInputStream(fis)){
            server = (Server) in.readObject();
            System.out.println("server loaded");
        } catch (Exception e) {
            System.out.println("no previous server is available!\ncreating new server...");
        }
        if(server == null) {
            server = new Server(5000);
            System.out.println("server created");
        }
        server.startServer();
    }
}
