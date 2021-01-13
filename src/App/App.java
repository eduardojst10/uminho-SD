package App;

import Server.ResponseWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class App {
    private Mapa mapa;
    private Map<String,String> users;
    private Map<String,ResponseWorker> usersAtivos;
    private ReentrantLock lock;
    //private Condition

    public App(){
        this.mapa = new Mapa();
        this.users = new HashMap<>();
        this.usersAtivos = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public boolean login(String user, String pass){
        boolean r = false;
        lock.lock();
        try{
            if(this.users.containsKey(user)){
                if(this.users.get(user).equals(pass)){
                    r = true;
                }

            }
        }finally {
            lock.unlock();
        }
        return r;
    }

    public boolean isOnline(String user){
        boolean r=false;
        lock.lock();
        try{
            r = this.usersAtivos.containsKey(user);
        }finally {
            lock.unlock();
        }
        return r;
    }

    public void addOnline(String user,ResponseWorker rw){
        this.usersAtivos.put(user,rw);

    }

    public boolean registaUser(String user,String pass){
        lock.lock();
        try {
            if(this.users.containsKey(user)){
                return false;
            }else{
                this.users.put(user,pass);
                return true;
            }
        }finally {
            lock.unlock();
        }
    }




}
