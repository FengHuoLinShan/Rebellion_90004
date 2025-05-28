import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class ChartGenerator {
    
    public static void generateTimeSeriesChart(List<Integer> activeCounts, 
                                             List<Integer> jailedCounts,
                                             List<Integer> quietCounts,
                                             String experimentId) {
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
        try {
            ImageIO.write(image, "png", 
                new File("parameter_exploration_results/experiment_" + experimentId + "_timeseries.png"));
        } catch (IOException e) {
            System.err.println("Error saving time series chart: " + e.getMessage());
        }
        
        frame.dispose();
    }

    public static void generateRebellionSizeChart(List<Integer> rebellionSizes, String experimentId) {
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
        try {
            ImageIO.write(image, "png", 
                new File("parameter_exploration_results/experiment_" + experimentId + "_rebellion_size.png"));
        } catch (IOException e) {
            System.err.println("Error saving rebellion size chart: " + e.getMessage());
        }
        
        frame.dispose();
    }

    public static void generateParameterInteractionChart(List<Double> copDensities,
                                                       List<Double> legitimacies,
                                                       List<Double> stabilityIndices) {
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
        try {
            ImageIO.write(image, "png", 
                new File("parameter_exploration_results/parameter_interaction.png"));
        } catch (IOException e) {
            System.err.println("Error saving parameter interaction chart: " + e.getMessage());
        }
        
        frame.dispose();
    }
}

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
        
        int w = getWidth(), h = getHeight();
        int padding = 50;
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, padding, padding - 20);
        
        // Find maximum value
        int max = Math.max(Math.max(
            active.stream().max(Integer::compareTo).orElse(1),
            jailed.stream().max(Integer::compareTo).orElse(1)),
            quiet.stream().max(Integer::compareTo).orElse(1));
        
        // If all values are 0, set max to 1 to avoid division by zero
        if (max == 0) max = 1;
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, h - padding, w - padding, h - padding); // x-axis
        g2d.drawLine(padding, h - padding, padding, padding);         // y-axis
        
        // Draw axis labels
        g2d.drawString("0", padding - 15, h - padding + 15);
        g2d.drawString(String.valueOf(max), padding - 25, padding + 5);
        g2d.drawString("Time Step", w/2, h - padding + 30);
        g2d.drawString("Count", padding - 40, h/2);
        
        // Draw lines
        drawLine(g2d, active, Color.RED, w, h, max, padding);
        drawLine(g2d, jailed, Color.BLUE, w, h, max, padding);
        drawLine(g2d, quiet, Color.GREEN, w, h, max, padding);
        
        // Draw legend
        g2d.setColor(Color.RED);
        g2d.drawString("Active", w - 100, padding + 20);
        g2d.setColor(Color.BLUE);
        g2d.drawString("Jailed", w - 100, padding + 40);
        g2d.setColor(Color.GREEN);
        g2d.drawString("Quiet", w - 100, padding + 60);
    }
    
    private void drawLine(Graphics2D g2d, List<Integer> data, Color color, int w, int h, int max, int padding) {
        if (data.isEmpty()) return;
        
        g2d.setColor(color);
        int n = data.size();
        for (int i = 1; i < n; i++) {
            int x1 = padding + (w - 2 * padding) * (i-1) / (n-1);
            int y1 = h - padding - (h - 2 * padding) * data.get(i-1) / max;
            int x2 = padding + (w - 2 * padding) * i / (n-1);
            int y2 = h - padding - (h - 2 * padding) * data.get(i) / max;
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}

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
        
        int w = getWidth(), h = getHeight();
        int padding = 50;
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, padding, padding - 20);
        
        // Find maximum value
        int max = rebellionSizes.stream().max(Integer::compareTo).orElse(1);
        
        // If all values are 0, set max to 1 to avoid division by zero
        if (max == 0) max = 1;
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, h - padding, w - padding, h - padding); // x-axis
        g2d.drawLine(padding, h - padding, padding, padding);         // y-axis
        
        // Draw axis labels
        g2d.drawString("0", padding - 15, h - padding + 15);
        g2d.drawString(String.valueOf(max), padding - 25, padding + 5);
        g2d.drawString("Time Step", w/2, h - padding + 30);
        g2d.drawString("Rebellion Size", padding - 40, h/2);
        
        // Draw line
        if (!rebellionSizes.isEmpty()) {
            g2d.setColor(Color.RED);
            int n = rebellionSizes.size();
            for (int i = 1; i < n; i++) {
                int x1 = padding + (w - 2 * padding) * (i-1) / (n-1);
                int y1 = h - padding - (h - 2 * padding) * rebellionSizes.get(i-1) / max;
                int x2 = padding + (w - 2 * padding) * i / (n-1);
                int y2 = h - padding - (h - 2 * padding) * rebellionSizes.get(i) / max;
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }
}

class ParameterInteractionChart extends JPanel {
    private final List<Double> copDensities;
    private final List<Double> legitimacies;
    private final List<Double> stabilityIndices;
    private final String title;
    
    public ParameterInteractionChart(List<Double> copDensities, List<Double> legitimacies, 
                                   List<Double> stabilityIndices, String title) {
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
        
        int w = getWidth(), h = getHeight();
        int padding = 80;
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(title, padding, padding - 20);
        
        // Find maximum value
        double maxStability = stabilityIndices.stream().max(Double::compareTo).orElse(1.0);
        
        // If all values are 0, set max to 1 to avoid division by zero
        if (maxStability == 0) maxStability = 1.0;
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, h - padding, w - padding, h - padding); // x-axis
        g2d.drawLine(padding, h - padding, padding, padding);         // y-axis
        
        // Draw axis labels
        g2d.drawString("0", padding - 15, h - padding + 15);
        g2d.drawString(String.format("%.2f", maxStability), padding - 40, padding + 5);
        g2d.drawString("Legitimacy", w/2, h - padding + 30);
        g2d.drawString("Stability Index", padding - 60, h/2);
        
        // Draw bars
        if (!stabilityIndices.isEmpty()) {
            int barWidth = (w - 2 * padding) / (copDensities.size() * legitimacies.size());
            int currentX = padding;
            
            for (int i = 0; i < copDensities.size(); i++) {
                for (int j = 0; j < legitimacies.size(); j++) {
                    int index = i * legitimacies.size() + j;
                    if (index < stabilityIndices.size()) {
                        double stability = stabilityIndices.get(index);
                        int barHeight = (int)((h - 2 * padding) * stability / maxStability);
                        
                        // Create gradient color based on stability
                        Color barColor = new Color(
                            (int)(255 * stability / maxStability),
                            (int)(255 * (1 - stability / maxStability)),
                            0
                        );
                        g2d.setColor(barColor);
                        
                        g2d.fillRect(currentX, h - padding - barHeight, barWidth, barHeight);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(currentX, h - padding - barHeight, barWidth, barHeight);
                        
                        // Draw value
                        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                        g2d.drawString(String.format("%.2f", stability), 
                            currentX, h - padding - barHeight - 5);
                        
                        currentX += barWidth;
                    }
                }
            }
        }
        
        // Draw legend
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Cop Density:", w - 150, padding + 20);
        for (int i = 0; i < copDensities.size(); i++) {
            g2d.drawString(String.format("%.2f", copDensities.get(i)), 
                w - 150, padding + 40 + i * 20);
        }
    }
} 