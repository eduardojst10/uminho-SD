package Cliente;

public class Frame {
    public final int tag;
    public final byte[] data;

    public Frame(int tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }
}
