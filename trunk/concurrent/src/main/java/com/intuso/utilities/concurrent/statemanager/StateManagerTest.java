package com.intuso.utilities.concurrent.statemanager;

/**
 * Test of the state manager
 * @author tclabon
 *
 */
public class StateManagerTest {
	
	/**
	 * Possible States
	 * @author tclabon
	 *
	 */
	private enum States {
		
		/**
		 * Reading
		 */
		READING,
		
		/**
		 * Updating
		 */
		UPDATING
	}
	
	/**
	 * Main method
	 * @param args program args
	 */
	public static void main(String args[]) {
		StateManager<States> sm = new QueueStateManager<States>();
		
		for(int i = 0; i < 100; i++) {
			new Test(sm, (i % 2 == 1 ? States.READING : States.UPDATING)).start();
		}
	}
	
	/**
	 * Class to do the test
	 * @author tclabon
	 *
	 */
	private static class Test extends Thread {
		
		/**
		 * The state manager
		 */
		private StateManager<States> m_sm;
		
		/**
		 * The state to test
		 */
		private States m_state;
		
		/**
		 * Create a new tester
		 * @param sm the state manager to use
		 * @param state the state to use
		 */
		public Test(StateManager<States> sm, States state) {
			m_sm = sm;
			m_state = state;
		}
		
		@Override
		public void run() {
			try {
				m_sm.requestState(m_state);
				System.out.println("Running state " + m_state);
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				return;
			} finally {
				m_sm.finished();
			}
		}
	}
}
