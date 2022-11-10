package placebo_cafe_apu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Customer extends Thread {
    AtomicLong startOrderTime = new AtomicLong(System.currentTimeMillis());
    AtomicLong endOrderTime = new AtomicLong(0);
    AtomicLong startWaitingTime = new AtomicLong(0);
    AtomicLong endWaitingTime = new AtomicLong(0);
    AtomicLong endElapsedTime = new AtomicLong(0);
    AtomicLong endWastedTime = new AtomicLong(0);

    public int id;
    boolean ordered;
    String order = null;
    private Table tableRef = null;
    private MainCafe cafeRef = null;
    private Clock clockRef = null;
    private Server[] serverRef = null;
    private Statistics statsRef = null;
    boolean seated = false;

    public Customer(int id, Table tableRef, MainCafe cafeRef, Clock clockRef, Server[] serverRef,
            Statistics statsRef) {
        this.id = id;
        this.tableRef = tableRef;
        this.cafeRef = cafeRef;
        this.clockRef = clockRef;
        this.serverRef = serverRef;
        this.statsRef = statsRef;
    }

    // customer can place an order
    // customer can drink
    // customer can leave
    // customer can wait
    // customer can pay
    // customer can sit

    // create a "drinkOrder" method
    private void drinkOrder(String order) {
        // let customer thread sleep for for random interval
        try {
            final int randomDrinkingDuration = (int) ((int) (ThreadLocalRandom.current().nextInt(1, 5 + 1) * 200));
            TextWrapper.compactWrap("CUSTOMER",
                    "Customer " + id + " is drinking " + order);

            Thread.sleep(randomDrinkingDuration);
            try {
                if (order == "juice") {
                    tableRef.empty_glass.acquire();
                    TextWrapper.compactWrap("GLASS [" + (2 - tableRef.empty_glass.availablePermits()) + " on table]",
                            "Customer " + id + " placed an empty glass");

                    statsRef.addJuices();

                } else if (order == "cappuccino") {
                    tableRef.empty_cups.acquire();
                    TextWrapper.compactWrap("CUP [" + (2 - tableRef.empty_cups.availablePermits()) + " on table]",
                            "Customer " + id + " placed an empty cup");

                    statsRef.addCappuccinos();

                }
                tableRef.table_drinking_apparatus.acquire();
            } catch (InterruptedException e) {
                System.out.println("Wanted to place cup or glass on the table but was interrupted");
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void placeOrder(Server server) {
        // let customer thread sleep for for random interval
        try {

            final int randomOrderDuration = (int) ((int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 1000));
            TextWrapper.compactWrap("CUSTOMER",
                    "Customer " + id + " is placing an order");
            startOrderTime.set(System.currentTimeMillis());

            Thread.sleep(randomOrderDuration);
            ordered = true;

            if (ThreadLocalRandom.current().nextInt(0, 1 + 1) == 0) {
                order = "cappuccino";
            } else {
                order = "juice";
            }

            TextWrapper.compactWrap("CONFIRM ORDER",
                    "Customer " + id + " has placed an order for " + order);
            startWaitingTime.set(System.currentTimeMillis());
            endOrderTime.set(System.currentTimeMillis());

            leaveQueue(cafeRef.orderingQueue);
            server.serveOrder(this);
            endWaitingTime.set(System.currentTimeMillis());
            drinkOrder(order);
            statsRef.addServed();
            statsRef.addOrderTime(endOrderTime.get() - startOrderTime.get());
            statsRef.addWaitingTime(endWaitingTime.get() - startWaitingTime.get());
            endElapsedTime.set(System.currentTimeMillis());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void joinQueue(BlockingQueue<Customer> queue) {
        if (ordered == false) {
            // join queue
            try {
                TextWrapper.compactWrap("CUSTOMER", "Customer " + id + " is joining the queue");

                queue.put(this);
            } catch (InterruptedException e) {
                TextWrapper.bigWrap("CUSTOMER", "Customer " + id + " could not join the queue");

                e.printStackTrace();
            }

        }
    }

    public void leaveQueue(BlockingQueue<Customer> queue) {
        if (ordered) {

            try {
                queue.take();
                TextWrapper.compactWrap("QUEUE [" + queue.size() + "/4]",
                        "Customer " + id + " is leaving the queue, going back to table");

            } catch (InterruptedException e) {
                TextWrapper.bigWrap("CUSTOMER", "Customer " + id + " wanted to leave the queue but was interrupted");

                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        // cannot jump to ordering queue if table is full
        // try taking a seat at the table
        AtomicLong startElapsedTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong startWastedTime = new AtomicLong(System.currentTimeMillis());

        boolean isFull = false;
        int MAX_waitCount = 20;
        int waitCount = 0;
        do {
            if (tableRef.seats.remainingCapacity() > 0) {
                isFull = false;
            } else {
                TextWrapper.compactWrap("CUSTOMER", "Customer " + id + " could not take a seat and left");
                statsRef.addUnseated();
                statsRef.addUnserved();
                endElapsedTime.set(System.currentTimeMillis());
                endWastedTime.set(System.currentTimeMillis());
                statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());
                statsRef.addWastedTime(endWastedTime.get() - startWastedTime.get());
                isFull = true;
                break;
            }

            try {

                tableRef.takeSeat(this);

                seated = true;
                if (cafeRef.orderingQueue.size() < 4) {

                    try {
                        seated = false;
                        cafeRef.orderingQueue.add(this);
                        TextWrapper.compactWrap("QUEUE ["
                                + cafeRef.orderingQueue.size() + "/4]",
                                "Customer " + id + " is joining the ordering queue ");
                        startWaitingTime.set(System.currentTimeMillis());

                        do {
                            try {

                                if (serverRef[0].orderLock.tryLock()) {
                                    ordered = true;
                                    TextWrapper.compactWrap("TAKE ORDER",
                                            serverRef[0].role + ": " + serverRef[0].name
                                                    + " is taking order from customer "
                                                    + this.id);
                                    endWaitingTime.set(System.currentTimeMillis());
                                    statsRef.addWaitingTime(endWaitingTime.get() - startWaitingTime.get());
                                    placeOrder(serverRef[0]);
                                    seated = true;

                                } else if (serverRef[1].orderLock.tryLock()) {
                                    ordered = true;
                                    TextWrapper.compactWrap("TAKE ORDER",
                                            serverRef[1].role + ": " + serverRef[1].name
                                                    + " is taking order from customer "
                                                    + this.id);
                                    placeOrder(serverRef[1]);
                                    seated = true;

                                }
                            } catch (Exception err) {
                                err.printStackTrace();
                                System.out.println("ERROR IN TAKING AND PLACING ORDER");
                            }
                            waitCount++;
                            Thread.sleep(100);
                        } while (!ordered && waitCount < MAX_waitCount);

                    } catch (Exception e) {
                        tableRef.leaveSeat(this);
                        seated = false;
                        TextWrapper.compactWrap("CUSTOMER LEAVING",
                                "Customer " + id + " could not join the ordering queue");
                        statsRef.addLeftQueueFull();
                        statsRef.addUnserved();
                        endElapsedTime.set(System.currentTimeMillis());
                        endWastedTime.set(System.currentTimeMillis());
                        statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());
                        statsRef.addWastedTime(endWastedTime.get() - startWastedTime.get());
                        break;
                    }
                } else {
                    tableRef.leaveSeat(this);
                    seated = false;
                    TextWrapper.compactWrap("CUSTOMER LEAVING",
                            "Customer " + id + " could not join the ordering queue");
                    statsRef.addLeftQueueFull();
                    statsRef.addUnserved();
                    endElapsedTime.set(System.currentTimeMillis());
                    endWastedTime.set(System.currentTimeMillis());
                    statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());
                    statsRef.addWastedTime(endWastedTime.get() - startWastedTime.get());
                    break;
                }
                break;

            } catch (Exception e) {
                e.printStackTrace();
                TextWrapper.compactWrap("CUSTOMER LEAVING", "Customer " + id + " could not find a seat");
                statsRef.addUnseated();
                statsRef.addUnserved();
                endElapsedTime.set(System.currentTimeMillis());
                endWastedTime.set(System.currentTimeMillis());
                statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());
                statsRef.addWastedTime(endWastedTime.get() - startWastedTime.get());

                break;
            }

        } while (!clockRef.isClosing() && !isFull);

        if (clockRef.isClosing() && !ordered) {
            if (seated) {
                tableRef.leaveSeat(this);
            }
            TextWrapper.compactWrap("CUSTOMER LEAVING", "Customer " + id + " left.");
            statsRef.addUnserved();
            endElapsedTime.set(System.currentTimeMillis());
            endWastedTime.set(System.currentTimeMillis());
            statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());
            statsRef.addWastedTime(endWastedTime.get() - startWastedTime.get());
        }
        if (waitCount == MAX_waitCount) {
            leaveQueue(cafeRef.orderingQueue);
            TextWrapper.bigWrap("FRUSTRATED CUSTOMER", "Customer " + id + " couldn't order");
            statsRef.addLeftQueueFull();
            statsRef.addUnserved();
            endElapsedTime.set(System.currentTimeMillis());
            endWastedTime.set(System.currentTimeMillis());
            statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());
            statsRef.addWastedTime(endWastedTime.get() - startWastedTime.get());
        }
        startElapsedTime.set(System.currentTimeMillis());
        statsRef.addElapsedTime(endElapsedTime.get() - startElapsedTime.get());

        TextWrapper.compactWrap("CUSTOMER LEAVING", "Customer " + id + " left.");
    }

}
