package Server;

import java.io.*;
import java.net.Socket;
import App.App;
import Cliente.TaggedConnection;
import Cliente.Frame;
import static java.lang.Integer.parseInt;

public class ResponseWorker implements Runnable {
    private final TaggedConnection tg;
    private App app;
    // existe um worker por cada cliente, então o próprio worker guarda o username
    // desse cliente quando ele fizer login
    private String user = null;

    public ResponseWorker(Socket socket, App app) throws IOException {
        this.tg = new TaggedConnection(socket);
        this.app = app;
    }

    @Override
    public void run() {
        try (tg) {
            while (true) {
                Frame frame = tg.receive();
                int tag = frame.tag;
                String line = new String(frame.data);
                if (line.equals("q")) {
                    break;
                }

                // THREAD_1: main thread (menu) do user
                // - é preciso decifrar a mensagem
                // - efetuar a operação requisitada
                // - enviar mensagem de retorno
                // é tudo feito no método getOperacao()
                if (tag == 1) {
                    // tratar da operação a fazer
                    getOperacao(tag, line);
                }
                // THREAD_2: thread que envia a localização do user
                // - é preciso decifrar a mensagem das coordernadas
                // - atualiza as coordenadas na app
                // - não precisa enviar mensagem
                if (tag == 2) {
                    // thread que apenas envia as coordenadas constantemente
                    atualizaCoordUser(line);
                }
            }
        } catch (Exception e) {
            // TODO: Quando a ligação for abaixo, fazer logout do user
            System.out.println("USER DESCONETADO!");
        }
    }

    /**
     * Parser das operações disponiveis do server
     * @param tag   - tag da mensagem
     * @param input - dados da mensagem
     */

    private void getOperacao(int tag, String input) throws IOException {
        String[] p = input.split(">", 2);
        switch (p[0]) {
            case "AUTENTICA":
                login(tag, p[1]);
                break;

            case "REGISTA":
                regista(tag, p[1]);
                break;

            case "INFORMAR":
                informar(tag, p[1]);
                break;

            case "CONFIRMAR:":
                confirmar(tag, p[1]);
                break;

            case "RASTREAR":
                calculaQuantidadePessoasLocal(tag, p[1]);
                break;

            case "LOGOUT":
                logout(tag, p[1]);
                break;
            default:
                System.out.println("Erro" + p[0]);
                break;
        }
    }

    // logout do user, com o user name e as suas coordenadas
    // (para retirar o user da localização)
    private void logout(int tag, String username) throws IOException {
        this.app.logout(username);
        this.tg.send(tag, "LOGOUTDONE>".getBytes());
        System.out.println(username + " DESCONETADO!");
    }

    private void calculaQuantidadePessoasLocal(int tag, String s) throws IOException {
        String[] dados = s.split(",");
        int coordX = Integer.parseInt(dados[0]);
        int coordY = Integer.parseInt(dados[1]);
        String qtdPessoas = app.calculaQuantidadePessoasLocal(coordX, coordY);
        this.tg.send(tag, qtdPessoas.getBytes());
    }

    /**
     * Função que atualiza coordenadas de User mal ele entra na app. Sem ação feita
     * por user
     *
     * @param s - dados
     */

    public void atualizaCoordUser(String s) {
        String[] p = s.split(">", 2);
        if (p[0].equals("COORD")) {
            String[] dados = p[1].split(",");
            int x = parseInt(dados[0]);
            int y = parseInt(dados[1]);
            if (this.app.atualizaCoordUser(x, y, this.user)) {
                System.out.println("ATUALIZOU LOCALIZAÇÃO DE " + this.user + ": (" + x + "," + y + ")");
            }
        }
    }

    /**
     * Função que regista um User
     *
     * @param s Dados para registo
     */

    public void regista(int tag, String s) throws IOException {
        String[] dados = s.split(",");
        // TODO: inserir coordenadas
        boolean t = app.registaUser(dados[0], dados[1]);
        System.out.println(dados[0]);
        System.out.println(dados[1]);
        if (t) {
            System.out.println("REGISTOU USER!");
            String envia = "REGISTADOUSER>";
            byte[] data = envia.getBytes();
            this.tg.send(tag, data);
        } else {
            System.out.println("USER JÁ REGISTADO!");
            String envia = "ALREADYREG>";
            byte[] data = envia.getBytes();
            this.tg.send(tag, data);
        }
    }

    /**
     * Função de login de um User
     *
     * @param s User
     */

    /*
     * Alterar Login de forma a mandar localizacao
     */

    public void login(int tag, String s) throws IOException {
        String[] dados = s.split(",");
        System.out.println(dados[0]);
        System.out.println(dados[1]);
        if (app.isOnline(dados[0])) {
            System.out.println("USER JÁ ESTÁ ONLINE NO SERVER!");
            String envia = "ALREADYON>";
            byte[] data = envia.getBytes();
            this.tg.send(tag, data);
        } else {
            if (this.app.login(dados[0], dados[1], parseInt(dados[2]), parseInt(dados[3]))) {
                System.out.println("USER AUTENTICADO!");
                String envia = "AUTENTICADO>";
                byte[] data = envia.getBytes();
                this.user = dados[0];
                this.app.addOnline(user, this);
                this.tg.send(tag, data);
            } else {
                System.out.println("ERRO NO LOGIN DO USER!");
                String envia = "ERROLOGIN>";
                byte[] data = envia.getBytes();
                this.tg.send(tag, data);
            }
        }
    }

    /**
     * Função que envia a Localizacao do User em questão
     *
     * @param str - Dados de Localização a enviar respetivamente
     */

    public void informar(int tag, String str) throws IOException {
        String[] dados = this.app.informarAtual(str);
        int x = parseInt(dados[0]);
        int y = parseInt(dados[1]);
        String envia = "LOCALIZACAOATUAL>" + x + "," + y;
        byte[] data = envia.getBytes();
        this.tg.send(tag, data);
    }

    /**
     * Função que confirmar doença de User
     *
     * @param str - User a ser confirmado
     */

    public void confirmar(int tag, String str) throws IOException {
        if (this.app.confirmar(str)) {
            String envia = "ADICIONAUSERDOENTE>";
            byte[] data = envia.getBytes();
            this.tg.send(tag, data);
        } else {
            String envia = "ERROCONFIRMAR>";
            byte[] data = envia.getBytes();
            this.tg.send(tag, data);
        }
    }

    public void close() throws IOException {
        this.tg.close();
    }

}