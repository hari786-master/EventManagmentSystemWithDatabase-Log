
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

class Expense  {

    String description;
    double amount;

    Expense(String description, double amount) {
        this.amount = amount;
        this.description = description;
    }

    double totalExpenses() {
        return amount;
    }

    static String displayAllExpenses(int eventNumber , ArrayList<Organizer> organizer , int orgIndex) {
        StringBuilder result = new StringBuilder();

        result.append("╭──────┬───────────────────────────┬─────────────╮\n");
        result.append("│ S.No │ Description               │ Amount      │\n");
        result.append("├──────┼───────────────────────────┼─────────────┤\n");

        ArrayList<Expense> expenses = organizer.get(orgIndex).events
                .get(eventNumber).expenses;

        if (expenses == null || expenses.isEmpty()) {
            result.append("│  --  │ No expenses available     │      --     │\n");
        } else {
            for (int i = 0; i < expenses.size(); i++) {
                Expense e = expenses.get(i);
                String description = e.description;
                String amount = String.format("%.2f", e.amount);

                result.append(String.format("│ %-5d│ %-25s │ %-11s │\n", i + 1, description, amount));

                if (i != expenses.size() - 1) {
                    result.append("├──────┼───────────────────────────┼─────────────┤\n");
                }
            }
        }

        result.append("╰──────┴───────────────────────────┴─────────────╯\n");

        return result.toString();
    }

    void addExpensesDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement expensesQuery = con.prepareStatement(Data.WRITE_EXPENSE_QUERY);
        expensesQuery.setString(1, this.description);
        expensesQuery.setDouble(2, this.amount);
        expensesQuery.setInt(3, eventId);
        expensesQuery.executeUpdate();
    }


    void updateExpensesDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement updateExpensesQuery = con.prepareStatement(Data.UPDATE_EXPENSE_QUERY);
        updateExpensesQuery.setString(1, this.description);
        updateExpensesQuery.setDouble(2, this.amount);
        updateExpensesQuery.setInt(3, eventId);
        updateExpensesQuery.executeUpdate();
    }
    void removeExpensesDb(int eventId) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement removeExpensesQuery = con.prepareStatement(Data.DELETE_EXPENSE_QUERY);
        removeExpensesQuery.setInt(1, eventId);
        removeExpensesQuery.executeUpdate();
    }
}
