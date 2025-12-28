
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

class Session implements Serializable {
    private static final long serialVersionUID = 2L;
    String topic;
    int totalHour;
    Speaker speaker;
    double time;
    String timeRange;
    int session_ID;
    ArrayList<Feedback> feedback;
    ArrayList<Poll> polls;

    Session(String topic, String timeRange) {
        this.topic = topic;
        this.timeRange = timeRange;
    }

    Session(String topic) {
        this.topic = topic;
        this.feedback = new ArrayList<>();
        this.polls = new ArrayList<>();
    }

    void setSessionId(int id){
        this.session_ID = id;
    }

    void assignSpeaker(Speaker s) {
        speaker = s;
    }

    void addFeedback(Feedback f) {
        feedback.add(f);
    }

    // String displaySession() {
    //     if (speaker != null) {
    //         return period + " : " + topic + " | Speaker: " + speaker.name + " Gender: " + speaker.gender;
    //     } else {
    //         return period + " : " + topic;
    //     }
    // }

    double getAverageRating() {
        double rating = 0;
        for (Feedback f : feedback) {
            rating += f.rating;
        }
        return (rating / feedback.size());
    }

    static String displayAllSession(int eventNumber , ArrayList<Organizer> organizer,int orgIndex) {

        System.out.println(
                "\u001B[35m                                    ███████╗ ███████╗ ███████╗ ███████╗ ██╗  ██████╗  ███╗   ██╗\n                                    ██╔════╝ ██╔════╝ ██╔════╝ ██╔════╝ ██║ ██╔═══██╗ ████╗  ██║\n                                    ███████╗ █████╗   ███████╗ ███████╗ ██║ ██║   ██║ ██╔██╗ ██║\n                                    ╚════██║ ██╔══╝   ╚════██║ ╚════██║ ██║ ██║   ██║ ██║╚██╗██║\n                                    ███████║ ███████╗ ███████║ ███████║ ██║ ╚██████╔╝ ██║ ╚████║\n                                    ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚═╝  ╚═════╝  ╚═╝  ╚═══╝\n                                    \u001B[0m");

        StringBuilder result = new StringBuilder();

        result.append(
                "\u001B[97m     ╭──────┬──────────────────────┬───────────────────────────────────────────────────────────┬──────────────────────╮\n");
        result.append(
                "     │ S.No │ Time                 │ Sessions scheduled                                        │ Speaker              │\n");
        result.append(
                "     ├──────┼──────────────────────┼───────────────────────────────────────────────────────────┼──────────────────────┤\n");

        ArrayList<Session> sessions = organizer.get(orgIndex).events.get(eventNumber).schedule;

        if (sessions == null || sessions.isEmpty()) {
            result.append(
                    "     │  --  │   --                 │ No sessions available                                     │        --            │\n");
        } else {
            int i = 0;
            for (Session s : sessions) {
                String time = s.timeRange;
                String action = s.topic;
                String owner = (s.speaker != null)
                        ? s.speaker.name + " (" + s.speaker.gender + ")"
                        : "Not Added Yet";

                result.append(String.format("     │ %-5d│ %-20s │ %-57s │ %-20s │\n", i + 1, time, action, owner));
                if (i++ != sessions.size() - 1) {
                    result.append(
                            "     ├──────┼──────────────────────┼───────────────────────────────────────────────────────────┼──────────────────────┤\n");
                }
            }
        }

        result.append(
                "     ╰──────┴──────────────────────┴───────────────────────────────────────────────────────────┴──────────────────────╯\u001B[0m");
        return result.toString();
    }


    void addSessionDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement query = con.prepareStatement(Data.WRITE_SESSION_QUERY, Statement.RETURN_GENERATED_KEYS);
        query.setInt(1, eventId);
        query.setString(2, this.topic);
        query.setString(3, this.timeRange);
        query.executeUpdate();
        ResultSet rs = query.getGeneratedKeys();
        if (rs.next()) {
            this.setSessionId(rs.getInt(1));
        }
    }

    void updateSessionhDb() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement updateSession = con.prepareStatement(Data.UPDATE_SESSION_QUERY);
        updateSession.setString(1, this.topic);
        updateSession.setString(2, this.timeRange);
        updateSession.setInt(3, this.session_ID);
        updateSession.executeUpdate();
    }

    void removeSessionDb() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement removeSessionQuery = con.prepareStatement(Data.DELETE_SESSION_QUERY);
        removeSessionQuery.setInt(1, this.session_ID);
        removeSessionQuery.executeUpdate();
    }

}
