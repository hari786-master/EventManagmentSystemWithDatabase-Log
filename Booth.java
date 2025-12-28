import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

class Booth implements Serializable {
    private static final long serialVersionUID = 2L;

    int boothId;
    double size;
    double price;
    Sponsor assignedSponsor;
    String name;

//    Booth(String name, double size, double price) {
//        this(name, size, price,0);
//    }

    Booth(String name, double size, double price) {
        this.boothId = boothId;
        this.price = price;
        this.size = size;
        this.name = name;
    }

    void setBoothId(int boothId) {
        this.boothId = boothId;
    }

    boolean isAvailable() {
        return assignedSponsor == null;
    }

    boolean assignSponsor(Sponsor sponsor) {
        if (isAvailable()) {
            this.assignedSponsor = sponsor;
            return true;
        } else
            return false;
    }

    void unassignSponsor() {
        this.assignedSponsor = null;
    }

    double getPrice() {
        return price;
    }

    public String toString() {
        return name + "|" + size + "|" + price + "|" + boothId;
    }

        static String displayAllBooths(int eventNumber, ArrayList<Organizer> organizer , int orgIndex) {
        StringBuilder result = new StringBuilder();
        System.out.println(
                "                                            \u001B[33m$$$$$$$\\                       $$\\     $$\\       ");
        System.out.println(
                "                                            $$  __$$\\                      $$ |    $$ |      ");
        System.out.println(
                "                                            $$ |  $$ | $$$$$$\\   $$$$$$\\ $$$$$$\\   $$$$$$$\\  ");
        System.out.println(
                "                                            $$$$$$$\\ |$$  __$$\\ $$  __$$\\\\_$$  _|  $$  __$$\\ ");
        System.out.println(
                "                                            $$  __$$\\ $$ /  $$ |$$ /  $$ | $$ |    $$ |  $$ |");
        System.out.println(
                "                                            $$ |  $$ |$$ |  $$ |$$ |  $$ | $$ |$$\\ $$ |  $$ |");
        System.out.println(
                "                                            $$$$$$$  |\\$$$$$$  |\\$$$$$$  | \\$$$$  |$$ |  $$ |");
        System.out.println(
                "                                            \\_______/  \\______/  \\______/   \\____/ \\__|  \\__|\u001B[0m");

        result.append(
                "                                  ╭──────┬─────────────────────────────┬──────────────┬──────────────╮\n");
        result.append(
                "                                  │ S.No │ Booth Name                  │ Size (sq.ft) │ Cost (₹)     │\n");
        result.append(
                "                                  ├──────┼─────────────────────────────┼──────────────┼──────────────┤\n");

        ArrayList<Booth> booths = organizer.get(orgIndex).events.get(eventNumber).booths;

        if (booths == null || booths.isEmpty()) {
            result.append(
                    "                                  │  --  │ No booths available         │      --      │      --      │\n");
        } else {
            for (int i = 0; i < booths.size(); i++) {
                Booth b = booths.get(i);
                String name = b.name;
                String size = String.format("%.2f", b.size);
                String cost = String.format("%.2f", b.price);
                result.append(String.format("                                  │ %-5d│ %-27s │ %-12s │ %-12s │\n",
                        i + 1, name, size, cost));

                if (i != booths.size() - 1) {
                    result.append(
                            "                                  ├──────┼─────────────────────────────┼──────────────┼──────────────┤\n");
                }
            }
        }

        result.append(
                "                                  ╰──────┴─────────────────────────────┴──────────────┴──────────────╯\n");

        return result.toString();
    }


    void addBoothDb(Event event) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement query = con.prepareStatement(Data.WRITE_BOOTH_QUERY, Statement.RETURN_GENERATED_KEYS);
        query.setInt(1, event.event_ID);
        query.setString(2, this.name);
        query.setDouble(3, this.size);
        query.setDouble(4, this.price);
        query.executeUpdate();
        ResultSet boothRs = query.getGeneratedKeys();
        if (boothRs.next()) {
            this.setBoothId(boothRs.getInt(1));
        }
    }


    void updateBoothDb() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement updateBooth = con.prepareStatement(Data.UPDATE_BOOTH_QUERY);
        updateBooth.setString(1, this.name);
        updateBooth.setDouble(2, this.size);
        updateBooth.setDouble(3, this.price);
        updateBooth.setInt(4, this.boothId);
        updateBooth.executeUpdate();
    }


    void removeBoothDb() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement removeBoothQuery = con.prepareStatement(Data.DELETE_BOOTH_QUERY);
        removeBoothQuery.setInt(1, this.boothId);
        removeBoothQuery.executeUpdate();
    }

}
