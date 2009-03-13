package edu.colorado.phet.testing;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Spreadsheet {
    private ArrayList entries = new ArrayList();

    public Spreadsheet( Entry[] entries ) {
        this.entries.addAll( Arrays.asList( entries ) );
    }

    public HashSet getUniqueValues( String key ) {
        return new HashSet( Arrays.asList( listValues( key ) ) );
    }

    public String[] listValues( String key ) {
        ArrayList values = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            values.add( entry.getValue( key ) );
        }
        return (String[]) values.toArray( new String[values.size()] );
    }

    public int size() {
        return entries.size();
    }

    public static interface Matcher {
        boolean matches( Entry e );
    }

    public Spreadsheet getMatches( Matcher matcher ) {
        ArrayList matches = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            if ( matcher.matches( entry ) ) {
                matches.add( entry );
            }
        }
        return new Spreadsheet( (Entry[]) matches.toArray( new Entry[matches.size()] ) );
    }

    public Spreadsheet getMatches( String key, String value ) {
        ArrayList matches = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            if ( entry.getValue( key ).equals( value ) ) {
                matches.add( entry );
            }
        }
        return new Spreadsheet( (Entry[]) matches.toArray( new Entry[matches.size()] ) );
    }

    public static Spreadsheet load( File file ) throws IOException {
        CSVReader reader = new CSVReader( new FileReader( file ) );
        List myEntries = reader.readAll();
        ArrayList entries = new ArrayList();

        //start at 1 since 0 is header row
        for ( int i = 1; i < myEntries.size(); i++ ) {
            Entry entry = new Entry( (String[]) myEntries.get( 0 ), (String[]) myEntries.get( i ) );
            entries.add( entry );
        }


        Spreadsheet spreadsheet = new Spreadsheet( (Entry[]) entries.toArray( new Entry[entries.size()] ) );
        return spreadsheet;
    }
}
