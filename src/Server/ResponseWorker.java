package Server;

import App.App;
import Cliente.Input;

import java.io.*;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class ResponseWorker implements Runnable{
    private PrintWriter output;
    private BufferedReader inputStream;
    private App app;
    private Socket socket;
    private String user = null;


    public ResponseWorker(Socket s,App app) throws IOException {
            this.inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.socket = s;
            this.app = app;
            this.output = new PrintWriter(s.getOutputStream());

    }

    @Override
    public void run() {
        String line;
        while(true) {
            try {
                line=inputStream.readLine();
                if(line == null || line.equals("q")) {
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                    break;
                }
                //System.out.println(socket.getRemoteSocketAddress() +" >> " + line);
                getOperacoes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parser das operações disponiveis do server
     * @param input
     */

    private void getOperacoes(String input){
        String[] p = input.split(">",2);
        switch (p[0]){
            case "AUTENTICA":
                login(p[1]);
                break;

            case "REGISTA":
                regista(p[1]);
                break;

            case "INFORMAR":
                informar(p[1]);
                break;

            case "CONFIRMAR:":
                confirmar(p[1]);
                break;

            case "COORD":
                atualizaCoordUser(p[1]);
                break;

            default:
                System.out.println("Erro" + p[0]);
                break;

        }

    }

    /**
     * Função que atualiza coordenadas de User mal ele entra na app. Sem ação feita por user
     * @param s - dados
     */

    public void atualizaCoordUser(String s){
        String[] dados  = s.split(",");
        int x = parseInt(dados[0]);
        int y = parseInt(dados[1]);
        app.atualizaCoordUser(x,y,this.user);
    }

    /**
     * Função que regista um User
     * @param s Dados para registo
     */

    public void regista(String s){
        String[] dados = s.split(",");
        boolean t = app.registaUser(dados[0],dados[1]);
        System.out.println(dados[0]);
        System.out.println(dados[1]);
        if(t){
            output.println("REGISTADOUSER>");

        }else {
            output.println("ALREADYREG>");
        }
        output.flush();

    }

    /**
     * Função de login de um User
     * @param s User
     */

    /*
    Alterar Login de forma a mandar localizacao
     */

    public void login(String s){
        String[] dados = s.split(",");
        if(app.isOnline(dados[0])){
            output.println("ALREADYON>");
            output.flush();
        }
        if(app.login(dados[0],dados[1],parseInt(dados[2]),parseInt(dados[3]))){
            output.println("AUTENTICADO>" + dados[0]);
            this.app.addOnline(dados[0],this);
            this.user = dados[0];

        }else
            output.println("ERROLOGIN>");
        output.flush();

    }

    /**
     * Função que envia a Localizacao do User em questão
     * @param str
     */

    public void informar(String str){
        String[] dados = this.app.informarAtual(str);
        int x = parseInt(dados[0]);
        int y = parseInt(dados[1]);
        output.println("LOCALIZACAOATUAL>" + x + "," + y);
        output.flush();

    }


    /**
     * Função que confirmar doença de User
     * @param str
     */

    public void confirmar(String str){
        String user = str;
        if(this.app.confirmar(user)){
            output.println("ADICIONAUSERDOENTE>");
        }else{
            output.println("ERROCONFIRMAR>");
        }
        output.flush();

    }





}
