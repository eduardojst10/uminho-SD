package Cliente;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class WriterUpdate implements  Runnable{
    private Menu menu;
    private BufferedWriter outputStream;
    private Socket socket;

    public WriterUpdate(Menu menu,BufferedWriter out, Socket socket){
        this.menu = menu;
        this.outputStream = out;
        this.socket = socket;
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(5000);
                int x = menu.getX();
                int y = menu.getY();
                outputStream.write("COORD>" + x + "," + y);
                outputStream.newLine();
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}