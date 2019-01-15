package de.fhg.iais.roberta.visitor.collect;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.actors.arduino.PinReadValueAction;
import de.fhg.iais.roberta.syntax.actors.arduino.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.actors.arduino.RelayAction;
import de.fhg.iais.roberta.syntax.sensor.generic.VemlLightSensor;
import de.fhg.iais.roberta.visitor.hardware.IArduinoVisitor;

/**
 * This visitor collects information for used actors and sensors in blockly program.
 *
 * @author VinArt
 */
public final class SenseboxUsedHardwareCollectorVisitor extends AbstractUsedHardwareCollectorVisitor implements IArduinoVisitor<Void> {

    protected final Set<UsedSensor> usedSensors = new LinkedHashSet<>();

    public SenseboxUsedHardwareCollectorVisitor(ArrayList<ArrayList<Phrase<Void>>> phrasesSet) {
        super(null);
        this.check(phrasesSet);
    }

    @Override
    public Void visitPinWriteValueAction(PinWriteValueAction<Void> pinWriteValueSensor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitPinReadValueAction(PinReadValueAction<Void> pinReadValueActor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitRelayAction(RelayAction<Void> relayAction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitVemlLightSensor(VemlLightSensor<Void> lightSensor) {
        return null;
    }

}
