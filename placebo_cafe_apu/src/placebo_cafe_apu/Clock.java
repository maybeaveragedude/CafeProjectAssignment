package placebo_cafe_apu;

public class Clock extends Thread {
    final int SIMTIME = 1000;
    final int LAST_ORDER_TIME = 10;
    final int CLOSING_TIME = 15;
    // initialise
    int time = 0;

    // constructor
    Clock() {

    };

    @Override
    public void run() {
        TextWrapper.bigWrap("CLOCK", "Clock started");
        // operating time
        for (; time < LAST_ORDER_TIME; time++)
            try {
                Thread.sleep(SIMTIME);
            } catch (Exception e) {
            }
        ;

        // notify last call
        synchronized (this) {
            this.notifyAll();
        }
        ;

        // last order time
        for (; time < CLOSING_TIME; time++)
            try {
                Thread.sleep(SIMTIME);
            } catch (Exception e) {
            }
        ;
        // notify closing
        synchronized (this) {
            this.notifyAll();
        }
        ;
    };

    // check if past last order
    public Boolean isLastOrder() {
        return (time >= LAST_ORDER_TIME);
    };

    // check if past closing
    public Boolean isClosing() {
        return (time >= CLOSING_TIME);
    };
};
