package Cliente;

import java.io.*;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        Menu menu = new Menu();


        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


        Writer wr = new Writer(menu,out,socket);
        Reader rd = new Reader(menu,in,socket);
        WriterUpdate wp = new WriterUpdate(menu,out,socket);


        Thread threadWriter = new Thread(wr);
        Thread threadReader = new Thread(rd);
        Thread threadWriterUpdate = new Thread(wp);

        threadWriter.start();
        threadReader.start();
        threadWriterUpdate.start();
    }
}
