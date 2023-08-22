

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;
import org.jsoup.Jsoup;

public class Main {
    public static void main(String[] args) throws IOException {

        String host = "imap.gmail.com";
        String mailStoreType = "imap";
        String username = "chandra.hstest@gmail.com";
        String password = "xfzhyvomfhqdyjys";

        check(host, mailStoreType, username, password);



    }

    public static void check(String host, String storeType, String user,
                             String password) {
        try {

            //create properties field
            Properties properties = new Properties();

            properties.put("mail.imaps.host", host);
            properties.put("mail.imaps.port", "993");
            properties.put("mail.imaps.starttls.enable", "true");
            properties.put("mail.imaps.ssl.trust", "*");
            properties.setProperty("mail.store.protocol", "imaps");

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore();

            store.connect(host, 993, user, password);

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int n = messages.length, i = n-1; i > n-6; i--) {
                Message message = messages[i];
                if(message.getSubject().contains("Thank You For Your Order")) {
                    System.out.println("---------------------------------");
                    System.out.println("Email Number " + (i));
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    System.out.println("ReceivedDate: " + message.getReceivedDate());
                    System.out.println("ContentType: " + message.getContentType());
                    printPlainTextContent(message);
                }

            }

            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printPlainTextContent(Message message) throws Exception {
        Object content = message.getContent();

        if (content instanceof String) {
            String mimeType = message.getContentType();
            if (mimeType.startsWith("text/plain")) {
                System.out.println(content);
            } else if (mimeType.startsWith("TEXT/HTML")) {
                String plainText = htmlToPlainText((String) content);
                System.out.println("EmailBody: \n" + plainText);
            }
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    System.out.println(bodyPart.getContent());
                } else if (bodyPart.isMimeType("TEXT/HTML")) {
                    String plainText = htmlToPlainText((String) bodyPart.getContent());
                    System.out.println("EmailBody: \n" + plainText);
                }
            }
        }
    }


    private static String htmlToPlainText(String htmlContent) {
        return Jsoup.parse(htmlContent).text();
    }

}