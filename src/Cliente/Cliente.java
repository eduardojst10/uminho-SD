package Cliente;

import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws Exception {
        // cria socket relativo a este User
        Socket s = new Socket("localhost", 12345);
        // cria e estabelece uma conexão com o server, através de um demultiplexer
        Demultiplexer m = new Demultiplexer(new TaggedConnection(s));
        m.start();
        // criar menu, com este demultiplexer
        new Menu(m);

        // m.close(); -> fechar no menu
    }
}
