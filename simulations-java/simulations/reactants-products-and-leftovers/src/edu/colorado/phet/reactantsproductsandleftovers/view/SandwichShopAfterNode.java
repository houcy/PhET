package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.BreadLeftoverDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.CheeseLeftoverDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.MeatLeftoverDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.SandwichesQuantityDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double DISPLAYS_X_SPACING = 40;
    private static final double Y_MARGIN = 25;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final OldSandwichShop model;

    private final BoxNode boxNode;
    private final PComposite sandwichesParent, breadParent, meatParent, cheeseParent;
    private final ArrayList<OldSandwichNode> sandwichesList;
    private final ArrayList<BreadNode> breadList;
    private final ArrayList<MeatNode> meatList;
    private final ArrayList<CheeseNode> cheeseList;
    private final PNode sandwichesNode;
    
    public SandwichShopAfterNode( final OldSandwichShop model ) {
        super();
        
        this.model = model;
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        sandwichesList = new ArrayList<OldSandwichNode>();
        breadList = new ArrayList<BreadNode>();
        meatList = new ArrayList<MeatNode>();
        cheeseList = new ArrayList<CheeseNode>();
        
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        sandwichesParent = new PComposite();
        addChild( sandwichesParent );
        
        breadParent = new PComposite();
        addChild( breadParent );
        
        meatParent = new PComposite();
        addChild( meatParent );
        
        cheeseParent = new PComposite();
        addChild( cheeseParent );
        
        PText titleNode = new PText( RPALStrings.LABEL_AFTER_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        PNode valuesNode = new PNode();
        addChild( valuesNode );
        sandwichesNode = new SandwichesQuantityDisplayNode( model );
        PNode breadNode = new BreadLeftoverDisplayNode( model );
        PNode meatNode = new MeatLeftoverDisplayNode( model );
        PNode cheeseNode = new CheeseLeftoverDisplayNode( model );
        valuesNode.addChild( sandwichesNode );
        valuesNode.addChild( breadNode );
        valuesNode.addChild( meatNode );
        valuesNode.addChild( cheeseNode );
        double sandwichWidth = sandwichesNode.getFullBoundsReference().getWidth();
        double breadWidth = breadNode.getFullBoundsReference().getWidth();
        double meatWidth = meatNode.getFullBoundsReference().getWidth();
        double cheeseWidth = cheeseNode.getFullBoundsReference().getWidth();
        double maxWidth = Math.max( sandwichWidth, Math.max( breadWidth, Math.max( meatWidth, cheeseWidth ) ) );
        double deltaX = maxWidth + DISPLAYS_X_SPACING;
        double xOffset = 0;
        sandwichesNode.setOffset( xOffset, 0 );
        xOffset += deltaX;
        breadNode.setOffset( xOffset, 0 );
        xOffset += deltaX;
        meatNode.setOffset( xOffset, 0 );
        xOffset += deltaX;
        cheeseNode.setOffset( xOffset, 0 );
        
        // leftovers label
        double width = cheeseNode.getFullBoundsReference().getMaxX() - breadNode.getFullBoundsReference().getMinX();
        PNode leftoversLabel = new LeftoversLabelNode( width );
        addChild( leftoversLabel ); // add after using valueNodes bounds
        
        // layout, origin at upper-left corner of box
        boxNode.setOffset( 0, 0 );
        // title
        double x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        double y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 10;
        titleNode.setOffset( x, y );
        // value display
        x = boxNode.getFullBoundsReference().getCenterX() - ( valuesNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( valuesNode ) + 15; //XXX
        y = boxNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( valuesNode ) + 15;
        valuesNode.setOffset( x, y );
        // sandwiches
        x = 20; //XXX
        y = boxNode.getFullBoundsReference().getHeight() - 50; //XXX
        sandwichesParent.setOffset( x, y );
        // bread
        x =+ 125; //XXX
        breadParent.setOffset( x, y );
        // meat
        x += 100; //XXX
        meatParent.setOffset( x, y );
        // cheese
        x += 100; //XXX
        cheeseParent.setOffset( x, y );
        // leftovers label
        x = 125; //XXX
        y = valuesNode.getFullBoundsReference().getMaxY() + 2;
        leftoversLabel.setOffset( x, y );

        update();
    }
    
    private void update() {
        
        sandwichesNode.setVisible( model.getFormula().isReaction() );
        
        // sandwiches
        if ( model.getSandwiches() < sandwichesList.size() ) {
            while ( model.getSandwiches() < sandwichesList.size() ) {
                OldSandwichNode node = sandwichesList.get( sandwichesList.size() - 1 );
                sandwichesParent.removeChild( node );
                sandwichesList.remove( node );
            }
        }
        else {
            while ( model.getSandwiches() > sandwichesList.size() ) {
                OldSandwichNode node = new OldSandwichNode( model.getFormula() );
                sandwichesParent.addChild( node );
                sandwichesList.add( node );
                node.scale( REACTANTS_SCALE );
                if ( sandwichesParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = sandwichesParent.getChild( sandwichesParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - PNodeLayoutUtils.getOriginYOffset( node )- Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // bread
        if ( model.getBreadLeftover() < breadList.size() ) {
            while ( model.getBreadLeftover() < breadList.size() ) {
                BreadNode node = breadList.get( breadList.size() - 1 );
                breadParent.removeChild( node );
                breadList.remove( node );
            }
        }
        else {
            while ( model.getBreadLeftover() > breadList.size() ) {
                BreadNode node = new BreadNode();
                breadParent.addChild( node );
                breadList.add( node );
                node.scale( REACTANTS_SCALE );
                if ( breadParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = breadParent.getChild( breadParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // meat
        if ( model.getMeatLeftover() < meatList.size() ) {
            while ( model.getMeatLeftover() < meatList.size() ) {
                MeatNode node = meatList.get( meatList.size() - 1 );
                meatParent.removeChild( node );
                meatList.remove( node );
            }
        }
        else {
            while ( model.getMeatLeftover() > meatList.size() ) {
                MeatNode node = new MeatNode();
                meatParent.addChild( node );
                meatList.add( node );
                node.scale( REACTANTS_SCALE );
                if ( meatParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = meatParent.getChild( meatParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // cheese
        if ( model.getCheeseLeftover() < cheeseList.size() ) {
            while ( model.getCheeseLeftover() < cheeseList.size() ) {
                CheeseNode node = cheeseList.get( cheeseList.size() - 1 );
                cheeseParent.removeChild( node );
                cheeseList.remove( node );
            }
        }
        else {
            while ( model.getCheeseLeftover() > cheeseList.size() ) {
                CheeseNode node = new CheeseNode();
                cheeseParent.addChild( node );
                cheeseList.add( node );
                node.scale( REACTANTS_SCALE );
                if ( cheeseParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = cheeseParent.getChild( cheeseParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
    }
}
