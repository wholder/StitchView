import java.awt.*;
import java.util.ArrayList;

  /*
   *  Stitch file parser for .pes files used by Brother and Babylock embroidery home sewing machines
   *  Adapted from pesformat.js by Josh Varge (https://github.com/JoshVarga/html5-embroidery)
   */

class PesPattern extends StitchPattern {
  private ArrayList<Integer> pesColors = new ArrayList<>();

  static class PesColor {
    Color color;
    String  name;

    private PesColor (Color color, String name) {
      this.color = color;
      this.name = name;
    }

    // Note: StitchBuddy has 61 defined colors (indexes 1-61, excluding Applique colors)

    static PesColor[] colorPalette = {
      new PesColor(new Color(  0,   0,   0),   "Unknown"),         // 0
      new PesColor(new Color( 14,  31, 124),   "Prussian Blue"),   // 1  007 - 588 (Satin - Country)
      new PesColor(new Color( 10,  85, 163),   "Blue"),            // 2  405 - 586
      new PesColor(new Color(  0, 135, 119),   "Teal Green"),      // 3  534 - 483
      new PesColor(new Color( 75, 107, 175),   "Cornflower Blue"), // 4  070 - 015
      new PesColor(new Color(237,  23,  31),   "Red"),             // 5  800 - 149
      new PesColor(new Color(209,  92,   0),   "Reddish Brown"),   // 6  337 - 264
      new PesColor(new Color(145,  54, 151),   "Magenta"),         // 7  620 - 625
      new PesColor(new Color(228, 154, 203),   "Light Lilac"),     // 8  810 - 133
      new PesColor(new Color(145,  95, 172),   "Lilac"),           // 9  612 - 624
      new PesColor(new Color(158, 214, 125),   "Mint Green"),      // 10 502 - 461
      new PesColor(new Color(232, 169,   0),   "Deep Gold"),       // 11 214 - 354
      new PesColor(new Color(254, 186,  53),   "Orange"),          // 12 208 - 335   - StitchBuddy verified
      new PesColor(new Color(255, 255,   0),   "Yellow"),          // 13 205 - 043
      new PesColor(new Color(112, 188,  31),   "Lime Green"),      // 14 513 - 444
      new PesColor(new Color(186, 152,   0),   "Brass"),           // 15 328 - 404
      new PesColor(new Color(168, 168, 168),   "Silver"),          // 16 005 - 020
      new PesColor(new Color(125, 111,   0),   "Russet Brown"),    // 17 330 - 414
      new PesColor(new Color(255, 255, 179),   "Cream Brown"),     // 18 010 - 331
      new PesColor(new Color( 79,  85,  86),   "Pewter"),          // 19 704 - 745
      new PesColor(new Color(  0,   0,   0),   "Black"),           // 20 900 - 100
      new PesColor(new Color( 11,  61, 145),   "Ultramarine"),     // 21 406 - 575
      new PesColor(new Color(119,   1, 118),   "Royal Purple"),    // 22 869 - 626
      new PesColor(new Color( 41,  49,  51),   "Dark Gray"),       // 23 707 - 747
      new PesColor(new Color( 42,  19,   1),   "Dark Brown"),      // 24 058 - 717
      new PesColor(new Color(246,  74, 138),   "Deep Rose"),       // 25 086 - 024
      new PesColor(new Color(178, 118,  36),   "Light Brown"),     // 26 323 - 255
      new PesColor(new Color(252, 187, 197),   "Salmon Pink"),     // 27 079 - 122
      new PesColor(new Color(254, 55,   15),   "Vermillion"),      // 28 030 - 148
      new PesColor(new Color(240, 240, 240),   "White"),           // 29 001 - 000
      new PesColor(new Color(106,  28, 138),   "Violet"),          // 30 613 - 634
      new PesColor(new Color(168, 221, 196),   "Seacrest"),        // 31 542 - 505
      new PesColor(new Color( 37, 132, 187),   "Sky Blue"),        // 32 019 - 512
      new PesColor(new Color(254, 179,  67),   "Pumpkin"),         // 33 126 - 322
      new PesColor(new Color(255, 243, 107),   "Cream Yellow"),    // 34 812 - 370   - StitchBuddy verified
      new PesColor(new Color(208, 166,  96),   "Khaki"),           // 35 348 - 242
      new PesColor(new Color(209,  84,   0),   "Clay Brown"),      // 36 339 - 224
      new PesColor(new Color(102, 186,  73),   "Leaf Green"),      // 37 509 - 463
      new PesColor(new Color( 19,  74,  70),   "Peacock Blue"),    // 38 415 - 057
      new PesColor(new Color(135, 135, 135),   "Gray"),            // 39 817 - 734
      new PesColor(new Color(216, 204, 198),   "Warm Gray"),       // 40 399 - 706
      new PesColor(new Color( 67,  86,   7),   "Dark Olive"),      // 41 517 - 473
      new PesColor(new Color(253, 217, 222),   "Linen"),           // 42 307 - 025   - StitchBuddy (Was "Flesh Pink")
      new PesColor(new Color(249, 147, 188),   "Hot Pink"),        // 43 085 - 155   - (changed to "Hot Pink")
      new PesColor(new Color(  0,  56,  34),   "Deep Green"),      // 44 808 - 467
      new PesColor(new Color(178, 175, 212),   "Lavender"),        // 45 804 - 604
      new PesColor(new Color(104, 106, 176),   "Wisteria Violet"), // 46 607 - 003
      new PesColor(new Color(239, 227, 185),   "Beige"),           // 47 843 - 012
      new PesColor(new Color(247,  56, 102),   "Carmine"),         // 48 807 - 158
      new PesColor(new Color(181,  75, 100),   "Amber Red"),       // 49 333 - 212
      new PesColor(new Color( 19,  43,  26),   "Olive Green"),     // 50 519 - 476
      new PesColor(new Color(199,   1,  86),   "Dark Fuschia"),    // 51 107 - 126
      new PesColor(new Color(254, 158,  50),   "Tangerine"),       // 52 209 - 336
      new PesColor(new Color(168, 222, 235),   "Light Blue"),      // 53 017 - 150
      new PesColor(new Color(  0, 103,  62),   "Emerald Green"),   // 54 507 - 485
      new PesColor(new Color( 78,  41, 144),   "Purple"),          // 55 614 - 635
      new PesColor(new Color( 47, 126,  32),   "Moss Green"),      // 56 515 - 446
      new PesColor(new Color(255, 204, 204),   "Flesh Pink"),      // 57 124 - 152   - StitchBuddy verified
      new PesColor(new Color(255, 217,  17),   "Harvest Gold"),    // 58 206 - 334
      new PesColor(new Color(  9,  91, 166),   "Electric Blue"),   // 59 420 - 564
      new PesColor(new Color(240, 249, 112),   "Lemon Yellow"),    // 60 202 - 342
      new PesColor(new Color(227, 243,  91),   "Fresh Green"),     // 61 027 - 442
      new PesColor(new Color(255, 153,   0),   "Orange"),          // 62 208 - 335   - DUP ?
      new PesColor(new Color(255, 240, 141),   "Cream Yellow"),    // 63 812 - 370   - DUP ?
      new PesColor(new Color(255, 200, 200),   "Applique")};       // 64
  }

  PesPattern (byte[] data) {
    super();
    int start = (int) getLong32(data, 8);
    // Read colors
    int idx = start + 48;
    int numColors = data[idx++] + 1;
    for (int ii = 0; ii < numColors; ii++) {
      int color = (int) data[idx++] & 0xFF;
      pesColors.add(color);
    }
    idx = start + 532;
    // Read stitches
    int val1, val2;
    int stitchType;
    while (idx < data.length) {
      val1 = (int) data[idx++] & 0xFF;
      val2 = (int) data[idx++] & 0xFF;
      stitchType = StitchPattern.TYPE_NORMAL;
      if (val1 == 0xFF && val2 == 0x00) {
        // End of stitch pattern
        addRelativeStitch(0, 0, StitchPattern.TYPE_END);
        break;
      }
      if (val1 == 0xFE && val2 == 0xB0) {
        // Stop command (skips a byte)
        idx++;
        addRelativeStitch(0, 0, StitchPattern.TYPE_STOP);
      } else {
        // High bit set means 12-bit offset, otherwise 7-bit signed delta
        if ((val1 & 0x80) != 0) {
          if ((val1 & 0x20) != 0) {
            stitchType = StitchPattern.TYPE_TRIM;
          }
          if ((val1 & 0x10) != 0) {
            stitchType = StitchPattern.TYPE_JUMP;
            jumps++;
          }
          val1 = ((val1 & 0x0F) << 8) + val2;
          // Signed 12-bit arithmetic
          if ((val1 & 0x800) != 0) {
            val1 -= 0x1000;
          }
          val2 = (int) data[idx++] & 0xFF;
        } else if (val1 >= 0x40) {
          val1 -= 0x80;
        }
        if ((val2 & 0x80) != 0) {
          if ((val2 & 0x20) != 0) {
            stitchType = StitchPattern.TYPE_TRIM;
          }
          if ((val2 & 0x10) != 0) {
            stitchType = StitchPattern.TYPE_NORMAL;
            jumps++;
          }
          val2 = ((val2 & 0x0F) << 8) + ((int) data[idx++] & 0xFF);
          // Signed 12-bit arithmetic
          if ((val2 & 0x800) != 0) {
            val2 -= 0x1000;
          }
        } else if (val2 > 0x3F) {
          val2 -= 0x80;
        }
        addRelativeStitch(val1, val2, stitchType);
      }
    }
  }

  Rectangle getBounds () {
    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  int getStitchCount () {
    return stitches.size();
  }

  private void addAbsoluteStitch (int xx, int yy, int type) {
    if (type == StitchPattern.TYPE_STOP && stitches.size() == 0) {
      return;
    }
    if (type == StitchPattern.TYPE_STOP) {
      colorIdx++;
      if (colorIdx >= pesColors.size()) {
        // Duplicate last color to fix pattern error
        pesColors.add(pesColors.get(pesColors.size() - 1));
      }
    }
    minX = Math.min(minX, xx);
    minY = Math.min(minY, yy);
    maxX = Math.max(maxX, xx);
    maxY = Math.max(maxY, yy);
    if (type == StitchPattern.TYPE_NORMAL) {
      stitches.add(new Stitch(xx, yy, PesColor.colorPalette[pesColors.get(colorIdx)].color));
    }
  }

  private void addRelativeStitch (int dx, int dy, int type) {
    if (type == StitchPattern.TYPE_NORMAL) {
      maxStitch = Math.max(maxStitch, (int) Math.sqrt(dx * dx + dy * dy));
    }
    maxDist = Math.max(maxDist, (int) Math.sqrt(dx * dx + dy * dy));
    addAbsoluteStitch(lastX += dx, lastY += dy, type);
  }

  // Convert 8 little endian bytes into a 32 bit long value
  private static long getLong32 (byte[] data, int idx) {
    long val = 0;
    for (int ii = 3; ii >= 0; ii--) {
      val = (val << 8) + ((long) data[idx + ii] & 0xFF);
    }
    return val;
  }

  @Override
  void printColors () {
    for (int ii = 0; ii < pesColors.size(); ii++) {
      int colorIndex = pesColors.get(ii);
      PesColor color = PesColor.colorPalette[colorIndex];
      System.out.println("color[" + ii + "] = " + color.name + " idx = " + colorIndex);
    }
  }

  @Override
  void printInfo () {
    System.out.println("Stitches:   " + stitches.size());
    System.out.println("Max Dist:   " + maxDist);
    System.out.println("Max Stitch: " + maxStitch);
    System.out.println("Jumps:      " + jumps);
    System.out.println("Bounds:     " + minX + ", " + minY + ", " + (maxX - minX) + ", " + (maxY - minY));
  }
}
