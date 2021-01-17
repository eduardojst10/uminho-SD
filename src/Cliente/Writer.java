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

    /**
     * Parser da escolha que o User faz
     * @param op
     */

    private void parseWriter(Integer op) {
        try {
            switch (this.menu.getEstado()) {
                case MAIN:
                    if (op == 1) login(1);

                    if(op == 2) login(2);

                    if(op == 0) logout();

                    //falta o 0;
                   break;



                case AUTENTICADO:
                    if(op == 1)
                        informar();

                    if(op == 2); //rastrear(op);

                    if(op == 3); //comunicarD(op);

                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Função que permite o User fazer login, se este não estiver
     * registado então segue para o menu de registar
     * @param i - operação
     * @throws IOException
     */

    private void login(int i) throws IOException{
        String username = menu.lerDadosUser("Username: ");
        String password = menu.lerDadosUser("Password: ");
        String coord = menu.getX()+"," + menu.getY();
        String envia;
        if(i == 1){
            envia = "AUTENTICA" + ">" + username + "," + password + "," + coord;


        }else  envia = "REGISTA" + ">" + username + "," + password + "," + coord;

        outputStream.write(envia);
        outputStream.newLine();
        outputStream.flush();


    }

    /**
     * Função que permite o User fazer logout
     * @throws IOException
     */

    private void logout() throws IOException{
        String user = menu.getUser();
        outputStream.write("LOGOUT>" + user);
        outputStream.flush();
        menu.alteraEstado(Menu.Estado.MAIN);
        menu.show();
    }

    /**
     * Função que permite o User informar Sistema da sua posição atual
     * @throws IOException
     */

    public void informar() throws IOException {
        String xuser = menu.getUser();
        String envia;
        envia = "INFORMAR" + ">" + xuser ;
        outputStream.write(envia);
        outputStream.newLine();
        outputStream.flush();
    }






    /*
    -Falar sobre por exemplo se um cliente confirmar a doença o menu vai automaticamente alterar para um onde
    para outro menu
    -Quando este cliente confirma doença supostamente não pode aceder mais ao cinema
    ---> Alterar login pois vamos ter de verificar se este cliente está doente ou não

     */

    /**
     * Função que representa o User confirmar que contraiu doença
     * @throws IOException
     */

    public void confirmarD() throws IOException {
        String resposta = menu.lerDadosUser("Pretente confirmar contracao de sintomas de doenca: ");
        if(resposta  == "sim") {
            String user = menu.lerDadosUser("Id de utilizador: ");
            String envia = "CONFIRMAR" + ">" + user;
            outputStream.write(envia);
            outputStream.newLine();
            outputStream.flush();
            menu.alteraEstado(Menu.Estado.AUTENTICADOCONTAMINADO);
            menu.show();
            //menu.alteraEstado(Menu.Estado.MAIN);
            //menu.show();
        }else {
            menu.alteraEstado(Menu.Estado.AUTENTICADO);
            menu.show();

        }
    }






}
