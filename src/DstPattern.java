import java.awt.*;

/*
 *  Stitch file parser for .dst files used by Tajima commercial embroidery sewing machines
 *  Adapted from dstformat.js by Josh Varga (https://github.com/JoshVarga/html5-embroidery)
 */

class DstPattern extends StitchPattern {

  DstPattern (byte[] data) {
    super();
    int dx, dy;
    int idx = 512;
    while (idx < (data.length - 3)) {
      dx = 0;
      dy = 0;
      if ((data[idx] & 0x01) != 0) {
        dx += 1;
      }
      if ((data[idx] & 0x02) != 0) {
        dx -= 1;
      }
      if ((data[idx] & 0x04) != 0) {
        dx += 9;
      }
      if ((data[idx] & 0x08) != 0) {
        dx -= 9;
      }
      if ((data[idx] & 0x80) != 0) {
        dy -= 1;
      }
      if ((data[idx] & 0x40) != 0) {
        dy += 1;
      }
      if ((data[idx] & 0x20) != 0) {
        dy -= 9;
      }
      if ((data[idx] & 0x10) != 0) {
        dy += 9;
      }
      if ((data[idx + 1] & 0x01) != 0) {
        dx += 3;
      }
      if ((data[idx + 1] & 0x02) != 0) {
        dx -= 3;
      }
      if ((data[idx + 1] & 0x04) != 0) {
        dx += 27;
      }
      if ((data[idx + 1] & 0x08) != 0) {
        dx -= 27;
      }
      if ((data[idx + 1] & 0x80) != 0) {
        dy -= 3;
      }
      if ((data[idx + 1] & 0x40) != 0) {
        dy += 3;
      }
      if ((data[idx + 1] & 0x20) != 0) {
        dy -= 27;
      }
      if ((data[idx + 1] & 0x10) != 0) {
        dy += 27;
      }
      if ((data[idx + 2] & 0x04) != 0) {
        dx += 81;
      }
      if ((data[idx + 2] & 0x08) != 0) {
        dx -= 81;
      }
      if ((data[idx + 2] & 0x20) != 0) {
        dy -= 81;
      }
      if ((data[idx + 2] & 0x10) != 0) {
        dy += 81;
      }
      /*
       * Placeholder for future implementation
      if (data[idx + 2] == (int) 0xF3) {
        // StitchPattern.TYPE_END
      }
      if ((data[idx + 2] & 0xC3) == 0xC3) {
        // StitchPattern.TYPE_TRIM & StitchPattern.TYPE_STOP
      }
      if ((data[idx + 2] & 0x80) != 0) {
        // StitchPattern.TYPE_TRIM
      }
      if ((data[idx + 2] & 0x40) != 0) {
        //StitchPattern.TYPE_STOP;
      }
      */
      addRelativeStitch(dx, dy, StitchPattern.TYPE_NORMAL);
      idx += 3;
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
}
