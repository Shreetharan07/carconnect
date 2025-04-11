package hexa.org.dao;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;



public class EmailSender {
    public static void sendReservationConfirmation(String toEmail, String customerName, int reservationId, long hours, double cost) {
    	// Sender and receiver
        final String fromEmail = "shreests07@gmail.com";
        

        // Secure password from environment
        final String password = System.getenv("EMAIL_PASS");
        
        

        // Mail server settings
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        props.put("mail.smtp.port", "587");            // TLS Port
        props.put("mail.smtp.auth", "true");           // Enable authentication
        props.put("mail.smtp.starttls.enable", "true");// Enable STARTTLS

        // Create Authenticator
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, "CarConnect 🚗"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Reservation Confirmed ✅");

            String content = String.format(
                "Dear %s,\n\nYour reservation (ID: %d) has been successfully confirmed.\n" +
                "Duration: %d hours\nTotal Cost: ₹%.2f\n\nThank you for choosing CarConnect!\n\n- CarConnect Team 💚",
                customerName, reservationId, hours, cost
            );

            message.setText(content);
            Transport.send(message);
            System.out.println("✅ Email sent to: " + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


