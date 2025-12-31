import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController {
    private static final Logger log = LogManager.getLogger(MainController.class);
    ArrayList<Organizer> organizer = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    int userIndex;
    int orgIndex;


    public static void main(String[] args) throws SQLException {
        Scanner input = new Scanner(System.in);
        MainController controller = new MainController();
        controller.readUser(controller);
        Logger logger = LogManager.getLogger();


        //log
        logger.info("====== Event Management System Started ======");
        logger.info("Initialized MainController and loaded data");

        String line = "╭───────────────────────────────╮";
        String bottom = "                                               ╰───────────────────────────────╯";
        start:
        while (true) {
            LocalDate dateInput;
            int roleChoice = 0;
            int userOrOrg = 0;
            //log
            logger.debug("Displaying main menu");

            System.out.println("\n");
            System.out.println(
                    "                                                     \u001B[33m\u001B[1m╭───────────────╮");
            System.out.println("                                                     │               │");
            System.out.println(
                    "                                                     │  \u001B[36m1. Sign In   \u001B[33m\u001B[1m│");
            System.out.println(
                    "                                                     │  \u001B[32m2. Sign Up   \u001B[33m\u001B[1m│");
            System.out.println(
                    "                                                     │  \u001B[31m3. Exit      \u001B[33m\u001B[1m│");
            System.out.println("                                                     │               │");
            System.out.println("                                                     ╰───────────────╯\u001B[0m");
            try {
                System.out.print("Enter Your Choice: ");
                userOrOrg = input.nextInt();
            } catch (InputMismatchException e) {
                //log
                logger.error("Invalid numeric input in main menu choice");
                System.out.println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                input.nextLine();
                continue;
            }

            if (userOrOrg == 1) {

                //log
                logger.info("User selected Sign In option");

                controller.readUser(controller);
                input.nextLine();
                System.out.print("Enter Your Email: ");
                String email = input.nextLine();

                //log
                logger.debug("Login attempt for email: {}", email);

                boolean isValidUser = false;
                for (User u : controller.users) {
                    if (u.email.equalsIgnoreCase(email.toLowerCase())) {
                        System.out.print("Enter Your Password: ");
//                        char c[] = System.console().readPassword();
                        String pas = input.nextLine();
//                        String password = ;
//                        for (char ch : pas.toCharArray()) {
//                            password += (ch + 1);
//                        }
//                        String password = new String(pas);
                        if (BCrypt.checkpw(pas,u.password)) {
                            isValidUser = true;
                            //log
                            logger.info("User authentication successful: {}", email);

                            if (u.role.equalsIgnoreCase("user")) {
                                //log
                                logger.info("User: " + u.name + " Logined Sucessfully");
                                roleChoice = 2;
                                controller.userIndex = controller.users.indexOf(u);
                            } else if (u.role.equalsIgnoreCase("organizer")) {
                                boolean found = false;
                                for (Organizer Org : controller.organizer) {
                                    if (Org.email.equalsIgnoreCase(u.email)) {
                                        controller.orgIndex = controller.organizer.indexOf(Org);
                                        found = true;
                                        //log
                                        logger.debug("Found organizer with ID: {}", Org.org_ID);
                                        break;
                                    }
                                }
                                //log
                                logger.info("Organizer: " + u.name + " Logined Sucessfully");
                                logger.trace(controller.organizer.get(controller.orgIndex).name + " Logined Sucessfully");
                                roleChoice = 1;
                            }
                            break;
                        } else {
                            //log
                            logger.warn("Invalid password for email: {}", email);
                            System.out.println("\u001B[91mInvalid Password\u001B[0m\n");
                            continue start;
                        }
                    }
                }
                if (!isValidUser) {
                    //log
                    logger.warn("Login failed - user not found: {}", email);
                    System.out.println("\u001B[91mInvalid User\u001B[0m\n");
                    continue start;
                }

            } else if (userOrOrg == 2) {
                //log
                logger.info("User selected Sign Up option");
                input.nextLine();
                String name = "";
                String email = "";
                String password = "";
                while (true) {
                    System.out.print("Enter Your Name: ");
                    name = input.nextLine();
                    if (controller.isValidName(name)) {
                        //log
                        logger.debug("Name validation passed: {}", name);
                        break;
                    }
                    //log
                    logger.warn("Name validation failed: {}", name);
                }
                while (true) {
                    System.out.print("Enter Your Email ID: ");
                    email = input.nextLine();
                    for (User u : controller.users) {
                        if (u.email.equalsIgnoreCase(email)) {
                            //log
                            logger.warn("Email already exists during signup: {}", email);
                            System.out.println("\u001B[31m╭─────────────────────────────────────╮\n" +
                                    "│ Email Already Exist. Please Sign In │\n" +
                                    "╰─────────────────────────────────────╯\u001B[0m");
                            continue start;
                        }
                    }
                    if (controller.isValidEmail(email)) {
                        //log
                        logger.debug("Email validation passed: {}", email);
                        break;
                    }
                    //log
                    logger.warn("Email validation failed: {}", email);

                }
                while (true) {
                    System.out.print("Enter The Password: ");
                    password = input.nextLine();
                    if (controller.isValidPassword(password)) {
                        //log
                        logger.debug("Password validation passed");
                        break;
                    }
                    //log
                    logger.warn("Password validation failed");
                }
                String role = "";
                while (true) {
                    System.out.print("Enter The Role (user(1) or organizer(2)) : ");
                    try {
                        roleChoice = input.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                        input.nextLine();
                        continue;
                    }
                    if (roleChoice == 1)
                        role = "user";
                    else if (roleChoice == 2)
                        role = "organizer";
                    if (role.equals("user") || role.equals("organizer")) {
                        //log
                        logger.debug("Role selected: {}", role);
                        break;
                    } else {
                        //log
                        logger.warn("Invalid role selected: {}", roleChoice);
                        System.out.println("\u001B[91mInvalid Role\u001B[0m\n");
                    }
                    //log
                    logger.info("User registration complete - Name: {}, Email: {}, Role: {}",
                            name, email, role);
                }

                // if (role.equals("organizer"))
                // roleChoice = 1;
                // else if (role.equals("user"))
                // roleChoice = 2;

                String pass = BCrypt.hashpw(password,BCrypt.gensalt(10));
//                for (char ch : password.toCharArray()) {
//                    pass += (ch + 1);
//                }

                User user = new User(name, email, pass, role);
                controller.users.add(user);
                //log
                logger.info("New account created successfully: {} ({}) as {}",
                        name, email, role);
                System.out.println(
                        "\n\u001B[92m\u001B[1m" +
                                "╭────────────────────────────────────────────╮\n" +
                                "│        Account Created Successfully!       │\n" +
                                "╰────────────────────────────────────────────╯" +
                                "\u001B[0m");
                User.writeUser(user);
                //log
                logger.debug("User data written to database");
                continue;
            } else if (userOrOrg == 3) {
                //log
                logger.info("User selected Exit option - Application shutting down");

                System.out.println(
                        "\u001B[92m╭───────────────────────────────╮\n" +
                                "│            Thank You          │\n" +
                                "╰───────────────────────────────╯\u001B[0m");
                DbConnection.connection.close();
                //log
                logger.info("Database connection closed successfully");
                logger.info("====== Event Management System Shutdown ======");
                break;
            }

            if (roleChoice == 1) {
                logger.info("Organizer menu accessed by: {}",
                        controller.organizer.get(controller.orgIndex).name);
                int choice = 0;
                eve:
                while (true) {
                    try {
                        //log
                        logger.debug("Displaying organizer menu options");
                        System.out.println(
                                "                                               \u001B[33m\u001B[1m" +
                                        line + "\n" +
                                        "                                               │                               │\n" +
                                        "                                               │        1. View Event          │\n" +
                                        "                                               │        2. Add Event           │\n" +
                                        "                                               │        3. Modify Event        │\n" +
                                        "                                               │        4. Delete Event        │\n" +
                                        "                                               │        5. Logout              │\n" +
                                        "                                               │                               │\n" +
                                        bottom +
                                        "\u001B[0m");
                        System.out.print("Enter Your Choice: ");
                        choice = input.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                        input.nextLine();
                    }
                    if (choice == 1) {
                        //log
                        logger.info("Organizer {} viewing all events",
                                controller.organizer.get(controller.orgIndex).name);
                        logger.debug("Event count: {}",
                                controller.organizer.get(controller.orgIndex).events.size());

                        System.out.println(controller.displayAllEvents());
                        //log
                        logger.trace(controller.organizer.get(controller.orgIndex).name + " Viewed All Events");
                    } else if (choice == 2) {
                        //log
                        logger.info("Organizer {} starting to create new event",
                                controller.organizer.get(controller.orgIndex).name);
                        logger.trace("Organizer Started Creating Event...");
                        input.nextLine();
//                        System.out.print("Enter the Event Name: ");
                        String eventName = controller.isEmptyCheck("Enter the Event Name: ");


                        //log
                        logger.debug("Event name entered: {}", eventName);
                        String location = controller.isEmptyCheck("Enter The Location: ");
                        //log
                        logger.debug("Event Location entered: {}", location);
                        double budget = 0;
                        while (true) {
                            try {
                                System.out.print("Enter Your Budget: ");
                                budget = input.nextDouble();
                                //log
                                logger.debug("Event budget: {}", budget);
                                break;
                            } catch (InputMismatchException e) {
                                //log
                                logger.error("Invalid budget input not a number");
                                System.out.println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                input.nextLine();
                                continue;
                            }
                        }
                        input.nextLine();
                        String category = controller.isEmptyCheck("Enter The Category: ");
                        //log
                        logger.debug("Event Category entered: {}", category);
                        int capacity = 0;
                        while (true) {
                            try {
                                System.out.print("Enter The People Count: ");
                                capacity = input.nextInt();
                                //log
                                logger.debug("Event People Count entered: {}", capacity);
                                input.nextLine();
                                break;
                            } catch (InputMismatchException e) {
                                //log
                                logger.error("Invalid Capacity input not a number");
                                System.out.println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                input.nextLine();
                                continue;
                            }
                        }
                        dateInput = controller.isValidDate(logger);
                        Venue venue = new Venue(location, capacity);
                        Event event = new Event(eventName, budget, venue, category, dateInput);
                        controller.addEvents(event);
                        //log
                        logger.debug("Event object created: {} (Venue: {}, Capacity: {})",
                                eventName, location, capacity);

                        try {
                            event.addEventDb(controller.organizer.get(controller.orgIndex).org_ID);
                            //log
                            logger.info("Event '{}' added to database successfully with ID: {}",
                                    eventName, event.event_ID);
                        } catch (SQLException e) {
                            //log
                            logger.error("Database error while adding event '{}': {}",
                                    eventName, e.getMessage());
                        }
                        //log
                        logger.info("Event creation completed: {}", eventName);
                        System.out.println(
                                "\u001B[92m\u001B[1m" +
                                        "╭────────────────────────────────────╮\n" +
                                        "│      Event Added Successfully :)   │\n" +
                                        "╰────────────────────────────────────╯" +
                                        "\u001B[0m");
                    } else if (choice == 3) {
                        //log
                        logger.info("Organizer {} accessing modify event menu",
                                controller.organizer.get(controller.orgIndex).name);
                        System.out.println(controller.displayAllEvents());
                        if (controller.organizer.get(controller.orgIndex).events.isEmpty()) {
                            //log
                            logger.warn("No events available to modify");
                            continue;
                        }
                        int eventNumber = 0;
                        while (true) {
                            try {
                                System.out.print("Enter The Event Number To Modify (Back:0): ");
                                eventNumber = input.nextInt();
                                if (eventNumber == 0) {
                                    //log
                                    logger.debug("User canceled event modification");
                                    continue eve;
                                }
                                controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1);
                                //log
                                logger.debug("Event selected for modification: {}", eventNumber);
                                break;
                            } catch (Exception e) {
                                //log
                                logger.error("Invalid event number entered: {}", eventNumber);
                                System.out
                                        .println(
                                                "\u001B[91mInvalid Input \u001B[0m\n");
                                input.nextLine();
                                continue;
                            }

                        }
                        while (true) {
                            int eventChoice = 0;
                            while (true) {
                                System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                System.out.println("                                               │                          │");
                                System.out.println("                                               │      \u001B[38m1. Booth            \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[32m2. Session          \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[34m3. Speaker          \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[35m4. Sponsor          \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[36m5. Expense          \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[37m6. Tickets          \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[34m7. Update Event     \u001B[33m\u001B[1m│");
                                System.out.println("                                               │      \u001B[31m8. Back             \u001B[33m\u001B[1m│");
                                System.out.println("                                               │                          │");
                                System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                System.out.print("Enter Your Choice: ");
                                try {
                                    eventChoice = input.nextInt();
                                    break;
                                } catch (InputMismatchException e) {
                                    System.out.println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                    //log
                                    logger.error("Organizer Selected Invalid Input In Booth operation");
                                    input.nextLine();
                                    continue;
                                }
                            }
                            if (eventChoice == 1) {
                                //log
                                logger.debug("Organizer selected booth operations");

                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │      \u001B[38m1. View Booth       \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[32m2. Add Booth        \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[34m3. Update Booth     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[35m4. Delete Booth     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[31m5. Back             \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int boothChoice = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter Your Choice: ");
                                            boothChoice = input.nextInt();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out
                                                    .println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            logger.error("Organizer Entered Invalid Input In Booth Session");
                                            input.nextLine();
                                        }
                                    }
                                    if (boothChoice == 1) {
                                        //log
                                        logger.debug("Viewing all booths for event");
                                        System.out.println(Booth.displayAllBooths(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        //log
                                        logger.trace("Organizer Viewed All Booths");
                                    } else if (boothChoice == 2) {
                                        //log
                                        logger.info("Adding new booth to event");
                                        while (true) {
                                            input.nextLine();
                                            double size = 0;
                                            double cost = 0;

                                            String boothName = controller.isEmptyCheck("Enter The Booth Name: ");
                                            while (true) {
                                                try {
                                                    System.out.print("Enter The Booth Size in Square Feet: ");
                                                    size = input.nextDouble();
                                                    if (size < 1) {
                                                        System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                        //log
                                                        logger.warn("Invalid booth size entered: {}", size);
                                                        continue;
                                                    }
                                                    //log
                                                    logger.debug("Booth size validated: {}", size);
                                                    break;
                                                } catch (InputMismatchException e) {
                                                    System.out
                                                            .println(
                                                                    "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                                    //log
                                                    logger.warn("Invalid booth size entered: {}", size);
                                                    input.nextLine();
                                                    continue;
                                                }
                                            }
                                            while (true) {
                                                try {
                                                    System.out.print("Enter The Booth Cost: ");
                                                    cost = input.nextDouble();
                                                    if (cost < 1) {
                                                        System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                        //log
                                                        logger.warn("Invalid booth cost entered: {}", cost);
                                                        continue;
                                                    }
                                                    //log
                                                    logger.debug("Booth cost validated: {}", cost);
                                                    break;
                                                } catch (InputMismatchException e) {
                                                    System.out
                                                            .println(
                                                                    "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                                    //log
                                                    logger.warn("Invalid booth cost entered: {}", cost);
                                                    input.nextLine();
                                                    continue;
                                                }
                                            }

                                            Booth booth = new Booth(boothName, size, cost);
                                            controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1)
                                                    .addBooth(booth);
                                            booth.addBoothDb(controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1));
                                            //log
                                            logger.info("Booth created successfully: {} (Size: {}, Cost: {})",
                                                    boothName, size, cost);
                                            System.out.println(
                                                    "\u001B[92m\u001B[1m" +
                                                            "╭──────────────────────────────────╮\n" +
                                                            "│      Booth Added Successfully :) │\n" +
                                                            "╰──────────────────────────────────╯" +
                                                            "\u001B[0m");
                                            break;
                                        }
                                    } else if (boothChoice == 3) {
                                        //log
                                        logger.info("Updating existing booth");
                                        ArrayList<Booth> booths = controller.organizer.get(controller.orgIndex).events
                                                .get(eventNumber - 1).booths;

                                        if (booths == null || booths.isEmpty()) {
                                            System.out.println("\u001B[91mNo booths available to update.\u001B[0m");
                                            continue;
                                        }

                                        System.out.println(Booth.displayAllBooths(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        int boothNum = 0;
                                        while (true) {
                                            try {
                                                System.out.print("\nEnter the booth number to update (Back: 0): ");
                                                boothNum = input.nextInt();
                                                if (boothNum == 0) {
                                                    //log
                                                    logger.debug("Booth update cancelled");
                                                    break;
                                                }
                                                if (boothNum < 1
                                                        || boothNum > booths.size()) {
                                                    //log
                                                    logger.warn("Invalid booth number for update: {}", boothNum);
                                                    System.out.println("\u001B[91mInvalid booth number.\u001B[0m");
                                                    continue;
                                                }
                                                //log
                                                logger.debug("Selected booth {} for update", boothNum);
                                                break;
                                            } catch (InputMismatchException e) {
                                                //log
                                                logger.warn("Invalid booth number for update: {}", boothNum);
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m\n");
                                                input.nextLine();
                                            }

                                        }
                                        if (boothNum == 0) {
                                            continue;
                                        }
                                        Booth boothToUpdate = booths.get(boothNum - 1);
                                        input.nextLine();
                                        System.out.println("\nUpdating Booth: " + boothToUpdate.name);
                                        System.out.println("Leave field blank to keep current value.");

                                        System.out.print("Enter new name (" + boothToUpdate.name + "): ");
                                        String newName = input.nextLine();
                                        if (!newName.trim().isEmpty()) {
                                            boothToUpdate.name = newName;
                                        }
                                        double newSize = 0;
                                        while (true) {
                                            System.out.print("Enter new size in sq.ft (" + boothToUpdate.size + "): ");
                                            String sizeStr = input.nextLine();
                                            if (sizeStr.trim().isEmpty())
                                                break;
                                            try {
                                                newSize = Double.parseDouble(sizeStr);
                                                if (newSize < 1) {
                                                    //log
                                                    logger.error("Entered Invalid Booth Size : {}", newSize);
                                                    System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                    continue;
                                                }
                                                //log
                                                logger.debug("Booth size updated from {} to {}",
                                                        boothToUpdate.size, newSize);
                                                boothToUpdate.size = newSize;
                                                break;
                                            } catch (NumberFormatException e) {
                                                //log
                                                logger.error("Entered Invalid Booth Size : {}", newSize);
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m\n");
                                            }
                                        }
                                        double newCost = 0;
                                        while (true) {
                                            System.out.print("Enter new cost (" + boothToUpdate.price + "): ");
                                            String costStr = input.nextLine();
                                            if (costStr.trim().isEmpty())
                                                break;
                                            try {
                                                newCost = Double.parseDouble(costStr);
                                                if (newCost < 1) {
                                                    //log
                                                    logger.error("Entered Invalid Booth Cost : {}", newCost);
                                                    System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                    continue;
                                                }
                                                boothToUpdate.price = newCost;
                                                break;
                                            } catch (NumberFormatException e) {
                                                //log
                                                logger.error("Entered Invalid Booth Cost : {}", newCost);
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m\n");
                                            }
                                        }
                                        boothToUpdate.updateBoothDb();
                                        //log
                                        logger.info("Booth '{}' updated in database", boothToUpdate.name);
                                        logger.info("Booth {} Updated Sucessfully", boothToUpdate.name);
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭──────────────────────────────────╮\n" +
                                                        "│     Booth Updated Successfully!  │\n" +
                                                        "╰──────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (boothChoice == 4) {
                                        //log
                                        logger.info("Deleting booth from event");
                                        ArrayList<Booth> booths = controller.organizer
                                                .get(controller.orgIndex).events
                                                .get(eventNumber - 1).booths;

                                        if (booths == null || booths.isEmpty()) {
                                            System.out.println("\u001B[91mNo booths available to delete.\u001B[0m");
                                            continue;
                                        }

                                        System.out.println(Booth.displayAllBooths(eventNumber - 1, controller.organizer, controller.orgIndex));

                                        int boothNum = 0;
                                        while (true) {
                                            try {
                                                System.out.print("Enter the booth number to delete (Back: 0): ");
                                                boothNum = input.nextInt();
                                                if (boothNum == 0) {
                                                    //log
                                                    logger.debug("Booth deletion cancelled");
                                                    break;
                                                }
                                                if (boothNum < 1 || boothNum > booths.size()) {
                                                    //log
                                                    logger.warn("Invalid booth number for deletion: {}", boothNum);
                                                    System.out.println("\u001B[91mInvalid booth number.\u001B[0m");
                                                    continue;
                                                }
                                                //log
                                                logger.debug("Selected booth {} for deletion", boothNum);
                                                break;
                                            } catch (InputMismatchException e) {
                                                //log
                                                logger.error("Invalid booth number for deletion: {}", boothNum);
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m\n");
                                                input.nextLine();
                                            }
                                        }

                                        if (boothNum == 0) {
                                            continue;
                                        }

                                        input.nextLine();
                                        Booth removedBooth = booths.get(boothNum - 1);
                                        System.out.print(
                                                "Are you sure you want to delete '" + removedBooth.name + "'? (y/n): ");
                                        String confirm = input.nextLine().trim().toLowerCase();

                                        if (confirm.equals("y") || confirm.equals("yes")) {
                                            booths.remove(boothNum - 1);
                                            removedBooth.removeBoothDb();
                                            //log
                                            logger.warn("Booth '{}' deleted from event", removedBooth.name);

                                            System.out.println(
                                                    "\u001B[91m\u001B[1m" +
                                                            "╭──────────────────────────────────────────────╮\n" +
                                                            "│   Booth '" + removedBooth.name
                                                            + "' deleted successfully!\n" +
                                                            "╰──────────────────────────────────────────────╯" +
                                                            "\u001B[0m");
                                        } else {
                                            //log
                                            logger.info("Booth deletion cancelled");
                                            System.out.println(" Deletion cancelled.");
                                        }
                                    } else if (boothChoice == 5) {
                                        logger.trace("Organizer Exited From Booth");
                                        break;
                                    } else {
                                        logger.error("Invalid Options on Booth options");
                                        System.out.println("\u001B[91mInvalid input\u001B[0m");
                                    }
                                }
                            } else if (eventChoice == 2) {
                                //log
                                logger.debug("Organizer selected session operations");
                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │      \u001B[38m1. View Session     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[32m2. Add Session      \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[34m3. Update Session   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[35m4. Delete Session   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[31m5. Back             \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int sessionChoice = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter Your Choice: ");
                                            sessionChoice = input.nextInt();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out
                                                    .println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }
                                    if (sessionChoice == 1) {
                                        //log
                                        logger.info("Viewing All Sessions");
                                        System.out.println(Session.displayAllSession(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        //log
                                        logger.info("Viewed All Sessions");
                                    } else if (sessionChoice == 2) {
                                        input.nextLine();
                                        //log
                                        logger.info("Adding new session to event");
                                        ArrayList<Session> existingSessions = controller.organizer
                                                .get(controller.orgIndex).events
                                                .get(eventNumber - 1).schedule;

                                        double nextTime = 9;
                                        String period = "AM";

                                        if (existingSessions != null && !existingSessions.isEmpty()) {
                                            Session lastSession = existingSessions.get(existingSessions.size() - 1);

                                            String lastTimeRange = lastSession.timeRange;

                                            String endTimeStr = lastTimeRange.split(" to ")[1];

                                            String[] parts = endTimeStr.trim().split(" ");
                                            nextTime = Double.parseDouble(parts[0].split(":")[0]);
                                            period = parts[1].toUpperCase();
                                        }

                                        DecimalFormat df = new DecimalFormat("0.00");
                                        par:
                                        while (true) {
                                            String session = controller.isEmptyCheck("Enter The Program : ");

                                            while (true) {
                                                try {
                                                    System.out.print("Enter Duration (hours)(Max:3 hr): ");
                                                    int totalHour = input.nextInt();
                                                    if (totalHour > 3 || totalHour < 1) {
                                                        System.out.println(
                                                                "\u001B[91mInvalid input. Please enter a Correct Time \u001B[0m\n");
                                                        continue;
                                                    }
                                                    input.nextLine();

                                                    double endTime = nextTime + totalHour;
                                                    String endPeriod = period;

                                                    if (endTime >= 12) {
                                                        if (endTime > 12)
                                                            endTime -= 12;
                                                        endPeriod = period.equals("AM") ? "PM" : "AM";
                                                    }

                                                    String startFormatted = String.format("%.0f:00 %s", nextTime,
                                                            period);
                                                    String endFormatted = String.format("%.0f:00 %s", endTime,
                                                            endPeriod);

                                                    String timeRange = startFormatted + " to " + endFormatted;
                                                    Session ses = new Session(session, timeRange);
                                                    controller.organizer.get(controller.orgIndex).events
                                                            .get(eventNumber - 1).schedule
                                                            .add(ses);
                                                    ses.addSessionDb(controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1).event_ID);
                                                    nextTime = endTime;
                                                    period = endPeriod;
                                                    //log
                                                    logger.info("Session '{}' added to event", session);
                                                    break;
                                                } catch (InputMismatchException e) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                                    input.nextLine();
                                                }
                                            }

                                            while (true) {
                                                System.out.print("\nDo You Want To Add Extra Program(y/n): ");
                                                String yesOrNo = input.nextLine().trim().toLowerCase();
                                                if (yesOrNo.equals("y") || yesOrNo.equals("yes"))
                                                    continue par;
                                                else if (yesOrNo.equals("n") || yesOrNo.equals("no")) {
                                                    System.out.println(
                                                            "\u001B[92m\u001B[1m" +
                                                                    "╭────────────────────────────────────╮\n" +
                                                                    "│      Sessions Added Successfully   │\n" +
                                                                    "╰────────────────────────────────────╯" +
                                                                    "\u001B[0m");
                                                    break par;
                                                } else
                                                    System.out.println("\u001B[91mInvalid input\u001B[0m");
                                            }
                                        }
                                    } else if (sessionChoice == 3) {
                                        //log
                                        logger.info("Updating existing session");
                                        ArrayList<Session> sessions = controller.organizer
                                                .get(controller.orgIndex).events.get(eventNumber - 1).schedule;

                                        if (sessions.isEmpty()) {
                                            System.out.println(
                                                    "\u001B[91m╭────────────────────────────────────╮\n" +
                                                            "│No sessions available               │\n" +
                                                            "╰────────────────────────────────────╯\u001B[0m\n");
                                            continue;
                                        }
                                        System.out.println(Session.displayAllSession(eventNumber - 1, controller.organizer, controller.orgIndex));

                                        int updateIndex = 0;

                                        while (true) {
                                            try {
                                                System.out.print("Enter the Session Number to Update (Back:0): ");
                                                updateIndex = input.nextInt();
                                                input.nextLine(); // Consume newline
                                                if (updateIndex == 0)
                                                    break;
                                                if (updateIndex < 1 || updateIndex > sessions.size()) {
                                                    System.out.println("\u001B[91m╭────────────────────────────╮\n" +
                                                            "│Invalid session number      │\n" +
                                                            "╰────────────────────────────╯\u001B[0m\n");
                                                    continue;
                                                }
                                                break;
                                            } catch (InputMismatchException e) {
                                                System.out
                                                        .println("\u001B[91m╭──────────────────────────────────────╮\n" +
                                                                "│ Invalid input. Please enter a number │\n" +
                                                                "╰──────────────────────────────────────╯\u001B[0m\n");
                                                input.nextLine();
                                            }
                                        }

                                        if (updateIndex == 0)
                                            continue;

                                        Session selectedSession = sessions.get(updateIndex - 1);

                                        int updateChoice = 0;
                                        while (true) {
                                            try {
                                                System.out.println("\nWhat do you want to update?");
                                                System.out.println("1. Topic");
                                                System.out.println("2. Time Range");
                                                System.out.println("3. Both");
                                                System.out.print("Enter your choice: ");
                                                updateChoice = input.nextInt();
                                                input.nextLine(); // Consume newline
                                                if (updateChoice < 1 || updateChoice > 3) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid choice. Enter 1, 2, or 3.\u001B[0m");
                                                    continue;
                                                }
                                                break;
                                            } catch (InputMismatchException e) {
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                                input.nextLine();
                                            }
                                        }

                                        if (updateChoice == 1 || updateChoice == 3) {
                                            String newTopic = controller.isEmptyCheck("Enter new topic name: ");
                                            if (!newTopic.isEmpty()) {
                                                selectedSession.topic = newTopic;
                                                selectedSession.updateSessionhDb();
                                                System.out.println(
                                                        "\u001B[92m╭────────────────────────────────────────╮\n" +
                                                                "│   Session Updated Successfully !       │\n" +
                                                                "╰────────────────────────────────────────╯\u001B[0m");
                                            } else {
                                                //log
                                                logger.debug("Session topic update cancelled - empty input");
                                                System.out.println("Topic name not changed.");
                                            }
                                        }

                                        if (updateChoice == 2 || updateChoice == 3) {
                                            double eventStart = 9.0;
                                            double eventEnd = 18.0;
                                            while (true) {
                                                try {
                                                    System.out
                                                            .print("Enter new start time (e.g., 9:00 AM or 1:00 PM): ");
                                                    String startTimeStr = input.nextLine();
                                                    System.out
                                                            .print("Enter new end time (e.g., 11:00 AM or 3:00 PM): ");
                                                    String endTimeStr = input.nextLine();

                                                    double start = parseTime(startTimeStr);
                                                    double end = parseTime(endTimeStr);

                                                    if (start < eventStart || end > eventEnd) {
                                                        System.out.println(
                                                                "\u001B[91m╭───────────────────────────────────────────────╮\n"
                                                                        +
                                                                        "│ Time must be within event hours (9 AM - 6 PM) │\n"
                                                                        +
                                                                        "╰───────────────────────────────────────────────╯\u001B[0m");
                                                        continue;
                                                    }

                                                    if (end <= start) {
                                                        System.out.println(
                                                                "\u001B[91m╭────────────────────────────────────────────╮\n"
                                                                        +
                                                                        "│ End time must be after start time!         │\n"
                                                                        +
                                                                        "╰────────────────────────────────────────────╯\u001B[0m");
                                                        continue;
                                                    }

                                                    boolean conflict = false;
                                                    for (Session s : sessions) {
                                                        if (s != selectedSession) {
                                                            String[] times = s.timeRange.split(" to ");
                                                            double existingStart = parseTime(times[0]);
                                                            double existingEnd = parseTime(times[1]);

                                                            if (start < existingEnd && end > existingStart) {
                                                                conflict = true;
                                                                break;
                                                            }
                                                        }
                                                    }

                                                    if (conflict) {
                                                        System.out.println(
                                                                "\u001B[91m╭────────────────────────────────────────────────╮\n"
                                                                        +
                                                                        "│ Time overlaps with another session. Try again! │\n"
                                                                        +
                                                                        "╰────────────────────────────────────────────────╯\u001B[0m");
                                                        continue;
                                                    }

                                                    String startFormatted = formatTime(start);

                                                    String endFormatted = formatTime(end);
                                                    selectedSession.timeRange = startFormatted + " to " + endFormatted;
                                                    selectedSession.updateSessionhDb();
                                                    System.out.println(
                                                            "\u001B[92m╭────────────────────────────────────────╮\n" +
                                                                    "│   Session Updated Successfully !       │\n" +
                                                                    "╰────────────────────────────────────────╯\u001B[0m");
                                                    break;

                                                } catch (Exception e) {
                                                    System.out.println(
                                                            "\u001B[91m╭──────────────────────────────────────────╮\n" +
                                                                    "│ Invalid input. Please enter a valid time │\n" +
                                                                    "╰──────────────────────────────────────────╯\u001B[0m");

                                                }
                                            }
                                        }
                                    } else if (sessionChoice == 4) {
                                        //log
                                        logger.info("Deleting session from event");
                                        ArrayList<Session> sessions = controller.organizer
                                                .get(controller.orgIndex).events.get(eventNumber - 1).schedule;

                                        if (sessions == null || sessions.isEmpty()) {
                                            System.out.println("\u001B[91mNo sessions available to delete!\u001B[0m");
                                            continue;
                                        }

                                        System.out.println(Session.displayAllSession(eventNumber - 1, controller.organizer, controller.orgIndex));

                                        int deleteIndex = -1;

                                        while (true) {
                                            try {
                                                System.out.print("Enter S.No of the session to delete: ");
                                                deleteIndex = input.nextInt() - 1;
                                                input.nextLine();

                                                if (deleteIndex < 0 || deleteIndex >= sessions.size()) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid S.No. Please enter a valid number.\u001B[0m");
                                                    continue;
                                                }

                                                Session selected = sessions.get(deleteIndex);

                                                String confirm = "";
                                                while (true) {
                                                    System.out.print("Are you sure you want to delete \""
                                                            + selected.topic + "\"? (y/n): ");
                                                    confirm = input.nextLine().trim().toLowerCase();

                                                    if (confirm.equals("y") || confirm.equals("n"))
                                                        break;

                                                    System.out
                                                            .println("\u001B[91mInvalid input. Enter y or n.\u001B[0m");
                                                }

                                                if (confirm.equals("n")) {
                                                    System.out.println("\u001B[93mDeletion canceled.\u001B[0m");
                                                    break;
                                                }

                                                Session removed = sessions.remove(deleteIndex);
                                                removed.removeSessionDb();
                                                System.out.println(
                                                        "\u001B[92m\u001B[1m" +
                                                                "╭──────────────────────────────────────────────╮\n" +
                                                                "│  Session \"" + removed.topic
                                                                + "\" deleted successfully!  \n" +
                                                                "╰──────────────────────────────────────────────╯" +
                                                                "\u001B[0m");

                                                double nextTime = 9;
                                                String period = "AM";

                                                for (Session s : sessions) {
                                                    String[] times = s.timeRange.split(" to ");
                                                    double startHour = Double
                                                            .parseDouble(times[0].split(":")[0].trim());
                                                    double endHour = Double.parseDouble(times[1].split(":")[0].trim());
                                                    double duration = endHour - startHour;

                                                    if (duration <= 0)
                                                        duration += 12;

                                                    double endTime = nextTime + duration;
                                                    String endPeriod = period;

                                                    if (endTime >= 12) {
                                                        if (endTime > 12)
                                                            endTime -= 12;
                                                        endPeriod = period.equals("AM") ? "PM" : "AM";
                                                    }

                                                    s.timeRange = String.format("%.0f:00 %s to %.0f:00 %s", nextTime,
                                                            period, endTime, endPeriod);

                                                    nextTime = endTime;
                                                    period = endPeriod;
                                                    s.updateSessionhDb();
                                                }

                                                break;

                                            } catch (InputMismatchException e) {
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                                input.nextLine();
                                            }
                                        }
                                    } else if (sessionChoice == 5) {
                                        break;
                                    } else {
                                        System.out.println(
                                                "\u001B[91mInvalid input \u001B[0m");
                                    }

                                }
                            } else if (eventChoice == 3) {
                                //log
                                logger.debug("Organizer selected speaker operations");

                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │      \u001B[38m1. View Speaker     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[32m2. Add Speaker      \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[34m3. Update Speaker   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[35m4. Delete Speaker   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[31m5. Back             \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int speakerChoice = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter Your Choice: ");
                                            speakerChoice = input.nextInt();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out
                                                    .println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }
                                    ArrayList<Session> sess = controller.organizer
                                            .get(controller.orgIndex).events
                                            .get(eventNumber - 1).schedule;
                                    if (speakerChoice == 1) {
                                        //log
                                        logger.info("Viewing All Speaker");
                                        System.out.println(Speaker.displayAllSpeakers(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        //log
                                        logger.info("Viewed All Speaker");

                                    } else if (speakerChoice == 2) {
                                        //log
                                        logger.info("Adding speaker to session");
                                        if (sess == null || sess.isEmpty()) {
                                            System.out.println(
                                                    "\u001B[91mNo sessions available to Add Speakers!\u001B[0m");
                                            continue;
                                        }

                                        input.nextLine();
                                        int secNumber = 0;
                                        System.out.println(Session.displayAllSession(eventNumber - 1, controller.organizer, controller.orgIndex));

                                        while (true) {
                                            try {
                                                System.out.print("Enter the Session Number To Add Speaker: ");
                                                secNumber = input.nextInt();

                                                int sessionCount = controller.organizer.get(controller.orgIndex).events
                                                        .get(eventNumber - 1).schedule.size();

                                                if (secNumber >= 1 && secNumber <= sessionCount) {
                                                    break;
                                                } else {
                                                    System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                                                }
                                            } catch (InputMismatchException e) {
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a number.\u001B[0m\n");
                                                input.nextLine();
                                            }
                                        }

                                        while (true) {

                                            input.nextLine();
                                            String speakerName = controller.isEmptyCheck("Enter The Speaker Name: ");
                                            String gender = "";

                                            while (true) {
                                                try {
                                                    System.out.print("Gender(Male(1) or Female(2)): ");
                                                    int genderChoice = input.nextInt();

                                                    if (genderChoice == 1) {
                                                        gender = "Male";
                                                        break;
                                                    } else if (genderChoice == 2) {
                                                        gender = "Female";
                                                        break;
                                                    } else {
                                                        System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                                                        input.nextLine();
                                                        continue;
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid input. Please enter a number.\u001B[0m\n");
                                                    input.nextLine();
                                                    continue;
                                                }
                                            }
                                            if (controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1).schedule.get(secNumber - 1).speaker == null) {
                                                Speaker speaker = new Speaker(speakerName, gender);
                                                System.out.println(controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1).schedule.get(secNumber - 1).speaker);
                                                controller.organizer.get(controller.orgIndex).events
                                                        .get(eventNumber - 1).schedule
                                                        .get(secNumber - 1)
                                                        .assignSpeaker(speaker);

                                                if (!controller.organizer.get(controller.orgIndex).events
                                                        .get(eventNumber - 1).speakers.contains(speaker)) {

                                                    controller.organizer.get(controller.orgIndex).events
                                                            .get(eventNumber - 1).speakers.add(speaker);


                                                    speaker.addSpeakerDb(controller.organizer.get(controller.orgIndex).events
                                                            .get(eventNumber - 1).event_ID, controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1).schedule.get(secNumber - 1));

                                                    //log
                                                    logger.info("Speaker '{}' assigned to session", speakerName);
                                                    System.out.println(
                                                            "\u001B[92m\u001B[1m" +
                                                                    "╭────────────────────────────────────╮\n" +
                                                                    "│      Speaker Added Successfully    │\n" +
                                                                    "╰────────────────────────────────────╯" +
                                                                    "\u001B[0m");
                                                }
                                            } else {
                                                System.out.println(
                                                        "\u001B[91m\u001B[1m" +
                                                                "╭────────────────────────────────────────────────────╮\n" +
                                                                "│  Session already has a Speaker                     │\n" +
                                                                "│  Please go to Update Speaker                       │\n" +
                                                                "╰────────────────────────────────────────────────────╯" +
                                                                "\u001B[0m"
                                                );

                                            }
                                            break;

                                        }
                                    } else if (speakerChoice == 3) {

                                        while (true) {

                                            if (sess == null || sess.isEmpty()) {
                                                System.out.println(
                                                        "\u001B[91mNo sessions available to Update Speakers!\u001B[0m");
                                                break;
                                            }

                                            input.nextLine();
                                            System.out.println(Session.displayAllSession(eventNumber - 1, controller.organizer, controller.orgIndex));

                                            int secNumber = 0;
                                            int sessionCount = controller.organizer.get(controller.orgIndex).events
                                                    .get(eventNumber - 1).schedule.size();

                                            while (true) {
                                                try {
                                                    System.out.print("Enter the Session Number To Update Speaker: ");
                                                    secNumber = input.nextInt();

                                                    if (secNumber >= 1 && secNumber <= sessionCount) {
                                                        break;
                                                    } else {
                                                        System.out
                                                                .println("\u001B[91mInvalid session number.\u001B[0m");
                                                    }

                                                } catch (InputMismatchException e) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                                    input.nextLine();
                                                }
                                            }

                                            Session session = controller.organizer.get(controller.orgIndex).events
                                                    .get(eventNumber - 1).schedule.get(secNumber - 1);

                                            if (session.speaker != null) {
                                                System.out.println("Current Speaker: " + session.speaker.name + " ("
                                                        + session.speaker.gender + ")");
                                            } else {
                                                System.out.println("No speaker assigned yet.");
                                                break;
                                            }

                                            input.nextLine();
                                            String speakerName = controller.isEmptyCheck("Enter New Speaker Name: ");

                                            String gender = "";
                                            while (true) {
                                                try {
                                                    System.out.print("Enter New Gender (Male=1, Female=2): ");
                                                    int genderChoice = input.nextInt();

                                                    if (genderChoice == 1) {
                                                        gender = "Male";
                                                        break;
                                                    } else if (genderChoice == 2) {
                                                        gender = "Female";
                                                        break;
                                                    } else {
                                                        System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                    }

                                                } catch (InputMismatchException e) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                                    input.nextLine();
                                                }
                                            }
                                            ArrayList<Speaker> eventSpeakers = controller.organizer
                                                    .get(controller.orgIndex).events.get(eventNumber - 1).speakers;

                                            eventSpeakers.remove(session.speaker);
                                            Speaker newSpeaker = new Speaker(speakerName, gender);
                                            session.assignSpeaker(newSpeaker);


                                            if (!eventSpeakers.contains(newSpeaker)) {
                                                eventSpeakers.add(newSpeaker);
                                            }
                                            newSpeaker.updateSpeakerDb(session);
                                            //log
                                            logger.info("Speaker updated from '{}' to '{}' for session '{}'",
                                                    session.speaker != null ? session.speaker.name : "None",
                                                    speakerName,
                                                    session.topic);
                                            System.out.println(
                                                    "\u001B[92m\u001B[1m" +
                                                            "╭────────────────────────────────────╮\n" +
                                                            "│    Speaker Updated Successfully!   │\n" +
                                                            "╰────────────────────────────────────╯" +
                                                            "\u001B[0m");

                                            break;
                                        }
                                    } else if (speakerChoice == 4) {
                                        if (sess == null || sess.isEmpty()) {
                                            System.out.println(
                                                    "\u001B[91mNo sessions available to Add Speakers!\u001B[0m");
                                            continue;
                                        }
                                        input.nextLine();
                                        System.out.println(Session.displayAllSession(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        System.out.print("Enter the Session Number To Delete Speaker: ");
                                        try {
                                            int secNumber = input.nextInt();
                                            Session session = controller.organizer.get(controller.orgIndex).events
                                                    .get(eventNumber - 1).schedule.get(secNumber - 1);
                                            System.out.print(
                                                    "Are you sure you want to delete '" + session.speaker.name
                                                            + "'? (y/n): ");
                                            input.nextLine();
                                            String confirm = input.nextLine().trim().toLowerCase();

                                            if (confirm.equals("y") || confirm.equals("yes")) {

                                                if (session.speaker != null) {
                                                    System.out
                                                            .println("Removing Speaker: " + session.speaker.name + " ("
                                                                    + session.speaker.gender + ")");

                                                    Speaker removedSpeaker = session.speaker;
                                                    session.assignSpeaker(null);

                                                    ArrayList<Speaker> eventSpeakers = controller.organizer
                                                            .get(controller.orgIndex).events
                                                            .get(eventNumber - 1).speakers;

                                                    eventSpeakers.remove(removedSpeaker);
                                                    removedSpeaker.removeSpeakerDb(session);
                                                    System.out.println(
                                                            "\u001B[91m\u001B[1m" +
                                                                    "╭────────────────────────────────────╮\n" +
                                                                    "│   Speaker Deleted Successfully!    │\n" +
                                                                    "╰────────────────────────────────────╯" +
                                                                    "\u001B[0m");
                                                } else {
                                                    System.out.println(
                                                            "\n\u001B[93mNo speaker assigned to this session.\u001B[0m\n");
                                                }
                                            } else {
                                                System.out.println(
                                                        "\u001B[93m\u001B[1m" +
                                                                "╭────────────────────────────╮\n" +
                                                                "│       Deletion Cancelled    │\n" +
                                                                "╰────────────────────────────╯" +
                                                                "\u001B[0m");
                                            }

                                        } catch (Exception e) {
                                            System.out
                                                    .println("\n\u001B[91mInvalid input. Please try again.\u001B[0m\n");
                                            input.nextLine();
                                        }
                                    } else if (speakerChoice == 5) {
                                        break;
                                    } else {
                                        System.out.println("\n\u001B[91mInvalid input\u001B[0m\n");
                                    }
                                }
                            } else if (eventChoice == 4) {
                                //log
                                logger.debug("Organizer selected sponsor operations");

                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │      \u001B[38m1. View Sponsor     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[32m2. Add Sponsor      \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[34m3. Update Sponsor   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[35m4. Delete Sponsor   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[31m5. Back             \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int sponsorChoice = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter Your Choice: ");
                                            sponsorChoice = input.nextInt();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out
                                                    .println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }
                                    if (sponsorChoice == 1) {
                                        //log
                                        logger.trace("Viewing All Sponsor");
                                        System.out.println(Sponsor.displayAllSponsor(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        //log
                                        logger.info("Viewed All Sponsors");
                                    } else if (sponsorChoice == 2) {
                                        //log
                                        logger.info("Adding sponsor to event");
                                        input.nextLine();
                                        String companyName = controller.isEmptyCheck("Enter The Company Name: ");
                                        double amountSponsor = 0;
                                        while (true) {
                                            try {
                                                System.out.print("Enter The Amount To Sponsor: ");
                                                amountSponsor = input.nextDouble();
                                                if (amountSponsor < 1) {
                                                    System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                    continue;
                                                }
                                                break;
                                            } catch (InputMismatchException e) {
                                                System.out
                                                        .println(
                                                                "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                                input.nextLine();
                                                continue;
                                            }
                                        }
                                        Sponsor sponsor = new Sponsor(companyName, amountSponsor);
                                        controller.organizer.get(controller.orgIndex).events
                                                .get(eventNumber - 1).sponsors
                                                .add(sponsor);
                                        sponsor.addSponsorDb(controller.organizer.get(controller.orgIndex).events
                                                .get(eventNumber - 1).event_ID);
                                        //log
                                        logger.info("Sponsor '{}' added with amount ${}", companyName, amountSponsor);

                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────╮\n" +
                                                        "│      Sponsor Added Successfully    │\n" +
                                                        "╰────────────────────────────────────╯" +
                                                        "\u001B[0m");

                                    } else if (sponsorChoice == 3) {
                                        ArrayList<Sponsor> sponsors = controller.organizer
                                                .get(controller.orgIndex).events
                                                .get(eventNumber - 1).sponsors;

                                        if (sponsors == null || sponsors.isEmpty()) {
                                            System.out.println("\u001B[91mNo sponsors available to update!\u001B[0m");
                                        } else {
                                            System.out.println(Sponsor.displayAllSponsor(eventNumber - 1, controller.organizer, controller.orgIndex));

                                            System.out.print("Enter the Sponsor Number to Update: ");
                                            int sponsorNumber = -1;
                                            try {
                                                sponsorNumber = input.nextInt();
                                                input.nextLine();
                                                if (sponsorNumber < 1 || sponsorNumber > sponsors.size()) {
                                                    System.out.println("\u001B[91mInvalid Sponsor Number!\u001B[0m");
                                                } else {
                                                    Sponsor selectedSponsor = sponsors.get(sponsorNumber - 1);

                                                    System.out.print("Enter New Company Name (leave blank to keep '"
                                                            + selectedSponsor.companyName + "'): ");
                                                    String companyName = input.nextLine();
                                                    if (!companyName.trim().isEmpty()) {
                                                        selectedSponsor.companyName = companyName;
                                                    }

                                                    while (true) {
                                                        try {
                                                            System.out.print("Enter New Sponsorship Amount (current: "
                                                                    + selectedSponsor.amountSponsor + "): ");
                                                            String amountInput = input.nextLine();
                                                            if (!amountInput.trim().isEmpty()) {
                                                                double amountSponsor = Double.parseDouble(amountInput);
                                                                if (amountSponsor < 1) {
                                                                    System.out.println(
                                                                            "\u001B[91mInvalid input.\u001B[0m");
                                                                    continue;
                                                                }
                                                                selectedSponsor.amountSponsor = amountSponsor;
                                                            }
                                                            break;
                                                        } catch (NumberFormatException e) {
                                                            System.out.println(
                                                                    "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                                        }
                                                    }

                                                    selectedSponsor.updateSponsorDb(controller.organizer
                                                            .get(controller.orgIndex).events
                                                            .get(eventNumber - 1).event_ID);
                                                    System.out.println(
                                                            "\u001B[92m\u001B[1m" +
                                                                    "╭────────────────────────────────────╮\n" +
                                                                    "│     Sponsor Updated Successfully   │\n" +
                                                                    "╰────────────────────────────────────╯" +
                                                                    "\u001B[0m");
                                                }
                                            } catch (InputMismatchException e) {
                                                System.out.println(
                                                        "\u001B[91mInvalid input. Please enter a valid number.\u001B[0m");
                                                input.nextLine();
                                            }
                                        }

                                    } else if (sponsorChoice == 4) {

                                        ArrayList<Sponsor> sponsors = controller.organizer
                                                .get(controller.orgIndex).events
                                                .get(eventNumber - 1).sponsors;

                                        if (sponsors == null || sponsors.isEmpty()) {
                                            System.out.println("\u001B[91mNo sponsors available to delete!\u001B[0m");
                                        } else {

                                            System.out.println(Sponsor.displayAllSponsor(eventNumber - 1, controller.organizer, controller.orgIndex));

                                            while (true) {
                                                try {
                                                    System.out.print("Enter the Sponsor Number to Delete: ");
                                                    int sponsorNumber = input.nextInt();
                                                    input.nextLine();

                                                    if (sponsorNumber < 1 || sponsorNumber > sponsors.size()) {
                                                        System.out
                                                                .println("\u001B[91mInvalid Sponsor Number!\u001B[0m");
                                                        continue;
                                                    }

                                                    Sponsor selected = sponsors.get(sponsorNumber - 1);

                                                    String confirm = "";
                                                    while (true) {
                                                        System.out.print("Are you sure you want to delete '"
                                                                + selected.companyName + "'? (y/n): ");
                                                        confirm = input.nextLine().trim().toLowerCase();

                                                        if (confirm.equals("y") || confirm.equals("n"))
                                                            break;

                                                        System.out.println(
                                                                "\u001B[91mInvalid input. Enter y or n.\u001B[0m");
                                                    }

                                                    if (confirm.equals("n")) {
                                                        System.out.println("\u001B[93mDeletion canceled.\u001B[0m");
                                                        break;
                                                    }

                                                    Sponsor removedSponsor = sponsors.remove(sponsorNumber - 1);
                                                    removedSponsor.removeSponsorDb(controller.organizer
                                                            .get(controller.orgIndex).events
                                                            .get(eventNumber - 1).event_ID);
                                                    System.out.println(
                                                            "\u001B[92m\u001B[1m" +
                                                                    "╭──────────────────────────────────────────────╮\n"
                                                                    +
                                                                    "│  Sponsor '" + removedSponsor.companyName
                                                                    + "' deleted successfully!  │\n" +
                                                                    "╰──────────────────────────────────────────────╯" +
                                                                    "\u001B[0m");

                                                    break;

                                                } catch (InputMismatchException e) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid input. Please enter a valid number.\u001B[0m");
                                                    input.nextLine();
                                                }
                                            }
                                        }
                                    } else if (sponsorChoice == 5) {
                                        break;
                                    } else {
                                        System.out.println(
                                                "\u001B[91mInvalid input\u001B[0m");
                                    }
                                }
                            } else if (eventChoice == 5) {
                                //log
                                logger.debug("Organizer selected expense operations");

                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │      \u001B[38m1. View Expense     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[32m2. Add Expense      \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[34m3. Update Expense   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[35m4. Delete Expense   \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │      \u001B[31m5. Back             \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int expenseChoice = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter Your Choice: ");
                                            expenseChoice = input.nextInt();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out
                                                    .println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }
                                    double amount = 0;
                                    if (expenseChoice == 1) {
                                        //log
                                        logger.trace("Viewing All Expenses");
                                        System.out.println(Expense.displayAllExpenses(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        //log
                                        logger.trace("Viewed All Expenses");

                                    } else if (expenseChoice == 2) {
                                        input.nextLine();
                                        // log
                                        logger.info("Adding expense to event");
                                        String description = controller.isEmptyCheck("Enter The Description: ");
                                        while (true) {
                                            try {
                                                System.out.print("Enter The Expenses : ");
                                                amount = input.nextInt();
                                                if (amount < 1) {
                                                    System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                    continue;
                                                }
                                                break;
                                            } catch (InputMismatchException e) {
                                                System.out
                                                        .println(
                                                                "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                                input.nextLine();
                                                continue;
                                            }
                                        }
                                        Expense expense = new Expense(description, amount);
                                        controller.organizer.get(controller.orgIndex).events
                                                .get(eventNumber - 1).expenses
                                                .add(expense);
                                        expense.addExpensesDb(controller.organizer.get(controller.orgIndex).events
                                                .get(eventNumber - 1).event_ID);
                                        //log
                                        logger.info("Expense '{}' added: ${}", description, amount);
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────╮\n" +
                                                        "│      Expenses Added Successfully   │\n" +
                                                        "╰────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (expenseChoice == 3) {
                                        ArrayList<Expense> expenses = controller.organizer
                                                .get(controller.orgIndex).events
                                                .get(eventNumber - 1).expenses;

                                        if (expenses == null || expenses.isEmpty()) {
                                            System.out.println("\u001B[91mNo expenses available to update!\u001B[0m\n");
                                            continue;
                                        }

                                        System.out.println(Expense.displayAllExpenses(eventNumber - 1, controller.organizer, controller.orgIndex));
                                        System.out.print("Enter the Expense Number to Update: ");
                                        try {
                                            int expNumber = input.nextInt();
                                            if (expNumber < 1 || expNumber > expenses.size()) {
                                                System.out.println("\u001B[91mInvalid Expense Number!\u001B[0m\n");
                                                continue;
                                            }
                                            input.nextLine();

                                            Expense expenseToUpdate = expenses.get(expNumber - 1);
                                            System.out.println("Leave blank To keep the same");
                                            System.out.print("Enter New Description (Current: "
                                                    + expenseToUpdate.description + "): ");
                                            String newDescription = input.nextLine();
                                            if (!newDescription.trim().isEmpty()) {
                                                expenseToUpdate.description = newDescription;
                                            }

                                            double newAmount = -1;
                                            while (true) {
                                                try {
                                                    System.out.print("Enter New Amount (Current: "
                                                            + expenseToUpdate.amount + "): ");
                                                    newAmount = input.nextDouble();
                                                    if (newAmount >= 0) {
                                                        expenseToUpdate.amount = newAmount;
                                                        break;
                                                    } else {
                                                        System.out.println(
                                                                "\u001B[91mAmount cannot be negative!\u001B[0m");
                                                    }
                                                } catch (InputMismatchException e) {
                                                    System.out.println(
                                                            "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                                    input.nextLine();
                                                }
                                            }
                                            expenseToUpdate.updateExpensesDb(controller.organizer
                                                    .get(controller.orgIndex).events
                                                    .get(eventNumber - 1).event_ID);
                                            System.out.println(
                                                    "\u001B[92m\u001B[1m" +
                                                            "╭────────────────────────────────────╮\n" +
                                                            "│     Expense Updated Successfully!  │\n" +
                                                            "╰────────────────────────────────────╯" +
                                                            "\u001B[0m");

                                        } catch (InputMismatchException e) {
                                            System.out.println("\u001B[91mInvalid input!\u001B[0m\n");
                                            input.nextLine();
                                        }
                                    } else if (expenseChoice == 4) {

                                        ArrayList<Expense> expenses = controller.organizer
                                                .get(controller.orgIndex).events
                                                .get(eventNumber - 1).expenses;

                                        if (expenses == null || expenses.isEmpty()) {
                                            System.out.println("\u001B[91mNo expenses available to delete!\u001B[0m\n");
                                            continue;
                                        }

                                        System.out.println(Expense.displayAllExpenses(eventNumber - 1, controller.organizer, controller.orgIndex));

                                        while (true) {
                                            try {
                                                System.out.print("Enter the Expense Number to Delete: ");
                                                int expNumber = input.nextInt();
                                                input.nextLine();

                                                if (expNumber < 1 || expNumber > expenses.size()) {
                                                    System.out.println("\u001B[91mInvalid Expense Number!\u001B[0m\n");
                                                    continue;
                                                }

                                                Expense selected = expenses.get(expNumber - 1);

                                                String confirm = "";
                                                while (true) {
                                                    System.out.print("Are you sure you want to delete \""
                                                            + selected.description + "\"? (y/n): ");
                                                    confirm = input.nextLine().trim().toLowerCase();

                                                    if (confirm.equals("y") || confirm.equals("n"))
                                                        break;

                                                    System.out
                                                            .println("\u001B[91mInvalid input. Enter Y or N.\u001B[0m");
                                                }

                                                if (confirm.equals("n")) {
                                                    System.out.println("\u001B[93mDeletion canceled.\u001B[0m\n");
                                                    break;
                                                }

                                                Expense removedExpense = expenses.remove(expNumber - 1);
                                                removedExpense.removeExpensesDb(controller.organizer
                                                        .get(controller.orgIndex).events
                                                        .get(eventNumber - 1).event_ID);
                                                System.out.println(
                                                        "\u001B[92m\u001B[1m" +
                                                                "╭────────────────────────────────────╮\n" +
                                                                "│     Expense Deleted Successfully!  │\n" +
                                                                "╰────────────────────────────────────╯" +
                                                                "\u001B[0m");

                                                break;

                                            } catch (InputMismatchException e) {
                                                System.out.println("\u001B[91mInvalid input!\u001B[0m\n");
                                                input.nextLine();
                                            }
                                        }
                                    } else if (expenseChoice == 5) {
                                        break;
                                    } else {
                                        System.out
                                                .println("\u001B[91mInvalid input\u001B[0m");
                                    }

                                }
                            } else if (eventChoice == 6) {
                                //log
                                logger.debug("Organizer selected ticket operations");
                                Event currentEvent = controller.organizer.get(controller.orgIndex).events
                                        .get(eventNumber - 1);
                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │   \u001B[32m1. Available Tickets\u001B[33m\u001B[1m   │");
                                    System.out.println("                                               │   \u001B[34m2. Update Price     \u001B[33m\u001B[1m   │");
                                    System.out.println("                                               │   \u001B[35m3. Update Count     \u001B[33m\u001B[1m   │");
                                    System.out.println("                                               │   \u001B[33m4. View Price       \u001B[33m\u001B[1m   │");
                                    System.out.println("                                               │   \u001B[31m5. Back             \u001B[33m\u001B[1m   │");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int ticketChoice = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter Your Choice: ");
                                            ticketChoice = input.nextInt();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out
                                                    .println(
                                                            "\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }
                                    double rate = 0;
                                    boolean modified = false;
                                    int updateCount = 0;

                                    if (ticketChoice == 1) {
                                        //log
                                        logger.trace("Viewing Available Tickets");
                                        System.out.println("                                               \u001B[33m\u001B[1m╭─────────────────────────────────╮");
                                        System.out.println("                                               │                                 │");
                                        System.out.println(
                                                "                                               │ \u001B[36mAvailable Tickets: " + currentEvent.ticketsSold.size()
                                                        + " \u001B[33m\u001B[1m         │");
                                        System.out.println("                                               │                                 │");
                                        System.out.println("                                               ╰─────────────────────────────────╯\u001B[0m");
                                        logger.trace("Viewed Available Tickets");
                                    } else if (ticketChoice == 2) {
                                        //log
                                        logger.trace("Entered To update Ticket Price");

                                        if (currentEvent.ticketsSold.isEmpty()
                                                || currentEvent.ticketsSold.get(0) == null) {
                                            System.out.println("No Tickets Available");
                                            continue;
                                        }
                                        try {
                                            while (true) {
                                                System.out.print("Enter the new ticket price: ");
                                                rate = input.nextDouble();
                                                if (rate < 1) {
                                                    System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                    continue;
                                                }
                                                break;
                                            }
                                            for (Ticket t : currentEvent.ticketsSold) {
                                                t.price = rate;
                                            }
                                            currentEvent.updatePriceDb(rate);
                                            //log
                                            logger.info("Ticket price updated to ${} for event '{}'",
                                                    rate, currentEvent.name);
                                            System.out.println(
                                                    "\u001B[92m\u001B[1m" +
                                                            "╭────────────────────────────────────────╮\n" +
                                                            "│   Ticket Price Updated Successfully    │\n" +
                                                            "╰────────────────────────────────────────╯" +
                                                            "\u001B[0m");
                                        } catch (InputMismatchException e) {
                                            System.out.println(
                                                    "\u001B[91mInvalid input. Please enter a number.\u001B[0m");
                                            input.nextLine();
                                        }
                                    } else if (ticketChoice == 3) {
                                        //log
                                        logger.trace("Entered To update Ticket Count");
                                        while (true) {
                                            System.out.print("Enter the new ticket count: ");
                                            updateCount = input.nextInt();
                                            if (updateCount < 1) {
                                                System.out.println("\u001B[91mInvalid input.\u001B[0m");
                                                continue;
                                            }
                                            break;
                                        }
                                        controller.organizer.get(controller.orgIndex).events
                                                .get(eventNumber - 1).venue.capacity = updateCount;
                                        double price = currentEvent.ticketsSold.get(0).price;
                                        currentEvent.ticketsSold.clear();
                                        for (int i = 0; i < updateCount; i++) {
                                            currentEvent.ticketsSold.add(new Ticket(currentEvent.name, currentEvent.category, price));
                                        }
                                        currentEvent.reduceTicketCount();
                                        //log
                                        logger.info("Ticket Count updated to ${} for event '{}'",
                                                updateCount, currentEvent.name);
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────────╮\n" +
                                                        "│   Ticket Count Updated Successfully    │\n" +
                                                        "╰────────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (ticketChoice == 4) {
                                        //log
                                        logger.trace("Viewing Ticket Price");
                                        if (currentEvent.ticketsSold.isEmpty()
                                                || currentEvent.ticketsSold.get(0) == null) {
                                            System.out.println("No Tickets Avaliable");
                                            continue;
                                        }
                                        System.out.println("\u001B[33m\u001B[1m╭─────────────────────────────────╮");
                                        System.out.println("│                                 │");
                                        System.out.println(
                                                "│ \u001B[36mTickets Price : " + currentEvent.ticketsSold.get(0).price
                                                        + " \u001B[33m\u001B[1m             │");
                                        System.out.println("│                                 │");
                                        System.out.println("╰─────────────────────────────────╯\u001B[0m");
                                        //log
                                        logger.info("Viewed Ticket Price");
                                    } else if (ticketChoice == 5) {
                                        break;
                                    } else {
                                        System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                                        input.nextLine();
                                    }
                                }

                            } else if (eventChoice == 7) {
                                while (true) {
                                    System.out.println("\u001B[33m\u001B[1m                                               ╭──────────────────────────╮");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               │   \u001B[32m1. Change Name         \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │   \u001B[34m2. Change Category     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │   \u001B[35m3. Change Location     \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │   \u001B[32m4. Change Date         \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │   \u001B[36m5. Change Budget       \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │   \u001B[31m6. Back                \u001B[33m\u001B[1m│");
                                    System.out.println("                                               │                          │");
                                    System.out.println("                                               ╰──────────────────────────╯\u001B[0m");
                                    int changeChoice = controller.isValidInteger("Enter Your Choice: ", 1000000);
                                    Event selectedEvent = controller.organizer.get(controller.orgIndex).events.get(eventNumber - 1);
                                    if (changeChoice == 1) {
                                        String msg = "Enter The New Name ( " + selectedEvent.name + " ): ";
                                        selectedEvent.name = controller.isEmptyCheck(msg);
                                        selectedEvent.updateEventNameDb();
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────────╮\n" +
                                                        "│   Event Name Updated Successfully      │\n" +
                                                        "╰────────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (changeChoice == 2) {
                                        String msg = "Enter The New Category ( " + selectedEvent.category + " ): ";
                                        selectedEvent.category = controller.isEmptyCheck(msg);
                                        selectedEvent.updateEventCategoryDb();
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────────╮\n" +
                                                        "│   Event Category Updated Successfully  │\n" +
                                                        "╰────────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (changeChoice == 3) {
                                        String msg = "Enter The New Location ( " + selectedEvent.venue.name + " ): ";
                                        selectedEvent.venue.name = controller.isEmptyCheck(msg);
                                        selectedEvent.updateEventLocationDb();
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────────╮\n" +
                                                        "│   Event Location Updated Successfully  │\n" +
                                                        "╰────────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (changeChoice == 4) {
                                        LocalDate date = controller.isValidDate(logger);
                                        selectedEvent.date = date;
                                        selectedEvent.updateEventDateDb();
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────────╮\n" +
                                                        "│   Event Date Updated Successfully      │\n" +
                                                        "╰────────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (changeChoice == 5) {
                                        int budget = controller.isValidInteger("Enter The New Budget : ", 100000000000000000000000000.0);
                                        selectedEvent.budget = budget;
                                        selectedEvent.updateEventBudgetDb();
                                        System.out.println(
                                                "\u001B[92m\u001B[1m" +
                                                        "╭────────────────────────────────────────╮\n" +
                                                        "│   Event Budget Updated Successfully    │\n" +
                                                        "╰────────────────────────────────────────╯" +
                                                        "\u001B[0m");
                                    } else if (changeChoice == 6) {
                                        break;
                                    } else {
                                        System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                                    }
                                }
                            } else if (eventChoice == 8) {
                                break;
                            } else {
                                System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                            }
                        }
                    } else if (choice == 4) {
                        //log
                        logger.warn("Organizer {} attempting to delete event",
                                controller.organizer.get(controller.orgIndex).name);
                        Organizer org = controller.organizer.get(controller.orgIndex);
                        System.out.println(controller.displayAllEvents());
                        if (org.events.isEmpty()) {
                            continue;
                        }
                        int deleteEventIndex = 0;
                        while (true) {
                            try {
                                System.out.print("Enter The Event Number To Delete(Back: 0): ");
                                deleteEventIndex = input.nextInt();
                                if (deleteEventIndex < 0 || deleteEventIndex > org.events.size()) {
                                    System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                                    input.nextLine();
                                    continue;
                                }
                                break;
                            } catch (Exception e) {
                                System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                                input.nextLine();
                            }
                        }
                        if (deleteEventIndex == 0) {
                            continue;
                        }
                        if (org.events.isEmpty()) {
                            System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                        } else {
                            while (true) {
                                input.nextLine();
                                System.out.print(
                                        "Are you sure you want to delete '" + org.events.get(deleteEventIndex - 1).name
                                                + "'? (y/n): ");
                                String confirm = input.nextLine().trim().toLowerCase();

                                if (confirm.equals("y") || confirm.equals("yes")) {
                                    org.events.get(deleteEventIndex - 1).removeEventDb();
                                    //log
                                    logger.warn("Event '{}' deleted from database",
                                            org.events.get(deleteEventIndex - 1).name);
                                    System.out.println(
                                            "\u001B[91m\u001B[1m" +
                                                    "╭────────────────────────────────────────────────────────╮\n" +
                                                    "│  Event '" + org.events.get(deleteEventIndex - 1).name
                                                    + "' deleted successfully!  \n" +
                                                    "╰────────────────────────────────────────────────────────╯" +
                                                    "\u001B[0m");

                                    Event removerdEvent = org.events.remove(deleteEventIndex - 1);
                                    //log
                                    logger.warn("Event '{}' removed from memory",
                                            removerdEvent.name);
                                    break;
                                } else {
                                    //log
                                    logger.info("Event '{}' removed  Canceled",
                                            org.events.get(deleteEventIndex - 1).name);
                                    System.out.println(
                                            "\u001B[93m\u001B[1m" +
                                                    "╭────────────────────────────╮\n" +
                                                    "│       Deletion Cancelled    │\n" +
                                                    "╰────────────────────────────╯" +
                                                    "\u001B[0m");
                                    break;
                                }
                            }
                        }

                    } else if (choice == 5) {
                        break;
                    } else {
                        System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                    }
                }
            } else if (roleChoice == 2) {
                //log
                logger.info("User menu accessed by: {}",
                        controller.users.get(controller.userIndex).name);
                organ:
                while (true) {
                    int userOrgChoice = 0;
                    System.out.println(Organizer.displayAllOrganizer(controller.organizer));
                    //log
                    logger.debug("User {} viewing organizer list",
                            controller.users.get(controller.userIndex).name);
                    try {
                        System.out.print("Enter Your Choice(Logout: 0): ");
                        userOrgChoice = input.nextInt();
                        //log
                        logger.debug("User entered organizer choice: {}", userOrgChoice);

                        if (userOrgChoice < 0 || controller.organizer.size() < userOrgChoice) {
                            //log
                            logger.warn("Invalid organizer choice entered: {}", userOrgChoice);
                            System.out.println("\u001B[91mInvalid input.\u001B[0m\n");
                            continue;
                        }
                    } catch (InputMismatchException e) {
                        //log
                        logger.error("Invalid input for organizer selection");
                        System.out.println("\u001B[91mInvalid input. Enter The Number\u001B[0m\n");
                        input.nextLine();
                        continue;
                    }
                    if (userOrgChoice == 0) {
                        //log
                        logger.info("User {} logged out from organizer selection",
                                controller.users.get(controller.userIndex).name);
                        break;
                    }
                    Organizer org = controller.organizer.get(userOrgChoice - 1);
                    controller.orgIndex = userOrgChoice - 1;
                    //log
                    logger.info("User {} selected organizer: {} (Org ID: {})",
                            controller.users.get(controller.userIndex).name,
                            org.name,
                            org.org_ID);
                    System.out.println("\n" + controller.displayAllEvents() + "\n");
                    int userEventNumber = 0;
                    while (true) {
                        try {
                            System.out.print("Enter The Event Number You want to Get In(Back:0): ");
                            userEventNumber = input.nextInt();
                            //log
                            logger.debug("User entered event number: {}", userEventNumber);
                            if (userEventNumber == 0) {
                                //log
                                logger.debug("User returning to organizer selection");
                                continue start;
                            }
                            Event selectedEvent = controller.organizer.get(controller.orgIndex).events.get(userEventNumber - 1);
                            //log
                            logger.info("User {} selected event: '{}' (Event ID: {})",
                                    controller.users.get(controller.userIndex).name,
                                    selectedEvent.name,
                                    selectedEvent.event_ID);
                            break;
                        } catch (InputMismatchException e) {
                            System.out
                                    .println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                            input.nextLine();
                        } catch (IndexOutOfBoundsException e) {
                            System.out
                                    .println("\u001B[91mInvalid Event\u001B[0m\n");
                            input.nextLine();
                        }
                    }

                    int userChoice = 0;
                    while (true) {
                        System.out.println(
                                "                                                 \u001B[33m\u001B[1m╭──────────────────────────────╮");
                        System.out.println(
                                "                                                 │                              │");
                        System.out.println(
                                "                                                 │      \u001B[36m1. View Session         \u001B[33m\u001B[1m│");
                        System.out.println(
                                "                                                 │      \u001B[32m2. View Speaker         \u001B[33m\u001B[1m│");
                        System.out.println(
                                "                                                 │      \u001B[34m3. View Sponsor         \u001B[33m\u001B[1m│");
                        System.out.println(
                                "                                                 │      \u001B[35m4. View Booth           \u001B[33m\u001B[1m│");
                        System.out.println(
                                "                                                 │      \u001B[35m5. Purchase Ticket      \u001B[33m\u001B[1m│");
                        System.out.println(
                                "                                                 │      \u001B[31m6. Back                 \u001B[33m\u001B[1m│");
                        System.out.println(
                                "                                                 │                              │");
                        System.out.println(
                                "                                                 ╰──────────────────────────────╯\u001B[0m");


                        try {
                            System.out.print("Enter Your Choice: ");
                            userChoice = input.nextInt();
                        } catch (InputMismatchException e) {
                            System.out
                                    .println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                            input.nextLine();
                        }
                        Organizer currentOrg = controller.organizer.get(controller.orgIndex);
                        Event selectedEvent = currentOrg.events.get(userEventNumber - 1);
                        User currentUser = controller.users.get(controller.userIndex);

                        if (userChoice == 1) {
                            System.out.println("SESSION\n");
                            System.out.println(Session.displayAllSession(userEventNumber - 1, controller.organizer, controller.orgIndex));
                            //log
                            logger.trace("User '{}' viewing sessions for event '{}' (Event ID: {}) from organizer '{}' (Org ID: {})",
                                    currentUser.name,
                                    selectedEvent.name,
                                    selectedEvent.event_ID,
                                    currentOrg.name,
                                    currentOrg.org_ID);
                        } else if (userChoice == 2) {
                            //log
                            logger.trace("User '{}' viewing speakers for event '{}' (Event ID: {}) from organizer '{}' (Org ID: {})",
                                    currentUser.name,
                                    selectedEvent.name,
                                    selectedEvent.event_ID,
                                    currentOrg.name,
                                    currentOrg.org_ID);
                            System.out.println("ALL SPEAKER\n");
                            System.out.println(Speaker.displayAllSpeakers(userEventNumber - 1, controller.organizer, controller.orgIndex));
                        } else if (userChoice == 3) {
                            //log
                            logger.trace("User '{}' viewing sponsors for event '{}' (Event ID: {}) from organizer '{}' (Org ID: {})",
                                    currentUser.name,
                                    selectedEvent.name,
                                    selectedEvent.event_ID,
                                    currentOrg.name,
                                    currentOrg.org_ID);
                            System.out.println("ALL SPONSORS\n");
                            System.out.println(Sponsor.displayAllSponsor(userEventNumber - 1, controller.organizer, controller.orgIndex));
                        } else if (userChoice == 4) {
                            //log
                            logger.trace("User '{}' viewing booths for event '{}' (Event ID: {}) from organizer '{}' (Org ID: {})",
                                    currentUser.name,
                                    selectedEvent.name,
                                    selectedEvent.event_ID,
                                    currentOrg.name,
                                    currentOrg.org_ID);
                            System.out.println(Booth.displayAllBooths(userEventNumber - 1, controller.organizer, controller.orgIndex));
                        } else if (userChoice == 5) {
                            //log
                            logger.info("User '{}' attempting to purchase tickets for event '{}' (Event ID: {}) from organizer '{}' (Org ID: {})",
                                    currentUser.name,
                                    selectedEvent.name,
                                    selectedEvent.event_ID,
                                    currentOrg.name,
                                    currentOrg.org_ID);
                            try {
                                if (selectedEvent.ticketsSold.isEmpty()) {
                                    //log
                                    logger.warn("No tickets available for event '{}' - Purchase attempt failed",
                                            selectedEvent.name);
                                    System.out.println("\u001B[91mSorry! No Tickets Available\u001B[0m");
                                } else {
                                    //log
                                    logger.debug("Displaying available ticket information to user");
                                    logger.info("Available tickets for event '{}': {}, Ticket price: ${}",
                                            selectedEvent.name,
                                            selectedEvent.ticketsSold.size(),
                                            selectedEvent.ticketsSold.get(0).price);
                                    System.out.println("                                               \u001B[33m\u001B[1m╭─────────────────────────────────╮");
                                    System.out.println("                                               │                                 │");
                                    System.out.println(
                                            "                                               │ \u001B[36mAvailable Tickets: "
                                                    + selectedEvent.ticketsSold
                                                    .size()
                                                    + " \u001B[33m\u001B[1m          │");
                                    System.out.println("                                               │                                 │");
                                    System.out.println("                                               ╰─────────────────────────────────╯\u001B[0m");
                                    Payment payment = new Payment(
                                            controller.organizer.get(controller.orgIndex).events
                                                    .get(userEventNumber - 1).ticketsSold
                                                    .get(0).price);
                                    //log
                                    logger.debug("Payment object created with amount: ${}", payment.amount);
                                    int noOfTicket = 0;
                                    Ticket purchasedTicket = null;
                                    max:
                                    while (true) {
                                        try {
                                            System.out
                                                    .print("Enter The Number Of Ticket You Are Going To Purchase (Max : 10): ");
                                            noOfTicket = input.nextInt();
                                            //log
                                            logger.debug("User entered ticket quantity: {}", noOfTicket);
                                            if (noOfTicket < 1 || noOfTicket > 10
                                                    || noOfTicket > controller.organizer.get(controller.orgIndex).events
                                                    .get(userEventNumber - 1).ticketsSold.size()) {
                                                //log
                                                logger.warn("Invalid ticket count entered: {} (Available: {}, Max: 10)",
                                                        noOfTicket,
                                                        selectedEvent.ticketsSold.size());
                                                System.out
                                                        .println("\u001B[91mInvalid Ticket Count\u001B[0m\n");
                                                input.nextLine();
                                                continue;
                                            }
                                            //log
                                            logger.info("Valid ticket count selected: {}", noOfTicket);
                                            logger.debug("Ticket purchase calculation - Quantity: {}, Unit Price: ${}, Total: ${}",
                                                    noOfTicket,
                                                    payment.amount,
                                                    (payment.amount * noOfTicket));
                                            break;
                                        } catch (InputMismatchException e) {
                                            //log
                                            logger.error("Invalid numeric input for ticket quantity");
                                            System.out
                                                    .println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }

                                    while (true) {
                                        try {
                                            double totalAmount = payment.amount * noOfTicket;
                                            System.out
                                                    .print("Enter The Payment Amount " + (totalAmount)
                                                            + " : ");
                                            int amount = input.nextInt();
                                            //log
                                            logger.debug("User entered payment amount: ${} (Required: ${})",
                                                    amount, totalAmount);
                                            if (amount < totalAmount) {
                                                // log
                                                logger.warn("Insufficient payment: ${} entered, ${} required",
                                                        amount, totalAmount);
                                                System.out
                                                        .println("\u001B[91mInvalid Amount\u001B[0m\n");
                                                input.nextLine();
                                                continue;
                                            }
                                            if (amount > totalAmount) {
                                                double change = amount - totalAmount;
                                                logger.debug("Excess payment - Change returned: ${}", change);
                                                System.out.println(
                                                        "Here is Your Balance " + change);
                                            }
                                            //log
                                            logger.debug("Removing {} tickets from available pool", noOfTicket);
                                            for (int i = 0; i < noOfTicket; i++) {
                                                purchasedTicket = selectedEvent.ticketsSold
                                                        .remove(selectedEvent.ticketsSold.size() - 1);
                                            }
                                            payment.processPayment(noOfTicket);
                                            //log
                                            logger.info("Payment processed successfully - Amount: ${}, Tickets: {}", (payment.amount * noOfTicket), noOfTicket);
                                            break;
                                        } catch (InputMismatchException e) {
                                            //log
                                            logger.error("Invalid input for payment amount");
                                            System.out
                                                    .println("\u001B[91mInvalid input. Please enter a number \u001B[0m\n");
                                            input.nextLine();
                                        }
                                    }

                                    controller.users.get(controller.userIndex).addTicket(purchasedTicket);
                                    selectedEvent.reduceTicketCount();
                                    //log
                                    logger.info("TICKET PURCHASE COMPLETED - User: '{}', Event: '{}', Tickets: {}, Total Amount: ${}",
                                            currentUser.name,
                                            selectedEvent.name,
                                            noOfTicket,
                                            (payment.amount * noOfTicket));
                                    //log
                                    logger.debug("Ticket added to user account, ticket count updated in database");
                                    System.out.println(
                                            "\u001B[92m\u001B[1m" +
                                                    "╭────────────────────────────────────────╮\n" +
                                                    "│   Ticket Purchased Successfully!       │\n" +
                                                    "╰────────────────────────────────────────╯" +
                                                    "\u001B[0m");
                                }
                            } catch (IndexOutOfBoundsException e) {
                                logger.error("Index error during ticket purchase - orgIndex: {}, userEventNumber: {}, Error: {}",
                                        controller.orgIndex, userEventNumber, e.getMessage());
                                System.out.println("\u001B[91mError accessing event data\u001B[0m\n");

                            } catch (NullPointerException e) {
                                logger.error("Null pointer error during ticket purchase: {}", e.getMessage());
                                System.out.println("\u001B[91mError accessing event data\u001B[0m\n");

                            } catch (Exception e) {
                                logger.error("Unexpected error during ticket purchase: {}", e.getMessage());
                                logger.error("Stack trace:", e);
                                System.out.println("\u001B[91mAn unexpected error occurred during ticket purchase\u001B[0m\n");
                            }
                        } else if (userChoice == 6) {
                            //log
                            logger.debug("User {} exiting event menu for organizer {}",
                                    currentUser.name,
                                    currentOrg.name);
                            continue organ;
                        }
                    }

                }
            } else {
                //log
                logger.warn("User entered invalid menu choice");
                System.out.println("\u001B[91mInvalid Choice\u001B[0m\n");
            }
        }

    }


    void addEvents(Event e) {
        organizer.get(orgIndex).events.add(e);
    }

    String displayAllEvents() {
        if (!organizer.get(orgIndex).events.isEmpty()) {
            String result = "                                  \033[33m███████╗ ██╗ ██╗ ███████╗ ███╗   ██╗ ████████╗ ███████╗\n                                  ██╔════╝ ██║ ██║ ██╔════╝ ████╗  ██║ ╚══██╔══╝ ██╔════╝\n                                  █████╗   ██║ ██║ █████╗   ██╔██╗ ██║    ██║    ███████╗\n                                  ██╔══╝   ██║ ██║ ██╔══╝   ██║╚██╗██║    ██║    ╚════██║\n                                  ███████╗ ╚████╔╝ ███████╗ ██║ ╚████║    ██║    ███████║\n                                  ╚══════╝  ╚═══╝  ╚══════╝ ╚═╝ ╚═══╝     ╚═╝    ╚══════╝\n                                  \n";
            for (Event e : organizer.get(orgIndex).events) {
                e.num = organizer.get(orgIndex).events.indexOf(e) + 1;
                result += e.displayEvent() + "\n\n";
            }
            return result;
        } else {
            return "\u001B[32m\u001B[1m" +
                    "╭───────────────────────────────╮\n" +
                    "│       No Events Available     │\n" +
                    "╰───────────────────────────────╯" +
                    "\u001B[0m";
        }
    }


    private static double parseTime(String timeStr) {
        timeStr = timeStr.toUpperCase().trim();
        boolean isPM = timeStr.contains("PM");
        timeStr = timeStr.replace("AM", "").replace("PM", "").trim();
        double hour = Double.parseDouble(timeStr.split(":")[0]);
        if (isPM && hour != 12)
            hour += 12;
        return hour;
    }

    // Validations
    public boolean isValidName(String name) {
        if (!(name.length() > 2)) {
            System.out.println(
                    "\u001B[91m╭────────────────────────────────────────────╮\n" +
                            "│      Name must be greater than 2 letters   │\n" +
                            "╰────────────────────────────────────────────╯\u001B[0m");
            return false;
        }
        return true;
    }

    public boolean isValidEmail(String email) {
        if (!(email.endsWith("@gmail.com") || email.endsWith("@zoho.com") || email.endsWith("@zohocorp.com"))) {
            System.out.println(
                    "\u001B[91m╭───────────────────────────────────────╮\n" +
                            "│         Invalid Email Format          │\n" +
                            "╰───────────────────────────────────────╯\u001B[0m");
            return false;
        } else if (email.length() < 13) {
            System.out.println(
                    "\u001B[91m╭────────────────────────────────────────────╮\n" +
                            "│        Email must be at least 13 letters   │\n" +
                            "╰────────────────────────────────────────────╯\u001B[0m");
            return false;
        }

        return true;
    }

    public boolean isValidPassword(String password) {

        if (password.length() < 6) {
            System.out.println(
                    "\u001B[91m╭────────────────────────────────────────────╮\n" +
                            "│     Password must be at least 6 characters │\n" +
                            "╰────────────────────────────────────────────╯\u001B[0m");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            System.out.println(
                    "\u001B[91m╭────────────────────────────────────────────╮\n" +
                            "│    Password must contain an uppercase letter │\n" +
                            "╰────────────────────────────────────────────╯\u001B[0m");
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            System.out.println(
                    "\u001B[91m╭────────────────────────────────────────────╮\n" +
                            "│        Password must contain a number       │\n" +
                            "╰────────────────────────────────────────────╯\u001B[0m");
            return false;
        }

        if (!password.matches(".*[!@#$%^&()_+\\-={}\\[\\]|:;\"'<>,.?/].*")) {
            System.out.println(
                    "\u001B[91m╭────────────────────────────────────────────╮\n" +
                            "│    Password must contain a special symbol   │\n" +
                            "╰────────────────────────────────────────────╯\u001B[0m");
            return false;
        }

        return true;
    }

    static String formatTime(double hour) {
        String period = "AM";
        if (hour == 0) {
            hour = 12;
        } else if (hour >= 12) {
            period = "PM";
            if (hour > 12) {
                hour -= 12;
            }
        }
        return String.format("%.0f:00 %s", hour, period);
    }

    public void readUser(MainController main) throws SQLException {

        main.organizer.clear();
        Connection con = DbConnection.getConnection();
        PreparedStatement query = con.prepareStatement(Data.READ_ACCOUNT_QUERY);
        ResultSet rs = query.executeQuery();
        while (rs.next()) {
            User user = new User(rs.getString("Name"), rs.getString("Email"), rs.getString("Password"), rs.getString("Role"));
            main.users.add(user);
        }
        PreparedStatement orgQuery = con.prepareStatement(Data.READ_ORG_QUERY);
        orgQuery.setString(1, "ORGANIZER");
        rs = orgQuery.executeQuery();
        while (rs.next()) {
            Organizer org = new Organizer(rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getInt("id"));
            main.organizer.add(org);
        }
        for (Organizer org : main.organizer) {

            // adding Event
            PreparedStatement eventQuery = con.prepareStatement(Data.READ_EVENT_QUERY);
            eventQuery.setInt(1, org.org_ID);
            rs = eventQuery.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("Event_ID");
                Event event = new Event(rs.getString("Name"), rs.getDouble("Budget"), new Venue(rs.getString("Location"), rs.getInt("Capacity")), rs.getString("Category"), rs.getDate("Event_Date").toLocalDate());
                event.setEventId(eventId);
                for (int i = 0; i < rs.getInt("Ticket_Count"); i++) {
                    event.ticketsSold.add(new Ticket(event.name, event.category, rs.getInt("Ticket_Price")));
                }

                //adding Booth
                PreparedStatement boothQuery = con.prepareStatement(Data.READ_BOOTH_QUERY);
                boothQuery.setInt(1, eventId);
                ResultSet boothResult = boothQuery.executeQuery();

                while (boothResult.next()) {
                    Booth booth = new Booth(boothResult.getString("booth_name"), boothResult.getInt("size_sq_ft"), boothResult.getInt("cost"));
                    event.booths.add(booth);
                    booth.setBoothId(boothResult.getInt("booth_id"));
                }

                //adding Session
                PreparedStatement sessionQuery = con.prepareStatement(Data.READ_SESSION_QUERY);
                sessionQuery.setInt(1, eventId);
                ResultSet sessionSet = sessionQuery.executeQuery();
                while (sessionSet.next()) {
                    Session session = new Session(sessionSet.getString("title"), sessionSet.getString("duration"));
                    int session_ID = sessionSet.getInt("session_id");
                    session.setSessionId(session_ID);
                    PreparedStatement speakerQuery = con.prepareStatement(Data.READ_SPEAKER_QUERY);
                    speakerQuery.setInt(1, eventId);
                    speakerQuery.setInt(2, session_ID);
                    ResultSet speakerQueryRs = speakerQuery.executeQuery();
                    while (speakerQueryRs.next()) {
                        Speaker speaker = new Speaker(speakerQueryRs.getString("name"), speakerQueryRs.getString("gender"));
                        session.assignSpeaker(speaker);
                        event.speakers.add(speaker);
                    }
                    event.schedule.add(session);
                }
                PreparedStatement sponsorQuery = con.prepareStatement(Data.READ_SPONSOR_QUERY);
                sponsorQuery.setInt(1, eventId);
                ResultSet sponsorRs = sponsorQuery.executeQuery();
                while (sponsorRs.next()) {
                    Sponsor sponsor = new Sponsor(sponsorRs.getString("company_Name"), sponsorRs.getDouble("amount"));
                    event.sponsors.add(sponsor);
                }

                PreparedStatement expenseQuery = con.prepareStatement(Data.READ_EXPENSE_QUERY);
                expenseQuery.setInt(1, eventId);
                ResultSet expenseRs = expenseQuery.executeQuery();
                while (expenseRs.next()) {
                    Expense expense = new Expense(expenseRs.getString("description"), expenseRs.getDouble("Amount"));
                    event.expenses.add(expense);
                }

                org.events.add(event);
            }
        }
    }

    Scanner input = new Scanner(System.in);

    String isEmptyCheck(String msg) {
        while (true) {
            System.out.print(msg);
            String value = input.nextLine();
            if (value.trim().isEmpty()) {
                System.out.println("\u001B[91mInvalid Input\n\u001B[0m");
                continue;
            }
            return value.trim();
        }
    }

    int isValidInteger(String msg, double range) {
        int i;
        while (true) {
            try {
                System.out.print(msg);
                i = input.nextInt();

                if (i >= 1 && i <= range) {
                    input.nextLine();
                    return i;
                } else {
                    System.out.println("Invalid Input. Enter a number between 1 and " + range);
                }

            } catch (Exception e) {
                input.nextLine();
                System.out.println("Invalid Input. Enter a valid integer.");
            }
        }
    }

    LocalDate isValidDate(Logger logger) {
        LocalDate dateInput = null;
        while (true) {
            System.out.print("Enter event date (yyyy-mm-dd): ");
            String date = input.nextLine();
            try {
                dateInput = LocalDate.parse(date);
                if (!dateInput.isAfter(LocalDate.now())) {
                    //log
                    logger.warn("Past date entered for event: {}", date);
                    System.out.println("Date cannot be in the past. Please enter a future date.");
                } else {
                    //log
                    logger.debug("Event date validated: {}", dateInput);
                    return dateInput;
                }
            } catch (DateTimeParseException e) {
                //log
                logger.error("Invalid date format entered: {}", date);
                System.out.println("Enter a valid date (format: yyyy-MM-dd)");
            }
        }
    }
}
