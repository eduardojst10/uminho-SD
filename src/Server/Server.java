package Server;

import App.App;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        App aplication = new App();

        while(true){
            Socket socket = ss.accept();
            ResponseWorker rw = new ResponseWorker(socket,aplication);
            System.out.println("Bem Vindo!");
            Thread client = new Thread(rw);
            client.start();
        }

    }
}
