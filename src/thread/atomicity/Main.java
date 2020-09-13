package thread.atomicity;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();
        BusinessLogic bl1 = new BusinessLogic(metrics);
        BusinessLogic bl2 = new BusinessLogic(metrics);

        PrintMetrics printer = new PrintMetrics(metrics);
        bl1.start();
        bl2.start();
        printer.start();
    }
    public static class PrintMetrics extends Thread {
        private Metrics metrics;

        public PrintMetrics(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double currentAverage = metrics.getAverage();
                System.out.println("Current average: " + currentAverage);
            }
        }
    }
    public static class BusinessLogic extends Thread {
        Metrics metrics;
        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }
        Random randomTime = new Random();

        @Override
        public void run() {
            while(true) {
                long startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(randomTime.nextInt(2));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                metrics.addSample(endTime - startTime);
            }
        }
    }
    public static class Metrics {
        private long count = 0;
        private volatile double average = 0; // reading a long is non atomic (getAverage())

        public synchronized void addSample(long sample) {
            double sum = average * count + sample;
            count++;
            average = sum / count;
        }

        public double getAverage() {
            return average;
        }
    }
}
