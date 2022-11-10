package placebo_cafe_apu;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread {

    String name;
    int id;
    String role;
    ReentrantLock orderLock = new ReentrantLock(true);
    private Table tableRef = null;
    private MainCafe cafeRef = null;
    private Clock clockRef = null;
    private Cupboard cupboardRef = null;
    private Statistics statsRef = null;
    Semaphore juiceTap = null;

    // initialise
    AtomicInteger numberOfServed = new AtomicInteger(0);
    public AtomicInteger runningOrder = new AtomicInteger(0);
    // initialize semaphores in cupboard
    // public MyCupboard cupboardRef = new MyCupboard(2, 2, 1, 1, 1);
    // waiter and owner are server
    // servers can take materials form the cupboard
    // create synchronized method to take materials from cupboard

    public Server(int id, String name, Table tableRef, MainCafe cafeRef, Clock clockRef, String role,
            Semaphore juiceTap, Cupboard cupboardRef, Statistics statsRef) {
        this.id = id;
        this.name = name;
        this.tableRef = tableRef;
        this.cafeRef = cafeRef;
        this.clockRef = clockRef;
        this.role = role;
        this.juiceTap = juiceTap;
        this.cupboardRef = cupboardRef;
        this.statsRef = statsRef;
    }

    public Boolean useJuiceTap() {
        try {

            juiceTap.acquire();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("JUICE",
                    role + ": " + name + " using the juice tap [" + juiceTap.availablePermits() + "/1]");
            Thread.sleep(random);

            return true;
        } catch (InterruptedException e) {

            TextWrapper.compactWrap("ERROR",
                    "Tap is being used by another server");
            return false;
        }
    }

    public void returnJuiceTap() {
        try {
            juiceTap.release();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("JUICE",
                    role + ": " + name + " is returning the juice tap ["
                            + juiceTap.availablePermits() + "/1]");
            Thread.sleep(random);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public Boolean takeOrder(Customer customer) {

        while (!orderLock.tryLock()) {
            TextWrapper.compactWrap("TAKE ORDER", role + ": " + name + " is taking order from customer " + customer.id);

            return true;
        }

        return false;

    }

    synchronized public void serveOrder(Customer customer) {
        // random order
        switch (customer.order) {
            // cappuccino
            case "cappuccino":
                make_servingCappuccino(customer);

                break;

            // fruit juice
            case "juice":
                make_servingJuice(customer);

                break;

            default:
                break;
        }
        // orderLock.unlock();
    }

    private synchronized void takeCup() {
        try {
            // sleep for random interval
            cupboardRef.no_cups.acquire();
            TextWrapper.compactWrap("CAPPUCCINO", role + ": " + name + " is taking cup ["
                    + cupboardRef.no_cups.availablePermits() + "/2]");

            Thread.sleep((int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 200));
        } catch (InterruptedException e) {
            TextWrapper.compactWrap("ERROR", role + ": " + name + " wanted to take cup but no cup available");

            washDrinkingApparatus("cup");

            e.printStackTrace();
        }
    }

    private synchronized void takeGlass() {
        try {
            cupboardRef.no_glasses.acquire();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("JUICE",
                    role + ": " + name + " taking a glass [" + cupboardRef.no_glasses.availablePermits() + "/2]");
            Thread.sleep(random);
        } catch (InterruptedException e) {
            System.out.println(role + ": " + name + " wanted to take glass but no glass available");
            washDrinkingApparatus("glass");
            e.printStackTrace();

        }
    }

    // take milk from cupboard
    private synchronized void takeMilk() {
        try {

            cupboardRef.no_milk.acquire();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("CAPPUCCINO",
                    role + ": " + name + " taking a milk [" + cupboardRef.no_milk.availablePermits() + "/1]");
            Thread.sleep(random);

        } catch (InterruptedException e) {
            System.out.println(role + ": " + name + " wanted to take milk but no milk available");
            e.printStackTrace();

        }
    }

    // take coffee from cupboard
    private synchronized void takeCoffee() {
        try {

            cupboardRef.no_coffee.acquire();
            TextWrapper.compactWrap("CAPPUCCINO",
                    role + ": " + name + " taking a coffee [" + cupboardRef.no_coffee.availablePermits() + "/1]");
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            Thread.sleep(random);

        } catch (InterruptedException e) {
            System.out.println("Wanted to take coffee but was interrupted");
            e.printStackTrace();

        }
    }

    protected synchronized void washDrinkingApparatus(String apparatus) {
        // wash drinking apparatus
        try {
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 200);
            int extraCooldown = 100;
            if (apparatus == "") {

                TextWrapper.compactWrap("WASH ALL",
                        role + ": " + name + " washing [" + (4 - tableRef.table_drinking_apparatus.availablePermits())
                                + "] drinking apparatus");
            }
            Thread.sleep(random);

            if (apparatus == "cup") {
                // wait for at least 1 empty cup to be available for washing

                while (tableRef.empty_cups.availablePermits() != 0) {
                    Thread.sleep(100);
                }
                TextWrapper.compactWrap("URGENT WASH",
                        role + ": " + name + " washing [" + 1
                                + "] cup");
                Thread.sleep(extraCooldown);
                tableRef.empty_cups.release();
                tableRef.table_drinking_apparatus.release();
                // cupboardRef.no_cups.release();
                statsRef.addCupsWashed();
                returnCup();
            } else if (apparatus == "glass") {

                // wait for at least 1 empty glass to be available for washing
                while (tableRef.empty_glass.availablePermits() != 0) {
                    System.out.println("checking glass");
                    Thread.sleep(100);
                }
                TextWrapper.compactWrap("URGENT WASH",
                        role + ": " + name + " washing [" + 1
                                + "] glass");
                Thread.sleep(extraCooldown);
                tableRef.empty_glass.release();
                tableRef.table_drinking_apparatus.release();

                statsRef.addGlassWashed();

                returnGlass();
            }
            boolean hasEmptyCups = false;
            if (tableRef.empty_cups.availablePermits() < 2) {
                hasEmptyCups = true;
            }

            do {
                if (tableRef.empty_cups.availablePermits() < 2) {
                    hasEmptyCups = true;
                    try {
                        Thread.sleep(extraCooldown);
                        tableRef.empty_cups.release();
                        tableRef.table_drinking_apparatus.release();
                        TextWrapper.compactWrap("WASH CUP [" + tableRef.empty_cups.availablePermits() + "/2]",
                                role + ": " + name + " washing cup");
                        statsRef.addCupsWashed();

                        returnCup();
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                } else { // no more empty cups
                    hasEmptyCups = false;
                    break;
                }

            } while (hasEmptyCups);

            boolean hasEmptyGlasses = false;
            if (tableRef.empty_glass.availablePermits() < 2) {
                hasEmptyGlasses = true;
            }
            do {
                if (tableRef.empty_glass.availablePermits() < 2) {
                    hasEmptyGlasses = true;
                    Thread.sleep(extraCooldown);
                    tableRef.empty_glass.release();
                    tableRef.table_drinking_apparatus.release();
                    TextWrapper.compactWrap("WASH GLASS [" + (tableRef.empty_glass.availablePermits())
                            + "/2]",
                            role + ": " + name + " washing glass");
                    statsRef.addGlassWashed();

                    returnGlass();
                } else {
                    hasEmptyGlasses = false;
                    break;
                }

            } while (hasEmptyGlasses);

        } catch (Exception e) {
            TextWrapper.compactWrap("ERROR", "Wanted to wash drinking apparatus but was interrupted");

            e.printStackTrace();
        }
    }

    // create a synchronized method to put materials back in cupboard
    private synchronized void returnCup() {
        try {

            cupboardRef.no_cups.release();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("CUP [" + cupboardRef.no_cups.availablePermits() + "/2]",
                    role + ": " + name + " returning cup");
            Thread.sleep(random);

        } catch (Exception e) {
            TextWrapper.compactWrap("ERROR", "Wanted to put cup back in cupboard but was interrupted");

            e.printStackTrace();
        }
    }

    private synchronized void returnGlass() {
        try {
            cupboardRef.no_glasses.release();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("GLASS [" + cupboardRef.no_glasses.availablePermits() + "/2]",
                    role + ": " + name + " returning glass");
            Thread.sleep(random);

        } catch (Exception e) {
            TextWrapper.compactWrap("ERROR", "Wanted to put glass back in cupboard but was interrupted");

            e.printStackTrace();
        }
    }

    private synchronized void returnMilk() {
        try {
            cupboardRef.no_milk.release();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("CAPPUCCINO", role + ": " + name + " returning milk ["
                    + cupboardRef.no_milk.availablePermits() + "/1]");
            Thread.sleep(random);

        } catch (Exception e) {
            TextWrapper.compactWrap("ERROR", "Wanted to put milk back in cupboard but was interrupted");

            e.printStackTrace();
        }
    }

    private synchronized void returnCoffee() {
        try {
            cupboardRef.no_coffee.release();
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("CAPPUCCINO", role + ": " + name + " returning coffee ["
                    + cupboardRef.no_coffee.availablePermits() + "/1]");
            Thread.sleep(random);

        } catch (Exception e) {
            TextWrapper.compactWrap("ERROR", "Wanted to put coffee back in cupboard but was interrupted");

            e.printStackTrace();
        }
    }

    private synchronized void mixCoffee() {
        try {
            int random = (int) (ThreadLocalRandom.current().nextInt(1, 3 + 1) * 350);
            TextWrapper.compactWrap("CAPPUCCINO", role + ": " + name + " mixing coffee");
            Thread.sleep(random);

        } catch (Exception e) {
            TextWrapper.compactWrap("ERROR", "Wanted to mix coffee but was interrupted");

            e.printStackTrace();
        }
    }

    private synchronized void make_servingCappuccino(Customer customer) {
        // make coffee
        AtomicLong startCappuccinoTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong endCappuccinoTime = new AtomicLong(0);
        TextWrapper.compactWrap("SERVING CAPPUCCINO",
                role + ": " + name + " is making cappuccino for customer " + customer.id);

        Boolean readyToMake = false;

        do {
            try {

                if (cupboardRef.cappuccinoInProgress.availablePermits() == 1) {
                    readyToMake = true;
                    cupboardRef.cappuccinoInProgress.acquire();
                    if (cupboardRef.no_cups.availablePermits() < 1) {
                        washDrinkingApparatus("cup");
                    }
                    takeCup();
                    takeCoffee();
                    takeMilk();
                    mixCoffee();
                    returnCoffee();
                    returnMilk();
                }

                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!readyToMake);

        orderLock.unlock();
        cupboardRef.cappuccinoInProgress.release();

        TextWrapper.bigWrap("SERVED CAPPUCCINO",
                role + ": " + name + " served cappuccino to customer " + customer.id);
        endCappuccinoTime.set(System.currentTimeMillis());
        statsRef.addCappuccinoElapsedTime(endCappuccinoTime.get() - startCappuccinoTime.get());

    }

    private synchronized void make_servingJuice(Customer customer) {
        AtomicLong startJuiceTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong endJuiceTime = new AtomicLong(0);

        boolean useTap = false;
        // make coffee

        TextWrapper.compactWrap("SERVING JUICE",
                role + ": " + name + " is making juice for customer " + customer.id);

        do {
            try {

                useTap = useJuiceTap();

                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        } while (!useTap);

        if (useTap) {
            // System.out.println("Hello there");
            if (cupboardRef.no_glasses.availablePermits() < 1) {
                // System.out.println("General Kenobi");
                washDrinkingApparatus("glass");
            }
            // System.out.println("something");
            takeGlass();

            returnJuiceTap();
        }

        orderLock.unlock();
        TextWrapper.bigWrap("SERVED JUICE",
                role + ": " + name + " served juice to customer " + customer.id);
        endJuiceTime.set(System.currentTimeMillis());
        statsRef.addJuiceElapsedTime(endJuiceTime.get() - startJuiceTime.get());

    }

    @Override
    public void run() {

        synchronized (clockRef) {
            try {
                wait();
                if ((4 - tableRef.table_drinking_apparatus.availablePermits()) >= 1) {
                    washDrinkingApparatus("");
                }
                wait();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

    }

}
