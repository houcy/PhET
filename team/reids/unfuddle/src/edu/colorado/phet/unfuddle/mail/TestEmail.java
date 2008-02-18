package edu.colorado.phet.unfuddle.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Send a simple, single part, text/plain e-mail
public class TestEmail {

    public static void main( String[] args ) {
        String from = args[0];
        String to = args[1];
        String host = args[2];
        sendEmail( from, new String[]{to}, host, "Hello self email body", "Hello self subject" );
    }

    private static void sendEmail( String from, String[] to, String host, String s, String subject ) {
        // Create properties, get Session
        Properties props = new Properties();

        // If using static Transport.send(),
        // need to specify which host to send it to
        props.put( "mail.smtp.host", host );
        // To see what is going on behind the scene
        props.put( "mail.debug", "true" );
        Session session = Session.getInstance( props );

        try {
            // Instantiatee a message
            Message msg = new MimeMessage( session );

            //Set message attributes
            msg.setFrom( new InternetAddress( from ) );
            InternetAddress[] toAddresses = new InternetAddress[to.length];
            for ( int i = 0; i < toAddresses.length; i++ ) {
                toAddresses[i] = new InternetAddress( to[i] );
            }
//            InternetAddress[] address = {new InternetAddress( to )};
            msg.setRecipients( Message.RecipientType.TO, toAddresses );
            msg.setSubject( subject );
            msg.setSentDate( new Date() );

            // Set message content
            msg.setText( s );

            //Send the message
            Transport.send( msg );
        }
        catch( MessagingException mex ) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
    }
}//End of class