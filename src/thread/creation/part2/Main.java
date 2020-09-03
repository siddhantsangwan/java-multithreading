package thread.creation.part2;

import java.util.Random;

public class Main {
    public static final int MAX_PASSWORD = 9999;
    public static void main(String[] args) {
        // randomly generate password
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        Thread ascendingHacker = new AscendingHackerThread(vault);
        Thread descendingHacker = new DescendingHackerThread(vault);
        Thread police = new PoliceThread();
        ascendingHacker.start();
        descendingHacker.start();
        police.start();
    }
    // Vault that the thieves are trying to break into
    private static class Vault {
        private int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return guess == this.password;
        }
    }
    // create an abstract HackerThread class that different types
    // of hacker threads can use
    private static abstract class HackerThread extends Thread {
        // has access to a reference to the vault it's trying to break
        protected Vault vault;
        public HackerThread(Vault vault) {
            this.vault = vault;
            // give the inheriting concrete HackerThread classes their names
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }
    private static class AscendingHackerThread extends HackerThread {
        public AscendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int guess = 0; guess < MAX_PASSWORD; guess++) {
                if(vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " cracked the password " + guess);
                    System.exit(0);
                }
            }
        }
    }
    private static class DescendingHackerThread extends HackerThread {
        public DescendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int guess = MAX_PASSWORD; guess >= 0; guess--) {
                if(vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " cracked the password " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {
        @Override
        public void run() {
            for(int count = 0; count <= 10; count++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(count);
            }
            System.out.println("This is the end, hackers");
            System.exit(0);
        }
    }
}

