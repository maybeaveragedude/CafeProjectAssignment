package placebo_cafe_apu;

import java.util.concurrent.Semaphore;

public class Owner extends Server {
        Clock clockRef = null;
        Table tableRef = null;
        Server waiterRef = null;
        Statistics statsRef = null;

        // build a constructor
        public Owner(int id, String name, Table tableRef, MainCafe cafeRef, Clock clockRef, Semaphore juiceTap,
                        Server waiterRef, Cupboard cupboard, Statistics statsRef) {
                super(id, name, tableRef, cafeRef, clockRef, "Owner", juiceTap, cupboard, statsRef);
                this.clockRef = clockRef;
                this.tableRef = tableRef;
                this.waiterRef = waiterRef;
                this.statsRef = statsRef;
                // super(id);
        }

        @Override
        public void run() {
                synchronized (clockRef) {
                        try {
                                clockRef.wait();
                                TextWrapper.bigWrap("OWNER", "Last order time");
                                clockRef.wait();
                                TextWrapper.bigWrap("OWNER", "Closing time");
                        } catch (InterruptedException e) {
                                
                                e.printStackTrace();
                        }
                }

                try {
                        waiterRef.join();
                } catch (InterruptedException e) {
                }

                TextWrapper.bigWrap("OWNER", "Cafe is CLOSED");
                System.out.println("\nSTATISTICS");
                System.out.println("----------");
                System.out.println("\nTotal customers served\t\t\t\t: " + statsRef.cust_successfully_served.get());
                System.out.println(
                                "Total elapsed time serving customers\t\t: " + statsRef.totalElapsedTime.get() + " ms");

                if (statsRef.cust_successfully_served.get() == 0) {
                        System.out.println("Average elapsed time per customer served\t: 0 ms");
                } else {

                        System.out.println("Average elapsed time per customer served\t: "
                                        + statsRef.totalElapsedTime.get() / statsRef.cust_successfully_served.get()
                                        + " ms");

                }
                System.out.println("Maximum elapsed time per customer served\t: " + statsRef.longestElapsedTime.get()
                                + " ms");
                System.out.println("Minimum elapsed time per customer served\t: " + statsRef.shortestElapsedTime.get()
                                + " ms");
                System.out.println("\nTotal cappuccinos served\t\t\t: " + statsRef.cappuccino_orders.get());
                if (statsRef.cappuccino_orders.get() == 0) {
                        System.out.println("Average elapsed time preparing cappuccinos\t: 0 ms");
                } else {

                        System.out.println("Average elapsed time preparing cappuccinos\t: "
                                        + statsRef.cappuccinoElapsedTime.get() / statsRef.cappuccino_orders.get()
                                        + " ms");

                }

                System.out.println("Total cups washed\t\t\t\t: " + statsRef.totalCups_washed.get());
                System.out.println("\nTotal fruit juice served\t\t\t: " + statsRef.juices_orders.get());
                if (statsRef.juices_orders.get() == 0) {
                        System.out.println("Average elapsed time preparing juice\t\t: 0 ms");
                } else {

                        System.out.println("Average elapsed time preparing juice\t\t: "
                                        + statsRef.juiceElapsedTime.get() / statsRef.juices_orders.get()
                                        + " ms");

                }

                System.out.println("Total glasses washed\t\t\t\t: " + statsRef.totalGlassWashed.get());
                if (statsRef.cust_successfully_served.get() == 0) {
                        System.out.println("Average waiting time per served customer\t: 0 ms");
                } else {

                        System.out.println("\nAverage waiting time per served customer\t: "
                                        + ((statsRef.totalWaitingTime.get() / statsRef.cust_successfully_served.get()))
                                        + " ms");

                }

                System.out.println("Maximum customer waiting time\t\t\t: "
                                + statsRef.longestWaitingTime.get() + " ms");
                System.out.println("Minimum customer waiting time\t\t\t: "
                                + statsRef.shortestElapsedTime.get() + " ms");
                System.out.println("\nTotal customers unserved\t\t\t: " + statsRef.cust_never_served.get());
                System.out.println("Total customers left without a seat\t\t: " + statsRef.cust_left_0_seat.get());
                System.out.println(
                                "Total customers left because full queue\t\t: " + statsRef.cust_left_queue_full.get());

                if (statsRef.cust_never_served.get() == 0) {
                        System.out.println("Average time wasted per unserved customers\t: 0 ms");
                } else {

                        System.out.println("\nAverage time wasted per unserved customers\t: "
                                        + ((statsRef.totalWastedTime.get() / statsRef.cust_never_served.get()))
                                        + " ms");

                }

                System.out.println("Maximum unserved wasted time\t\t\t: " + statsRef.longestWastedTime.get() + " ms");
                System.out.println("Minimum unserved wasted time\t\t\t: " + statsRef.shortestWastedTime.get() + " ms");

        }
}
