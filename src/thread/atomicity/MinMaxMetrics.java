package thread.atomicity;

public class MinMaxMetrics {
    private long sample;
    private volatile long  minSoFar;
    private volatile long maxSoFar;

    /**
     * Initializes all member variables
     */
    public MinMaxMetrics() {
        // Add code here
        this.sample = sample;
        minSoFar = Long.MAX_VALUE;
        maxSoFar = Long.MIN_VALUE;
    }

    /**
     * Adds a new sample to our metrics.
     */
    public void addSample(long newSample) {
        // Add code here
        synchronized (this) {
            this.sample = newSample;
            minSoFar = Long.min(minSoFar, newSample);
            maxSoFar = Long.max(maxSoFar, newSample);
        }
    }

    /**
     * Returns the smallest sample we've seen so far.
     */
    public long getMin() {
        // Add code here
        return minSoFar;
    }

    /**
     * Returns the biggest sample we've seen so far.
     */
    public long getMax() {
        // Add code here
        return maxSoFar;
    }
}
