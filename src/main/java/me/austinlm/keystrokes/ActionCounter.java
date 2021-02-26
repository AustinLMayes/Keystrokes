package me.austinlm.keystrokes;

import com.google.common.collect.Queues;
import java.util.Queue;

/**
 * Simple class to keep track of the number of actions performed within a specified period
 */
public class ActionCounter {

	private final Queue<Long> actions = Queues.newPriorityQueue();

	// mills
	private final long period;

	public ActionCounter(long period) {
		this.period = period;
	}

	/**
	 * An action has occured.
	 */
	public void action() {
		this.actions.add(System.currentTimeMillis());
	}

	/**
	 * @return total number of actions performed in the specified period
	 */
	public double getActionsInPeriod() {
		long now = System.currentTimeMillis();
		double actionCount = actions.stream().filter(t -> now - t < this.period).count();
		actions.removeIf(t -> now - t > this.period);

		return actionCount;
	}
}
