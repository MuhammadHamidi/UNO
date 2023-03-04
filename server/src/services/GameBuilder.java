package services;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class GameBuilder {
    public int numOfPlayers;

    public GameBuilder(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public abstract void play(ObjectOutputStream out , ObjectInputStream in);
}
