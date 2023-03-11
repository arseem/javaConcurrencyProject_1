import javax.swing.*;
import java.awt.*;

public class PrimeNumberGeneratorGUI extends JFrame {
    private JProgressBar executorProgressBar;
    private JProgressBar manualProgressBar;
    private JLabel executorLabel;
    private JLabel manualLabel;
    private JButton startButton;
    private JButton resetButton;
    private JLabel buttons;
    private PrimeNumberGenerator generatorExecutor;
    private PrimeNumberGenerator generatorManual;
    private final int numNotPrimes;
    private long startTime;
    private long endTimeExecutor;
    private long endTimeManual;

    public PrimeNumberGeneratorGUI(int numNotPrimes, PrimeNumberGenerator generatorExecutor, PrimeNumberGenerator generatorManual) {
        super("Prime Number Generator");
        this.numNotPrimes = numNotPrimes;
        this.generatorExecutor = generatorExecutor;
        this.generatorManual = generatorManual;
        int WIDTH = 400;
        int HEIGHT = 100;

        // Set up GUI components
        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        executorProgressBar = new JProgressBar(0, 100);
        executorProgressBar.setStringPainted(true);
        executorLabel = new JLabel("Executor");
        manualProgressBar = new JProgressBar(0, 100);
        manualProgressBar.setStringPainted(true);
        manualLabel = new JLabel("Manual");
        buttons = new JLabel();
        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            this.generatorExecutor.start();
            this.generatorManual.start();
            this.startTime = System.currentTimeMillis();
        });
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            this.generatorExecutor.reset();
            this.generatorManual.reset();
            this.endTimeExecutor = 0;
            this.endTimeManual = 0;
        });
        buttons.setLayout(new GridLayout(1, 2));
        buttons.add(startButton, BorderLayout.WEST);
        buttons.add(resetButton, BorderLayout.EAST);
        panel.add(executorLabel, BorderLayout.CENTER);
        panel.add(executorProgressBar);
        panel.add(manualLabel, BorderLayout.CENTER);
        panel.add(manualProgressBar);
        panel.add(buttons);
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void runGUI() {
        // Update progress bars in real time
        new Timer(10, e -> {
            float executorProgress = (float) this.generatorExecutor.getProgress() / (float) this.numNotPrimes * 100;
            float manualProgress = (float) this.generatorManual.getProgress() / (float) this.numNotPrimes * 100;
            executorProgressBar.setValue((int) executorProgress);
            manualProgressBar.setValue((int) manualProgress);
            if (executorProgress < 100) {
                executorProgressBar.setString((int) executorProgress + "%");
            } else {
                if (!(this.endTimeExecutor > 0)) {
                    this.endTimeExecutor = System.currentTimeMillis();
                }
                executorProgressBar.setString(((double)endTimeExecutor - (double)startTime)/1000 + "s");
            }
            if (manualProgress < 100) {
                manualProgressBar.setString((int) manualProgress + "%");
            } else {
                if (!(this.endTimeManual > 0)) {
                    this.endTimeManual = System.currentTimeMillis();
                }
                manualProgressBar.setString(((double)endTimeManual - (double)startTime)/1000 + "s");
            }
        }).start();
    }
}
