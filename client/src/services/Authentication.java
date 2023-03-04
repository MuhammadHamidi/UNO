package services;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Authentication {
    public ObjectInputStream in;
    public ObjectOutputStream out;

    public Authentication(ObjectInputStream in , ObjectOutputStream out){
        this.in = in;
        this.out = out;
    }

    public abstract void authorize();
}
