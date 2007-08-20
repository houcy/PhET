package edu.colorado.phet.cck.controls;

import edu.colorado.phet.cck.CCKResources;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.ColorDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:48:55 AM
 */

public class BackgroundColorMenuItem extends JMenuItem {
    public BackgroundColorMenuItem( final PhetApplication application, final ICCKModule cck ) {
        super( CCKResources.getString( "OptionsMenu.BackgroundColorMenuItem" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.setMyBackground( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.setMyBackground( orig );
                    }

                    public void ok( Color color ) {
                        cck.setMyBackground( color );
                    }
                };
                ColorDialog.showDialog( CCKResources.getString( "OptionsMenu.BackgroundColorDialogTitle" ),
                                        application.getPhetFrame(), cck.getMyBackground(), listy );
            }
        } );
    }
}
