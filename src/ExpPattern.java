import java.awt.*;

class ExpPattern extends StitchPattern {

  /*
   *  Stitch file parser for .exp files used by Melco commercial embroidery sewing machines
   *  Adapted from expformat.js by Josh Varge (https://github.com/JoshVarga/html5-embroidery)
   *
   *  Notes: https://github.com/Embroidermodder/Embroidermodder/issues/91
   */

  ExpPattern (byte[] data) {
      int dx = 0;
      int dy = 0;
      int idx = 0;
    while (idx < data.length) {
      int flags = StitchPattern.TYPE_NORMAL;
      int b0 = data[idx++] & 0xFF;
      int b1 = data[idx++] & 0xFF;
      if (b0 == 128) {
        if ((b1 & 1) != 0) {
          b0 = data[idx++] & 0xFF;
          b1 = data[idx++] & 0xFF;
          flags = StitchPattern.TYPE_STOP;
        } else if ((b1 == 2) || (b1 == 4)) {
          b0 =  data[idx++] & 0xFF;
          b1 =  data[idx++] & 0xFF;
          flags = StitchPattern.TYPE_TRIM;
        } else if (b1 == 128) {
          b0 =  data[idx++] & 0xFF;
          b1 =  data[idx++] & 0xFF;
          flags = StitchPattern.TYPE_TRIM;
        }
      }
      dx = expDecode(b0);
      dy = expDecode(b1);
      addRelativeStitch(dx, -dy, flags);      // Note: invert Y axis
    }
    addRelativeStitch(0, 0, StitchPattern.TYPE_END);
  }

  private void addAbsoluteStitch (int xx, int yy, int type) {
    if (type == StitchPattern.TYPE_STOP && stitches.size() == 0) {
      return;
    }
    minX = Math.min(minX, xx);
    minY = Math.min(minY, yy);
    maxX = Math.max(maxX, xx);
    maxY = Math.max(maxY, yy);
    if (type == StitchPattern.TYPE_NORMAL) {
      stitches.add(new Stitch(xx, yy, Color.black));
    }
  }

  private void addRelativeStitch (int dx, int dy, int type) {
    if (type == StitchPattern.TYPE_NORMAL) {
      maxStitch = Math.max(maxStitch, (int) Math.sqrt(dx * dx + dy * dy));
    }
    maxDist = Math.max(maxDist, (int) Math.sqrt(dx * dx + dy * dy));
    addAbsoluteStitch(lastX += dx, lastY += dy, type);
  }

  private int expDecode (int input) {
    return (input > 128) ? (-(~input & 255) - 1) : input;
  }
}
