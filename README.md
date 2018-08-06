<p align="center"><img src="https://github.com/wholder/StitchView/blob/master/images/StitchView.gif"></p>

## StitchView
StitchView allows you to view the designs in several types of embroidery files.  I wrote StitchView as a way to examine the stitch patterns generated by embroidery design software, as well as commercially sold embroidery files without having to actually sew the patterns an a real embroidery machine.  Once a pattern is loaded, StitchView lets you quickly step backward and forward, stitch by stitch, or move quickly to any point in the pattern using a slider control.  This makes it easy to see the various different stitching techniques used to generate the pattern or spot the stitch "watermarks' often included in commercially-sold patterns.  StitchView is based on an extensible design to suport easily adding other formats by extending the StitchPattern class.  The currently formats supported are:

 - **`.pes`** - Used by Brother and Babylock embroidery home sewing machines
 - **`.dst`** - Used by Tajima commercial embroidery sewing machines
 - **`.exp`** - Used by Melco commercial embroidery sewing machines

Note: StitchView can display files in the `.pes` format using a built-in palette of 64 colors.  The `.dst` and `.exp` formats are monocolor and display the stitch patterns in black.
        
### Requirements
StitchView requires the Java 8 JRE, or later.  There is a [**Runnable JAR file**](https://github.com/wholder/StitchView/tree/master/out/artifacts/StitchView_jar) included in the checked in code that you can download.   On a Mac, just double click the `StitchView.jar` file and it should start (_see note below if this doesn't work_.)  By default, it shows a built-in sample stitch pattern, but you can use `File->Open Embroidery File` to open a different pattern.  The slider control and the left/right arrows under the view area allow you to rewind the stitches back to any point, or move forward again.  While the slider is selected, the left/right arrow keys will also control it.
  
_Note: you may have to select the `StitchView.jar` file, then do a right click and select "Open" the first time you run the file to satisfy Mac OS' security checks._  You should also be able to run the JAR file on Windows or Linux systems, but you'll need to have a Java 8 JRE, or later installed and follow the appropriate process for each needed to run an executable JAR file.
### License
I'm publishing this source code under the MIT License (See: https://opensource.org/licenses/MIT)
## Credits
 - The embroidery file parsing code was adapted from Javascript code written by Josh Varga (https://github.com/JoshVarga/html5-embroidery)
 - [IntelliJ IDEA from JetBrains](https://www.jetbrains.com/idea/) (my favorite development environment for Java coding. Thanks JetBrains!)
