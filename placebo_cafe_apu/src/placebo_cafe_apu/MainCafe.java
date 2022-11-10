package placebo_cafe_apu;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class MainCafe {
    // allow 4 customers to queue for ordering
    protected BlockingQueue<Customer> orderingQueue = new ArrayBlockingQueue<>(4);

    public static void main(String[] args) {
        Statistics statistics= new Statistics();
        MainCafe main = new MainCafe();
        Table table = new Table();
        Cupboard cupboard = new Cupboard(2, 2, 1, 1, 1);
        Clock clock = new Clock();
        clock.start();
        Semaphore juiceTapLock = new Semaphore(1);

        Server waiter = new Waiter(140, "Mark", table, main, clock, juiceTapLock, cupboard, statistics);
        Server cafeOwner = new Owner(200, "Oliver", table, main, clock, juiceTapLock, waiter, cupboard, statistics);
        waiter.start();
        cafeOwner.start();
        Server[] servers = { waiter, cafeOwner };
        System.out.println(servers[1].getState());
        CrowdGenerator crowd = new CrowdGenerator(clock, table, main, servers, 10, statistics);
        crowd.start();
        try {
            clock.join();
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        }

    }

}
