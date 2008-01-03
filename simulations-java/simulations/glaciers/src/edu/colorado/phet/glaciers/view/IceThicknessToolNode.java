/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.colorado.phet.glaciers.model.IceThicknessTool.IceThicknessToolListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;


public class IceThicknessToolNode extends AbstractToolNode {
    
    private static final Font FONT = new PhetDefaultFont( 10 );
    
    private IceThicknessTool _iceThicknessTool;
    private IceThicknessToolListener _iceThicknessToolListener;
    private JLabel _iceThicknessLabel;

    public IceThicknessToolNode( IceThicknessTool iceThicknessTool ) {
        super( iceThicknessTool );
        
        _iceThicknessTool = iceThicknessTool;
        _iceThicknessToolListener = new IceThicknessToolListener() {
            public void thicknessChanged() {
                updateThickness();
            }
        };
        _iceThicknessTool.addListener( _iceThicknessToolListener );
        
        PImage imageNode = new PImage( GlaciersImages.ICE_THICKNESS_TOOL );
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth(), -imageNode.getFullBoundsReference().getHeight() ); // lower right
        
        _iceThicknessLabel = new JLabel();
        _iceThicknessLabel.setFont( FONT );
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
        panel.add( _iceThicknessLabel );
        PSwing panelNode = new PSwing( panel );
        addChild( panelNode );
        panelNode.setOffset( imageNode.getFullBoundsReference().getMaxX() + 2, imageNode.getFullBoundsReference().getMinY() );
        
        // initial state
        updateThickness();
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    private void updateThickness() {
        _iceThicknessLabel.setText( _iceThicknessTool.getThickness() + " " + GlaciersStrings.UNITS_ICE_THICKNESS );
    }
}
