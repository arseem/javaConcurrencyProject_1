import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimeNumberGenerator {
    private int numThreads;
    private final int range;
    private boolean[] primes;
    public Thread[] threads;
    private final AtomicInteger numNonPrimes;
    private AtomicBoolean run;

    public PrimeNumberGenerator(int numThreads, int range) {
        this.numThreads = numThreads;
        this.range = range;
        this.primes = new boolean[range + 1];
        this.numNonPrimes = new AtomicInteger(range - 1);
        this.run = new AtomicBoolean(false);
    }

    public void start() {
        if (!run.get()) {
            run.set(true);
        }
    }
    public void start(int newNumThreads, int newRange) {
        if (!run.get()) {
            numThreads = newNumThreads;
            numNonPrimes.set(newRange - 1);
            primes = new boolean[range + 1];
            run.set(true);
        }
    }

    public void reset() {
        if (!run.get()){
            Arrays.fill(primes, true);
            numNonPrimes.set(range-1);
        }
    }

    private void waitForStart() {
        while (!run.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numNonPrimes.set(range-1);
    }

    public void generatePrimes() throws InterruptedException {
        waitForStart();
        // Initialize all numbers as potential primes
        Arrays.fill(primes, true);

        // Create executor service with fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Divide range into chunks and submit PrimeChecker tasks to executor
        int chunkSize = (range + numThreads - 1) / numThreads; // round up
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize - 1, range);
            executor.submit(new PrimeChecker(start, end));
        }

        // Shut down executor service and wait for all tasks to finish
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        run.set(false);
    }

    public void generatePrimesManually() throws InterruptedException {
        waitForStart();
        // Initialize all numbers as potential primes
        Arrays.fill(primes, true);
        
        // Create threads to work on different portions of the range
        threads = new Thread[numThreads];
        int chunkSize = (range + numThreads - 1) / numThreads; // round up
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize - 1, range);
            threads[i] = new Thread(new PrimeChecker(start, end));
            threads[i].start();
        }
        
        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }
        run.set(false);
    }

    public boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        return primes[n];
    }

    public int getProgress() {
        return range - numNonPrimes.get();
    }

    public int getNumThreads() {
        return numThreads;
    }

    public int getRange() {
        return range;
    }

    private class PrimeChecker implements Runnable {
        private final int start;
        private final int end;

        public PrimeChecker(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = 2; i * i <= end; i++) {
                if (primes[i]) {
                    for (int j = Math.max(i * i, ((start + i - 1) / i) * i); j <= end; j += i) {
                        if (primes[j]) {
                            primes[j] = false;
                            numNonPrimes.decrementAndGet();
                        }
                    }
                }
            }
        }
    }
}
