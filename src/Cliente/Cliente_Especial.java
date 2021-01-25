package Cliente;

import java.net.Socket;

public class Cliente_Especial {
    private static final int MAIN_THREAD = 1;

    public static void main(String[] args) throws Exception {
        // cria socket relativo a este User
        Socket s = new Socket("localhost", 20000);
        // cria e estabelece uma conexão com o server, através de um demultiplexer
        Demultiplexer dem = new Demultiplexer(new TaggedConnection(s));
        dem.start();

        try {
            // send request
            dem.send(MAIN_THREAD, "BACKUP>".getBytes());
            // get reply
            byte[] data = dem.receive(MAIN_THREAD);
            // print da resposta
            System.out.println(new String(data));

            // fechar dem
            dem.close();
        } catch (Exception ignored) {
            //
        }
    }
}
