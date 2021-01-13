package App;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa {

    private Map<Integer, List<Integer>> map;

    public Mapa(){
        this.map= new HashMap<>();
    }
    public Mapa(Map<Integer, List<Integer>> map) {
        this.map = map;
    }
}
