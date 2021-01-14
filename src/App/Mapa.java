package App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa {

    private List<Localizacao> map;

    public Mapa(){
        this.map= new ArrayList<>();
    }
    public Mapa(List<Localizacao> map) {
        this.map = map;
    }


    public boolean informar(int x, int y,String user){
        boolean b = false;
        for(Localizacao l: map){
            if(l.getX() == x && l.getY() == y){
                if(l.getUsers().contains(user));

                else{
                    l.getUsers().add(user);
                    return true;
                }
            }
        }
        return b;
    }
}
