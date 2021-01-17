package App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa {

    private List<Localizacao> map;

    public Mapa(){
        this.map= new ArrayList<>();

        Localizacao l1 = new Localizacao(0,0,new ArrayList<>());
        Localizacao l2 = new Localizacao(0,1,new ArrayList<>());
        Localizacao l3 = new Localizacao(1,0,new ArrayList<>());
        Localizacao l4 = new Localizacao(1,1,new ArrayList<>());
        Localizacao l5 = new Localizacao(0,2,new ArrayList<>());
        Localizacao l6 = new Localizacao(2,0,new ArrayList<>());
        Localizacao l7 = new Localizacao(2,2,new ArrayList<>());
        map.add(l1);
        map.add(l2);
        map.add(l3);
        map.add(l4);
        map.add(l5);
        map.add(l6);
        map.add(l7);
    }

    public Mapa(List<Localizacao> map) {
        this.map = map;
    }

/*
if(l.getUsers().contains(user)) {

            }
 */
    public String[] informarAtual(String user){
        String coord = null;
        for(Localizacao l: map) {
            coord = l.getX() + "," + l.getY();
        }
        String[] dados = coord.split(",");

        return dados;
    }


    public void removerUser(String user){
        for(Localizacao l : map){
            if(l.getUsers().contains(user)){
                l.removeUser(user);
            }

        }
    }

    public void adicionaUser(String user , int x, int y){
        boolean val = false;
        for(Localizacao l : map){
            if(l.getY() == y && l.getX() == x) {
                l.addUser(user);
                val = true;
            }
        }
        if(val){
            Localizacao nova = new Localizacao(x,y,new ArrayList<>());
            nova.addUser(user);
            this.map.add(nova);

        }

    }
}