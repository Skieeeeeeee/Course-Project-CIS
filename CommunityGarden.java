
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

// User class for registration
class User {

    private final String name;
    private final String phone;
    private final String email;

    public User(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}

// Interface for Donations
interface Donation {

    void donate();
}

// Class for monetary donation
class MoneyDonation implements Donation {

    private final double amount;

    public MoneyDonation(double amount) {
        this.amount = amount;
    }

    @Override
    public void donate() {
        System.out.println("Monetary donation of $" + amount + " received.");
    }
}

// Class for item-based donation
class ItemDonation implements Donation {

    private final String item;

    public ItemDonation(String item) {
        this.item = item;
    }

    @Override
    public void donate() {
        System.out.println("Donation received: " + item);
    }
}

// Main application class
public class CommunityGarden {

    private static final ArrayList<User> users = new ArrayList<>();
    private static final ArrayList<String> appointments = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CommunityGarden::createGUI);
    }

    private static void createGUI() {
        JFrame frame = new JFrame("Moreno Valley Community Garden");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel dateLabel = new JLabel("Appointment Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField();
        JLabel timeLabel = new JLabel("Appointment Time (HH:mm, 24hr format):");
        JTextField timeField = new JTextField();
        JButton submitButton = new JButton("Register");

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(timeLabel);
        panel.add(timeField);
        panel.add(submitButton);

        frame.add(panel);
        frame.setVisible(true);

        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String date = dateField.getText();
            String time = timeField.getText();

            if (validateAppointment(date, time)) {
                users.add(new User(name, phone, email));
                appointments.add(date + " " + time);
                saveToFile(name, phone, email, date, time);
                JOptionPane.showMessageDialog(frame, "Registration Successful!");

                // Prompt for donation
                String donationChoice = JOptionPane.showInputDialog("Would you like to donate? (yes/no)");
                if ("yes".equalsIgnoreCase(donationChoice)) {
                    String donationType = JOptionPane.showInputDialog("Enter donation type (money/item):");
                    if ("money".equalsIgnoreCase(donationType)) {
                        double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter donation amount:"));
                        MoneyDonation donation = new MoneyDonation(amount);
                        donation.donate();
                    } else {
                        String item = JOptionPane.showInputDialog("Enter item donation:");
                        ItemDonation donation = new ItemDonation(item);
                        donation.donate();
                    }
                }

                // Display registered users and appointments
                System.out.println("Registered Users:");
                for (User user : users) {
                    System.out.println(user.getName() + " - " + user.getPhone() + " - " + user.getEmail());
                }

                System.out.println("Scheduled Appointments:");
                for (String appt : appointments) {
                    System.out.println(appt);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid appointment time. Check operational hours.");
            }
        });
    }

    private static boolean validateAppointment(String dateStr, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr + " " + timeStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int hour = cal.get(Calendar.HOUR_OF_DAY);

            return dayOfWeek != Calendar.SUNDAY && !((dayOfWeek == Calendar.SATURDAY && (hour < 8 || hour > 17))
                    || (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY && (hour < 8 || hour > 20)));
        } catch (ParseException e) {
            return false;
        }
    }

    private static void saveToFile(String name, String phone, String email, String date, String time) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("appointments.txt", true))) {
            writer.println("Name: " + name);
            writer.println("Phone: " + phone);
            writer.println("Email: " + email);
            writer.println("Appointment: " + date + " at " + time);
            writer.println("------------------------");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
