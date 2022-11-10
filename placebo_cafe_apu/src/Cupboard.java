package placebo_cafe_apu;

import java.util.concurrent.Semaphore;

public class Cupboard {

    // cups and glasses are drinking apparatus
    final Semaphore no_cups;
    final Semaphore no_glasses;
    final Semaphore no_milk;
    final Semaphore no_coffee;
    final Semaphore cappuccinoInProgress;

    public Cupboard(int no_cups, int no_glasses, int no_milk, int no_coffee, int no_cappuccinoInProgress) {
        this.no_cups = new Semaphore(no_cups);
        this.no_glasses = new Semaphore(no_glasses);
        this.no_milk = new Semaphore(no_milk);
        this.no_coffee = new Semaphore(no_coffee);
        this.cappuccinoInProgress = new Semaphore(no_cappuccinoInProgress);
    }

}
