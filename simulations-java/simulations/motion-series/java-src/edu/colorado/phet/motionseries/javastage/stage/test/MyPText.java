package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.umd.cs.piccolo.nodes.PText;

public class MyPText extends PText {
    public MyPText(String text, double x, double y, double scale) {
        setText(text);
        setOffset(x, y);
        setScale(scale);
    }

    public MyPText(String text, double x, double y) {
        this(text, x, y, 1.0);
    }
}
