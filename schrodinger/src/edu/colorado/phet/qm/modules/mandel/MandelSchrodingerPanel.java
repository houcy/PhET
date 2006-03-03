/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:03:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelSchrodingerPanel extends HighIntensitySchrodingerPanel {
    private MandelModule mandelModule;

    public MandelSchrodingerPanel( MandelModule mandelModule ) {
        super( mandelModule );
        this.mandelModule = mandelModule;
    }

    protected SchrodingerScreenNode createSchrodingerScreenNode( SchrodingerModule module ) {
        return new MandelSchrodingerScreenNode( module, this );
    }

    protected void doAddGunControlPanel() {
//        super.doAddGunControlPanel();//not.
    }

    protected MandelModule getMandelModule() {
        return mandelModule;
    }

    protected HighIntensityGunGraphic createGun() {
        return new MandelGunSet( this );
    }

    protected boolean useGunChooserGraphic() {
        return false;
    }
}
