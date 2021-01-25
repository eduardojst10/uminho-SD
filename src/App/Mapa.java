package App;

import java.util.ArrayList;
import java.util.List;

public class Mapa {

    private List<Localizacao> map;

    public Mapa() {
        this.map = new ArrayList<>();
    }

    public Mapa(List<Localizacao> map) {
        this.map = map;
    }

    public void removerUser(String user) {
        for (Localizacao l : map) {
            if (l.getUsers().contains(user)) {
                l.removeUser(user);
            }

        }
    }

    public void adicionaUser(String user, int x, int y) {
        boolean val = false;
        for (Localizacao l : map) {
            if (l.getY() == y && l.getX() == x) {
                l.addUser(user);
                val = true;
            }
        }
        if (!val) {
            Localizacao nova = new Localizacao(x, y, new ArrayList<>(), new ArrayList<>());
            nova.addUser(user);
            this.map.add(nova);
        }
    }

    public String qtdPessoas(int coordX, int coordY) {
        // iniciar string com valor 0
        String qtd = String.valueOf(0);
        for (Localizacao l : map) {
            // caso exista
            if (l.getY() == coordY && l.getX() == coordX) {
                // calcula quantidade de users nesse local
                return String.valueOf(l.getUsers().size());
            }
        }
        // caso a localização não exista, cria-se uma nova e adiciona-se
        this.map.add(new Localizacao(coordX, coordY, new ArrayList<>(), new ArrayList<>()));
        return qtd;
    }

    // get do histórico de users de uma localização
    public List<String> getUsersHistorico(Integer x, Integer y) {
        // procura a localizacao
        for (Localizacao l : this.map) {
            //
            if (l.getX() == x && l.getY() == y) {
                // retorna o histórico
                return l.getHistorico();
            }
        }
        return new ArrayList<>();
    }

    //
    public List<Localizacao> getMapa() {
        return this.map;
    }
}
