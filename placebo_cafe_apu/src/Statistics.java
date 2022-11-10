package placebo_cafe_apu;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// statistics additional requirements
public class Statistics {
	// initialise
	AtomicInteger cust_successfully_served = new AtomicInteger(0);
	AtomicInteger cust_never_served = new AtomicInteger(0);
	AtomicInteger cust_left_0_seat = new AtomicInteger(0);
	AtomicInteger cust_left_queue_full = new AtomicInteger(0);
	AtomicInteger cappuccino_orders = new AtomicInteger(0);
	AtomicInteger juices_orders = new AtomicInteger(0);
	AtomicInteger totalCups_washed = new AtomicInteger(0);
	AtomicInteger totalGlassWashed = new AtomicInteger(0);

	AtomicLong cappuccinoElapsedTime = new AtomicLong(0);
	AtomicLong c_shortestElapsedTime = new AtomicLong(0);
	AtomicLong c_longestElapsedTime = new AtomicLong(0);

	AtomicLong juiceElapsedTime = new AtomicLong(0);
	AtomicLong j_shortestElapsedTime = new AtomicLong(0);
	AtomicLong j_longestElapsedTime = new AtomicLong(0);

	AtomicLong totalElapsedTime = new AtomicLong(0);
	AtomicLong shortestElapsedTime = new AtomicLong(0);
	AtomicLong longestElapsedTime = new AtomicLong(0);

	AtomicLong totalWaitingTime = new AtomicLong(0);
	AtomicLong shortestWaitingTime = new AtomicLong(0);
	AtomicLong longestWaitingTime = new AtomicLong(0);

	AtomicLong totalWastedTime = new AtomicLong(0);
	AtomicLong shortestWastedTime = new AtomicLong(0);
	AtomicLong longestWastedTime = new AtomicLong(0);

	AtomicLong totalOrderTime = new AtomicLong(0);
	AtomicLong shortestOrderTime = new AtomicLong(0);
	AtomicLong longestOrderTime = new AtomicLong(0);

	// AtomicInteger to get first run of add elapsed time
	AtomicInteger init = new AtomicInteger(0);

	public void addServed() {
		cust_successfully_served.incrementAndGet();
	};

	public void addUnserved() {
		cust_never_served.incrementAndGet();
	};

	public void addUnseated() {
		cust_left_0_seat.incrementAndGet();
	};

	public void addLeftQueueFull() {
		cust_left_queue_full.incrementAndGet();
	};

	public void addCappuccinos() {
		cappuccino_orders.incrementAndGet();
	};

	public void addJuices() {
		juices_orders.incrementAndGet();
	};

	public void addCupsWashed() {
		totalCups_washed.incrementAndGet();
	};

	public void addGlassWashed() {
		totalGlassWashed.incrementAndGet();
	};

	public void addWaitingTime(long waitingTime) {
		totalWaitingTime.addAndGet(waitingTime);

		// set shortest time on first function call
		if (init.get() != 1) {
			shortestWaitingTime.set(waitingTime);
			init.incrementAndGet();
		}

		// set longest time or shortest time
		if (waitingTime >= longestWaitingTime.get()) {
			longestWaitingTime.set(waitingTime);
		} else if (waitingTime <= shortestWaitingTime.get()) {

			shortestWaitingTime.set(waitingTime);
		}
		;
	};

	public void addWastedTime(long wastedTime) {
		totalWastedTime.addAndGet(wastedTime);

		// set shortest time on first function call
		if (init.get() != 1) {
			shortestWastedTime.set(wastedTime);
			init.incrementAndGet();
		}

		// set longest time or shortest time
		if (wastedTime >= longestWastedTime.get()) {
			longestWastedTime.set(wastedTime);
		} else if (wastedTime <= shortestWastedTime.get()) {
			shortestWastedTime.set(wastedTime);
		}
		;
	};

	public void addCappuccinoElapsedTime(long elapsedTime) {
		cappuccinoElapsedTime.addAndGet(elapsedTime);

		// set shortest time on first function call
		if (init.get() != 1) {
			c_shortestElapsedTime.set(elapsedTime);
			init.incrementAndGet();
		}

		// set longest time or shortest time
		if (elapsedTime >= c_longestElapsedTime.get()) {
			c_longestElapsedTime.set(elapsedTime);
		} else if (elapsedTime <= c_shortestElapsedTime.get()) {
			c_shortestElapsedTime.set(elapsedTime);
		}
		;
	};

	public void addJuiceElapsedTime(long elapsedTime) {
		juiceElapsedTime.addAndGet(elapsedTime);

		// set shortest time on first function call
		if (init.get() != 1) {
			j_shortestElapsedTime.set(elapsedTime);
			init.incrementAndGet();
		}

		// set longest time or shortest time
		if (elapsedTime >= j_longestElapsedTime.get()) {
			j_longestElapsedTime.set(elapsedTime);
		} else if (elapsedTime <= j_shortestElapsedTime.get()) {
			j_shortestElapsedTime.set(elapsedTime);
		}
		;
	};

	public void addElapsedTime(long elapsedTime) {
		totalElapsedTime.addAndGet(elapsedTime);

		// set shortest time on first function call
		if (init.get() != 1) {
			shortestWaitingTime.set(elapsedTime);
			init.incrementAndGet();
		}

		// set longest time or shortest time
		if (elapsedTime >= longestElapsedTime.get()) {
			longestElapsedTime.set(elapsedTime);
		} else if (elapsedTime <= shortestElapsedTime.get()) {
			shortestElapsedTime.set(elapsedTime);
		}
		;
	};

	public void addOrderTime(long orderTime) {
		totalOrderTime.addAndGet(orderTime);

		// set shortest time on first function call
		if (init.get() != 1) {
			shortestOrderTime.set(orderTime);
			init.incrementAndGet();
		}

		// set longest time or shortest time
		if (orderTime >= longestOrderTime.get()) {
			longestOrderTime.set(orderTime);
		} else if (orderTime <= shortestOrderTime.get()) {
			shortestOrderTime.set(orderTime);
		}
		;
	};
};
