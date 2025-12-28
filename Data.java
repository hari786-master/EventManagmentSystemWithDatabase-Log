public class Data {
    static final String url = "jdbc:mysql://localhost:3306/EventManagement";
    static final String user = "root";
    static final String password = "Hariharasuthan@zoho2006";

    // ==================== READ QUERIES ====================

    // READ ALL ACCOUNTS
    static final String READ_ACCOUNT_QUERY =
            "SELECT * FROM Account";

    // READ ACCOUNTS BY ROLE
    static final String READ_ORG_QUERY =
            "SELECT * FROM Account WHERE Role = ?";

    // READ EVENTS CREATED BY A SPECIFIC ORGANIZER
    static final String READ_EVENT_QUERY =
            "SELECT * FROM Events WHERE Organizer_ID = ?";

    // READ BOOTH DETAILS FOR A SPECIFIC EVENT
    static final String READ_BOOTH_QUERY =
            "SELECT booth_name, size_sq_ft, cost, booth_id FROM Booth WHERE event_id = ?";

    // READ SESSIONS FOR A SPECIFIC EVENT
    static final String READ_SESSION_QUERY =
            "SELECT * FROM Session WHERE event_id = ?";

    // READ SPEAKER DETAILS FOR A SPECIFIC SESSION IN AN EVENT
    static final String READ_SPEAKER_QUERY =
            "SELECT name, gender FROM Speaker WHERE event_id = ? AND session_id = ?";

    // READ SPONSOR DETAILS FOR A SPECIFIC EVENT
    static final String READ_SPONSOR_QUERY =
            "SELECT company_Name, amount FROM Sponsor WHERE event_id = ?";

    // READ EXPENSE DETAILS FOR A SPECIFIC EVENT
    static final String READ_EXPENSE_QUERY =
            "SELECT description, Amount FROM Expense WHERE event_id = ?";


// ==================== INSERT QUERIES ====================

    // INSERT NEW ACCOUNT DETAILS
    static final String WRITE_ACCOUNT_QUERY =
            "INSERT INTO Account (Name, Email, Password, Role) VALUES (?,?,?,?)";

    // INSERT NEW EVENT DETAILS
    static final String WRITE_EVENT_QUERY =
            "INSERT INTO Events (Name, Category, Location, Event_Date, Budget, Status, Organizer_ID, Capacity, Ticket_Count) VALUES (?,?,?,?,?,?,?,?,?)";

    // INSERT NEW BOOTH FOR AN EVENT
    static final String WRITE_BOOTH_QUERY =
            "INSERT INTO Booth (event_id, booth_name, size_sq_ft, cost) VALUES (?,?,?,?)";

    // INSERT NEW SESSION FOR AN EVENT
    static final String WRITE_SESSION_QUERY =
            "INSERT INTO Session (event_id, title, duration) VALUES (?,?,?)";

    // INSERT SPEAKER DETAILS FOR A SESSION
    static final String WRITE_SPEAKER_QUERY =
            "INSERT INTO Speaker (event_id, name, gender, session_id) VALUES (?,?,?,?)";

    // INSERT SPONSOR DETAILS FOR AN EVENT
    static final String WRITE_SPONSOR_QUERY =
            "INSERT INTO Sponsor (company_Name, amount, event_id) VALUES (?,?,?)";

    // INSERT EXPENSE DETAILS FOR AN EVENT
    static final String WRITE_EXPENSE_QUERY =
            "INSERT INTO Expense (description, Amount, event_id) VALUES (?,?,?)";


// ==================== UPDATE QUERIES ====================

    // UPDATE BOOTH DETAILS
    static final String UPDATE_BOOTH_QUERY =
            "UPDATE Booth SET booth_name = ?, size_sq_ft = ?, cost = ? WHERE booth_id = ?";

    // UPDATE SESSION DETAILS
    static final String UPDATE_SESSION_QUERY =
            "UPDATE Session SET title = ?, duration = ? WHERE session_id = ?";

    // UPDATE SPEAKER DETAILS
    static final String UPDATE_SPEAKER_QUERY =
            "UPDATE Speaker SET name = ?, gender = ? WHERE session_id = ?";

    // UPDATE SPONSOR DETAILS
    static final String UPDATE_SPONSOR_QUERY =
            "UPDATE Sponsor SET company_Name = ?, amount = ? WHERE event_id = ?";

    // UPDATE EXPENSE DETAILS
    static final String UPDATE_EXPENSE_QUERY =
            "UPDATE Expense SET description = ?, Amount = ? WHERE event_id = ?";

    // UPDATE AVAILABLE TICKET COUNT
    static final String UPDATE_TICKET_COUNT_QUERY =
            "UPDATE Events SET Ticket_Count = ? WHERE Event_ID = ?";

    // UPDATE TICKET PRICE
    static final String UPDATE_TICKET_PRICE_QUERY =
            "UPDATE Events SET Ticket_Price = ? WHERE Event_ID = ?";


// ==================== DELETE QUERIES ====================

    // DELETE A BOOTH
    static final String DELETE_BOOTH_QUERY =
            "DELETE FROM Booth WHERE booth_id = ?";

    // DELETE A SESSION
    static final String DELETE_SESSION_QUERY =
            "DELETE FROM Session WHERE session_id = ?";

    // DELETE A SPEAKER
    static final String DELETE_SPEAKER_QUERY =
            "DELETE FROM Speaker WHERE session_id = ?";

    // DELETE A SPONSOR
    static final String DELETE_SPONSOR_QUERY =
            "DELETE FROM Sponsor WHERE event_id = ?";

    // DELETE AN EXPENSE
    static final String DELETE_EXPENSE_QUERY =
            "DELETE FROM Expense WHERE event_id = ?";

    // DELETE AN EVENT
    static final String DELETE_EVENT_QUERY =
            "DELETE FROM Events WHERE Event_ID = ?";


}
