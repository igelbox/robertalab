package de.fhg.iais.roberta.util;

import java.io.FileReader;
import java.sql.Timestamp;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.brick.BrickCommunicationData;
import de.fhg.iais.roberta.brick.BrickCommunicationData.State;
import de.fhg.iais.roberta.brick.BrickCommunicator;
import de.fhg.iais.roberta.persistence.AbstractProcessor;
import de.fhg.iais.roberta.persistence.util.HttpSessionState;

public class Util {
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);
    private static final String PROPERTY_DEFAULT_PATH = "openRoberta.properties";
    private static final String[] reservedWords = new String[] {
        //  @formatter:off
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
        "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
        "throw", "throws", "transient", "true", "try", "void", "volatile", "while"
        //  @formatter:on
        };

    private static final AtomicInteger errorTicketNumber = new AtomicInteger(0);

    private Util() {
        // no objects
    }

    /**
     * load the OpenRoberta properties. The URI of the properties refers either to the file system or to the classpath. To be used for production and test. If
     * the parameters is null,
     * the classpath is searched for a default property file.<br>
     * <br>
     * The URI start with "file:" if a path of the file system should be used or starts with "classpath:" if the properties should be loaded as a resource
     * from the classpath. If <code>null</code>, the resource is loaded from the classpath using the default name "openRoberta.properties".
     *
     * @param propertyURI URI of the property file. May be null
     * @return the properties. Returns null, if errors occur (file not found, ...)
     */
    public static Properties loadProperties(String propertyURI) {
        Properties properties = new Properties();
        try {
            if ( propertyURI == null ) {
                LOG.info("properties from classpath. Using the resource: " + PROPERTY_DEFAULT_PATH);
                properties.load(Util.class.getClassLoader().getResourceAsStream(PROPERTY_DEFAULT_PATH));
            } else if ( propertyURI.startsWith("file:") ) {
                String filesystemPathName = propertyURI.substring(5);
                LOG.info("properties from file system. Path: " + filesystemPathName);
                properties.load(new FileReader(filesystemPathName));
            } else if ( propertyURI.startsWith("classpath:") ) {
                String classPathName = propertyURI.substring(10);
                LOG.info("properties from classpath. Using the resource: " + classPathName);
                properties.load(Util.class.getClassLoader().getResourceAsStream(classPathName));
            } else {
                LOG.error("Could not load properties. Invalid URI: " + propertyURI);
                return null;
            }
            return properties;
        } catch ( Exception e ) {
            LOG.error("Could not load properties. Inspect the stacktrace", e);
            return null;
        }
    }

    /**
     * Check whether a String is a valid Java identifier. It is checked, that no reserved word is used
     *
     * @param s String to check
     * @return <code>true</code> if the given String is a valid Java
     *         identifier; <code>false</code> otherwise.
     */
    public final static boolean isValidJavaIdentifier(String s) {
        if ( s == null || s.length() == 0 ) {
            return false;
        }
        CharacterIterator citer = new StringCharacterIterator(s);
        // first
        char c = citer.first();
        if ( c == CharacterIterator.DONE ) {
            return false;
        }
        if ( !Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c) ) {
            return false;
        }
        // remainder
        c = citer.next();
        while ( c != CharacterIterator.DONE ) {
            if ( !Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c) ) {
                return false;
            }
            c = citer.next();
        }
        return Arrays.binarySearch(reservedWords, s) < 0;
    }

    /**
     * get the actual date as timestamp
     *
     * @return the actual date as timestamp
     */
    public static Timestamp getNow() {
        return new Timestamp(new Date().getTime());
    }

    public static void addResultInfo(JSONObject response, AbstractProcessor processor) throws JSONException {
        String realKey = processor.getMessage().getKey();
        response.put("rc", processor.getRC());
        response.put("message", realKey);
        response.put("cause", realKey);
        response.put("parameter", processor.getParameter()); // if getParameters returns null, nothing bad happens :-)
    }

    public static JSONObject addSuccessInfo(JSONObject response, Key key) throws JSONException {
        addResultInfo(response, "ok", key);
        return response;
    }

    public static JSONObject addErrorInfo(JSONObject response, Key key) throws JSONException {
        addResultInfo(response, "error", key);
        return response;
    }

    private static void addResultInfo(JSONObject response, String rc, Key key) throws JSONException {
        String realKey = key.getKey();
        response.put("rc", rc);
        response.put("message", realKey);
        response.put("cause", realKey);
    }

    /**
     * add information for the Javascript client to the result json, especially about the state of the robot.<br>
     * This method must be <b>total</b>, i.e. must
     * <b>never</b> throw exceptions.
     *
     * @param response the response object to enrich with data
     * @param httpSessionState needed to access the token
     * @param brickCommunicator needed to access the robot's state
     */
    public static void addFrontendInfo(JSONObject response, HttpSessionState httpSessionState, BrickCommunicator brickCommunicator) {
        try {
            response.put("serverTime", new Date());
            if ( httpSessionState != null ) {
                String token = httpSessionState.getToken();
                if ( token != null ) {
                    BrickCommunicationData state = brickCommunicator.getState(token);
                    if ( state != null ) {
                        long elapsedMsecOfStartOfLastRequest = state.getElapsedMsecOfStartOfLastRequest();
                        response.put("robot.wait", elapsedMsecOfStartOfLastRequest);
                        response.put("robot.battery", state.getBattery());
                        response.put("robot.name", state.getRobotName());
                        State communicationState = state.getState();
                        String infoAboutState;
                        if ( communicationState == State.BRICK_IS_BUSY ) {
                            infoAboutState = "busy";
                        } else if ( communicationState == State.WAIT_FOR_PUSH_CMD_FROM_BRICK && elapsedMsecOfStartOfLastRequest > 5000 ) {
                            infoAboutState = "disconnected";
                        } else if ( communicationState == State.BRICK_WAITING_FOR_PUSH_FROM_SERVER ) {
                            infoAboutState = "wait";
                        } else {
                            infoAboutState = "wait"; // is there a need to distinguish the communication state more detailed?
                        }
                        response.put("robot.state", infoAboutState);
                    }
                }
            }
        } catch ( Exception e ) {
            LOG.error("when adding info for the client, an unexpected exception occured. Some info for the client may be missing", e);
        }
    }

    public static String getErrorTicketId() {
        return "E-" + errorTicketNumber.incrementAndGet();
    }
}
