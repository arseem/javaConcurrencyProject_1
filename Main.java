import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int numTasks = 10;
        int range = 100000000;
        int maxThreads = 16;
        // Załączenie / wyłączenie trybu pobierania danych
        boolean dataFetchingMode = false;

        PrimeNumberGenerator generatorExecutor = new PrimeNumberGenerator(numTasks, range, maxThreads);
        PrimeNumberGenerator generatorManual = new PrimeNumberGenerator(numTasks, range, maxThreads);

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
        int maxTasks = 256;
        int maxRange = 2000000000;
        int maxThreads = 256;
        float executorProgress = 0;
        float manualProgress = 0;
        double startTime = 0;
        int[] numTasksArray = new int[maxTasks];
        double[] executorTimeArray = new double[maxTasks];
        double[] manualTimeArray = new double[maxTasks];
        int range = generatorExecutor.getRange();
        int numTasks = generatorExecutor.getNumTasks();

        for (int tasks=1; tasks<maxTasks; tasks++) {
            executorProgress = 0;
            manualProgress = 0;
            numTasksArray[tasks-1] = tasks;
            System.out.println(tasks + " tasks, " + range + " Range");

            startTime = (double) System.currentTimeMillis();
            generatorExecutor.start(tasks, range, maxThreads);
            while (executorProgress!=100) {
                executorProgress = (float) generatorExecutor.getProgress() / (float) generatorExecutor.getRange() * 100;
            }
            executorTimeArray[tasks-1] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorExecutor.reset();
            System.out.println("Executor: " + executorTimeArray[tasks-1] + "s");

            startTime = (double) System.currentTimeMillis();
            generatorManual.start(tasks, range, maxThreads);
            while (manualProgress!=100) {
                manualProgress = (float) generatorManual.getProgress() / (float) generatorManual.getRange() * 100;
            }
            manualTimeArray[tasks-1] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorManual.reset();
            System.out.println("Manual: " + manualTimeArray[tasks-1] + "s");

        }
        try {
			writeToExcel("Threads", numTasksArray, executorTimeArray, manualTimeArray);
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
            System.out.println(numTasks + " Threads, " + i + " Range");

            startTime = (double) System.currentTimeMillis();
            generatorExecutor.start(numTasks, i, maxThreads);
            while (executorProgress!=100) {
                executorProgress = (float) generatorExecutor.getProgress() / (float) generatorExecutor.getRange() * 100;
            }
            executorTimeArray[index] = ((double) System.currentTimeMillis() - (double) startTime)/1000;
            generatorExecutor.reset();
            System.out.println("Executor: " + executorTimeArray[index] + "s");

            startTime = (double) System.currentTimeMillis();
            generatorManual.start(numTasks, i, maxThreads);
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
