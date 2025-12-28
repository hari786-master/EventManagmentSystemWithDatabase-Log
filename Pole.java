import java.io.Serializable;
import java.util.ArrayList;

class Poll implements Serializable {
    private static final long serialVersionUID = 2L;

    String question;
    ArrayList<String> votes = new ArrayList<>();

    Poll(String question) {
        this.question = question;
    }

    void castVote(String option) {
        votes.add(option);
    }

    String displayResults() {
        if (votes.size() == 0)
            return "No Votes Polled";
        String result = "";
        ArrayList<String> duplicate = new ArrayList<>();
        for (String string : votes) {
            int count = 0;
            if (!duplicate.contains(string)) {
                for (String vote : votes) {
                    if (vote.equals(string)) {
                        count++;
                    }
                }
                result += string + ": " + count + "\n";
            }
            duplicate.add(string);
        }
        return result;
    }

}
