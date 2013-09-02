package com.intuso.utilities.actions;

/**
 * Test the action performer framework
 * @author tclabon
 *
 */
public class ActionTest {

	/**
	 * Start the test
	 */
	public ActionTest() {
		// create the action performer
		ActionPerformer ap = new TestActionPerformer();
		
		// pass it one of our methods
		ap.performAction(new TestAction());
	}
	
	/**
	 * Action subclass for our test to check that the reflection stuff works properly
	 * @author tclabon
	 *
	 */
	@SuppressWarnings("serial")
	private class TestAction extends Action {
		// do nothing
	}
	
	/**
	 * Our action performer with a method for our action type 
	 * @author tclabon
	 *
	 */
	private class TestActionPerformer extends ActionPerformer {
		/**
		 * Perform a test action
		 * @param action the action to perform
		 */
		@SuppressWarnings("unused")
		public void performTestAction(TestAction action) {
			System.out.println("Performed a test action");
		}
	}
	
	/**
	 * Main method
	 * @param args program args
	 */
	@SuppressWarnings("unused")
	public static void main(String args[]) {
		new ActionTest();
	}
}
