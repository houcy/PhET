package edu.colorado.phet.common.phetgraphics.test;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;

/**
 * @deprecated use phetapplication and phetapplicationconfig
 */
public class DeprecatedPhetApplicationLauncher {
    private String[] args;
    private String title;
    private String description;
    private String version;
    private ArrayList modules = new ArrayList();

    public DeprecatedPhetApplicationLauncher( String[] args, String title, String description, String version ) {
        this.args = args;
        this.title = title;
        this.description = description;
        this.version = version;
    }

    public void addModule( Module module ) {
        modules.add( module );
    }

    public void startApplication() {
        PhetApplicationConfig config = new PhetApplicationConfig( args, new PhetApplicationConfig.ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                PhetApplication phetApplication = new PhetApplication( config );
                phetApplication.setModules( (Module[]) modules.toArray( new Module[modules.size()] ) );
                return phetApplication;
            }
        }, "phetcommon" );
        config.launchSim();
    }

    public void setModules( Module[] m ) {
        modules.addAll( Arrays.asList( m ) );
    }

    public PhetFrame getPhetFrame() {
        return PhetApplication.instance().getPhetFrame();
    }

    public void addModules( PhetGraphicsModule[] m ) {
        for ( int i = 0; i < m.length; i++ ) {
            addModule( m[i] );
        }
    }
}
