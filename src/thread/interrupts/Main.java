package thread.interrupts;

public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingClass());
        thread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // interrupt the BlockingClass thread that keeps running even after the main thread ends
        thread.interrupt();
    }

    // sleeps for a long time
    private static class BlockingClass implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(500000);
            } catch (InterruptedException e) {
                // catch the interrupt
                System.out.println("BlockingThread interrupted");
            }
        }
    }
}
