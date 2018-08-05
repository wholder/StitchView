import java.awt.*;
import java.util.LinkedList;
import java.util.List;

class StitchPattern {
  static final int  TYPE_NORMAL = 0;
  static final int  TYPE_JUMP = 1;
  static final int  TYPE_TRIM = 2;
  static final int  TYPE_STOP = 4;
  static final int  TYPE_END = 8;
  List<Stitch> stitches = new LinkedList<>();
  int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
  int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
  int                lastX, lastY, colorIdx;
  int                maxDist, maxStitch, jumps;


  static class Stitch {
    int xx, yy;
    Color color;


    Stitch (int dx, int dy, Color color) {
      this.xx = dx;
      this.yy = dy;
      this.color = color;
    }
  }

  List<Stitch> getStitches () {
    return stitches;
  }

  Rectangle getBounds () {
    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  int getStitchCount () {
    return stitches.size();
  }

  void printColors () {
    // Override in subclass
  }

  void printInfo () {
    // Override in subclass
  }
}
