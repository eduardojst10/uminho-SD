package Cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Reader implements Runnable{
    private Menu menu;
    //private DataInputStream inputStream;
    private BufferedReader bufferedReader;
    private Socket socket;

    public Reader(Menu menu, BufferedReader bf,Socket socket){
        this.menu = menu;
        this.bufferedReader = bf;
        this.socket = socket;
    }

    @Override
    public void run() {
        String in;
        try{
            while ((in = bufferedReader.readLine()) != null){
                parseReader(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Função que lê respostas do server para os diferentes casos
     * @param r
     */

    private void parseReader(String r){
        String[] dados = r.split(">",2);
        switch (dados[0]){
            case ("ALREADYON"):
                System.out.println("Dados de User/Pass não válidos!");
                menu.show();
                break;

            case ("ERROLOGIN"):
                System.out.println("User já autenticado!");
                menu.show();
                break;

            case ("AUTENTICADO"):
                menu.alteraEstado(Menu.Estado.AUTENTICADO);
                menu.alteraUser(dados[1]);
                menu.show();
                break;

            case ("ALREADYREG"):
                System.out.println("User já registado!");
                menu.alteraEstado(Menu.Estado.MAIN);
                menu.show();
                break;

            case ("REGISTADOUSER"):
                System.out.println("User registado!");
                menu.alteraEstado(Menu.Estado.MAIN);
                menu.show();
                break;

            case ("LOCALIZACAOATUAL"):
                String[] coord = dados[1].split(",");
                System.out.println("Localização atual: ");
                System.out.println("X:" + coord[0]);
                System.out.println("Y:" + coord[1]);
                menu.alteraEstado(Menu.Estado.AUTENTICADO);
                menu.show();
                break;

            case("ERROINFORMAR"):
                System.out.println("Localizacao já existente ou não existem Localizacoes");
                menu.alteraEstado(Menu.Estado.AUTENTICADO);
                menu.show();
                break;


            case ("ADICIONARUSERDOENTE"):
                System.out.println("User infetado registado e em isolamento!");
                menu.alteraEstado(Menu.Estado.MAIN);
                menu.show();
                break;

            case ("ERROCONFIRMAR"):
                System.out.println("User não online ou não registado!");
                menu.alteraEstado(Menu.Estado.MAIN);
                menu.show();
                break;
            case ("LOGOUT"):
                System.out.println();

            default:
                System.out.println("ERROR");
                menu.alteraEstado(Menu.Estado.MAIN);
                menu.show();
                break;



        }

    }
}
