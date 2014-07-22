package de.fhg.iais.roberta.ast.syntax.action;

import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.dbc.Assert;

/**
 * This class represents the <b>robActions_motor_getPower</b> block from Blockly into the AST (abstract syntax tree).
 * Object from this class will generate code for returning the power of the motor on given port.<br/>
 * <br/>
 * The client must provide the {@link ActorPort} on which the motor is connected.
 */
public class MotorGetPowerAction extends Action {
    private final ActorPort port;

    private MotorGetPowerAction(ActorPort port) {
        super(Phrase.Kind.MotorGetPowerAction);
        Assert.isTrue(port != null);
        this.port = port;
        setReadOnly();
    }

    /**
     * Creates instance of {@link MotorGetPowerAction}. This instance is read only and can not be modified.
     * 
     * @param port on which the motor is connected that we want to check,
     * @return read only object of class {@link MotorGetPowerAction}.
     */
    public static MotorGetPowerAction make(ActorPort port) {
        return new MotorGetPowerAction(port);
    }

    /**
     * @return the port number on which the motor is connected.
     */
    public ActorPort getPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return "MotorGetPower [port=" + this.port + "]";
    }

    @Override
    public void generateJava(StringBuilder sb, int indentation) {
        // TODO Auto-generated method stub

    }
}
