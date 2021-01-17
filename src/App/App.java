package App;

import Server.ResponseWorker;

import java.io.DataInputStream;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class App {
    private Mapa mapa;
    private Map<String,String> users;
    private Map<String,ResponseWorker> usersAtivos;
    private Map<String,String> doentes; //mapa para guardar doentes
    private ReentrantLock lock;
    //private Condition

    public App(){
        this.mapa = new Mapa();
        this.users = new HashMap<>();
        this.usersAtivos = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Função de um User com o id e a pass correspodente
     * @param user
     * @param pass
     * @return true se a operação é bem sucedida
     */
    public boolean login(String user, String pass, int x,int y){
        boolean r = false;
        lock.lock();
        try{
            if(this.users.containsKey(user)){
                if(this.users.get(user).equals(pass)){
                    r = true;
                    this.mapa.adicionaUser(user,x,y);
                }

            }
        }finally {
            lock.unlock();
        }
        return r;
    }

    /**
     * Função que verifica se um User está online
     * @param user
     * @return true se está online , false se não
     */

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

    /**
     * Função que adiciona User à lista de Users Online
     * @param user
     * @param rw
     */

    public void addOnline(String user,ResponseWorker rw){
        this.usersAtivos.put(user,rw);

    }

    /**
     * Função que regista novo User
     * @param user
     * @param pass
     * @return true se a operação é bem sucedida
     */

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

    /**
     * Função que providencia Localização atual do User
     * @param user
     * @return Coordenadas X e Y
     */

    public String[] informarAtual(String user){
        return this.mapa.informarAtual(user);
    }



    public boolean confirmar(String user){
        boolean val = false;
        if(this.users.containsKey(user) && this.usersAtivos.containsKey(user)) {
            this.usersAtivos.remove(user);
            this.doentes.put(user, user);
            val = true;
        }
        return val;
    }

    /**
     * Função que atualiza Localizacao do User
     * @param x
     * @param y
     * @param user
     * @return
     */

    public boolean atualizaCoordUser(int x,int y, String user){
        boolean val = false;
        if(this.usersAtivos.containsKey(user)) {
            this.mapa.removerUser(user);
            this.mapa.adicionaUser(user, x, y);
            val = true;
        }
        return val;
    }

    

}


