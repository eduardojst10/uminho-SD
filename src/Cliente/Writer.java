package Cliente;

import java.io.*;
import java.net.Socket;

public class Writer implements  Runnable{
    private Menu menu;
    private BufferedWriter outputStream;
    private Socket socket;

    public Writer(Menu menu,BufferedWriter out, Socket socket){
        this.menu = menu;
        this.outputStream = out;
        this.socket = socket;
    }

    public void run() {
        this.menu.show();
        int opcao;
        try {
            while ((opcao = this.menu.escolheOption()) != -1) {
                parseWriter(opcao);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseWriter(Integer op) {
        try {
            switch (this.menu.getEstado()) {
                case MAIN:
                    if (op == 1) login(1);

                    if(op == 2) login(2);

                    if(op == 0) logout();

                    //falta o 0;
                   break;



                case REGISTANDO:
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(int i) throws IOException{
        String username = menu.lerDadosUser("Username: ");
        String password = menu.lerDadosUser("Password: ");
        String envia;
        if(i == 1){
            envia = "AUTENTICA" + ">" + username + "," + password;


        }else  envia = "REGISTA" + ">" + username + "," + password;

        outputStream.write(envia);
        outputStream.newLine();
        outputStream.flush();


    }

    private void logout() throws IOException{
        String user = menu.getUser();
        outputStream.write("LOGOUT>" + user);
        outputStream.flush();
        menu.alteraEstado(Menu.Estado.MAIN);
        menu.show();
    }
}
