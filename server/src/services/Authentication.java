package services;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Authentication {

    public abstract void authorize(ObjectInputStream in, ObjectOutputStream out);
}
