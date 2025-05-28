/**
 * ChartGenerator class for creating visualizations of simulation results.
 * This class provides methods to generate various types of charts including
 * time series, rebellion size, and parameter interaction charts using Swing.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class ChartGenerator {
    /**
     * Generates a time series chart showing the evolution of agent states over time.
     * @param activeCounts List of active agent counts
     * @param jailedCounts List of jailed agent counts
     * @param quietCounts List of quiet agent counts
     * @param experimentId Identifier for the experiment
     * @param outputDir Directory to save the chart
     */
    public static void generateTimeSeriesChart(List<Integer> activeCounts, 
                                             List<Integer> jailedCounts, 
                                             List<Integer> quietCounts,
                                             String experimentId,
                                             String outputDir) {
        JPanel chartPanel = new TimeSeriesChart(activeCounts, jailedCounts, quietCounts, 
            "Experiment " + experimentId + " Time Series");
        
        // Create a frame to render the chart
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        
        // Create an image from the panel
        BufferedImage image = new BufferedImage(
            chartPanel.getWidth(),
            chartPanel.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = image.createGraphics();
        chartPanel.paint(g2d);
        g2d.dispose();

        // Save the image
        String filename = outputDir + "/experiment_" + experimentId + "_timeseries.png";
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            System.err.println("Error saving time series chart: " + e.getMessage());
        }
        
        frame.dispose();
    }

    /**
     * Generates a chart showing the size of rebellion over time.
     * @param rebellionSizes List of rebellion sizes
     * @param experimentId Identifier for the experiment
     * @param outputDir Directory to save the chart
     */
    public static void generateRebellionSizeChart(List<Integer> rebellionSizes, 
                                                String experimentId,
                                                String outputDir) {
        JPanel chartPanel = new RebellionSizeChart(rebellionSizes, 
            "Experiment " + experimentId + " Rebellion Size Over Time");
        
        // Create a frame to render the chart
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        
        // Create an image from the panel
        BufferedImage image = new BufferedImage(
            chartPanel.getWidth(),
            chartPanel.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = image.createGraphics();
        chartPanel.paint(g2d);
        g2d.dispose();

        // Save the image
        String filename = outputDir + "/experiment_" + experimentId + "_rebellion_size.png";
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            System.err.println("Error saving rebellion size chart: " + e.getMessage());
        }
        
        frame.dispose();
    }

    /**
     * Generates a chart showing the interaction between cop density and legitimacy.
     * @param copDensities List of cop density values
     * @param legitimacies List of legitimacy values
     * @param stabilityIndices List of stability indices
     * @param outputDir Directory to save the chart
     */
    public static void generateParameterInteractionChart(List<Double> copDensities,
                                                       List<Double> legitimacies,
                                                       List<Double> stabilityIndices,
                                                       String outputDir) {
        JPanel chartPanel = new ParameterInteractionChart(copDensities, legitimacies, stabilityIndices,
            "Parameter Interaction: Cop Density vs Legitimacy");
        
        // Create a frame to render the chart
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        
        // Create an image from the panel
        BufferedImage image = new BufferedImage(
            chartPanel.getWidth(),
            chartPanel.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = image.createGraphics();
        chartPanel.paint(g2d);
        g2d.dispose();

        // Save the image
        String filename = outputDir + "/parameter_interaction.png";
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            System.err.println("Error saving parameter interaction chart: " + e.getMessage());
        }
        
        frame.dispose();
    }
}

/**
 * Custom chart panel for displaying time series data.
 */
class TimeSeriesChart extends JPanel {
    private final List<Integer> active, jailed, quiet;
    private final String title;

    public TimeSeriesChart(List<Integer> active, List<Integer> jailed, List<Integer> quiet, String title) {
        this.active = active;
        this.jailed = jailed;
        this.quiet = quiet;
        this.title = title;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, 50, 30);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(50, 550, 750, 550); // x-axis
        g2d.drawLine(50, 50, 50, 550);   // y-axis

        // Draw data
        drawLine(g2d, active, Color.RED, "Active");
        drawLine(g2d, jailed, Color.BLUE, "Jailed");
        drawLine(g2d, quiet, Color.GREEN, "Quiet");

        // Draw legend
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.RED);
        g2d.drawString("Active", 600, 30);
        g2d.setColor(Color.BLUE);
        g2d.drawString("Jailed", 600, 45);
        g2d.setColor(Color.GREEN);
        g2d.drawString("Quiet", 600, 60);
    }

    private void drawLine(Graphics2D g2d, List<Integer> data, Color color, String label) {
        g2d.setColor(color);
        int max = data.stream().mapToInt(Integer::intValue).max().orElse(1);
        int n = data.size();
        
        for (int i = 1; i < n; i++) {
            int x1 = 50 + (700 * (i-1) / (n-1));
            int y1 = 550 - (500 * data.get(i-1) / max);
            int x2 = 50 + (700 * i / (n-1));
            int y2 = 550 - (500 * data.get(i) / max);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}

/**
 * Custom chart panel for displaying rebellion size data.
 */
class RebellionSizeChart extends JPanel {
    private final List<Integer> rebellionSizes;
    private final String title;

    public RebellionSizeChart(List<Integer> rebellionSizes, String title) {
        this.rebellionSizes = rebellionSizes;
        this.title = title;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, 50, 30);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(50, 550, 750, 550); // x-axis
        g2d.drawLine(50, 50, 50, 550);   // y-axis

        // Draw data
        g2d.setColor(Color.RED);
        int max = rebellionSizes.stream().mapToInt(Integer::intValue).max().orElse(1);
        int n = rebellionSizes.size();
        
        for (int i = 1; i < n; i++) {
            int x1 = 50 + (700 * (i-1) / (n-1));
            int y1 = 550 - (500 * rebellionSizes.get(i-1) / max);
            int x2 = 50 + (700 * i / (n-1));
            int y2 = 550 - (500 * rebellionSizes.get(i) / max);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}

/**
 * Custom chart panel for displaying parameter interaction data.
 */
class ParameterInteractionChart extends JPanel {
    private final List<Double> copDensities, legitimacies, stabilityIndices;
    private final String title;

    public ParameterInteractionChart(List<Double> copDensities, 
                                   List<Double> legitimacies,
                                   List<Double> stabilityIndices,
                                   String title) {
        this.copDensities = copDensities;
        this.legitimacies = legitimacies;
        this.stabilityIndices = stabilityIndices;
        this.title = title;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, 50, 30);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(50, 550, 750, 550); // x-axis
        g2d.drawLine(50, 50, 50, 550);   // y-axis

        // Draw bars
        int n = stabilityIndices.size();
        double maxStability = stabilityIndices.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        int barWidth = 600 / n;
        
        for (int i = 0; i < n; i++) {
            int x = 50 + (i * barWidth);
            int height = (int)(500 * stabilityIndices.get(i) / maxStability);
            g2d.setColor(new Color(0, 0, 255, 128));
            g2d.fillRect(x, 550 - height, barWidth - 5, height);
            
            // Draw parameter values
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(String.format("Cop=%.2f", copDensities.get(i)), x, 565);
            g2d.drawString(String.format("Leg=%.2f", legitimacies.get(i)), x, 580);
        }
    }
} 