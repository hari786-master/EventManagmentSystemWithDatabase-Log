
import java.time.LocalDateTime;
import java.util.UUID;

class Payment {
    String paymentId;
    double amount;
    String status;
    LocalDateTime timestamp;

    Payment(double price) {
        this.paymentId = UUID.randomUUID().toString();
        this.amount = price;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
    }

    void processPayment(int count) {
        System.out.println("Processing payment of $" + (amount*count));
        status = "COMPLETED";
        this.timestamp = LocalDateTime.now();
        System.out.println("Payment successful. Status: " + status);
    }
}

