import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int numThreads = 10;
        int range = 100000000;
        // Załączenie / wyłączenie trybu pobierania danych
        boolean dataFetchingMode = false;

        PrimeNumberGenerator generatorExecutor = new PrimeNumberGenerator(numThreads, range);
        PrimeNumberGenerator generatorManual = new PrimeNumberGenerator(numThreads, range);

        // Utworzenie wątków do generowania liczb pierwszych
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

        if (!dataFetchingMode) {
            GUI gui = new GUI(generatorExecutor, generatorManual);

            Thread guiThread = new Thread(() -> {
                gui.runGUI();
            });

            guiThread.start();
        }
        
        // Start threads
        executorThread.start();
        manualThread.start();

        // Perform tests
        if (dataFetchingMode) {
            performTests(generatorExecutor, generatorManual);
            executorThread.interrupt();
            manualThread.interrupt();
            System.exit(0);
        }
    }

    private static void performTests(PrimeNumberGenerator generatorExecutor, PrimeNumberGenerator generatorManual) {
        int maxThreads = 256;
        int maxRange = 2000000000;
        float executorProgress = 0;
        float manualProgress = 0;
        double startTime = 0;
        int[] numThreadsArray = new int[maxThreads];
        double[] executorTimeArray = new double[maxThreads];
        double[] manualTimeArray = new double[maxThreads];
        int range = generatorExecutor.getRange();
        int numThreads = generatorExecutor.getNumThreads();

        for (int threads=1; threads<maxThreads; threads++) {
            executorProgress = 0;
            manualProgress = 0;
            numThreadsArray[threads-1] = threads;
            System.out.println(threads + " Threads, " + range + " Range");

            startTime = (double) System.currentTimeMillis();
            generatorExecutor.start(threads, range);
            while (executorProgress!=100) {
                executorProgress = (float) generatorExecutor.getProgress() / (float) generatorExecutor.getRange() * 100;
            }
            executorTimeArray[threads-1] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorExecutor.reset();
            System.out.println("Executor: " + executorTimeArray[threads-1] + "s");

            startTime = (double) System.currentTimeMillis();
            generatorManual.start(threads, range);
            while (manualProgress!=100) {
                manualProgress = (float) generatorManual.getProgress() / (float) generatorManual.getRange() * 100;
            }
            manualTimeArray[threads-1] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorManual.reset();
            System.out.println("Manual: " + manualTimeArray[threads-1] + "s");

        }
        try {
			writeToExcel("Threads", numThreadsArray, executorTimeArray, manualTimeArray);
		} catch (IOException e) {
			e.printStackTrace();
		}

        int[] rangeArray = new int[(String.valueOf(maxRange).length()-1)*2];
        executorTimeArray = new double[(String.valueOf(maxRange).length()-1)*2];
        manualTimeArray = new double[(String.valueOf(maxRange).length()-1)*2];
        int index = 0;

        for (int i=10; index<rangeArray.length; index++) {
            executorProgress = 0;
            manualProgress = 0;
            rangeArray[index] = i;
            System.out.println(numThreads + " Threads, " + i + " Range");

            startTime = (double) System.currentTimeMillis();
            generatorExecutor.start(numThreads, i);
            while (executorProgress!=100) {
                executorProgress = (float) generatorExecutor.getProgress() / (float) generatorExecutor.getRange() * 100;
            }
            executorTimeArray[index] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorExecutor.reset();
            System.out.println("Executor: " + executorTimeArray[index] + "s");

            startTime = (double) System.currentTimeMillis();
            generatorManual.start(numThreads, i);
            while (manualProgress!=100) {
                manualProgress = (float) generatorManual.getProgress() / (float) generatorManual.getRange() * 100;
            }
            manualTimeArray[index] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorManual.reset();
            System.out.println("Manual: " + manualTimeArray[index] + "s");

            if (Integer.toString(i).charAt(0)!='1') {
                i=i*2;
            } else {
                if (i<1000000000) { 
                    i=i*5; 
                } else { 
                    i=i*2; 
                }
            }
        }
        try {
            writeToExcel("Range", rangeArray, executorTimeArray, manualTimeArray);
        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    private static void writeToExcel(String firstColumn, int[] range, double[] timeExecutor, double[] timeManual) throws IOException {
        System.out.println("Writing to file...");
        BufferedWriter writer = new BufferedWriter(new FileWriter("timePer"+ firstColumn +".csv"));
        writer.write(firstColumn+",Executor Time[s],Manual Time[s]\n");
        
        for (int i=0; i<range.length; i++) {
            writer.write(String.valueOf(range[i]));
            writer.write(",");
            writer.write(String.valueOf(timeExecutor[i]));
            writer.write(",");
            writer.write(String.valueOf(timeManual[i]));
            writer.write("\n");
        }

        writer.close();
        System.out.println("Finished.");
    }
}
