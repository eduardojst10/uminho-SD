package Cliente;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable{
    private final Writer connWriter;
    private final Reader connReader;
    private final Lock l = new ReentrantLock();
    private final Map<Integer,Entry> buf = new HashMap<>(); //integer vai ser a tag
    private IOException exception=null;

    private class Entry{
        int waiters = 0; //numero de interessados em receber data deste entry
        final Condition cond = l.newCondition();
        final ArrayDeque<byte[]> queue = new ArrayDeque<>();
    }




    public Demultiplexer(Writer writer, Reader reader){
        this.connWriter = writer;
        this.connReader = reader;

    }

    public void close(){

    }

    public void start(){
        new Thread(() -> {
            try{
                for(;;){
                    Frame frame = connReader.receive();
                    l.lock();
                    try {
                        Entry entry = buf.get(frame.tag);
                        entry.queue.add(frame.data);
                        entry.cond.signal();
                    }finally {
                        l.unlock();
                    }
                }
            } catch (IOException e) {
                l.lock();
                try{
                    this.exception = e;
                    buf.forEach((k,v) -> v.cond.signalAll());
                }finally {
                    l.unlock();
                }

            }
        }).start();
    }

    public Entry get(int tag){
        Entry entry = buf.get(tag);
        if(entry == null){
            entry = new Entry();
            buf.put(tag,entry);
        }
        return entry;
    }

    public void send(Frame frame) throws IOException{
        this.connWriter.send(frame);
    }

    /*
    public void send(int tag,byte[] data) throws IOException {
        connWriter.send(tag,data);
    }

     */

    public byte[] receive(int tag) throws IOException,InterruptedException {
        l.lock();
        try {
            Entry entry = buf.get(tag);
            entry.waiters++; //temos um novo interessado
            for(;;){
                if(!entry.queue.isEmpty()){
                    byte[] data = entry.queue.poll();
                    entry.waiters--;
                    if(entry.queue.isEmpty() && entry.waiters == 0) buf.remove(tag);
                    return data;
                }

                if(this.exception != null) throw exception;

                entry.cond.await();
            }

        } finally {
            l.unlock();
        }
    }
}