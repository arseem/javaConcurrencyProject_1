import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private JProgressBar executorProgressBar;
    private JProgressBar manualProgressBar;
    private JLabel executorLabel;
    private JLabel manualLabel;
    private JButton startButton;
    private JButton resetButton;
    private JLabel buttons;
    private JSpinner numThreadsSpinner;
    private JSpinner rangeSpinner;
    private JLabel spinners;
    private PrimeNumberGenerator generatorExecutor;
    private PrimeNumberGenerator generatorManual;
    private long startTime;
    private long endTimeExecutor;
    private long endTimeManual;

    public GUI(PrimeNumberGenerator generatorExecutor, PrimeNumberGenerator generatorManual) {
        super("ThreadRace");
        this.generatorExecutor = generatorExecutor;
        this.generatorManual = generatorManual;
        int WIDTH = 600;
        int HEIGHT = 200;

        // Set up GUI components
        JPanel panel = new JPanel(new GridLayout(6, 1));
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
            this.generatorExecutor.start((int) numThreadsSpinner.getValue(), (int) rangeSpinner.getValue());
            this.generatorManual.start((int) numThreadsSpinner.getValue(), (int) rangeSpinner.getValue());
            this.startTime = System.currentTimeMillis();
            startButton.setEnabled(false);
            resetButton.setEnabled(false);
        });
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            this.generatorExecutor.reset();
            this.generatorManual.reset();
            this.endTimeExecutor = 0;
            this.endTimeManual = 0;
            startButton.setEnabled(true);
        });
        buttons.setLayout(new GridLayout(1, 2));
        buttons.add(startButton, BorderLayout.WEST);
        buttons.add(resetButton, BorderLayout.EAST);
        spinners = new JLabel();
        spinners.setLayout(new GridLayout(2, 2));
        numThreadsSpinner = new JSpinner(new SpinnerNumberModel(generatorExecutor.getNumThreads(), 1, 100000000, 1));
        rangeSpinner = new JSpinner(new SpinnerNumberModel(generatorExecutor.getRange(), 10, 2100000000, 1));
        spinners.add(new JLabel("Number of threads:"));
        spinners.add(numThreadsSpinner);
        spinners.add(new JLabel("Range:"));
        spinners.add(rangeSpinner);

        panel.add(executorLabel, BorderLayout.CENTER);
        panel.add(executorProgressBar);
        panel.add(manualLabel, BorderLayout.CENTER);
        panel.add(manualProgressBar);
        panel.add(buttons);
        panel.add(spinners);

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void runGUI() {
        // Update progress bars in real time
        new Timer(10, e -> {
            float executorProgress = (float) this.generatorExecutor.getProgress() / (float) this.generatorExecutor.getRange() * 100;
            float manualProgress = (float) this.generatorManual.getProgress() / (float) this.generatorManual.getRange() * 100;
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
            if (manualProgress == 100 && executorProgress == 100) {
                resetButton.setEnabled(true);
            }
        }).start();
    }
}
