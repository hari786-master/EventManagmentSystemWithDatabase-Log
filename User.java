import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

class User implements Serializable {
    private static final long serialVersionUID = 2L;
    int userId;
    String name;
    String email;
    String password;
    ArrayList<Ticket> ticket;
    String role;
    int user_Id;

    User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        ticket = new ArrayList<>();
    }

    void setId(int id) {
        this.user_Id = id;
    }

    boolean checkPassword(String pass) {
        return password.equals(pass);
    }

    void addTicket(Ticket t) {
        ticket.add(t);
    }

    static public void writeUser(User u) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement accountQuery = con.prepareStatement(Data.WRITE_ACCOUNT_QUERY, Statement.RETURN_GENERATED_KEYS);
        accountQuery.setString(1, u.name);
        accountQuery.setString(2, u.email);
        accountQuery.setString(3, u.password);
        accountQuery.setString(4, u.role.toUpperCase());
        accountQuery.executeUpdate();
        ResultSet rs = accountQuery.getGeneratedKeys();
        if (rs.next()) {
            u.setId(rs.getInt(1));
        }
    }

}
