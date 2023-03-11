public class Main {
    public static void main(String[] args) {
        int numThreads = 100;
        int range = 2000000000;

        PrimeNumberGenerator generatorExecutor = new PrimeNumberGenerator(numThreads, range);
        PrimeNumberGenerator generatorManual = new PrimeNumberGenerator(numThreads, range);
        GUI gui = new GUI(generatorExecutor, generatorManual);

        // Create threads for generating primes
        Thread executorThread = new Thread(() -> {
            while (true) {
                try {
                    generatorExecutor.generatePrimes();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread manualThread = new Thread(() -> {
            while (true) {
                try {
                    generatorManual.generatePrimesManually();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread guiThread = new Thread(() -> {
            gui.runGUI();
        });

        // Start threads
        guiThread.start();
        executorThread.start();
        manualThread.start();
    }
}
