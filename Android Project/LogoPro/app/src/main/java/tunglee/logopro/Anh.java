package tunglee.logopro;



public class Anh {
    private int Id;
    private byte[] Hinh;


    public Anh(int id, byte[] hinh) {
        Id = id;
        Hinh = hinh;

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public byte[] getHinh() {
        return Hinh;
    }

    public void setHinh(byte[] hinh) {
        Hinh = hinh;
    }
}


