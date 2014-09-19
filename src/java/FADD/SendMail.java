package FADD;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
//import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;

public class SendMail{
    private String from = "";
    private String to = "";
    private String subject = "";
    private String message = "";
    private String[] attachments=null;
    
    public SendMail(){}
    
    public SendMail(String from, String to){
        this.from=from;
        this.to=to;
    }
    
    public SendMail(String from, String to, String subject, String message){
        this(from, to);
        this.subject=subject;
        this.message=message;
    }
    
    public SendMail(String from, String to, String subject, String message, String attachments[]){
        this(from, to, subject, message);
        this.attachments=attachments;
    }
    
    public void addAtachments(String[] attachments, Multipart multipart)throws MessagingException, AddressException{
        for(int i = 0; i<= attachments.length -1; i++){
            String filename = attachments[i];
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            
            //use a JAF FileDataSource as it does MIME type detection
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            
            //assume that the filename you want to send is the same as the
            //actual file name - could alter this to remove the file path
            attachmentBodyPart.setFileName(filename);
            
            //add the attachment
            multipart.addBodyPart(attachmentBodyPart);
        }
    }
    
    public boolean send(){
        //Instance class Properties who will have contain the session properties
        Properties props = new Properties();
        // Mail host name, is smtp.gmail.com
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        // Transport Layer Security (TLS) if it able
        props.setProperty("mail.smtp.starttls.enable", "true");
        // Gmail port for send mails
        props.setProperty("mail.smtp.port","587");
        //User Name
        props.setProperty("mail.smtp.user", from);
        // If it requieres user and password for connecting
        props.setProperty("mail.smtp.auth", "true");
        //Instance class Session and give the previous properties 
        Session session = Session.getDefaultInstance(props);
        //We've to put setDebug(true) in order to get more information in Output about what's happening.
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);
        try{
            // Quien envia el correo
            message.setFrom(new InternetAddress(from));
            // A quien va dirigido
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            //message.setText(this.message, "ISO-8859-1", "html");

            BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setText(this.message);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            //addAtachments(attachments, multipart);
            message.setContent(multipart);


            Transport t = session.getTransport("smtp");
            t.connect("faddupchiapas@gmail.com","CTvPacXjMM56yMjR");
            t.sendMessage(message,message.getAllRecipients());
            t.close();
            return true;
        }catch(java.lang.Exception ex){
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
    }
}