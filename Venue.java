import java.io.Serializable;
import java.util.ArrayList;

class Venue implements Serializable {
    private static final long serialVersionUID = 2L;

    String name;
    int capacity;
    ArrayList<Booth> boothLayout;

    Venue(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String toString() {
        return name + "|" + capacity;
    }

}
