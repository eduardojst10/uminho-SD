package Connections;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    // stuff
    private final Socket s;
    private final DataInputStream is;
    private final DataOutputStream os;

    // os canais input e output são independentes, pode-se usar 2 locks, um para
    // cada
    private ReentrantLock sendLock = new ReentrantLock();
    private ReentrantLock receiveLock = new ReentrantLock();

    public TaggedConnection(Socket socket) throws IOException {
        this.s = socket;
        this.is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        send(frame.tag, frame.data);
    }

    public void send(int tag, byte[] data) throws IOException {
        sendLock.lock();
        try {
            this.os.writeInt(4 + data.length);
            this.os.writeInt(tag);
            this.os.write(data);
            this.os.flush();
        } finally {
            sendLock.unlock();
        }
    }

    public Frame receive() throws IOException {
        int tag;
        byte[] data;
        receiveLock.lock();
        try {
            data = new byte[this.is.readInt() - 4];
            tag = this.is.readInt();
            this.is.readFully(data);
        } finally {
            receiveLock.unlock();
        }
        return new Frame(tag, data);
    }

    public void close() throws IOException {
        this.s.close();
    }
}