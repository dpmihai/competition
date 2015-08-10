package competition.test;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * User: mihai.panaitescu Date: 01-Oct-2010 Time: 15:36:43
 * http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
 */
public class TestConnection {

	public static void main(String[] args) {

		final String user = "dpmihai@gmail.com";
		final String pass = "teo221099";

		try {
			Properties props = new Properties();
			// required for gmail
//			props.put("mail.smtp.auth", "true");
//			props.put("mail.smtp.starttls.enable", "true");
//			props.put("mail.smtp.host", "smtp.gmail.com");
//			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});

			sendEmail(session, "dpmihai@gmail.com", "mihai.panaitescu@asf.ro", "Test", "Salutare");

			System.out.println("success");
		} catch (AuthenticationFailedException e) {
			System.out.println("AuthenticationFailedException - for authentication failures");
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("for other failures");
			e.printStackTrace();
		}

	}

	public static void sendEmail(Session session, String from, String to, String subject, String body)
			throws AddressException, MessagingException {
		MimeMessage message = new MimeMessage(session);
		message.setFrom( new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(body);
		Transport.send(message);
	}
}
