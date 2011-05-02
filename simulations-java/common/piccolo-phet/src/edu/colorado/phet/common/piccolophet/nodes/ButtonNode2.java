// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventListener;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A Piccolo button with interface similar to JButton.
 * Supports both text (plain or HTML) and images.
 * Origin is at the upper left of the bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ButtonNode2 extends PhetPNode {

    // position of text relative to image
    public static enum TextPosition {
        ABOVE, BELOW, LEFT, RIGHT
    }

    private static final double COLOR_SCALING_FACTOR = 0.5; // scaling factor for creating brighter colors

    private final PNode parentNode; // intermediate parent for all nodes created herein
    private final ArrayList<ActionListener> actionListeners;

    // settable properties
    private String text;
    private BufferedImage image, disabledImage;
    private Font font;
    private Color foreground, background, shadowColor, strokeColor;
    private Color disabledForeground, disabledBackground, disabledShadowColor, disabledStrokeColor;
    private TextPosition textPosition;
    private boolean enabled;
    private Insets margin;
    private int imageTextGap; // space between image and text
    private int cornerRadius; // radius on the corners of the button and shadow
    private int shadowOffset; // horizontal and vertical offset of the shadow
    private String toolTipText;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    public ButtonNode2() {
        this( null, null );
    }

    public ButtonNode2( String text ) {
        this( text, null );
    }

    public ButtonNode2( BufferedImage image ) {
        this( null, image );
    }

    public ButtonNode2( String text, BufferedImage image ) {

        this.text = text;
        this.image = image;

        // default settings
        disabledImage = null;
        font = new PhetFont( Font.BOLD, 14 ); //TODO using bold as the default style is a bad practice
        foreground = Color.BLACK;
        background = Color.GRAY;
        shadowColor = new Color( 0f, 0f, 0f, 0.2f ); // translucent black
        strokeColor = Color.BLACK;
        disabledForeground = new Color( 180, 180, 180 ); // medium gray
        disabledBackground = new Color( 210, 210, 210 ); // light gray
        disabledShadowColor = new Color( 0, 0, 0, 0 ); // invisible
        disabledStrokeColor = new Color( 190, 190, 190 );
        textPosition = TextPosition.RIGHT;
        enabled = true;
        margin = new Insets( 3, 10, 3, 10 );
        imageTextGap = 5;
        cornerRadius = 8;
        shadowOffset = 3;
        toolTipText = null;

        // parent of all nodes created herein
        parentNode = new PNode();
        addChild( parentNode );

        actionListeners = new ArrayList<ActionListener>();

        update();
    }

    //------------------------------------------------------------------------
    //
    //------------------------------------------------------------------------

    // completely rebuilds the button when any property changes
    private void update() {

        // add children are parented to this node, so that clients can addChild
        parentNode.removeAllChildren();

        // text and image, with an intermediate parent
        PNode textNode = createTextNode( text, font, foreground, disabledForeground, enabled );
        PNode imageNode = createImageNode( image, disabledImage, enabled );
        PNode textImageParent = new PComposite();
        textImageParent.addChild( textNode );
        textImageParent.addChild( imageNode );

        // layout text and image based
        double textX, imageX = 0;
        double textY, imageY = 0;
        PBounds tb = textNode.getFullBoundsReference();
        PBounds ib = imageNode.getFullBoundsReference();
        if ( textPosition == TextPosition.ABOVE ) {
            textX = 0;
            imageX = tb.getCenterX() - ( ib.getWidth() / 2 );
            textY = 0;
            imageY = tb.getMaxY() + imageTextGap;
        }
        else if ( textPosition == TextPosition.BELOW ) {
            imageX = 0;
            textX = ib.getCenterX() - ( tb.getWidth() / 2 );
            imageY = 0;
            textY = ib.getMaxY() + imageTextGap;
        }
        else if ( textPosition == TextPosition.LEFT ) {
            textX = 0;
            imageX = tb.getMaxX() + imageTextGap;
            textY = 0;
            imageY = tb.getCenterY() - ( ib.getHeight() / 2 );
        }
        else if ( textPosition == TextPosition.RIGHT ) {
            imageX = 0;
            textX = ib.getMaxX() + imageTextGap;
            imageY = 0;
            textY = ib.getCenterY() - ( tb.getHeight() / 2 );
        }
        else {
            throw new UnsupportedOperationException( "unsupported text position: " + textPosition );
        }
        textNode.setOffset( textX, textY );
        imageNode.setOffset( imageX, imageY );

        // gradients
        final double gradientWidth = textImageParent.getFullBoundsReference().getWidth();
        final double gradientHeight = textImageParent.getFullBoundsReference().getHeight();
        final Paint mouseNotOverGradient = createMouseNotOverGradient( gradientWidth, gradientWidth );
        final Paint mouseOverGradient = createMouseOverGradient( gradientWidth, gradientWidth );
        final Paint armedGradient = createArmedGradient( gradientWidth, gradientWidth );

        // button shape
        double width = textImageParent.getFullBoundsReference().getWidth() + margin.left + margin.right;
        double height = textImageParent.getFullBoundsReference().getHeight() + margin.top + margin.bottom;
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double( 0, 0, width, height, cornerRadius, cornerRadius );

        // background
        final PPath backgroundNode = new PPath( buttonShape );
        backgroundNode.addInputEventListener( new CursorHandler() );
        if ( enabled ) {
            backgroundNode.setPaint( mouseNotOverGradient );
            backgroundNode.setStrokePaint( strokeColor );
        }
        else {
            backgroundNode.setPaint( createDisabledGradient( gradientWidth, gradientHeight ) );
            backgroundNode.setStrokePaint( disabledStrokeColor );
        }

        // shadow
        PPath shadowNode = new PPath( buttonShape );
        shadowNode.setPickable( false );
        shadowNode.setOffset( shadowOffset, shadowOffset );
        shadowNode.setStroke( null );
        shadowNode.setPaint( enabled ? shadowColor : disabledShadowColor );

        // text and image are children of background, to simplify interactivity
        backgroundNode.addChild( textImageParent );
        double x = margin.left - PNodeLayoutUtils.getOriginXOffset( textImageParent );
        double y = margin.top - PNodeLayoutUtils.getOriginYOffset( textImageParent );
        textImageParent.setOffset( x, y );

        // shadow behind background
        parentNode.addChild( shadowNode );
        parentNode.addChild( backgroundNode );

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        backgroundNode.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventListener() {

            private boolean focus = false; // true if the button has focus

            public void setFocus( boolean focus ) {
                this.focus = focus;
                if ( enabled ) {
                    backgroundNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                }
            }

            public void setArmed( boolean armed ) {
                if ( armed ) {
                    backgroundNode.setPaint( armedGradient );
                    backgroundNode.setOffset( shadowOffset, shadowOffset );
                }
                else {
                    backgroundNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                    backgroundNode.setOffset( 0, 0 );
                }
            }

            public void fire() {
                notifyActionPerformed();
            }
        } );

        // tool tip
        if ( toolTipText != null ) {
            parentNode.addChild( new ToolTipNode( toolTipText, this ) );
        }

        // ignore events when disabled
        setPickable( enabled );
        setChildrenPickable( enabled );
    }

    //------------------------------------------------------------------------
    // Setters and getters
    //------------------------------------------------------------------------

    public void setText( String text ) {
        if ( text == null || !text.equals( this.text ) ) {
            this.text = text;
            update();
        }
    }

    public String getText() {
        return text;
    }

    public void setImage( BufferedImage image ) {
        if ( image == null || !image.equals( this.image ) ) {
            this.image = image;
            update();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image to be used when the button is disabled.
     * If an image has not been provided for the enabled state, then this disabled image is ignored.
     * If no disabled image is provided, the default behavior is to generate a grayscale version
     * of the enabled image.
     *
     * @param disabledImage
     */
    public void setDisabledImage( BufferedImage disabledImage ) {
        if ( disabledImage == null || !disabledImage.equals( this.disabledImage ) ) {
            this.disabledImage = disabledImage;
            update();
        }
    }

    public BufferedImage getDisabledImage() {
        return disabledImage;
    }

    public void setFont( Font font ) {
        if ( !font.equals( this.font ) ) {
            this.font = font;
            update();
        }
    }

    public Font getFont() {
        return font;
    }

    public void setBackground( Color background ) {
        if ( !background.equals( this.background ) ) {
            this.background = background;
            update();
        }
    }

    public Color getBackground() {
        return background;
    }

    public void setForeground( Color foreground ) {
        if ( !foreground.equals( this.foreground ) ) {
            this.foreground = foreground;
            update();
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setShadowColor( Color shadowColor ) {
        if ( !shadowColor.equals( this.shadowColor ) ) {
            this.shadowColor = shadowColor;
            update();
        }
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setStrokeColor( Color strokeColor ) {
        if ( !strokeColor.equals( this.strokeColor ) ) {
            this.strokeColor = strokeColor;
            update();
        }
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setDisabledBackground( Color disabledBackground ) {
        if ( !disabledBackground.equals( this.disabledBackground ) ) {
            this.disabledBackground = disabledBackground;
            update();
        }
    }

    public Color getDisabledBackground() {
        return disabledBackground;
    }

    public void setDisabledForeground( Color disabledForeground ) {
        if ( !disabledForeground.equals( this.disabledForeground ) ) {
            this.disabledForeground = disabledForeground;
            update();
        }
    }

    public Color getDisabledForeground() {
        return disabledForeground;
    }

    public void setDisabledShadowColor( Color disabledShadowColor ) {
        if ( !disabledShadowColor.equals( this.disabledShadowColor ) ) {
            this.disabledShadowColor = disabledShadowColor;
            update();
        }
    }

    public Color getDisabledShadowColor() {
        return disabledShadowColor;
    }


    public void setDisabledStrokeColor( Color disabledStrokeColor ) {
        if ( !disabledStrokeColor.equals( this.disabledStrokeColor ) ) {
            this.disabledStrokeColor = disabledStrokeColor;
            update();
        }
    }

    public Color getDisabledStrokeColor() {
        return disabledStrokeColor;
    }

    public void setEnabled( boolean enabled ) {
        if ( enabled != this.enabled ) {
            this.enabled = enabled;
            update();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTextPosition( TextPosition textPosition ) {
        if ( textPosition != this.textPosition ) {
            this.textPosition = textPosition;
            update();
        }
    }

    public TextPosition getTextPosition() {
        return textPosition;
    }

    public void setMargin( Insets margin ) {
        if ( !margin.equals( this.margin ) ) {
            this.margin = new Insets( margin.top, margin.left, margin.bottom, margin.right );
            update();
        }
    }

    // Convenience method
    public void setMargin( int top, int left, int bottom, int right ) {
        setMargin( new Insets( top, left, bottom, right ) );
    }

    public Insets getMargin() {
        return new Insets( margin.top, margin.left, margin.bottom, margin.right );
    }

    public void setImageTextGap( int imageTextGap ) {
        if ( imageTextGap != this.imageTextGap ) {
            this.imageTextGap = imageTextGap;
            update();
        }
    }

    public int getImageTextGap() {
        return imageTextGap;
    }

    /**
     * Provides very basic, default tool tip behavior.
     * If you want more control over tool tips, use ToolTipNode directly.
     *
     * @param toolTipText
     */
    public void setToolTipText( String toolTipText ) {
        if ( toolTipText == null || !toolTipText.equals( this.toolTipText ) ) {
            this.toolTipText = toolTipText;
            update();
        }
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setCornerRadius( int cornerRadius ) {
        if ( cornerRadius != this.cornerRadius ) {
            this.cornerRadius = cornerRadius;
            update();
        }
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setShadowOffset( int shadowOffset ) {
        if ( shadowOffset != this.shadowOffset ) {
            this.shadowOffset = shadowOffset;
            update();
        }
    }

    public int getShadowOffset() {
        return shadowOffset;
    }

    //------------------------------------------------------------------------
    // Listeners
    //------------------------------------------------------------------------

    public void addActionListener( ActionListener listener ) {
        actionListeners.add( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        actionListeners.remove( listener );
    }

    private void notifyActionPerformed() {
        ActionEvent event = new ActionEvent( this, 0, "BUTTON_FIRED" );
        for ( ActionListener actionListener : new ArrayList<ActionListener>( actionListeners ) ) {
            actionListener.actionPerformed( event );
        }
    }

    //------------------------------------------------------------------------
    // Node creation utilities
    //------------------------------------------------------------------------

    // Creates an HTML node that corresponds to the enabled state.
    private static PNode createTextNode( String text, Font font, Color textColor, Color disabledTextColor, boolean enabled ) {
        PNode node = null;
        if ( text != null ) {
            final HTMLNode htmlNode = new HTMLNode( text, enabled ? textColor : disabledTextColor );
            htmlNode.setFont( font );
            htmlNode.setPickable( false );
            node = htmlNode;
        }
        else {
            node = new PNode();
        }
        return node;
    }

    // Creates an image node that corresponds to the enabled state.
    private static PNode createImageNode( BufferedImage image, BufferedImage disabledImage, boolean enabled ) {
        PNode node = null;
        if ( image != null ) {
            if ( enabled ) {
                node = new PImage( image );
            }
            else {
                if ( disabledImage != null ) {
                    node = new PImage( disabledImage );
                }
                else {
                    node = new PImage( createGrayscaleImage( image ) );
                }
            }
        }
        else {
            node = new PNode();
        }
        return node;
    }

    // Converts an image to grayscale, to denote that the button is disabled.
    //TODO There are undoubtedly better ways to do this, but this is the only method I found that worked with images that contain transparency.
    private static BufferedImage createGrayscaleImage( BufferedImage colorImage ) {
        ColorSpace cs = ColorSpace.getInstance( ColorSpace.CS_GRAY );
        ColorConvertOp op = new ColorConvertOp( cs, null );
        return op.filter( colorImage, null );
    }

    //------------------------------------------------------------------------
    // Gradient creation utilities
    //------------------------------------------------------------------------

    // When the mouse is not over the node, its specified background color is used.
    protected Paint createMouseNotOverGradient( double width, double height ) {
        return createGradient( createBrighterColor( background ), background, width, height );
    }

    // When the mouse is over the node but not pressed, the button gets brighter.
    protected Paint createMouseOverGradient( double width, double height ) {
        return createGradient( createBrighterColor( createBrighterColor( background ) ), createBrighterColor( background ), width, height );
    }

    // When the button is armed (pressed), the color is similar to mouse-not-over, but the button is brighter at the bottom.
    protected Paint createArmedGradient( double width, double height ) {
        return createGradient( background, createBrighterColor( background ), width, height );
    }

    // When the button is disabled, we use a different color to indicate disabled.
    private Paint createDisabledGradient( double width, double height ) {
        return createGradient( createBrighterColor( disabledBackground ), disabledBackground, width, height );
    }

    /*
    * Creates a gradient that vertically goes from topColor to bottomColor.
    * @param topColor
    * @param bottomColor
    * @return Paint
    */
    private Paint createGradient( Color topColor, Color bottomColor, double width, double height ) {
        if ( useGradient() ) {
            return new GradientPaint( (float) width / 2, 0f, topColor, (float) width * 0.5f, (float) height, bottomColor );
        }
        else {
            return bottomColor;
        }
    }

    // See Unfuddle Ticket #553, GradientPaint crashes Mac.
    private boolean useGradient() {
        return !PhetUtilities.isMacintosh();
    }

    private static Color createBrighterColor( Color origColor ) {
        int red = origColor.getRed() + (int) Math.round( ( 255 - origColor.getRed() ) * COLOR_SCALING_FACTOR );
        int green = origColor.getGreen() + (int) Math.round( ( 255 - origColor.getGreen() ) * COLOR_SCALING_FACTOR );
        int blue = origColor.getBlue() + (int) Math.round( ( 255 - origColor.getBlue() ) * COLOR_SCALING_FACTOR );
        int alpha = origColor.getAlpha(); // preserve transparency of original color, see #2123
        return new Color( red, green, blue, alpha );
    }

    //------------------------------------------------------------------------
    // Test Harness
    //------------------------------------------------------------------------

    public static void main( String[] args ) {

        PhetResources resources = new PhetResources( "phetcommon" );
        BufferedImage image = resources.getImage( "buttons/minimizeButton.png" );
        final BufferedImage disabledImage = resources.getImage( "buttons/maximizeButton.png" );

        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        ButtonNode2 testButton01 = new ButtonNode2( "1. Test Me", image ) {{
            setCornerRadius( 20 );
            setImageTextGap( 20 );
            setMargin( 20, 10, 20, 10 );
            setFont( new PhetFont( 16 ) );
            setBackground( Color.GREEN );
        }};
        testButton01.setOffset( 5, 5 );
        testButton01.addActionListener( listener );

        ButtonNode2 testButton02 = new ButtonNode2( "<html>2. Test <br> Me Too</html>", image ) {{
            setTextPosition( TextPosition.BELOW );
            setFont( new PhetFont( 24 ) );
            setBackground( new Color( 0x99cccc ) );
        }};
        testButton02.setOffset( 200, 5 );
        testButton02.addActionListener( listener );

        ButtonNode2 testButton03 = new ButtonNode2( "<html><center>3. Default Color<br>and Font<center></html>" );
        testButton03.setOffset( 5, 200 );
        testButton03.addActionListener( listener );

        ButtonNode2 testButton04 = new ButtonNode2( "4. Default Font Size" ) {{
            setBackground( new Color( 0xcc3366 ) );
        }};
        testButton04.setOffset( 200, 200 );
        testButton04.addActionListener( listener );

        ButtonNode2 testButton05 = new ButtonNode2( "5. Transparent" ) {{
            setBackground( new Color( 255, 0, 0, 100 ) );
        }};
        testButton05.setOffset( 200, 125 );
        testButton05.addActionListener( listener );

        final ButtonNode2 testButton06 = new ButtonNode2( "6. Test Enabled", image ) {{
            setBackground( new Color( 0, 200, 200 ) );
            setTextPosition( TextPosition.ABOVE );
        }};
        testButton06.setOffset( 230, 300 );
        testButton06.addActionListener( listener );

        ButtonNode2 testButton07 = new ButtonNode2( "7. Toggle Enabled ->" ) {{
            setFont( new PhetFont( Font.ITALIC, 16 ) );
            setForeground( Color.RED );
            setBackground( new Color( 200, 200, 0 ) );
            setToolTipText( "<html>click me to<br>disabled button 6</html>" );
        }};
        testButton07.setOffset( 10, 300 );
        testButton07.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
                testButton06.setEnabled( !testButton06.isEnabled() );
            }
        } );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.addScreenChild( testButton03 );
        canvas.addScreenChild( testButton04 );
        canvas.addScreenChild( testButton05 );
        canvas.addScreenChild( testButton06 );
        canvas.addScreenChild( testButton07 );

        JFrame frame = new JFrame( "ButtonNode.main" );
        frame.setContentPane( canvas );
        frame.setSize( 400, 450 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
