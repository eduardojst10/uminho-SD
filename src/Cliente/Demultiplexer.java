package Cliente;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private final TaggedConnection conn;

    // será necessário um mapa com as tags e 'entries'.
    // uma entry terá uma fila de espera.
    // todas as mensagens têm de ser distribuídas de acordo com a tag
    private final Map<Integer, Entry> buffer = new HashMap<>();

    // lock para o acesso ao buffer
    private final ReentrantLock lock = new ReentrantLock();

    //
    private IOException exception = null;

    // existe uma variável de condição em cada tag
    private class Entry {
        final ArrayDeque<byte[]> queue = new ArrayDeque<>();
        final Condition cond = lock.newCondition();
    }

    public Demultiplexer(TaggedConnection taggedConnection) {
        this.conn = taggedConnection;
    }

    private Entry get(int tag) {
        Entry e = buffer.get(tag);
        if (e == null) {
            e = new Entry();
            buffer.put(tag, e);
        }

        return e;
    }

    //
    public void start() {
        new Thread(() -> {
            try {
                for (; ; ) {
                    // 1. ler frame da connection
                    Frame frame = this.conn.receive();
                    lock.lock();
                    try {
                        // 2. ler tag da frame, obter a entrada correspondente e inserir na queue
                        Entry e = get(frame.tag);
                        e.queue.add(frame.data);

                        // 3. notificar thread que está a aguardar por mensagens
                        e.cond.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (IOException e) {
                // ver isto :
                lock.lock();
                this.exception = e; // poderia ser antes do lock, mas se for dentro da secção crítica não interessa
                // se fica antes ou depois (atomicidade)
                // Sinalizar todas as threads
                buffer.forEach((k, v) -> v.cond.signalAll());
                lock.unlock();
                // Print error
                System.out.println("Something went wrong: " + e);
            }
        }).start();
    }

    //
    public void send(Frame frame) throws IOException {
        this.conn.send(frame);
    }

    //
    public void send(int tag, byte[] data) throws IOException {
        this.conn.send(tag, data);
    }

    //
    public byte[] receive(int tag) throws IOException, InterruptedException {
        lock.lock();
        try {
            // 1. obtém entrada correspondente à tag pedida
            Entry e = get(tag);
            // 2. enquanto não houver mensagens para ler da queue dessa entrada, bloqueia
            while (e.queue.isEmpty() && this.exception == null) {
                e.cond.await();
            }

            // NOTA: nesta fase é necessário ver qual é o motivo de saída

            // 2.1. verificar se existe mensagens para ler
            if (!e.queue.isEmpty()) {
                // 3. retorna os dados
                return e.queue.poll();
            }

            // 2.2. dar manage do erro, fechar se der erro
            if (this.exception != null) {
                throw this.exception;
            }

            return e.queue.poll();
        } finally {
            lock.unlock();
        }
    }

    //
    public void close() throws IOException {
        this.conn.close();
    }
}