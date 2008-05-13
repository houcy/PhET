package edu.colorado.phet.unfuddle;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:58:28 PM
 */
public class EmailHandler implements IMessageHandler {
    private String fromAddress;
    private String server;
    private String username;
    private String password;
    private EmailList emailList;
    private boolean sendMail;

    public EmailHandler( String fromAddress, String server, String username, String password, EmailList emailList ) {
        this( fromAddress, server, username, password, emailList, true );
    }

    public EmailHandler( String fromAddress, String server, String username, String password, EmailList emailList, boolean sendMail ) {
        this.fromAddress = fromAddress;
        this.server = server;
        this.username = username;
        this.password = password;
        this.emailList = emailList;
        this.sendMail = sendMail;
    }

    public void handleMessage( IMessage m ) throws MessagingException {
        String[] to = new String[0];
        try {
            to = getTo( m.getComponent() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( SAXException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
        if ( sendMail ) {
            if ( to.length == 0 ) {
                System.out.println( "Had a message for delivery, but nobody signed up for notification of " + m.getComponent() );
                if ( m.getComponent() == null ) {
                    System.out.println( "Perhaps this indicates that phet.unfuddled.xml needs to be updated." );
                }
            }
            else {
                EmailAccount.sendEmail( fromAddress, to, server, m.getEmailBody(), m.getEmailSubject(), username, password );
            }
        }
        else {
            System.out.println( "email server would have sent message m: " + m.getEmailSubject() + " to " + Arrays.asList( to ) );
        }
    }

    private String[] getTo( String component ) throws IOException, SAXException, ParserConfigurationException {
        return emailList.getEmailsForComponent( component );
    }
}
