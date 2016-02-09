package eu.kristina.vsm.ssi;

/**
 * @author Gregor Mehlmann
 */
public interface SSIEventHandler {

    // Handle an event from SSI
    // TODO: Use an SSI event object
    public void handle(final String event);
}
