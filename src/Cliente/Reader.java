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

    //ler respostas do server para os diferentes casos

    private void parseReader(String r){
        String[] dados = r.split(">",2);
        System.out.println(dados);
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

            case ("LOCALIZACAOATUALIZADA"):
                System.out.println("Localizacao de User atualizada!");
                menu.alteraEstado(Menu.Estado.AUTENTICADO);
                menu.show();
                break;

            case("ERROINFORMAR"):
                System.out.println("Localizacao já existente ou não existem Localizacoes");
                menu.alteraEstado(Menu.Estado.AUTENTICADO);
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
