
import java.io.Serializable;

class Feedback implements Serializable {
    private static final long serialVersionUID = 2L;

    User user;
    int rating;
    String comment;

    Feedback(User user, int rating, String comment) {
        this.comment = comment;
        this.user = user;
        this.rating = rating;
    }

    static Feedback addFeedback(User user, int rating, String comment) {
        return new Feedback(user, rating, comment);
    }

    public String toString() {
        return "Feedback from " + user + " [Rating: " + rating + ", Comment: \"" + comment + "\"]";
    }

}
