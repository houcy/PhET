package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

public class TraitCanvas extends PhetPCanvas {

    private PNode rootNode;

    public static Dimension canvasSize = new Dimension( 530, 300 );
    private BigVanillaBunny bunny;

    public TraitCanvas() {
        super( canvasSize );

        rootNode = new PNode();
        addWorldChild( rootNode );

        bunny = new BigVanillaBunny();
        bunny.translate( 200, 150 );
        rootNode.addChild( bunny );

        PText traitsText = new PText( "Traits" );
        traitsText.setFont( new PhetFont( 16, true ) );
        traitsText.translate( 200 + ( 86 - traitsText.getWidth() ) / 2, 260 );
        rootNode.addChild( traitsText );

        MutationControlNode earsMutationNode = new EarsMutationNode();
        earsMutationNode.translate( 30, 60 );
        drawConnectingLine( earsMutationNode );
        rootNode.addChild( earsMutationNode );

        MutationControlNode tailMutationNode = new TailMutationNode();
        tailMutationNode.translate( 10, 210 );
        drawConnectingLine( tailMutationNode );
        rootNode.addChild( tailMutationNode );

        MutationControlNode eyesMutationNode = new EyesMutationNode();
        eyesMutationNode.translate( 215, 40 );
        drawConnectingLine( eyesMutationNode );
        rootNode.addChild( eyesMutationNode );

        MutationControlNode teethMutationNode = new TeethMutationNode();
        teethMutationNode.translate( 375, 85 );
        drawConnectingLine( teethMutationNode );
        rootNode.addChild( teethMutationNode );

        MutationControlNode colorMutationNode = new ColorMutationNode();
        colorMutationNode.translate( 330, 210 );
        drawConnectingLine( colorMutationNode );
        rootNode.addChild( colorMutationNode );

        setPreferredSize( canvasSize );

        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    private void drawConnectingLine( MutationControlNode mutationNode ) {
        PPath node = new PPath();

        node.setStroke( new BasicStroke( 1f ) );
        node.setStrokePaint( Color.BLACK );

        Point2D bunnySpot = mutationNode.getBunnyLocation( bunny );
        Point2D nodeCenter = mutationNode.getCenter();

        GeneralPath path = new GeneralPath();
        path.moveTo( bunnySpot.getX(), bunnySpot.getY() );
        path.lineTo( nodeCenter.getX(), nodeCenter.getY() );
        node.setPathTo( path );

        rootNode.addChild( node );
    }

    public void updateLayout() {

    }
}
