package Server;

import App.App;
import Cliente.Input;

import java.io.*;
import java.net.Socket;

public class ResponseWorker implements Runnable{
    private PrintWriter output;
    private BufferedReader inputStream;
    private App app;
    private Socket socket;


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


    private void getOperacoes(String input){
        String[] p = input.split(">",2);
        switch (p[0]){
            case "AUTENTICA":
                login(p[1]);
                break;

            case "REGISTA":
                regista(p[1]);
                break;

            default:
                System.out.println("Erro" + p[0]);
                break;


        }

    }

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

    public void login(String s){
        String[] dados = s.split(",");
        if(app.isOnline(dados[0])){
            output.println("ALREADYON>");
            output.flush();
        }
        if(app.login(dados[0],dados[1])){
            output.println("AUTENTICADO>" + dados[0]);
            this.app.addOnline(dados[0],this);

        }else
            output.println("ERROLOGIN|");
        output.flush();

    }


}
