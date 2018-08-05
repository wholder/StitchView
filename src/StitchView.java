import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class StitchView extends JPanel {
  private static final int    BUFFER_STEPS = 4000;
  private StitchPattern       pattern;
  private BufferedImage       offScr;
  private List<BufferedImage> buffers;
  private List<Point>         starts;
  private Rectangle           bounds;
  private Dimension           lastDim;
  private int                 aStep, numSteps;

  private StitchView (StitchPattern pattern) {
    this.pattern = pattern;
    bounds = pattern.getBounds();
    setPreferredSize(new Dimension(bounds.width, bounds.height));
    numSteps = aStep = pattern.getStitches().size();
  }

  public void paint (Graphics g) {
    Dimension d = getSize();
    if (offScr == null  ||  (lastDim != null  &&  (d.width != lastDim.width  ||  d.height != lastDim.height))) {
      GraphicsConfiguration gConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      offScr = gConfig.createCompatibleImage(d.width, d.height);
      buffers = new ArrayList<>();
      starts = new ArrayList<>();
    }
    lastDim = d;
    BufferedImage drawBuf;
    int tmp = aStep / BUFFER_STEPS;
    int start;
    Graphics2D g2;
    int lastX = -bounds.x, lastY = -bounds.y;
    int startX = lastX;
    int startY = lastY;
    if (tmp > 0 && buffers.size() >= tmp) {
      BufferedImage tmpBuf = buffers.get(tmp - 1);
      drawBuf = new BufferedImage(tmpBuf.getWidth(), tmpBuf.getHeight(), tmpBuf.getType());
      Graphics gc = drawBuf.getGraphics();
      gc.drawImage(tmpBuf, 0, 0, null);
      gc.dispose();
      Point lastStart = starts.get(tmp - 1);
      lastX = lastStart.x;
      lastY = lastStart.y;
      g2 = (Graphics2D) drawBuf.getGraphics();
      start = tmp * BUFFER_STEPS;
    } else {
      drawBuf = offScr;
      g2 = (Graphics2D) drawBuf.getGraphics();
      g2.setBackground(Color.white);
      g2.clearRect(0, 0, d.width, d.height);
      start = 0;
    }
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    List<StitchPattern.Stitch> stitches = pattern.getStitches();
    for (int ii = start; ii < stitches.size(); ii++) {
      if (ii >= aStep) {
        break;
      }
      StitchPattern.Stitch stitch = stitches.get(ii);
      int xLoc = stitch.xx - bounds.x;
      int yLoc = stitch.yy - bounds.y;
      g2.setColor(stitch.color);
      g2.drawLine(lastX, lastY, xLoc, yLoc);
      lastX = xLoc;
      lastY = yLoc;
      int bufIdx = ii / BUFFER_STEPS;
      if (bufIdx > 0 && buffers.size() < bufIdx) {
        BufferedImage newBuf = new BufferedImage(drawBuf.getWidth(), drawBuf.getHeight(), drawBuf.getType());
        Graphics gc = newBuf.getGraphics();
        gc.drawImage(drawBuf, 0, 0, null);
        gc.dispose();
        buffers.add(newBuf);
        starts.add(new Point(lastX, lastY));
      }
    }
    // Draw magenta Plus sign (+) at starting point of pattern
    g2.setStroke(new BasicStroke(3.0f));
    g2.setColor(Color.magenta);
    g2.drawLine(startX - 8, startY, startX + 8, startY);
    g2.drawLine(startX, startY - 8, startX, startY + 8);
    g.drawImage(drawBuf, 0, 0, this);
  }

  private void setStep (int step) {
    aStep = step;
    repaint();
  }

  private void incStep () {
    if (aStep < numSteps) {
      aStep++;
      repaint();
    }
  }

  private void decStep () {
    if (aStep > 0) {
      aStep--;
      repaint();
    }
  }

  public static void main (String[] args) throws IOException {
    if (args.length < 1) {
      System.out.println("Usage: java -jar StitchView.jar <file.pes|file.dst");
      System.exit(1);
    }
    StitchPattern pattern = new StitchPattern();
    int idx = args[0].lastIndexOf(".");
    if (idx > 0) {
      switch (args[0].substring(idx + 1).toLowerCase()) {
      case "pes":
        pattern = new PesPattern(getFile(args[0]));
        break;
      case "dst":
        pattern = new DstPattern(getFile(args[0]));
        break;
      case "exp":
        pattern = new ExpPattern(getFile(args[0]));
        break;
      default:
        System.out.println("Unknown file type: " + args[0]);
      }
    } else {
      System.out.println("Usage: java -jar StitchView.jar <file.pes|file.dst");
    }
    pattern.printColors();
    pattern.printInfo();
    JFrame frame = new JFrame("PES Embroider File Viewer");
    frame.setResizable(false);
    frame.setLayout(new BorderLayout());
    StitchView stitches = new StitchView(pattern);
    frame.add(stitches, BorderLayout.CENTER);
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, pattern.getStitchCount(), pattern.getStitchCount());
    slider.addChangeListener(ev -> stitches.setStep(slider.getValue()));
    JPanel bottomPane = new JPanel(new BorderLayout());
    bottomPane.setBorder(new EmptyBorder(0, 10, 0, 10));
    bottomPane.add(slider, BorderLayout.CENTER);
    JButton left = new JButton("<");
    left.addActionListener(e -> stitches.decStep());
    left.setPreferredSize(new Dimension(24, 12));
    bottomPane.add(left, BorderLayout.WEST);
    JButton right = new JButton(">");
    right.addActionListener(e -> stitches.incStep());
    right.setPreferredSize(new Dimension(24, 12));
    bottomPane.add(right, BorderLayout.EAST);
    frame.add(bottomPane, BorderLayout.SOUTH);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static byte[] getFile (String file) throws IOException {
    InputStream fis = new BufferedInputStream(new FileInputStream(file));
    byte[] data = new byte[fis.available()];
    fis.read(data);
    fis.close();
    return data;
  }
}

