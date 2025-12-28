
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

class Speaker implements Serializable {
    private static final long serialVersionUID = 2L;
    String name;
    String gender;

    Speaker(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }

    String displaySpeaker() {
        return "Name: " + name + "\nGender: " + gender;
    }

    static String displayAllSpeakers(int eventNumber,ArrayList<Organizer> organizer,int orgIndex) {
        System.out.println(
                "                                 \u001B[32m░██████                                    ░██                           \n                                ░██   ░██                                   ░██                           \n                               ░██         ░████████   ░███████   ░██████   ░██    ░██ ░███████  ░██░████ \n                                ░████████  ░██    ░██ ░██    ░██       ░██  ░██   ░██ ░██    ░██ ░███     \n                                       ░██ ░██    ░██ ░█████████  ░███████  ░███████  ░█████████ ░██      \n                                ░██   ░██  ░███   ░██ ░██        ░██   ░██  ░██   ░██ ░██        ░██      \n                                 ░██████   ░██░█████   ░███████   ░█████░██ ░██    ░██ ░███████  ░██      \n                                           ░██                                                            \n                                           ░██\u001B[0m");
        StringBuilder result = new StringBuilder();

        result.append(
                "                                            ╭──────┬────────────────────────────────┬────────────╮\n");
        result.append(
                "                                            │ S.No │ Name                           │ Gender     │\n");
        result.append(
                "                                            ├──────┼────────────────────────────────┼────────────┤\n");

        ArrayList<Speaker> speakers = organizer.get(orgIndex).events.get(eventNumber).speakers;
        if (speakers == null || speakers.isEmpty()) {
            result.append(
                    "                                            │  --  │ No speakers available          │    --      │\n");
        }
        for (int i = 0; i < speakers.size(); i++) {
            Speaker s = speakers.get(i);

            String name = s.name;
            String gender = s.gender;

            result.append(String.format("                                            │ %-5d│ %-30s │ %-10s │\n", i + 1,
                    name, gender));

            if (i != speakers.size() - 1) {
                result.append(
                        "                                            ├──────┼────────────────────────────────┼────────────┤\n");
            }
        }

        result.append(
                "                                            ╰──────┴────────────────────────────────┴────────────╯\n");

        return result.toString();
    }


    void addSpeakerDb(int eventId, Session session) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement speakerQuery = con.prepareStatement(Data.WRITE_SPEAKER_QUERY);
        speakerQuery.setInt(1, eventId);
        speakerQuery.setString(2, this.name);
        speakerQuery.setString(3, this.gender);
        speakerQuery.setInt(4, session.session_ID);
        PreparedStatement deletePs = con.prepareStatement(
                Data.DELETE_SPEAKER_QUERY
        );
        deletePs.setInt(1, session.session_ID);
        deletePs.executeUpdate();
        speakerQuery.executeUpdate();
    }

    void updateSpeakerDb(Session session) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement updateSpeakerQuery = con.prepareStatement(Data.UPDATE_SPEAKER_QUERY);
        updateSpeakerQuery.setString(1, this.name);
        updateSpeakerQuery.setString(2, this.gender);
        updateSpeakerQuery.setInt(3, session.session_ID);
        updateSpeakerQuery.executeUpdate();
    }

    void removeSpeakerDb(Session session) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement removeSessionQuery = con.prepareStatement(Data.DELETE_SPEAKER_QUERY);
        removeSessionQuery.setInt(1, session.session_ID);
        removeSessionQuery.executeUpdate();
    }
}
