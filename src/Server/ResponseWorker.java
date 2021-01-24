package Server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    // identificar qual a tag em que terá de notificar o cliente
    // 1 - notificar se uma localização ficou vazia
    // 2 - notificar se o this.user este em contacto com alguém infetado
    private static final int TAG_NOTIFICAR = 3;
    private Integer flagX = null;
    private Integer flagY = null;
    // lock para as flags
    private Lock lock = new ReentrantLock();

    public ResponseWorker(Socket socket, App app) throws IOException {
        this.tg = new TaggedConnection(socket);
        this.app = app;
    }

    @Override
    public void run() {
        try (tg) {
            new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(200);
                        notificacoes();
                    }
                } catch (Exception ignored) {
                    //
                }
            }).start();
            // antes de receber a mensagem, verifica se existem notificações a realizar
            while (true) {
                // espera para receber uma mensagem
                Frame frame = tg.receive();
                int tag = frame.tag;
                String line = new String(frame.data);

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
            // quando a ligação for abaixo, fazer logout do user
            this.app.logout(this.user);
            System.out.println("USER DESCONETADO!");
        }
    }

    //
    private void notificacoes() throws IOException {
        // verificar se existe uma localização para notificar e se está vazia
        if (this.flagX != null && this.flagY != null && app.ninguemLocal(flagX, flagY)) {
            // está vazia, então notificar TAG_NOTIFICAR
            this.tg.send(TAG_NOTIFICAR, ("LOCALDISPONIVEL>" + flagX + "," + flagY).getBytes());
            // set às flags
            try {
                lock.lock();
                this.flagX = null;
                this.flagY = null;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Parser das operações disponiveis do server
     *
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

            case "CONFIRMAR":
                confirmarDoenca(p[1]);
                this.app.procurarDoentes(p[1]);
                // TODO: quando se verifica doença, verificar quem este em contacto com ele
                break;

            case "CALCULARPESSOAS":
                calculaQuantidadePessoasLocal(tag, p[1]);
                break;

            case "RASTREAR":
                rastrearLocal(p[1]);
                break;

            case "LOGOUT":
                logout(tag, p[1]);
                break;

            case "FINAL": // acabar com isolamento
                acabarIsolamento(p[1]);
                break;
            default:
                System.out.println("Erro" + p[0]);
                break;
        }
    }

    // rastrear um local
    private void rastrearLocal(String string) throws IOException {
        String[] dados = string.split(",");
        int coordX = Integer.parseInt(dados[0]);
        int coordY = Integer.parseInt(dados[1]);
        String qtdPessoas = app.calculaQuantidadePessoasLocal(coordX, coordY);
        // caso existam pessoas
        if (!qtdPessoas.equals("0")) {
            // alterar as flags
            try {
                lock.lock();
                this.flagX = coordX;
                this.flagY = coordY;
            } finally {
                lock.unlock();
            }
        } else {
            // caso não existam pessoas nesse local
            this.tg.send(TAG_NOTIFICAR, ("LOCALVAZIO>" + coordX + "," + coordY).getBytes());
        }
    }

    // logout do user, com o user name e as suas coordenadas
    // (para retirar o user da localização)
    private void logout(int tag, String username) throws IOException {
        this.app.logout(username);
        this.tg.send(tag, "LOGOUTDONE>".getBytes());
        System.out.println(username + " DESCONETADO!");
    }

    // calcula a quantidade de pessoas num local
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

                int flag = 1;
                if (this.app.isDoente(dados[0])) {
                    flag = 0;
                }

                System.out.println("USER AUTENTICADO!");
                String envia = "AUTENTICADO>" + flag;
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

    public void confirmarDoenca(String str) {
        this.app.confirmar(str);
    }

    public void close() throws IOException {
        this.tg.close();
    }

    public void acabarIsolamento(String string) {
        this.app.removeDoente(string);
    }

    public void comunicarDoenca() throws IOException {
        this.tg.send(TAG_NOTIFICAR, ("NOTIFICACAO>").getBytes());
    }

}