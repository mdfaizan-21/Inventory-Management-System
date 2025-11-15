package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.util.Properties;

public class EmailUtil {
	public static void sendReport(String toEmail, String subject, String body, String attachmentPath) {
		final String fromEmail = "d18227518@gmail.com";
		final String password = "veye vrvc nzgw qduv";

//		if (fromEmail == null || password == null) {
//			throw new RuntimeException("❌ Email credentials not set in environment variables!");
//		}

		// SMTP configuration
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);

			// Email body
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(body);

			// Attachment part
			MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.attachFile(new File(attachmentPath));

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(textPart);
			multipart.addBodyPart(attachmentPart);

			message.setContent(multipart);

			Transport.send(message);
			System.out.println("✅ Report sent successfully to " + toEmail);

		} catch (Exception e) {
			System.out.println("❌ Error sending email: " + e.getMessage());
		}
	}

	public static void sendOTP(String toEmail, String subject, String body) {
		final String fromEmail = "d18227518@gmail.com"; // your email
		final String password = "veye vrvc nzgw qduv"; // your app password

//		if (fromEmail == null || password == null) {
//			throw new RuntimeException("❌ Email credentials not set in environment variables!");
//		}

		// SMTP configuration
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);

			// Email body
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(body);

			// Attachment part

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(textPart);

			message.setContent(multipart);

			Transport.send(message);
			System.out.println("✅ OTP sent successfully to " + toEmail);

		} catch (Exception e) {
			System.out.println("❌ Error sending email: " + e.getMessage());
		}
	}

	public static void sendAlert(String toEmail, String subject, String body) {
		final String fromEmail = "d18227518@gmail.com"; // your email
		final String password = "veye vrvc nzgw qduv"; // your app password

//		if (fromEmail == null || password == null) {
//			throw new RuntimeException("❌ Email credentials not set in environment variables!");
//		}

		// SMTP configuration
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(body, "text/html; charset=utf-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(htmlPart);

			message.setContent(multipart);

			Transport.send(message);
		} catch (Exception e) {
			System.out.println("❌ Error sending email: " + e.getMessage());
		}
	}
}
