public class Main {
    public static void main(String[] args) {
        int numThreads = 40;
        int range = 1000000000;

        PrimeNumberGenerator generatorCheck = new PrimeNumberGenerator(numThreads, range);
        try {
            generatorCheck.start();
            generatorCheck.generatePrimes();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final int numNotPrimes = generatorCheck.getProgress();

        PrimeNumberGenerator generatorExecutor = new PrimeNumberGenerator(numThreads, range);
        PrimeNumberGenerator generatorManual = new PrimeNumberGenerator(numThreads, range);
        PrimeNumberGeneratorGUI gui = new PrimeNumberGeneratorGUI(numNotPrimes, generatorExecutor, generatorManual);

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


        // Display progress in real time
        // while (executorProgress<100 || manualProgress<100) {
        //     executorProgress = (float)generatorExecutor.getProgress()/(float)numNotPrimes * 100;
        //     manualProgress = (float)generatorManual.getProgress()/(float)numNotPrimes * 100;
        //     System.out.println("Executor: " + (int)executorProgress + "%");
        //     System.out.println("Manual: " + (int)manualProgress + "%");
        //     try {
        //         Thread.sleep(10);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }

        // Print out the prime numbers
        // System.out.println("Primes found by Executor:");
        // for (int i = 2; i <= range; i++) {
        //     if (generatorExecutor.isPrime(i)) {
        //         System.out.print(i + " ");
        //     }
        // }
        // System.out.println("\n\nPrimes found by Manual:");
        // for (int i = 2; i <= range; i++) {
        //     if (generatorManual.isPrime(i)) {
        //         System.out.print(i + " ");
        //     }
        // }
    }
}
