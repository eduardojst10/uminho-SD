package Cliente;

import java.io.*;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        Writer wr = new Writer(menu,out,socket);
        Reader rd = new Reader(menu,in,socket);
        WriterUpdate wp = new WriterUpdate(menu,out,socket);

        Demultiplexer m = new Demultiplexer(wr,rd); //no final temos de ter new Writer(socket) new Reader(socket)
        m.start();
        Menu menu = new Menu(m);



        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));



        Demultiplexer m = new Demultiplexer(new Writer(menu,out,socket), new Reader(menu,in,socket)); //no final temos de ter new Writer(socket) new Reader(socket)
        m.start();



        Thread[] threads = {
                new Thread(() -> {
                    try {
                        //menu
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                new Thread(() -> {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        };







        Thread threadWriter = new Thread(wr);
        Thread threadReader = new Thread(rd);
        Thread threadWriterUpdate = new Thread(wp);

        threadWriter.start();
        threadReader.start();
        threadWriterUpdate.start();
    }
}
