/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CircuitListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:54:25 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ConstantDensityLayout extends CircuitListenerAdapter {
    CCK3Module module;

    public ConstantDensityLayout( CCK3Module module ) {
        this.module = module;
    }

    public void branchesMoved( Branch[] branches ) {
        ArrayList moved = new ArrayList( Arrays.asList( branches ) );
//        int num = module.getParticleSet().numParticles();
//        System.out.println( "num= " + num );
//        relayout( branches );
        ArrayList branchesToRelayout = new ArrayList();
        Branch[] all = module.getCircuit().getBranches();
        for( int i = 0; i < all.length; i++ ) {
            Branch branch = all[i];
            if( branch.getCurrent() != 0 ) {
                branchesToRelayout.add( branch );
            }
            else if( moved.contains( branch ) ) {
                branchesToRelayout.add( branch );
            }
        }
        Branch[] torelayout = (Branch[])branchesToRelayout.toArray( new Branch[0] );
        relayout( torelayout );
//        int numAfter = module.getParticleSet().numParticles();
//        System.out.println( "numAfter = " + numAfter );
    }

    public void relayout( Branch[] branches ) {
        for( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            relayout( branch );
        }
    }

    private void relayout( Branch branch ) {
        ParticleSet ps = module.getParticleSet();
        ParticleSetGraphic psg = module.getParticleSetGraphic();
        Electron[] electrons = ps.removeParticles( branch );
        psg.removeGraphics( electrons );
        if( module.isElectronsVisible() ) {
            double offset = CCK3Module.ELECTRON_DX / 2;
            double startingPoint = offset;
            double endingPoint = branch.getLength() - offset;
            //compress or expand, but fix a particle at startingPoint and endingPoint.
            double L = endingPoint - startingPoint;
            double desiredDensity = 1 / CCK3Module.ELECTRON_DX;
            double N = L * desiredDensity;
            int integralNumberParticles = (int)Math.ceil( N );
            double mydensity = ( integralNumberParticles - 1 ) / L;
            double dx = 1 / mydensity;
            if( mydensity == 0 ) {
                integralNumberParticles = 0;
            }
            for( int i = 0; i < integralNumberParticles; i++ ) {
                double x = i * dx + startingPoint;
                Electron e = new Electron( branch, x );
                ps.addParticle( e );
                psg.addGraphic( e );
            }
        }
    }
}
