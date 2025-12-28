import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

class Sponsor implements Serializable {
    private static final long serialVersionUID = 2L;
    String companyName;
    double amountSponsor;

    Sponsor(String companyName, double amountSponsor) {
        this.companyName = companyName;
        this.amountSponsor = amountSponsor;
    }

    String displaySponsor() {
        return "Company Name: " + companyName + "\nAmount Sponsor: " + amountSponsor;
    }

    static String displayAllSponsor(int eventNumber, ArrayList<Organizer> organizer, int orgIndex) {
        StringBuilder result = new StringBuilder();
        System.out.println("                           \u001B[34m$$$$$$\\ ");
        System.out.println("                          $$  __$$\\ ");
        System.out.println(
                "                          $$ /  \\__| $$$$$$\\   $$$$$$\\  $$$$$$$\\   $$$$$$$\\  $$$$$$\\   $$$$$$\\ ");
        System.out.println(
                "                          \\$$$$$$\\  $$  __$$\\ $$  __$$\\ $$  __$$\\ $$  _____|$$  __$$\\ $$  __$$\\ ");
        System.out.println(
                "                           \\____$$\\ $$ /  $$ |$$ /  $$ |$$ |  $$ |\\$$$$$$\\  $$ /  $$ |$$ |  \\__|");
        System.out.println(
                "                          $$\\   $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ | \\____$$\\ $$ |  $$ |$$ |      ");
        System.out.println(
                "                          \\$$$$$$  |$$$$$$$  |\\$$$$$$  |$$ |  $$ |$$$$$$$  |\\$$$$$$  |$$ |      ");
        System.out.println(
                "                           \\______/ $$  ____/  \\______/ \\__|  \\__|\\_______/  \\______/ \\__|      ");
        System.out.println("                                    $$ |");
        System.out.println("                                    $$ |");
        System.out.println("                                    \\__|\u001B[0m");

        result.append("                                     ╭───────┬───────────────────────────┬─────────────╮\n");
        result.append("                                     │ S.No  │ Company Name              │ Amount      │\n");
        result.append("                                     ├───────┼───────────────────────────┼─────────────┤\n");

        ArrayList<Sponsor> sponsors = organizer.get(orgIndex).events.get(eventNumber).sponsors;
        if (sponsors == null || sponsors.isEmpty()) {
            result.append("                                     │  --   │ No sponsors available     │     --      │\n");
        }
        for (int i = 0; i < sponsors.size(); i++) {
            Sponsor s = sponsors.get(i);

            String company = s.companyName;
            String amount = String.format("%.2f", s.amountSponsor);

            result.append(String.format("                                     │ %-6d│ %-25s │ %-11s │\n", i + 1,
                    company, amount));

            if (i != sponsors.size() - 1) {
                result.append(
                        "                                     ├───────┼───────────────────────────┼─────────────┤\n");
            }
        }

        result.append("                                     ╰───────┴───────────────────────────┴─────────────╯\n");

        return result.toString();
    }

    void addSponsorDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement sponsorQuery = con.prepareStatement(Data.WRITE_SPONSOR_QUERY);
        sponsorQuery.setString(1, this.companyName);
        sponsorQuery.setDouble(2, this.amountSponsor);
        sponsorQuery.setInt(3, eventId);
        sponsorQuery.executeUpdate();
    }

    void updateSponsorDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement updateSponsorQuery = con.prepareStatement(Data.UPDATE_SPONSOR_QUERY);
        updateSponsorQuery.setString(1, this.companyName);
        updateSponsorQuery.setDouble(2, this.amountSponsor);
        updateSponsorQuery.setInt(3, eventId);
        updateSponsorQuery.executeUpdate();
    }

    void removeSponsorDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement removeSponsorQuery = con.prepareStatement(Data.DELETE_SPONSOR_QUERY);
        removeSponsorQuery.setInt(1, eventId);
        removeSponsorQuery.executeUpdate();
    }

}
