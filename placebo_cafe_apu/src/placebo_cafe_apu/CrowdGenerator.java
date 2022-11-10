package placebo_cafe_apu;

import java.util.concurrent.ThreadLocalRandom;

public class CrowdGenerator extends Thread {

    Table tableRef = null;
    MainCafe cafeRef = null;
    Clock clockRef = null;
    Server[] serverRef = null;
    Statistics statsRef = null;
    int crowdSize = 0;
    int customerCount = 0;

    // create a constructor
    public CrowdGenerator(Clock clockRef, Table tableRef, MainCafe cafeRef, Server[] server, int crowdSize, Statistics statsRef) {
        // create a crowd of 10 customers
        this.tableRef = tableRef;
        this.cafeRef = cafeRef;
        this.clockRef = clockRef;
        this.crowdSize = crowdSize;
        this.serverRef = server;
        this.statsRef = statsRef;
    }

    @Override
    public void run() {
        TextWrapper.bigWrap("CAFE", "Cafe is open");
        // create a crowd of 10 customers
        while (!clockRef.isLastOrder()) {
            try {
                // sleep for random 1 - 3 second intervals
                if (customerCount < crowdSize) {
                    Customer customer = new Customer(customerCount, tableRef, cafeRef, clockRef, serverRef, statsRef);
                    customerCount++;
                    customer.start();
                }

                Thread.sleep((int) (ThreadLocalRandom.current().nextInt(1, 2 + 1) * 500));
                
            } catch (InterruptedException e) {
                
                e.printStackTrace();
            }

        }
        // MyCustomer customer = new MyCustomer(i, tableRef, cafeRef);
        // customer.start();

    }

}
