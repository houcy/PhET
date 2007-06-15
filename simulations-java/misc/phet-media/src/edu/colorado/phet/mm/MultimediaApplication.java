package edu.colorado.phet.mm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.text.DateFormat;
import java.util.*;

/**
 * User: Sam Reid
 * Date: Aug 11, 2006
 * Time: 9:02:20 AM
 * Copyright (c) Aug 11, 2006 by Sam Reid
 */

public class MultimediaApplication {
    private JFrame frame;
    private MultimediaPanel multimediaPanel;
    private ImageEntryList loadedList;
    private static final String PROPERTIES_HEADER = "Phet multimedia properties file.  Do not modify";
    private String PROPERTIES_FILENAME = "phet-mm.properties";
    private final String LAST_SAVED_KEY = "lastsaved";
    private final JTextArea textArea = new JTextArea();
    private ImageEntry[] imageEntries;

    public MultimediaApplication() {
        frame = new JFrame( "PhET Multimedia Browser" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        AbstractAction browseImages = new AbstractAction( "Browse Images" ) {
            public void actionPerformed( ActionEvent e ) {
                setImageEntries( getAllImageEntries() );
                tryAndLoadDefaults();
            }
        };

        multimediaPanel = new MultimediaPanel();
        multimediaPanel.add( new JButton( browseImages ) );

        multimediaPanel.add( new JButton( new AbstractAction( "Load XML" ) {
            public void actionPerformed( ActionEvent e ) {
                loadXML();
            }
        } ) );

        multimediaPanel.add( new JButton( new AbstractAction( "Save XML" ) {
            public void actionPerformed( ActionEvent e ) {
                saveXML();
            }
        } ) );
        multimediaPanel.add( new JButton( new AbstractAction( "Statistics" ) {
            public void actionPerformed( ActionEvent e ) {
                count();
            }
        } ) );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( multimediaPanel, BorderLayout.CENTER );
        textArea.setAutoscrolls( true );
        textArea.setRows( 10 );
        mainPanel.add( new JScrollPane( textArea ), BorderLayout.SOUTH );
        frame.setContentPane( mainPanel );
        frame.setSize( 1000, 1000 );
        appendLine( "For first time use, press Download Images (only needs to be done once)" );
        appendLine( "After images have been downloaded, press Browse Images." );
        appendLine();
    }

    private void appendLine() {
        appendLine( "" );
    }

    private void appendLine( String line ) {
        textArea.append( line + System.getProperty( "line.separator" ) );
        textArea.setCaretPosition( textArea.getDocument().getLength() );
    }

    private void count() {
        int numDone = 0;
        int total = 0;
        int numNonPhet = 0;
        Hashtable sourceTable = new Hashtable();
        for( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];
            total++;
            if( imageEntry.isDone() ) {
                numDone++;
            }
            if( imageEntry.isNonPhet() ) {
                numNonPhet++;
            }
            if( imageEntry.getSource() != null && imageEntry.getSource().trim().length() != 0 ) {
                String source = imageEntry.getSource();
                if( sourceTable.containsKey( source ) ) {
                    Integer value = (Integer)sourceTable.get( source );
                    sourceTable.put( source, new Integer( value.intValue() + 1 ) );
                }
                else {
                    sourceTable.put( source, new Integer( 1 ) );
                }
            }
        }
        DateFormat fulldate = DateFormat.getDateTimeInstance( DateFormat.LONG,
                                                              DateFormat.LONG );
        appendLine( "Count finished on " + fulldate.format( new Date() ) );
        appendLine( "Total number images: " + total );
        appendLine( "Number finished: " + numDone );
        appendLine( "Number Non-phet: " + numNonPhet );
        appendLine( "Sources:" );
        appendLine( sourceTable.toString() );
    }

    public static File getDataDirectory() {
        return new File( "./phet-mm-temp" );
    }

    public static ImageEntry[] getAllImageEntries() {
        ArrayList list = new ArrayList();
        File root = getDataDirectory();
        File[] children = root.listFiles();
        for( int i = 0; i < children.length; i++ ) {
            File simDir = children[i];
            ImageEntry[] e = getImageEntries( simDir );
            for( int j = 0; j < e.length; j++ ) {
                ImageEntry imageEntry = e[j];
                if( !list.contains( imageEntry ) ) {
                    list.add( imageEntry );
                }
            }
        }
        return (ImageEntry[])list.toArray( new ImageEntry[0] );
    }

    public void setImageEntries( ImageEntry[] imageEntries ) {
        this.imageEntries = imageEntries;
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        MultimediaTable table = new MultimediaTable();
        for( int i = 0; i < imageEntries.length; i++ ) {
            table.addEntry( imageEntries[i] );
        }
        JScrollPane comp = new JScrollPane( table );
        comp.setPreferredSize( new Dimension( 800, 700 ) );
        multimediaPanel.add( comp );
        multimediaPanel.invalidate();
        multimediaPanel.revalidate();
        multimediaPanel.doLayout();
        multimediaPanel.updateUI();
        multimediaPanel.paintImmediately( multimediaPanel.getBounds() );


    }

    private void tryAndLoadDefaults() {
        Properties properties = new Properties();
        try {
            File propertyFile = new File( PROPERTIES_FILENAME );
            if( propertyFile.exists() ) {
                properties.load( new FileInputStream( propertyFile ) );
                String lastSaved = properties.getProperty( LAST_SAVED_KEY );
                if( lastSaved != null && lastSaved.trim().length() != 0 ) {
                    File file = new File( lastSaved );
                    if( file.exists() ) {
                        loadXML( file );
                        System.out.println( "MultimediaApplication.browseImages autoloaded XML from " + file.getAbsolutePath() );
                        appendLine( "Autoloaded from " + file.getAbsolutePath() );
                    }
                }
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void loadXML() {
        JFileChooser chooser = new JFileChooser( new File( "." ) );
        int val = chooser.showOpenDialog( multimediaPanel );
        if( val == JFileChooser.APPROVE_OPTION ) {
            loadXML( chooser.getSelectedFile() );
        }
    }

    private void loadXML( File file ) {
        try {
            XMLDecoder decoder = new XMLDecoder( new FileInputStream( file ) );
            loadedList = (ImageEntryList)decoder.readObject();
            decorateAll();
            decoder.close();
            storeLastUsedFile( file );
            appendLine( "Loaded " + file.getAbsolutePath() );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private void decorateAll() {
        for( int i = 0; i < imageEntries.length; i++ ) {
            decorate( imageEntries[i] );
        }
    }

    private void saveXML() {
        JFileChooser dest = new JFileChooser( new File( "." ) );
        int returnVal = dest.showSaveDialog( multimediaPanel );
        if( returnVal == JFileChooser.APPROVE_OPTION ) {
            //            XMLEncoder encoder = new XMLEncoder( new FileOutputStream( "C:\\PhET\\projects\\phet-mm\\simlauncher-module" ) );
            XMLEncoder encoder = null;
            try {
                encoder = new XMLEncoder( new FileOutputStream( dest.getSelectedFile() ) );
            }
            catch( FileNotFoundException e ) {
                e.printStackTrace();
            }
            encoder.writeObject( new ImageEntryList( imageEntries ) );
            encoder.flush();
            encoder.close();
            storeLastUsedFile( dest.getSelectedFile() );
            appendLine( "Saved " + dest.getSelectedFile() );
        }

    }

    private void storeLastUsedFile( File dest ) {
        Properties properties = new Properties();
        properties.setProperty( LAST_SAVED_KEY, dest.getAbsolutePath() );
        try {
            properties.store( new FileOutputStream( PROPERTIES_FILENAME ), PROPERTIES_HEADER );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * This class is serialized, so shouldn't be modified.
     */
    public static class ImageEntryList {
        ArrayList list = new ArrayList();

        public ImageEntryList() {
        }

        public ArrayList getList() {
            return list;
        }

        public void setList( ArrayList list ) {
            this.list = list;
        }

        public ImageEntryList( ImageEntry[] imageEntries ) {
            for( int i = 0; i < imageEntries.length; i++ ) {
                list.add( new ImageEntryBean( imageEntries[i] ) );
            }
        }

        public void decorate( ImageEntry imageEntry ) {
            for( int i = 0; i < list.size(); i++ ) {
                ImageEntryBean imageEntryBean = (ImageEntryBean)list.get( i );
                if( pathMatches( imageEntryBean, imageEntry ) ) {
                    System.out.println( "path matches, imageData=" + imageEntryBean.getPath() + ", entry=" + imageEntry.getFile().getAbsolutePath() );
                    imageEntry.setNonPhet( imageEntryBean.isNonPhet() );
                    imageEntry.setSource( imageEntryBean.getSource() );
                    imageEntry.setNotes( imageEntryBean.getNotes() );
                    imageEntry.setDone( imageEntryBean.isDone() );
                }
            }
        }

        private boolean pathMatches( ImageEntryBean imageEntryBean, ImageEntry imageEntry ) {
            String asub = imageEntryBean.getPath().substring( imageEntryBean.getPath().lastIndexOf( "temp" ) );
            String bsum = imageEntry.getFile().getAbsolutePath().substring( imageEntry.getFile().getAbsolutePath().lastIndexOf( "temp" ) );
            return asub.equals( bsum );
        }
    }

    static String[] suffixes = new String[]{"png", "gif", "jpg", "tif", "tiff"};

    private static ImageEntry[] getImageEntries( File simDir ) {

        ArrayList all = new ArrayList();
        File[] children = simDir.listFiles();
        for( int i = 0; children != null && i < children.length; i++ ) {
            File child = children[i];
            if( child.isFile() ) {
                if( hasSuffix( child.getName(), suffixes ) ) {
                    String path = getPath( getDataDirectory().getParentFile(), child );
                    ImageEntry imageEntry = new ImageEntry( path );
//                    decorate( imageEntry );
                    all.add( imageEntry );
                }
            }
            else {
                ImageEntry[] entries = getImageEntries( child );
                all.addAll( Arrays.asList( entries ) );
            }
        }
        return (ImageEntry[])all.toArray( new ImageEntry[0] );
    }

    public static String getPath( File parent, File child ) {
        String parentAbs = parent.getAbsolutePath();
        String childAbs = child.getAbsolutePath();
        return childAbs.substring( parentAbs.length() + 1 );
    }

    private void decorate( ImageEntry imageEntry ) {
        loadedList.decorate( imageEntry );
    }


    private static boolean hasSuffix( String zipEntryName, String[] suffixes ) {
        boolean image = false;
        for( int i = 0; i < suffixes.length; i++ ) {
            String suffix = suffixes[i];
            if( zipEntryName.toLowerCase().endsWith( suffix ) ) {
                image = true;
                break;
            }
        }
        return image;
    }

    public static void main( String[] args ) {
        new MultimediaApplication().start();
    }

    public void start() {
        frame.setVisible( true );
    }
}
