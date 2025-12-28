import java.io.Serializable;
import java.util.ArrayList;

public class Organizer implements Serializable {
    private static final long serialVersionUID = 2L;
    String name;
    String email;
    String password;
    int org_ID;
    ArrayList<Event> events;

    Organizer(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        events = new ArrayList<>();
    }

    Organizer(String name, String email, String password, int org_ID) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.org_ID = org_ID;
        events = new ArrayList<>();
    }

    void setID(int id) {
        this.org_ID = id;
    }

    static String displayAllOrganizer(ArrayList<Organizer> organizer) {
        StringBuilder result = new StringBuilder();

        result.append("                                         ╭──────┬───────────────────────────┬─────────────╮\n");
        result.append("                                         │ S.No │ Organizer Name            │ Event Count │\n");
        result.append("                                         ├──────┼───────────────────────────┼─────────────┤\n");

        if (organizer == null || organizer.isEmpty()) {
            result.append("                                         │  --  │ No organizers available   │     --      │\n");
        } else {
            for (int i = 0; i < organizer.size(); i++) {
                Organizer o = organizer.get(i);
                String name = o.name;
                int eventCount = (o.events != null) ? o.events.size() : 0;

                result.append(String.format("                                         │ %-5d│ %-25s │ %-11d │\n", i + 1, name, eventCount));

                if (i != organizer.size() - 1) {
                    result.append("                                         ├──────┼───────────────────────────┼─────────────┤\n");
                }
            }
        }

        result.append("                                         ╰──────┴───────────────────────────┴─────────────╯\n");

        return result.toString();
    }
}
