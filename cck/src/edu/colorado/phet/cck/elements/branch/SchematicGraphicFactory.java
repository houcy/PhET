/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.bulb.BulbGraphic;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.Bulb;
import edu.colorado.phet.cck.elements.branch.components.Switch;
import edu.colorado.phet.cck.elements.branch.components.AmmeterBranch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:38:25 PM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class SchematicGraphicFactory implements BranchGraphicFactory {
    private static Stroke highlightStroke = new BasicStroke(12);
    private static Color highlightColor = Color.yellow;
    static Stroke wireStroke = new BasicStroke(7);
    private static Stroke bulbHighlightStroke = new BasicStroke(3);

    CCK2Module module;
    private BufferedImage switchImage;
    private ModelViewTransform2d transform;
    private Circuit circuit;
    private BufferedImage bulbImage;
    private BufferedImage resistorImage;
    private BufferedImage batteryImage;
    private Color wireColor = Color.black;

    public SchematicGraphicFactory(CCK2Module module, BufferedImage switchImage) throws IOException {
        this.module = module;
        this.switchImage = switchImage;
        this.transform = module.getTransform();
        this.batteryImage = module.getSchematicImageSuite().getBatteryImage();
        this.circuit = module.getCircuit();
        bulbImage = module.getImageSuite().getBulbImage();
        this.resistorImage = module.getSchematicImageSuite().getResistorImage();
    }

    public DefaultCompositeBranchGraphic getSwitchGraphic(Switch branch) {
        SwitchGraphic sg = new SwitchGraphic(circuit, transform, branch, wireColor, wireStroke, module, switchImage, module.getImageSuite().getImageHandle(), highlightStroke, highlightColor);
        DefaultCompositeBranchGraphic dcogs = new DefaultCompositeBranchGraphic(transform, branch, module, sg);
        return dcogs;
    }

    public AbstractBranchGraphic getBulbGraphic(Bulb bulb) {
        BulbGraphic bg = new BulbGraphic(circuit, transform, bulb, module, bulbImage, bulbHighlightStroke, highlightColor);
        return bg;
    }

    public AbstractBranchGraphic getResistorGraphic(Branch resistor) {
        ImageBranchGraphic ibg = new ImageBranchGraphic(circuit, module.getTransform(), resistor, wireColor, wireStroke, module, resistorImage, highlightStroke, highlightColor);
        DefaultCompositeBranchGraphic gr = new DefaultCompositeBranchGraphic(transform, resistor, module, ibg);
        return gr;
    }

    public DefaultCompositeBranchGraphic getWireGraphic(Branch wire) {
        BranchGraphic beegy = new BranchGraphic(circuit, module.getTransform(), wire, wireColor, wireStroke, module, highlightColor, highlightStroke);
        DefaultCompositeBranchGraphic bg = new DefaultCompositeBranchGraphic(transform, wire, module, beegy);
        wire.addSelectionListener(beegy);
        return bg;
    }

    public AbstractBranchGraphic getBatteryGraphic(Battery branch) {
        if (branch.DX == 0) {
            return getImageGraphic(circuit, transform, branch, module, batteryImage);
        }
        BatteryGraphic bg = new BatteryGraphic(circuit, transform, branch, module, batteryImage, DefaultCompositeBranchGraphic.JUNCTION_STROKE, highlightColor);
        return bg;
    }

    public DefaultCompositeBranchGraphic getImageGraphic(Circuit circuit, ModelViewTransform2d transform, Branch branch, CCK2Module module, BufferedImage image) {
        ImageBranchGraphic ibg = new ImageBranchGraphic(circuit, module.getTransform(), branch, wireColor, wireStroke, module, image, highlightStroke, highlightColor);
        DefaultCompositeBranchGraphic gr = new DefaultCompositeBranchGraphic(transform, branch, module, ibg);
        return gr;
    }

    public void apply(CCK2Module cck2Module) {
        Circuit c = cck2Module.getCircuit();
        for (int i = 0; i < c.numBranches(); i++) {
            Branch b = c.branchAt(i);
        }
    }

    public AbstractBranchGraphic getAmmeterBranchGraphic(AmmeterBranch resistor) {
                BufferedImage ammeterImage=module.getImageSuite().getAmmeterImage();
        ImageBranchGraphic ibg = new ImageBranchGraphic(circuit, module.getTransform(), resistor, wireColor, wireStroke, module, ammeterImage, LifelikeGraphicFactory.branchStroke, highlightColor);
        CurrentReadout cb=new CurrentReadout(true,ibg);
        ibg.addGraphicAfterImage(cb);
        DefaultCompositeBranchGraphic gr = new DefaultCompositeBranchGraphic(transform, resistor, module, ibg);
        return gr;
    }
}
