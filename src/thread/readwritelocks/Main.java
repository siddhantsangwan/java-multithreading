package thread.readwritelocks;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        PriceItemDataStructure database = new PriceItemDataStructure();

        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            database.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writer = new Thread(() -> {
            while (true) {
                database.addItem(random.nextInt(HIGHEST_PRICE));
                database.removeItem(random.nextInt(HIGHEST_PRICE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numberOfReaderThreads = 7;
        List<Thread> readers = new ArrayList<>();

        for (int readerIndex = 0; readerIndex < numberOfReaderThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 100000; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    database.numberOfItemsBetweenBounds(lowerBoundPrice, upperBoundPrice);
                }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();
        for (Thread reader : readers) {
            reader.start();
        }

        for (Thread reader : readers) {
            reader.join();
        }

        long endReadingTime = System.currentTimeMillis();

        System.out.println(String.format("Reading took %d ms", endReadingTime - startReadingTime));
    }

    public static class PriceItemDataStructure {
        private TreeMap<Integer, Integer> directory = new TreeMap<>();
        private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        // reader method
        public int numberOfItemsBetweenBounds(int lowerBound, int upperBound) {
            readLock.lock();
            try {
                Integer lowerNode = directory.ceilingKey(lowerBound);
                Integer upperNode = directory.floorKey(upperBound);
                if (lowerNode == null || upperNode == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> pricesRange = directory.subMap(lowerNode, true, upperNode, true);
                Integer totalValues = 0;
                for (Integer node : pricesRange.values()) {
                    totalValues += node;
                }
                return totalValues;
            }
            finally {
                readLock.unlock();
            }
        }

        // writer method
        public void addItem(int price) {
            writeLock.lock();
            try {
                Integer numItems = directory.get(price);
                if (numItems == null) {
                    directory.put(price, 1);
                } else {
                    directory.put(price, numItems + 1);
                }
            }
            finally {
                writeLock.unlock();
            }
        }

        // writer method
        private void removeItem(int price) {
            writeLock.lock();
            try {
                Integer numItems = directory.get(price);
                if (numItems == null || numItems == 1) {
                    directory.remove(price);
                } else {
                    directory.put(price, numItems - 1);
                }
            }
            finally {
                writeLock.unlock();
            }
        }

    }
}
