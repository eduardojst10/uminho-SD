package Server;

import App.App;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        System.out.println("--------- SD SERVER 2021 ---------");

        App app = new App();

        while (true) {
            Socket socket = ss.accept();
            System.out.println("--------- CONNECTION " + socket.getPort() + " ESTABLISHED ---------");

            new Thread(new ResponseWorker(socket, app)).start();
        }
    }

}
