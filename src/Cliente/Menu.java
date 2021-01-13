package Cliente;

import java.util.Scanner;

public class Menu {

    // Estados possíveis
    public enum Estado {
        MAIN,
        AUTENTICADO,
        REGISTANDO,
        RASTREANDO,
        COMUNICANDO,
        NOTIFICANDO;
    }

    // Variáveis de Instância
    private Estado estado;
    private Scanner scan;
    private String user;

    // Construtor vazio (inicia o Menu no 1º menu e state, MAIN)
    public Menu() {
        scan = new Scanner(System.in);
        this.estado = Estado.MAIN;
    }

    // Mostra um específico menu de acordo com o "state" atual
    public void show() {
        switch (estado) {
            case MAIN:
                System.out.println("+----------------- MENU INICIAL -----------------+\n" +
                        "| 1 - LOG-IN                                     |\n" +
                        "| 2 - REGISTAR                                   |\n" +
                        "| 0 - SAIR                                       |\n" +
                        "+------------------------------------------------+\n");
                System.out.print("Opção: ");
                break;
            case AUTENTICADO:
                System.out.println("+----------------- MENU CLIENTE ------------------+\n" +
                        "| 1 - RASTREAR LOCAL                              |\n" +
                        "| 2 - NOTIFICAR LOCALIZACAO                       |\n" +
                        "| 3 - COMUNICAR SITUACAO                          |\n" +
                        "| 0 - LOGOUT                                      |\n" +
                        "+ ------------------------------------------------+\n");
                System.out.print("Opção: ");
                break;
            case REGISTANDO:
                System.out.println("+------------------- REGISTAR --------------------+\n" +   //ISTO ESTA MAL
                        "|                                                 |\n" +
                        "|            A registar User....                  |\n" +
                        "|                                                 |\n" +
                        "+ ------------------------------------------------+\n");
                break;
            case RASTREANDO:
                break;
            case COMUNICANDO:
                System.out.println("+-------------------------------------------------+\n" +
                        "|                                                 |\n" +
                        "|          Comunicando estado de User....         |\n" +
                        "|                                                 |\n" +
                        "+-------------------------------------------------+\n");
                break;
            case NOTIFICANDO:
                System.out.println("+-------------------------------------------------+\n" +
                        "|                                                 |\n" +
                        "|             Notificando Localizacao....         |\n" +
                        "|                                                 |\n" +
                        "+-------------------------------------------------+\n");
                break;
        }
    }

    public Integer escolheOption() {
        int choice;
        while((choice = option()) == -1) {
            System.out.println("Insira opção válida!");
            System.out.print("Opção: ");
        }
        return choice;
    }


    private Integer option() {
        int c;
        try {
            c = Integer.parseInt(scan.nextLine());
            switch(estado){
                case MAIN:
                    while(c<0 || c >2) {
                        System.out.println("Insira opção válida!");
                        System.out.print("Opção: ");
                        c = Integer.parseInt(scan.nextLine());
                    }
                    break;
                case AUTENTICADO:
                    while(c<0 || c >3){
                        System.out.println("Insira opção válida!");
                        System.out.print("Opção: ");
                        c = Integer.parseInt(scan.nextLine());
                    }
                    break;
                default:
                    System.out.println("This is my final messsage... goodbye");
            }
        }
        catch(NumberFormatException e) {
            c = -1;
        }
        return c;
    }

    //USER


    //lê pedido
    public String lerDadosUser(String pedido){
        System.out.println(pedido);
        return scan.nextLine();
    }


    public int  readIntUser(){
        int option;
        System.out.println("Insira o ID: ");
        while((option = readAux()) == -1){
            System.out.println("ID Inválido, tente outra vez!");
            System.out.println("Insira o ID: ");
        }
        return option;
    }

    public int readAux(){
        int res = Integer.parseInt(scan.nextLine());
        return res;
    }

    public void notificaUser(String not){
        System.out.println(not);
    }

    public void alteraEstado(Estado estado){
        this.estado = estado;
    }

    public void alteraUser(String user){
        this.user = user;
    }

    public String getUser(){
        return this.user;
    }

    public Estado getEstado(){
        return this.estado;
    }
}