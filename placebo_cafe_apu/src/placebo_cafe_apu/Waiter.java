package placebo_cafe_apu;

import java.util.concurrent.Semaphore;

public class Waiter extends Server {
    Clock clockRef = null;
    Table tableRef = null;
    Statistics statsRef = null;

    // build a constructor
    public Waiter(int id, String name, Table tableRef, MainCafe cafeRef, Clock clockRef, Semaphore juiceTap,
            Cupboard cupboard, Statistics statsRef) {
        super(id, name, tableRef, cafeRef, clockRef, "Waiter", juiceTap, cupboard, statsRef);
        this.clockRef = clockRef;
        this.tableRef = tableRef;
        this.statsRef = statsRef;
        // super(id);
    }

    @Override
    public void run() {
        synchronized (clockRef) {
            try {
                clockRef.wait();
                clockRef.wait();
            } catch (InterruptedException e) {
                
                e.printStackTrace();
            }
        }
        tableRef.seats.forEach((customer) -> {
            try {
                customer.join();
            } catch (InterruptedException e) {
            }
        });
        washDrinkingApparatus("");

        TextWrapper.bigWrap("WAITER", "Waiter " + id + " left.");
    }
}
