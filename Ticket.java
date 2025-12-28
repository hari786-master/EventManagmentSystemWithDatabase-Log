
import java.io.Serializable;


class Ticket implements Serializable {
    private static final long serialVersionUID = 2L;

    String event;
    User owner;
    String type;
    double price;
    boolean isPaid;
    Payment payment;

    Ticket(String name, String type, double price) {
        this.event = event;
        isPaid = false;
        payment = null;
        this.type = type;
        this.price = price;
    }

    void markAsPaid() {
        isPaid = true;
    }

    void assignPayment(Payment p) {
        payment = p;
        if (p != null) {
            markAsPaid();
        }
    }

    boolean isPaid() {
        return isPaid;
    }

}