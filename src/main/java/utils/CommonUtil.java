package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class CommonUtil {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static Properties props = new Properties();

	public static synchronized void loadProps() {
		try {
			props.load(new FileInputStream(ConstUtil.PROPSFILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will send an email to specified people with an attachment
	 * 
	 * @param to
	 * @param cc
	 * @param attachmentSource
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void sendEmail(String to, String cc, String attachmentSource) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(ConstUtil.PROPSFILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (props.getProperty("emailreportswitch").equals("1")) {
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			final String from = "socialmodule@gmail.com";
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, "@lt12345");
				}
			});
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				if (to.contains(";")) {
					String[] toarr = to.split(";");
					for (int i = 0; i < to.split(";").length; i++)
						message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toarr[i]));
				} else
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

				if (cc.contains(";")) {
					String[] ccarr = cc.split(";");
					for (int i = 0; i < cc.split(";").length; i++)
						message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccarr[i]));
				} else
					message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

				message.setSubject("AutomationReport");
				BodyPart messageBodyPart = new MimeBodyPart();

				messageBodyPart.setText(props.getProperty("mail.body"));

				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				messageBodyPart = new MimeBodyPart();

				String filename = attachmentSource;
				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName("AutomationReport.zip");
				multipart.addBodyPart(messageBodyPart);

				message.setContent(multipart);

				Transport.send(message);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			System.out.println("Mail Sent to " + to + "," + cc + " successfully");
		} else {
			System.out.println(
					"Not sending any email as email report switch is disabled. To enable email report switch access config.properties and update emailreportswitch to 1");
		}
	}

	/**
	 * This method is used to archive folder.
	 * 
	 */
	public String zipFile(String fileToZip, boolean excludeContainingFolder) {
		String archivedFile = fileToZip + ".zip";
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(ConstUtil.PROPSFILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (props.getProperty("emailreportswitch").equals("1")) {
			ZipOutputStream zipOut;
			try {
				zipOut = new ZipOutputStream(new FileOutputStream(archivedFile));

				File srcFile = new File(fileToZip);
				if (excludeContainingFolder && srcFile.isDirectory()) {
					for (String fileName : srcFile.list()) {
						addToZip("", fileToZip + "/" + fileName, zipOut);
					}
				} else {
					addToZip("", fileToZip, zipOut);
				}
				zipOut.flush();
				zipOut.close();
				System.out.println("Successfully created " + archivedFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return archivedFile;
	}

	/**
	 * This method will add files to archive.
	 */
	private static void addToZip(String path, String srcFile, ZipOutputStream zipOut) throws IOException {
		File file = new File(srcFile);
		String filePath = "".equals(path) ? file.getName() : path + "/" + file.getName();
		if (file.isDirectory()) {
			for (String fileName : file.list()) {
				addToZip(filePath, srcFile + "/" + fileName, zipOut);
			}
		} else {
			zipOut.putNextEntry(new ZipEntry(filePath));
			FileInputStream in = new FileInputStream(srcFile);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int len;
			while ((len = in.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}
			in.close();
		}
	}


}
