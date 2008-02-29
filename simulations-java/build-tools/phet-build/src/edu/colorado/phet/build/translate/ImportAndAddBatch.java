package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.IOException;

/**
 * Created by: Sam
 * Feb 28, 2008 at 11:55:38 PM
 */
public class ImportAndAddBatch {
    private File baseDir;

    public ImportAndAddBatch( File baseDir ) {
        this.baseDir = baseDir;
    }

    public static void main( String[] args ) throws IOException {

        new ImportAndAddBatch( new File( args[0] ) ).importAndAddBatch( AddTranslation.prompt( "Which Directory to import and batch add?" ) );

    }

    private void importAndAddBatch( String dir ) throws IOException {
        System.out.println( "Importing" );
        new ImportTranslations( baseDir ).importTranslations( new File( dir ) );
        System.out.println( "Finished Importing." );

        System.out.println( "Adding to tigercat" );
        new AddTranslationBatch( baseDir, new File( dir ), AddTranslation.prompt( "username" ), AddTranslation.prompt( "password" ) );
        System.out.println( "Finished Adding" );
    }
}
