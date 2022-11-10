package placebo_cafe_apu;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class Table {
    // create a blocking queue for the seats
    protected BlockingQueue<Customer> seats = new ArrayBlockingQueue<>(10);

    Semaphore table_drinking_apparatus = new Semaphore(4);
    Semaphore empty_glass = new Semaphore(2);
    Semaphore empty_cups = new Semaphore(2);

    public boolean takeSeat(Customer customer) {
        // take seat
        try {
            seats.put(customer);
            TextWrapper.compactWrap("TABLE [" + seats.size() + "/10]",
                    "Customer " + customer.id + " is taking a seat ");

        } catch (Exception e) {

            TextWrapper.compactWrap("TABLE FULL", "Customer " + customer.id + " could not take a seat and left.");

            e.printStackTrace();
            return false;
        }
        return true;

    };

    public void leaveSeat(Customer customer) {
        // leave seat
        try {
            seats.take();
            TextWrapper.compactWrap("TABLE ["+ seats.size() + "/10]", "Customer " + customer.id + " is leaving the table");

        } catch (Exception e) {

            System.out.println("Customer " + customer.id + " wanted to leave the table but was interrupted.");
            e.printStackTrace();
        }
    };

}
