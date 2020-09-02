public class Main {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("We are now in thread " + Thread.currentThread().getName());
                System.out.println("Current thread priority is " + Thread.currentThread().getPriority());
                throw new RuntimeException("Intentional Exception");
            }
        });

        // set the name of the new thread
        thread.setName("New Worker Thread");
        thread.setPriority(Thread.MAX_PRIORITY);

        // need to actually run the thread
        // notice static methods
        System.out.println("We are in thread " + Thread.currentThread().getName() + " before starting a new thread");
        thread.start();
        System.out.println("We are in thread " + Thread.currentThread().getName() + " after starting a new thread");

        /*
         usually an error will bring down a thread
         but we can catch it
         the exception handler will get it if nothing else has
        */
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("A critical error happened in thread " + t.getName() + " and the error" +
                        " message is " + e.getMessage());
            }
        });

        /*
         make the main thread sleep for 10000 milliseconds
         this isn't spinning or busy waiting, actually the os has been instructed not to schedule the thread
         for the specified time
        */
        Thread.sleep(10000);

        Thread thread1 = new NewThread();
        thread1.start();
    }

    /*
     this is the other way, directly extend the Thread class
     which implements Runnable (and hence the run() method)
    */
    private static class NewThread extends Thread {
        @Override
        public void run() {
            // Code that executes on the new thread
            System.out.println("Hello from " + Thread.currentThread().getName());
        }
    }
}
