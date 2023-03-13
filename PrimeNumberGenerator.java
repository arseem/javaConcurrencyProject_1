import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimeNumberGenerator {
    private int numTasks;
    private int range;
    private int maxThreads;
    private BitSet primes;
    public Thread[] threads;
    private final AtomicInteger numChecked;
    private AtomicBoolean run;

    public PrimeNumberGenerator(int numTasks, int range, int maxThreads) {
        this.numTasks = numTasks;
        this.range = range;
        this.primes = new BitSet(range + 1);
        this.maxThreads = maxThreads;
        this.numChecked = new AtomicInteger(0);
        this.run = new AtomicBoolean(false);
    }

    public void start() {
        if (!run.get()) {
            run.set(true);
        }
    }

    public void start(int newNumTasks, int newRange, int newMaxThreads) {
        if (!run.get()) {
            range = newRange;
            numTasks = newNumTasks;
            maxThreads = newMaxThreads;
            numChecked.set(0);
            primes = new BitSet(range + 1);
            run.set(true);
        }
    }

    public void reset() {
        if (!run.get()){
            primes.set(0, range+1);
            numChecked.set(0);
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
        numChecked.set(0);
    }

    // Metoda generująca liczby pierwsze za pomocą puli wątków z ExecutorService
    public void generatePrimes() throws InterruptedException {
        // Oczekiwanie na start
        waitForStart();

        // Inicjalizacja wszystkich wartości jako potencjalne liczby pierwsze
        primes.set(0, range+1);

        // Utworzenie puli wątków
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        // Podział zakresu na mniejsze części
        int chunkSize = (range + numTasks - 1) / numTasks;
        for (int i = 0; i < numTasks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize - 1, range);
            executor.execute(new PrimeChecker(start, end));
        }

        // Zakończenie puli wątków i oczekiwanie na zakończenie wszystkich wątków
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        numChecked.set(range);
        run.set(false);
    }

    // Metoda generująca liczby pierwsze za pomocą ręcznego tworzenia wątków
    public void generatePrimesManually() throws InterruptedException {
        // Oczekiwanie na start
        waitForStart();

        // Inicjalizacja wszystkich wartości jako potencjalne liczby pierwsze
        primes.set(0, range+1);;
        
        // Utworzenie wątków i ich uruchomienie
        threads = new Thread[numTasks];
        int chunkSize = (range + numTasks - 1) / numTasks;
        for (int i = 0; i < numTasks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize - 1, range);
            threads[i] = new Thread(new PrimeChecker(start, end));
            threads[i].start();
        }
        
        // Oczekiwanie na zakończenie wszystkich wątków
        for (Thread thread : threads) {
            thread.join();
        }
        numChecked.set(range);
        run.set(false);
    }

    public boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        return primes.get(n);
    }

    public BitSet getPrimes() {
        return primes;
    }

    public int getProgress() {
        return numChecked.get();
    }

    public int getNumTasks() {
        return numTasks;
    }

    public int getRange() {
        return range;
    }

    public int getMaxThreads() {
        return maxThreads;
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
            // countedNums - liczba sprawdzonych liczb niepierwszych w danym zakresie
            int countedNums = 0;
            // sito Eratostenesa
            for (int i = 2; i * i <= end; i++) {
                // Sprawdzenie czy i jest [ptencjalną liczbą pierwszą
                if (primes.get(i)) {
                    // Sprawdzenie wszystkich liczb z zakresu, które są podzielne przez i i nie są liczbą pierwszą
                    for (int j = Math.max(i * i, ((start + i - 1) / i) * i); j <= end; j += i) {
                        if (primes.get(j)) {
                            primes.set(j, false);
                            countedNums++;
                        }
                    }
                }
            }
            // Dodanie ilości liczb sprawdzonych w danym wątku do liczby wszystkich sprawdzonych liczb 
            numChecked.addAndGet(countedNums);
            numChecked.addAndGet(primes.get(start, end).cardinality());
        }
    }
}
