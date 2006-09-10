package proc;

public class Semaphore {

    private int value = 0;

    public void lower() {
        try {
            value++;
            if (value > 0) {
                super.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void raise() {
        value--;
        if (value >= 0) {
            super.notify();
        }
    }
}