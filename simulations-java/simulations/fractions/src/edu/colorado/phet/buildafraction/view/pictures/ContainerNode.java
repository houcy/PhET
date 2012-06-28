package edu.colorado.phet.buildafraction.view.pictures;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * Container that can be subdivided into different divisions
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {
    private PImage splitButton;
    final IntegerProperty selectedPieceSize = new IntegerProperty( 1 );
    private final DynamicCursorHandler dynamicCursorHandler;
    private final PictureSceneNode parent;
    private final ContainerContext context;
    private boolean inTargetCell = false;
    public final PNode shapeNode;
    private double initialX;
    private double initialY;
    private double initialScale = 1;
    private ArrayList<VoidFunction0> listeners = new ArrayList<VoidFunction0>();
    private final SpinnerButtonNode leftSpinner;
    private final SpinnerButtonNode rightSpinner;
    private final PImage increaseButton;

    public ContainerNode( PictureSceneNode parent, final ContainerContext context, boolean showIncreaseButton ) {
        this.parent = parent;
        this.context = context;

        splitButton = new PImage( Images.SPLIT_BLUE );
        addChild( splitButton );
        splitButton.setVisible( false );
        splitButton.setPickable( false );

        final BufferedImage greenButton = multiScaleToWidth( PLUS_BUTTON, 50 );
        final BufferedImage greenButtonPressed = multiScaleToWidth( PLUS_BUTTON_PRESSED, 50 );
        increaseButton = new PImage( greenButton ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                boolean pressed = false;

                @Override public void mousePressed( final PInputEvent event ) {
                    setImage( greenButtonPressed );
                    pressed = true;
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    if ( getImage() == greenButtonPressed ) {
                        fireIncreaseEvent();
                    }
                    setImage( greenButton );
                }

                @Override public void mouseExited( final PInputEvent event ) {
                    setImage( greenButton );
                }

                @Override public void mouseEntered( final PInputEvent event ) {
                    if ( pressed ) {
                        setImage( greenButtonPressed );
                    }
                }
            } );
        }};
        splitButton.translate( -splitButton.getFullBounds().getWidth(),
                               -splitButton.getFullBounds().getHeight() );
        dynamicCursorHandler = new DynamicCursorHandler( Cursor.HAND_CURSOR );
        splitButton.addInputEventListener( dynamicCursorHandler );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( final PInputEvent event ) {
                SimSharingManager.sendButtonPressed( null );
                splitAll();
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        final VoidFunction1<Boolean> increment = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                selectedPieceSize.increment();
            }
        };
        final VoidFunction1<Boolean> decrement = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                selectedPieceSize.decrement();
            }
        };
        shapeNode = new PNode() {{
            selectedPieceSize.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer number ) {
                    removeAllChildren();
                    addChild( new SingleContainerNode( number ) );
                }
            } );
        }};
        leftSpinner = new SpinnerButtonNode( spinnerImage( LEFT_BUTTON_UP ), spinnerImage( LEFT_BUTTON_PRESSED ), spinnerImage( LEFT_BUTTON_GRAY ), decrement, selectedPieceSize.greaterThan( 1 ) );
        rightSpinner = new SpinnerButtonNode( spinnerImage( RIGHT_BUTTON_UP ), spinnerImage( RIGHT_BUTTON_PRESSED ), spinnerImage( RIGHT_BUTTON_GRAY ), increment, selectedPieceSize.lessThan( 6 ) );
        addChild( new VBox( shapeNode,
                            new HBox( leftSpinner, rightSpinner ) ) );

        if ( showIncreaseButton ) {
            addChild( increaseButton );
            increaseButton.setOffset( shapeNode.getFullBounds().getMaxX() + AbstractFractionsCanvas.INSET, shapeNode.getFullBounds().getCenterY() - increaseButton.getFullBounds().getHeight() / 2 );
        }
    }

    private void fireIncreaseEvent() {
        final SingleContainerNode child = new SingleContainerNode( selectedPieceSize.get() );
        child.setOffset( shapeNode.getFullBounds().getMaxX() + AbstractFractionsCanvas.INSET, shapeNode.getFullBounds().getY() );
        addChild( child );
    }

    public static BufferedImage spinnerImage( final BufferedImage image ) { return multiScaleToWidth( image, 50 ); }

    public static F<ContainerNode, Boolean> _isInTargetCell = new F<ContainerNode, Boolean>() {
        @Override public Boolean f( final ContainerNode containerNode ) {
            return containerNode.isInTargetCell();
        }
    };

    public static F<ContainerNode, Fraction> _getFractionValue = new F<ContainerNode, Fraction>() {
        @Override public Fraction f( final ContainerNode containerNode ) {
            return containerNode.getFractionValue();
        }
    };

    public double getYOffsetForContainer() { return shapeNode.getYOffset(); }

    public void splitAll() {
        int numPieces = getChildPieces().length();
        double separationBetweenPieces = 4;
        double totalDeltaSpacing = separationBetweenPieces * ( numPieces - 1 );
        int index = 0;
        LinearFunction f = new LinearFunction( 0, numPieces - 1, -totalDeltaSpacing / 2, totalDeltaSpacing / 2 );
        for ( RectangularPiece child : getChildPieces() ) {
            parent.splitPieceFromContainer( child, this, numPieces == 1 ? 0 : f.evaluate( index++ ) );
        }
        PInterpolatingActivity activity = splitButton.animateToTransparency( 0, 200 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                splitButton.setVisible( false );
                splitButton.setPickable( false );
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        context.syncModelFractions();
        notifyListeners();
    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
    }

    public void setInitialPosition( final double x, final double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }

    public void animateHome() {
        animateToPositionScaleRotation( initialX, initialY, initialScale, 0, 200 );
        leftSpinner.animateToTransparency( 1, 200 );
        rightSpinner.animateToTransparency( 1, 200 );
    }

    public void addPiece( final RectangularPiece piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        if ( !splitButton.getVisible() ) {
            splitButton.setVisible( true );
            splitButton.setPickable( true );
            splitButton.setTransparency( 0 );
            splitButton.animateToTransparency( 1, 200 );
            dynamicCursorHandler.setCursor( Cursor.HAND_CURSOR );
        }
        notifyListeners();
    }

    private void notifyListeners() {
        for ( VoidFunction0 listener : listeners ) {
            listener.apply();
        }
    }

//    public static Rectangle2D.Double createRect( int number ) {
//        final double pieceWidth = width / number;
//        return new Rectangle2D.Double( pieceWidth * number, 0, pieceWidth, height );
//    }

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
        List<RectangularPiece> children = getChildPieces();
        return children.length() == 0 ? 0 :
               fj.data.List.iterableList( children ).maximum( FJUtils.ord( new F<RectangularPiece, Double>() {
                   @Override public Double f( final RectangularPiece r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }

    private List<RectangularPiece> getChildPieces() {
        ArrayList<RectangularPiece> children = new ArrayList<RectangularPiece>();
        for ( Object c : getChildrenReference() ) {
            if ( c instanceof RectangularPiece ) {
                children.add( (RectangularPiece) c );
            }
        }
        return List.iterableList( children );
    }

    public Fraction getFractionValue() {
        return Fraction.sum( getChildPieces().map( new F<RectangularPiece, Fraction>() {
            @Override public Fraction f( final RectangularPiece r ) {
                return r.toFraction();
            }
        } ) );
    }

    //Get rid of it because it disrupts the layout when dropping into the scoring cell.
    public void removeSplitButton() { removeChild( splitButton ); }

    public void addBackSplitButton() { addChild( splitButton ); }

    public boolean isAtStartingLocation() { return getXOffset() == initialX && getYOffset() == initialY; }

    public static F<ContainerNode, Boolean> _isAtStartingLocation = new F<ContainerNode, Boolean>() {
        @Override public Boolean f( final ContainerNode containerNode ) {
            return containerNode.isAtStartingLocation();
        }
    };

    public Boolean isInTargetCell() {return inTargetCell;}

    public void setInTargetCell( final boolean inTargetCell ) {
        this.inTargetCell = inTargetCell;
        leftSpinner.animateToTransparency( inTargetCell ? 0 : 1, 200 );
        rightSpinner.animateToTransparency( inTargetCell ? 0 : 1, 200 );
    }

    public void setInitialState( final double x, final double y, final double scale ) {
        this.initialX = x;
        this.initialY = y;
        this.initialScale = scale;
        setOffset( x, y );
        setScale( scale );
    }

    public void addListener( final VoidFunction0 listener ) {
        listeners.add( listener );
    }

    public boolean isInToolbox() {
        return isAtStartingLocation() && initialY > 600;
    }

    private class SingleContainerNode extends PNode {
        private SingleContainerNode( final int number ) {

            SimpleContainerNode node = new SimpleContainerNode( number, Color.white ) {{
                for ( int i = 0; i < number; i++ ) {
                    final double pieceWidth = width / number;
                    addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), Color.white, new BasicStroke( 1 ), Color.black ) );
                }
                //Thicker outer stroke
                addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 2 ), Color.black ) );
                addInputEventListener( new SimSharingDragHandler( null, true ) {
                    @Override protected void startDrag( final PInputEvent event ) {
                        super.startDrag( event );
                        ContainerNode.this.moveToFront();
                        addActivity( new AnimateToScale( ContainerNode.this, 1.0, 200 ) );
                        notifyListeners();
                    }

                    @Override protected void drag( final PInputEvent event ) {
                        super.drag( event );
                        final PDimension delta = event.getDeltaRelativeTo( getParent() );
                        ContainerNode.this.translate( delta.width, delta.height );
                        notifyListeners();
                    }

                    @Override protected void endDrag( final PInputEvent event ) {
                        super.endDrag( event );
                        context.endDrag( ContainerNode.this, event );
                        notifyListeners();
                    }
                } );
                addInputEventListener( new CursorHandler() );
            }};
            addChild( node );

        }
    }
}