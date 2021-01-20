package Cliente;

public class Frame {
    private final int tag;
    private final byte[] data;

    public Frame(int tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }
}
