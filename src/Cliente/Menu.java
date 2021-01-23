package Cliente;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Menu {

    // Estados possíveis
    public enum Estado {
        MAIN, AUTENTICADO, AUTENTICADOCONTAMINADO
    }

    // lock para conseguir alterar os dados
    private Lock lock = new ReentrantLock();

    // Variável do estado do User na app
    private Estado estado;

    // Variáveis de Instância
    private Demultiplexer dem;
    private Scanner scan;
    private String username; // username do user, é registado quando o user faz login

    // LOCALIZAÇÃO: trata-se de uma matriz, então
    // x -> índice das colunas, y -> índice das linhas
    private int x; // LOCALIZAÇÃO: coordenada x
    private int y; // LOCALIZAÇÃO: coordenada y

    // Construtor vazio (inicia o Menu no 1º menu e state, MAIN)
    public Menu(Demultiplexer dem) {
        // set ao estado do user para MAIN
        this.estado = Estado.MAIN;
        // recebe o demultiplexer da conexão estabelecida
        this.dem = dem;
        // cria scanner para ler o input do user
        scan = new Scanner(System.in);
        // exibir o menu
        // TODO: ciclo para o menu fazer show recursivamente, estado = sair?
        while (true) {
            this.show();
        }
    }

    // variáveis constantes
    private static final String OPCAO_STRING = "Opção: ";
    private static final String I_STRING = "Insira opção válida!";
    private static final String INDIQUE_X = "Indique a coordenada X: ";
    private static final String INDIQUE_Y = "Indique a coordenada Y: ";
    private static final int THREAD_1 = 1;
    private static final int THREAD_2 = 2;
    // private static final int THREAD_3 = 3; -> vai ser preciso

    // Mostra um específico menu de acordo com o "state" atual
    public void show() {
        switch (estado) {
            // MAIN
            case MAIN:
                System.out.println("+----------------- MENU INICIAL -----------------+\n"
                        + "| 1 - LOG-IN                                     |\n"
                        + "| 2 - REGISTAR                                   |\n"
                        + "| 0 - SAIR                                       |\n"
                        + "+------------------------------------------------+\n");
                System.out.print(OPCAO_STRING);
                break;

            // AUTENTICADO
            case AUTENTICADO:
                System.out.println("+----------------- MENU USER ------------------+\n"
                        + "| 1 - ALTERAR LOCALIZACAO ATUAL                   |\n"
                        + "| 2 - PESSOAS NUMA LOCALIZACAO                    |\n"
                        + "| 3 - RASTREAR LOCALIZACAO                        |\n"
                        + "| 4 - COMUNICAR DOENCA                            |\n"
                        + "| 0 - LOGOUT                                      |\n"
                        + "+ ------------------------------------------------+\n");
                System.out.print(OPCAO_STRING);
                // ler dados aqui
                break;

            // AUTENTICADO_CONTAMINADO
            case AUTENTICADOCONTAMINADO:
                System.out.println("+----------------- MENU USER ISOLADO -------------+\n"
                        + "| 1 - COMUNICAR FINAL DE ISOLAMENTO               |\n"
                        + "| 2 - RENOVAR ISOLAMENTO                          |\n"
                        + "| 0 - LOGOUT                                      |\n"
                        + "+ ------------------------------------------------+\n");
                System.out.print(OPCAO_STRING);
                // ler dados aqui
                break;

            default:
                break;
        }
        // escolher a opção
        int opcao;
        try {
            if ((opcao = this.escolheOption()) != -1) {
                parseWriter(opcao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // escolhe a opção
    public Integer escolheOption() {
        int choice;
        if ((choice = option()) == -1) {
            System.out.println(I_STRING);
            System.out.print(OPCAO_STRING);
        }
        return choice;
    }

    // trata da opção
    private Integer option() {
        int c;
        try {
            c = Integer.parseInt(scan.nextLine());
            switch (estado) {
                case MAIN:
                    if (c < 0 || c > 2) {
                        System.out.println(I_STRING);
                        System.out.print(OPCAO_STRING);
                        c = Integer.parseInt(scan.nextLine());
                    }
                    break;
                case AUTENTICADO:
                    if (c < 0 || c > 4) {
                        System.out.println(I_STRING);
                        System.out.print(OPCAO_STRING);
                        c = Integer.parseInt(scan.nextLine());
                    }
                    break;
                default:
                    System.out.println("This is my final messsage... goodbye");
            }
        } catch (NumberFormatException e) {
            c = -1;
        }
        return c;
    }

    // lê pedido
    public String lerDadosUser(String pedido) {
        System.out.println(pedido);
        return scan.nextLine();
    }

    // notificar o user
    public void notificaUser(String message) {
        System.out.println(message);
    }

    // alterar o estado do menu
    public void alteraEstado(Estado estado) {
        this.estado = estado;
    }

    // alterar o username do menu
    public void alteraUser(String username) {
        this.username = username;
    }

    // get do username
    public String getUser() {
        return this.username;
    }

    // get do estado
    public Estado getEstado() {
        return this.estado;
    }

    // get X
    public int getX() {
        return x;
    }

    // set X
    public void setX(int x) {
        this.x = x;
    }

    // get Y
    public int getY() {
        return y;
    }

    // set Y
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Parser da escolha que o User faz
     *
     * @param op - Opção escolhida do User
     * @throws InterruptedException
     */

    private void parseWriter(Integer op) throws InterruptedException {
        try {
            switch (this.getEstado()) {
                case MAIN:
                    if (op == 1 || op == 2)
                        login(op);

                    if (op == 0) {
                        System.exit(1);
                    }

                    break;

                case AUTENTICADO:
                    if (op == 1)
                        // alterar posição atual do user
                        alterarLocal();

                    if (op == 2 || op == 3)
                        // fazer pedido de quantidade de pessoas num local
                        rastrearLocal();

                    if (op == 4)
                        // confirmar doença do user
                        confirmarD();

                    if (op == 0) {
                        // logout da sessão do user
                        logout();
                    }

                    break;

                case AUTENTICADOCONTAMINADO:
                    if (op == 1)
                        // TODO: verificar este caso
                        alteraEstado(Estado.AUTENTICADO);

                    if (op == 0) {
                        logout();
                    }

                    break;

                default:
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rastrearLocal() {
        // ler coordenadas do local
        int coordX = Integer.parseInt(lerDadosUser(INDIQUE_X));
        int coordY = Integer.parseInt(lerDadosUser(INDIQUE_Y));
        String envia = "RASTREAR>" + coordX + "," + coordY;
        try {
            // send request
            dem.send(THREAD_1, envia.getBytes());
            Thread.sleep(100);
            // get reply
            byte[] data = dem.receive(THREAD_1);
            // string resposta
            String resposta = new String(data);

            if (resposta.equals("0")) {
                System.out.println("Não existem pessoas neste local!");
                System.out.println("Será seguro deslocar-se para este local!");
            } else {
                System.out.println("Quantidade de pessoas neste local é: " + resposta);
            }

        } catch (Exception e) {
            // ignore
        }
    }

    // tratar de ler do input do user para fazer login
    private void login(int i) {
        String user = this.lerDadosUser("Username: ");
        String password = this.lerDadosUser("Password: ");
        String coord = this.getX() + "," + this.getY();
        String envia;
        if (i == 1) {
            envia = "AUTENTICA" + ">" + user + "," + password + "," + coord;

        } else {
            envia = "REGISTA" + ">" + user + "," + password + "," + coord;
        }

        // criar uma mensagem para enviar ao servidor pelo 'dem'
        try {
            // send request
            dem.send(THREAD_1, envia.getBytes());
            Thread.sleep(100);
            // get reply
            byte[] data = dem.receive(THREAD_1);
            // string resposta
            String resposta = new String(data);
            // se é autenticado, altera estado do menu
            if (i == 1) {
                if (resposta.equals("AUTENTICADO>")) {
                    this.username = user;
                    alteraEstado(Estado.AUTENTICADO);
                    System.out.println("Seja bem vindo " + username);

                    Thread[] threads = {
                            // THREAD_2 para mandar localizações
                            new Thread(() -> {
                                try {
                                    // send request
                                    while (true) {
                                        Thread.sleep(5000);
                                        int x1;
                                        int y1;
                                        try {
                                            lock.lock();
                                            x1 = this.getX();
                                            y1 = this.getY();
                                            dem.send(THREAD_2, ("COORD>" + x1 + "," + y1).getBytes());
                                        } finally {
                                            lock.unlock();
                                        }
                                    }
                                } catch (Exception ignored) {
                                    //
                                }
                            }),
                            // THREAD_3 só para ler coisas do server
                            new Thread(() -> {
                                try {
                                    while (true) {
                                        Thread.sleep(1000);
                                        // stuff
                                    }
                                } catch (Exception ignored) {
                                    //
                                }
                            }) };

                    for (Thread t : threads)
                        t.start();
                } else {
                    // avisa se o user já está on, avisa
                    if (resposta.equals("ALREADYON>")) {
                        System.out.println("O user já está online!");
                    } else {
                        // avisa se ocorreu algum erro com os dados
                        if (resposta.equals("ERROLOGIN>")) {
                            System.out.println("Os dados inseridos não estão corretos!");
                        }
                    }
                }
            } else {
                // se conseguiu registar
                if (resposta.equals("REGISTADOUSER>")) {
                    System.out.println("Registo efetuado!");
                    System.out.println("Por favor efetue o seu login com os dados que inseriu.");
                    System.out.print(OPCAO_STRING);
                } else {
                    // caso não tenha conseguido registar-se
                    if (resposta.equals("ALREADYREG>")) {
                        System.out.println(
                                "Os dados do user que inseriu já existem! Por favor seleciona opção novamente:");
                        System.out.print(OPCAO_STRING);
                    }
                }
            }
        } catch (Exception ignored) {
            System.out.println("(" + THREAD_1 + ")" + ignored); // ignore
        }
    }

    /**
     * Função que permite o User fazer logout
     *
     * @throws IOException
     * @throws InterruptedException
     */

    private void logout() throws IOException, InterruptedException {
        // tag == THREAD_1 : neste caso é a main thread
        dem.send(THREAD_1, ("LOGOUT>" + this.username).getBytes());
        Thread.sleep(100);
        // get reply
        byte[] data = dem.receive(THREAD_1);
        // string resposta
        String resposta = new String(data);

        // caso tenha conseguido fazer logout com sucesso
        if (resposta.equals("LOGOUTDONE>")) {
            System.out.println("Até uma próxima " + username);
            // alterar variáveis
            this.username = null;
            this.alteraEstado(Estado.MAIN);
        }
    }

    /**
     *
     * Função que permite o User de alterar a sua posiçao
     *
     * @throws IOException
     * @throws InterruptedException
     */

    public void alterarLocal() {
        // só fazer set das coordenadas
        int coordX = Integer.parseInt(lerDadosUser(INDIQUE_X));
        int coordY = Integer.parseInt(lerDadosUser(INDIQUE_Y));
        try {
            lock.lock();
            this.setX(coordX);
            this.setY(coordY);
        } finally {
            lock.unlock();
        }
    }

    /*
     * -Falar sobre por exemplo se um cliente confirmar a doença o menu vai
     * automaticamente alterar para um onde para outro menu -Quando este cliente
     * confirma doença supostamente não pode aceder mais ao cinema ---> Alterar
     * login pois vamos ter de verificar se este cliente está doente ou não
     *
     */

    /**
     * Função que representa o User confirmar que contraiu doença
     *
     * @throws IOException
     */

    public void confirmarD() throws IOException {
        String resposta = this.lerDadosUser("Encontra-se doente? [SIM]/[NÃO]");
        if (resposta.equals("SIM")) {
            dem.send(THREAD_1, ("CONFIRMAR" + ">" + this.username).getBytes());
            this.alteraEstado(Menu.Estado.AUTENTICADOCONTAMINADO);
        }
    }

}