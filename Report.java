import java.time.LocalDateTime;
import java.util.ArrayList;

class Report {
    String eventName;
    double totalRevenue;
    double totalExpenses;
    double netProfit;
    int attendeeCount;
    double averageRating;
    ArrayList<String> attendees;
    ArrayList<String> speakers;
    ArrayList<String> sponsors;
    LocalDateTime generatedOn;

    public Report(String eventName, double totalRevenue, double totalExpenses,
            int attendeeCount, double averageRating, ArrayList<String> speakers, ArrayList<String> sponsors) {

        this.eventName = eventName;
        this.totalRevenue = totalRevenue;
        this.totalExpenses = totalExpenses;
        this.netProfit = totalRevenue - totalExpenses;
        this.attendeeCount = attendeeCount;
        this.averageRating = averageRating;
        this.attendees = attendees;
        this.speakers = speakers;
        this.sponsors = sponsors;
        this.generatedOn = LocalDateTime.now();
    }

    String generateReport() {
        String result = "";
        result += "===== Event Report =====" + "\n";
        result += "Event Name: " + eventName + "\n";
        result += "Generated On: " + generatedOn + "\n";
        result += "Total Revenue: $" + totalRevenue + "\n";
        result += "Total Expenses: $" + totalExpenses + "\n";
        result += "Net Profit: $" + netProfit + "\n";
        result += "Attendee Count: " + attendeeCount + "\n";
        result += "Average Rating: " + averageRating + "\n";
        result += "Attendees: " + attendees + "\n";
        result += "Speakers: " + speakers + "\n";
        result += "Sponsors: " + sponsors + "\n";
        result += "========================" + "\n";
        return result;
    }

}
