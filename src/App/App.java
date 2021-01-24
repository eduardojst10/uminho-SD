package App;

import Server.ResponseWorker;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class App {
    private Mapa mapa; // localizações
    private Map<String, String> users; // usernames + passwords
    private Map<String, ResponseWorker> usersAtivos; // users ativos
    private Map<String, String> doentes; // mapa para guardar doentes
    private Map<String, List<Map.Entry<Integer, Integer>>> historico_users;

    // lock da app
    private ReentrantLock lock;

    public App() {
        this.mapa = new Mapa();
        this.users = new HashMap<>();
        this.usersAtivos = new HashMap<>();
        this.historico_users = new HashMap<>();
        this.doentes = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Função de um User com o id e a pass correspodente
     *
     * @param user - id de User
     * @param pass - password de User
     * @return true se a operação é bem sucedida
     */
    public boolean login(String user, String pass, int x, int y) {
        boolean r = false;
        lock.lock();
        try {
            if (this.users.containsKey(user) && this.users.get(user).equals(pass)) {
                this.mapa.adicionaUser(user, x, y);
                if (this.historico_users.containsKey(user)) {
                    this.historico_users.get(user).add(new AbstractMap.SimpleEntry<>(x, y));
                } else {
                    List<Map.Entry<Integer, Integer>> array = new ArrayList<>();
                    array.add(new AbstractMap.SimpleEntry<>(x, y));
                    this.historico_users.put(user, array);
                }
                r = true;
            }
        } finally {
            lock.unlock();
        }
        return r;
    }

    /**
     * Função que verifica se um User está online
     *
     * @param user - User a verficar
     * @return true se está online , false se não
     */

    public boolean isOnline(String user) {
        boolean r = false;
        lock.lock();
        try {
            r = this.usersAtivos.containsKey(user);
        } finally {
            lock.unlock();
        }
        return r;
    }

    /**
     * Função que adiciona User à lista de Users Online
     *
     * @param user -User
     * @param rw - ResponserWorker associado a esse User
     */

    public void addOnline(String user, ResponseWorker rw) {
        this.usersAtivos.put(user, rw);
    }

    /**
     * Função que regista novo User
     *
     * @param user - id de User
     * @param pass - password de User
     * @return true se a operação é bem sucedida
     */

    public boolean registaUser(String user, String pass) {
        lock.lock();
        try {
            if (this.users.containsKey(user)) {
                return false;
            } else {
                this.users.put(user, pass);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Função que providencia Localização atual do User
     *
     * @param user - User recebido
     * @return Coordenadas X e Y
     */

    public String[] informarAtual(String user) {
        return this.mapa.informarAtual(user);
    }

    /**
     * Função que submete um User especifico ao map de  User Doentes removendo dos Users Ativos
     * @param user - User a submeter
     * @return - operação com sucesso ou não
     */

    public boolean confirmar(String user) {
        boolean val = false;
        try {
            lock.lock();
            if (this.users.containsKey(user) && this.usersAtivos.containsKey(user)) {
                this.usersAtivos.remove(user);
                this.doentes.put(user, user);
                val = true;
            }
        }finally {
            lock.unlock();
        }
        return val;
    }

    /**
     * Função que atualiza Localizacao do User
     *
     * @param x - Coordenada x
     * @param y - Coordenada y
     * @param user - User a atualizar
     * @return boolean dependendo se a função foi bem realizada com sucesso
     */

    public boolean atualizaCoordUser(int x, int y, String user) {
        boolean val = false;
        try {
            lock.lock();
            if (this.usersAtivos.containsKey(user)) {
                this.mapa.removerUser(user);
                this.mapa.adicionaUser(user, x, y);
                if (this.historico_users.containsKey(user)) {
                    this.historico_users.get(user).add(new AbstractMap.SimpleEntry<>(x, y));
                } else {
                    List<Map.Entry<Integer, Integer>> array = new ArrayList<>();
                    array.add(new AbstractMap.SimpleEntry<>(x, y));
                    this.historico_users.put(user, array);
                }
                val = true;
            }
        } finally {
            lock.unlock();
        }
        return val;
    }

    /**
     * Função que calcula a quantidade de pessoas num determinada localização
     * @param coordX - Coordenada x da Localização
     * @param coordY - Coordenada y da Localização
     * @return - Número de pessoas
     */

    public String calculaQuantidadePessoasLocal(int coordX, int coordY) {
        String res = null;
        try {
            lock.lock();
            res = this.mapa.qtdPessoas(coordX, coordY);
        } finally {
            lock.unlock();
        }
        return res;
    }

    /**
     * Função que faz logout a um User especifico
     * @param username
     */

    public void logout(String username) {
        try {
            lock.lock();
            this.usersAtivos.remove(username);
            this.mapa.removerUser(username);
        } finally {
            lock.unlock();
        }
    }

    public boolean isDoente(String username){
        boolean val = false;
        try {
            lock.lock();
            val = this.doentes.containsKey(username);
        }finally {
            lock.unlock();
        }
        return val;
    }

    /**
     * Função que permite ao User saber quando não existe ninguem no local
     * @param flagX
     * @param flagY
     * @return
     */

    public boolean ninguemLocal(Integer flagX, Integer flagY) {
        boolean res = false;
        try {
            lock.lock();
            if (this.calculaQuantidadePessoasLocal(flagX, flagY).equals("0")) {
                res = true;
            }
        } finally {
            lock.unlock();
        }
        return res;
    }

    // remover o user da lista de doentes
    public void removeDoente(String user) {
        this.doentes.remove(user);
    }

    // procurar os doentes
    public void procurarDoentes(String username) {
        // pegar no histórico das localizações do username
        for(Map.Entry<Integer, Integer>> par : this.historico_users.get(username)){
            // para cada user de cada par
            for(String user : this.mapa.getUsersHistorico(par.getKey(), par.getValue())){
                this.usersAtivos.get(user).comunicarDoenca();
            }
        }
    }
}
