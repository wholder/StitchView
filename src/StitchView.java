import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

class StitchView extends JPanel {
  private static Preferences  prefs = Preferences.userRoot().node(StitchView.class.getName());
  private static final int    BUFFER_STEPS = 4000;
  private StitchPattern       pattern;
  private BufferedImage       offScr;
  private List<BufferedImage> buffers;
  private List<Point>         starts;
  private Rectangle           bounds;
  private Dimension           lastDim;
  private JSlider             slider;
  private int                 aStep, numSteps;

  private StitchView (JFrame frame) {
    // Setup slider and button controls
    JPanel bottomPane = new JPanel(new BorderLayout());
    bottomPane.setBorder(new EmptyBorder(0, 10, 0, 10));
    slider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
    slider.addChangeListener(ev -> setStep(slider.getValue()));
    bottomPane.add(slider, BorderLayout.CENTER);
    JButton left = new JButton("\u25C0");
    left.addActionListener(e -> decStep());
    left.setPreferredSize(new Dimension(24, 12));
    bottomPane.add(left, BorderLayout.WEST);
    JButton right = new JButton("\u25B6");
    right.addActionListener(e -> incStep());
    right.setPreferredSize(new Dimension(24, 12));
    bottomPane.add(right, BorderLayout.EAST);
    frame.add(this, BorderLayout.CENTER);
    frame.add(bottomPane, BorderLayout.SOUTH);
  }

  private void loadFile (String fName) throws Exception {
    int idx = fName.lastIndexOf(".");
    if (idx > 0) {
      switch (fName.substring(idx + 1).toLowerCase()) {
      case "pes":
        pattern = new PesPattern(getFile(fName));
        break;
      case "dst":
        pattern = new DstPattern(getFile(fName));
        break;
      case "exp":
        pattern = new ExpPattern(getFile(fName));
        break;
      default:
        System.out.println("Unknown file type: " + fName);
        return;
      }
    } else {
      return;
    }
    bounds = pattern.getBounds();
    // Add 8 pixel border
    bounds = new Rectangle(bounds.x - 8, bounds.y - 8, bounds.width + 16, bounds.height + 16);
    setPreferredSize(new Dimension(bounds.width, bounds.height));
    numSteps = aStep = pattern.getStitches().size();
    slider.setMaximum(numSteps);
    slider.setValue(numSteps);
    pattern.printColors();
    pattern.printInfo();
    repaint();
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
    if (pattern != null) {
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
    } else {
      // Draw Title Page
      Dimension size = getSize();
      String text = this.getClass().getSimpleName();
      // Set up Font attributes for title page
      HashMap<TextAttribute, Object> attrs = new HashMap<>();
      attrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
      attrs.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
      Font ocr = (new Font("Times", Font.PLAIN, 110)).deriveFont(attrs);
      GlyphVector gv = ocr.createGlyphVector(g2.getFontRenderContext(), text);
      Rectangle2D bnds = gv.getVisualBounds();
      AffineTransform at = new AffineTransform();
      at.translate(-bnds.getX() + (size.width - bnds.getWidth()) / 2, -bnds.getY() + (size.height - bnds.getHeight()) / 2);
      g2.setColor(Color.lightGray);
      g2.fill(at.createTransformedShape(gv.getOutline()));
      at.translate(-4, -4);
      g2.setColor(Color.black);
      g2.fill(at.createTransformedShape(gv.getOutline()));
    }
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

  public static void main (String[] args) {
    try {
      JFrame frame = new JFrame("StitchView");
      frame.setLayout(new BorderLayout());
      StitchView stitchView = new StitchView(frame);
      JMenuBar menuBar = new JMenuBar();
      // Add "File" Menu
      JMenu fileMenu = new JMenu("File");
      // Add "Open" Item to File Menu
      JMenuItem loadGbr = new JMenuItem("Open Embroidery File");
      loadGbr.addActionListener(ev -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Embroidery File");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        FileNameExtensionFilter nameFilter = new FileNameExtensionFilter("Embroidery files (*.pes,*.dst,*.exp)", "pes", "dst", "exp");
        fileChooser.addChoosableFileFilter(nameFilter);
        fileChooser.setFileFilter(nameFilter);
        fileChooser.setSelectedFile(new File(prefs.get("default.dir", "/")));
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
          try {
            File tFile = fileChooser.getSelectedFile();
            prefs.put("default.dir", tFile.getAbsolutePath());
            frame.setTitle(frame.getClass().getSimpleName() + " - " + tFile.toString());
            stitchView.loadFile(tFile.getCanonicalPath());
            frame.pack();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Unable to load file", "Error", JOptionPane.PLAIN_MESSAGE);
            ex.printStackTrace(System.out);
          }
        }
      });
      fileMenu.add(loadGbr);
      menuBar.add(fileMenu);
      frame.setJMenuBar(menuBar);
      frame.setResizable(false);
      stitchView.loadFile("res:StitchView2.dst");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private byte[] getFile (String fName) throws Exception {
    if (fName.startsWith("res:")) {
      return Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(fName.substring(4)).toURI()));
    } else {
      InputStream fis = new BufferedInputStream(new FileInputStream(new File(fName)));
      byte[] data = new byte[fis.available()];
      fis.read(data);
      fis.close();
      return data;
    }
  }
}

