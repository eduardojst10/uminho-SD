package Server;

import App.App;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final static int WORKERS_PER_CONNECTION = 3;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        System.out.println("--------- SD SERVER 2021 ---------");

        App app = new App();

        while (true) {
            Socket socket = ss.accept();
            System.out.println("--------- CONNECTION " + socket.getLocalPort() + " ESTABLISHED ---------");

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(new ResponseWorker(socket, app)).start();
        }
    }

}

