import com.mysql.cj.protocol.a.SqlDateValueEncoder;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

class Event   {
    String name;
    double budget;
    String category;
    Venue venue;
    LocalDate date;
    int event_ID;
    ArrayList<User> users;
    ArrayList<Session> schedule;
    ArrayList<Sponsor> sponsors;
    ArrayList<Ticket> ticketsSold;
    ArrayList<Expense> expenses;
    ArrayList<Feedback> feedback;
    ArrayList<Poll> polls;
    ArrayList<Booth> booths;
    ArrayList<Speaker> speakers;
    int num;
    String status;

    Event(String name, double budget, Venue venue, String category, LocalDate date) {
        this.name = name;
        this.budget = budget;
        this.venue = venue;
        this.category = category;
        this.date = date;
        this.users = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.sponsors = new ArrayList<>();
        this.ticketsSold = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.feedback = new ArrayList<>();
        this.polls = new ArrayList<>();
        this.booths = new ArrayList<>();
        this.speakers = new ArrayList<>();
    }

    void setEventId(int id) {
        this.event_ID = id;
    }

    void getDate() {
        System.out.println(date);
    }

    void addUser(User u) {
        users.add(u);
    }

    void addSession(Session s) {
        schedule.add(s);
    }

    void addSponsor(Sponsor s) {
        sponsors.add(s);
    }

    void changeVenue(Venue v) {
        venue = v;
    }

    void addExpense(Expense e) {
        expenses.add(e);
    }

    void addBooth(Booth b) {
        booths.add(b);
    }

    double totalExpenses() {
        double result = 0;
        for (Expense e : expenses) {
            result += e.totalExpenses();
        }
        return result;
    }

    public String toString() {
        return displayEvent();
    }

    String displayEvent() {
        String reset = "\033[0m";
        String bold = "\033[1m";
        String red = "\033[31m";
        String green = "\033[32m";
        String cyan = "\033[36m";
        String yellow = "\033[33m";

        // Box borders
        String line = "                                    ┌──────────────────────────────────────────────────┐";
        String bottom = "                                    └──────────────────────────────────────────────────┘";
        if (this.date.isAfter(LocalDate.now())) {
            status = "PENDING";
        } else if (this.date.isEqual(LocalDate.now())) {
            status = "LIVE";
        } else {
            status = "COMPLETED";
        }
        String result = "\n" + green + line;
        result += "\n                                    │                                                  │";
        result += "\n                                    │                " + bold + red + "   EVENT DETAILS   " + reset + green + "               │";
        result += "\n                                    │                                                  │" + reset;
        result += green + "\n                                    │   Name:       " + bold + cyan + name + reset;
        result += green + "\n                                    │   Category:   " + bold + yellow + category + reset;
        result += green + "\n                                    │   Location:   " + bold + cyan + venue.name + reset;
        result += green + "\n                                    │   Date:       " + bold + green + date + reset;
        result += green + "\n                                    │   Budget:     " + bold + "$" + budget + reset;
        result += green + "\n                                    │   Event No:   " + bold + num + reset;
        result += green + "\n                                    │   Status :    " + bold + status + reset;
        result += green + "\n                                    │                                                  │";
        result += "\n" + green + bottom + reset;
        return result;
    }


    void addEventDb(int orgIndex) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement query = con.prepareStatement(Data.WRITE_EVENT_QUERY, Statement.RETURN_GENERATED_KEYS);
        query.setString(1, this.name);
        query.setString(2, this.category);
        query.setString(3, this.venue.name);
        query.setDate(4, Date.valueOf(this.date));
        query.setDouble(5, this.budget);
        query.setString(6, this.status);
        query.setInt(7, orgIndex);
        query.setInt(8, this.venue.capacity);
        query.setInt(9, this.venue.capacity);
        query.executeUpdate();
        ResultSet rs = query.getGeneratedKeys();
        if (rs.next()) {
            this.setEventId(rs.getInt(1));
        }
        for (int i = 0; i < this.venue.capacity; i++) {
            this.ticketsSold.add(new Ticket(this.name, this.category, 0));
        }
    }

    void removeEventDb() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement removeEventQuery = con.prepareStatement(Data.DELETE_EVENT_QUERY);
        removeEventQuery.setInt(1, this.event_ID);
        removeEventQuery.executeUpdate();
    }


    void reduceTicketCount() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement reduceTicketQuary = con.prepareStatement(Data.UPDATE_TICKET_COUNT_QUERY);
        reduceTicketQuary.setInt(1, this.ticketsSold.size());
        reduceTicketQuary.setInt(2, this.event_ID);
        reduceTicketQuary.executeUpdate();
    }

    void updatePriceDb(double rate) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement updateQuery = con.prepareStatement(Data.UPDATE_TICKET_PRICE_QUERY);
        updateQuery.setDouble(1, rate);
        updateQuery.setInt(2, this.event_ID);
        updateQuery.executeUpdate();
    }

    void updateEventNameDb() throws SQLException{
        Connection con = DbConnection.getConnection();
        PreparedStatement updateNameQuery = con.prepareStatement(Data.UPDATE_EVENT_NAME_QUERY);
        updateNameQuery.setString(1,this.name);
        updateNameQuery.setInt(2,this.event_ID);
        updateNameQuery.executeUpdate();
    }

    void updateEventCategoryDb() throws SQLException{
        Connection con = DbConnection.getConnection();
        PreparedStatement updateNameQuery = con.prepareStatement(Data.UPDATE_EVENT_CATEGORY_QUERY);
        updateNameQuery.setString(1,this.category);
        updateNameQuery.setInt(2,this.event_ID);
        updateNameQuery.executeUpdate();
    }

    void updateEventLocationDb() throws SQLException{
        Connection con = DbConnection.getConnection();
        PreparedStatement updateNameQuery = con.prepareStatement(Data.UPDATE_EVENT_LOCATION_QUERY);
        updateNameQuery.setString(1,this.venue.name);
        updateNameQuery.setInt(2,this.event_ID);
        updateNameQuery.executeUpdate();
    }

    void updateEventDateDb() throws SQLException{
        Connection con = DbConnection.getConnection();
        PreparedStatement updateNameQuery = con.prepareStatement(Data.UPDATE_EVENT_DATE_QUERY);
        updateNameQuery.setDate(1, Date.valueOf(this.date));
        updateNameQuery.setInt(2,this.event_ID);
        updateNameQuery.executeUpdate();
    }

    void updateEventBudgetDb() throws SQLException{
        Connection con = DbConnection.getConnection();
        PreparedStatement updateNameQuery = con.prepareStatement(Data.UPDATE_EVENT_BUDGET_QUERY);
        updateNameQuery.setDouble(1, this.budget);
        updateNameQuery.setInt(2,this.event_ID);
        updateNameQuery.executeUpdate();
    }
}
