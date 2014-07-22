package de.fhg.iais.roberta.ast.syntax.action;

import java.util.Locale;

import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.dbc.Assert;
import de.fhg.iais.roberta.dbc.DbcException;

/**
 * This class represents the <b>robActions_brickLight_on</b> block from Blockly into the AST (abstract syntax tree).
 * Object from this class will generate code for turning the light on.<br/>
 * <br/>
 * The client must provide the {@link Color} of the lights and the mode of blinking.
 */
public class LightAction extends Action {
    private final Color color;
    private final boolean blink;

    private LightAction(Color color, boolean blink) {
        super(Phrase.Kind.LightAction);
        Assert.isTrue(color != null);
        this.color = color;
        this.blink = blink;
        setReadOnly();
    }

    /**
     * Creates instance of {@link LightAction}. This instance is read only and can not be modified.
     * 
     * @param color of the lights on the brick. All possible colors are defined in {@link Color},
     * @param blink type of the blinking,
     * @return read only object of class {@link LightAction}.
     */
    public static LightAction make(Color color, boolean blink) {
        return new LightAction(color, blink);
    }

    /**
     * @return {@link Color} of the lights.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return type of blinking.
     */
    public boolean isBlink() {
        return this.blink;
    }

    @Override
    public void generateJava(StringBuilder sb, int indentation) {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        return "LightAction [" + this.color + ", " + this.blink + "]";
    }

    /**
     * All possible colors of the lights that the brick have.
     */
    public static enum Color {
        GREEN(), ORANGE(), RED();

        private final String[] values;

        private Color(String... values) {
            this.values = values;
        }

        /**
         * get color from {@link Color} from string parameter. It is possible for one color to have multiple string mappings.
         * Throws exception if the color does not exists.
         * 
         * @param name of the color
         * @return color from the enum {@link Color}
         */
        public static Color get(String s) {
            if ( s == null || s.isEmpty() ) {
                throw new DbcException("Invalid color: " + s);
            }
            String sUpper = s.trim().toUpperCase(Locale.GERMAN);
            for ( Color co : Color.values() ) {
                if ( co.toString().equals(sUpper) ) {
                    return co;
                }
                for ( String value : co.values ) {
                    if ( sUpper.equals(value) ) {
                        return co;
                    }
                }
            }
            throw new DbcException("Invalid color: " + s);
        }
    }
}
