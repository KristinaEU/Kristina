package eu.kristina.vsm;

import com.sun.jersey.api.core.DefaultResourceConfig;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gregor Mehlmann
 */
public class TurnManager extends DefaultResourceConfig {
    /*
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> objects = new HashSet<>();
        objects.add(ControlService.class);
        return objects;
    }
    */
    private final ControlService mService = new ControlService(this);
    
    @Override
    public Set<Object> getSingletons() {
        final Set<Object> objects = new HashSet<>();
        objects.add(mService);
        return objects; 
    }
    
    
}
