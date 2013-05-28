package com.intuso.utilities.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class that all things that perform actions should extend. This class uses reflection to find all the performing methods 
 * @author tclabon
 *
 */
public abstract class ActionPerformer {
	
	/**
	 * Map of all valid perform methods
	 */
	private Map<String, Method> m_valid_perform_methods;
	
	/**
	 * Create a new action performer
	 */
	public ActionPerformer() {
		
		// initiate the map
		m_valid_perform_methods = Collections.synchronizedMap(new HashMap<String, Method>(new HashMap<String, Method>()));
		
		// get the subclass
		Class<?> this_class = this.getClass();
		String name;
		
		// for each of it's methods
		for(Method method : this_class.getMethods()) {
			
			// if it starts with perform
			name = method.getName();
			if(name.toLowerCase().startsWith("perform")) {
				
				// get the parameters
				Class<?>[] arguments = method.getParameterTypes();
				
				// check there's only one...
				if(arguments.length == 1) {
					//... and that it extends Action
					if(Action.class.isAssignableFrom(arguments[0]))
						// add it to the map
						m_valid_perform_methods.put(arguments[0].getName(), method);
					else
						System.err.println("Function " + method.getName() + " starts with \"perform\", takes one argument but the argument does not extend " + Action.class.getName());
				} else
					System.err.println("Function " + method.getName() + " starts with \"perform\" but takes more than one argument");
			}
		}
	}

	/**
	 * Perform an action. 
	 * @param action
	 */
	public final void performAction(Action action) {
		// check if we have a method for this type of action
		Method method = m_valid_perform_methods.get(action.getClass().getName());
		if(method == null)
			throw new UnsupportedOperationException("No perform method for " + action.getClass() + " was found");
		
		// invoke the method
		try {
			method.setAccessible(true);
			method.invoke(this, action);
		} catch (IllegalArgumentException e) {
			System.err.println("Error calling " + method.getName() + " to handle received action");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Error calling " + method.getName() + " to handle received action");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Error calling " + method.getName() + " to handle received action");
			e.printStackTrace();
		} catch(Exception e) {
			// catch any other exception that would've been thrown by the perform method and set it as the error for the action
			action.setException(e);
		}
	}
}
