package App;

import java.util.ArrayList;
import java.util.List;

public class Localizacao {
    private int x;
    private int y;
    private List<String> users; // users ativos
    private List<String> historico; // users que estiverem neste local

    public Localizacao() {
        this.users = new ArrayList<>();
        this.historico = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }

    public Localizacao(int x, int y, List<String> users, List<String> historico) {
        this.x = x;
        this.y = y;
        this.users = users;
        this.historico = historico;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getUser(String user) {
        String ret = null;

        for (String s : users) {
            if (s.equals(user))
                ret = s;
        }
        return ret;
    }

    public void removeUser(String user) {
        this.users.remove(user);
    }

    public void addUser(String user) {
        this.users.add(user);
        this.historico.add(user);
    }
}
