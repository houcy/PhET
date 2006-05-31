/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.actions.AutoUpdateAction;

import javax.swing.*;

/**
 * OptionsMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class OptionsMenu extends JMenu {
    public OptionsMenu() {
        super( "Options" );
        JCheckBoxMenuItem autoUpdateOption = new JCheckBoxMenuItem( "Automatically check for updates" );
        add( autoUpdateOption );
        autoUpdateOption.addActionListener( new AutoUpdateAction( this ) );
    }
}
