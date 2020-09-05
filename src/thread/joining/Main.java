package thread.joining;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    1. If thread.join() was absent, the Main thread would report results before they have been computed, since it
    finishes earlier(this is a kind of race condition, since both threads are checking foundFactorial).
    2. A very large number like 100000000L might make main wait for long, hanging the application. Therefore, set
    a limit to join().
    3. Exit gracefully by interrupting the long running thread.
*/

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // list of numbers to compute the factorial for
        List<Long> inputList = Arrays.asList(100000000L, 0L, 999L, 23L, 67L, 77L);
        List<FactorialFinder> threads = new ArrayList<>();

        for(long input : inputList) {
            threads.add(new FactorialFinder(input));
        }
        for(FactorialFinder thread : threads) {
            thread.start();
        }
        // make the main thread wait until each thread finishes, but only for 2 secs
        for(FactorialFinder thread : threads) {
            thread.join(2000);
            // if a thread is still running, it's performing a large computation
            // interrupt it
            thread.interrupt();
        }

        for(FactorialFinder thread : threads) {
            if(thread.isFoundFactorial()) {
                System.out.println("The factorial of " + thread.getNumber() + " is " + thread.getResult());
            }
            else {
                System.out.println("The factorial of " + thread.getNumber() + " was taking too long: Interrupted");
            }
        }
    }

    public static class FactorialFinder extends Thread {
        private BigInteger result;

        private long number;
        private boolean foundFactorial;

        public FactorialFinder(long number) {
            this.number = number;
            result = BigInteger.ONE;
            foundFactorial = false;
        }

        @Override
        public void run() {
            result = findFactorial(number, result);
            // only if the thread could complete its computation
            if(result != BigInteger.ZERO) {
                foundFactorial = true;
            }
        }

        private BigInteger findFactorial(long number, BigInteger result) {
            for(long i = 1; i <= number; i++) {
                // thread might have been interrupted if the computation is too long
                if(currentThread().isInterrupted()) {
                    return BigInteger.ZERO;
                }
                result = result.multiply(new BigInteger(Long.toString(i)));
            }
            return result;
        }

        public boolean isFoundFactorial() {
            return foundFactorial;
        }

        public long getNumber() {
            return number;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}
