<p align="center"><img src="https://github.com/wholder/StitchView/blob/master/images/StitchView.gif"></p>

## StitchView
This code allows you to view several types of embroidery files and has an extensible framework to suport easily adding other formats by extending the StitchPattern class.  The current formats supported are:

 - **`.pes`** - Used by Brother and Babylock embroidery home sewing machines
 - **`.dst`** - Used by Tajima commercial embroidery sewing machines
 - **`.exp`** - Used by Melco commercial embroidery sewing machines

Note: the `.pes` format support a built-in color palette of 64 colors.  The `.dst` and `.exp` formats are monocolor and render in black.
        
### Requirements
Java 8 JDK, or later must be installed in order to compile the code.  There is also a [**Runnable JAR file**](https://github.com/wholder/StitchView/tree/master/out/artifacts/StitchView_jar) included in the checked in code that you can download.   On a Mac, just double click the `StitchView.jar` file and it should start.  Then, use `File->Open Embroidery File` to open a file.  You can then use the slider control and the left/right arrows under the view area rewind the stitches or move forward again.  While the slider is selected, the left/right arrow keys will also control it.
  
_Note: you may have to select the `StitchView.jar` file, then do a right click and select "Open" the first time you run the file to satisfy Mac OS' security checks._  You should also be able to run the JAR file on Windows or Linux systems, but you'll need to have a Java 8 JRE, or later installed and follow the appropriate process for each needed to run an executable JAR file.
### License
I'm publishing this source code under the MIT License (See: https://opensource.org/licenses/MIT)