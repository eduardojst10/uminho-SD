package Server;

import App.App;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        // socket para os clientes normais
        ServerSocket s_normal = new ServerSocket(12345);
        // socket para os clientes especiais
        ServerSocket s_especial = new ServerSocket(20000);

        // inicializar a aplicação
        System.out.println("--------- SD SERVER 2021 ---------");
        App app = new App();

        // thread para estabelecer ligações com clientes com acesso especial
        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = s_especial.accept();
                    System.out.println("--------- CONNECTION " + socket.getPort() + " ESTABLISHED ---------");

                    new Thread(new ResponseWorker(socket, app)).start();
                }
            } catch (Exception ignored) {
                //
            }
        }).start();

        // main thread que estabelece ligações com clientes normais
        try {
            while (true) {
                Socket socket = s_normal.accept();
                System.out.println("--------- CONNECTION " + socket.getPort() + " ESTABLISHED ---------");

                new Thread(new ResponseWorker(socket, app)).start();
            }
        } catch (Exception ignored) {
            //
        }

    }
}
