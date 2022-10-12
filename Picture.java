import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.util.List; // resolves problem with java.awt.List and java.util.List

/**
 * A class that represents a picture.  This class inherits from 
 * SimplePicture and allows the student to add functionality to
 * the Picture class.  
 * 
 * @author Barbara Ericson ericson@cc.gatech.edu
 */
public class Picture extends SimplePicture 
{
  ///////////////////// constructors //////////////////////////////////
  
  /**
   * Constructor that takes no arguments 
   */
  public Picture ()
  {
    /* not needed but use it to show students the implicit call to super()
     * child constructors always call a parent constructor 
     */
    super();  
  }
  
  /**
   * Constructor that takes a file name and creates the picture 
   * @param fileName the name of the file to create the picture from
   */
  public Picture(String fileName)
  {
    // let the parent class handle this fileName
    super(fileName);
  }
  
  /**
   * Constructor that takes the width and height
   * @param height the height of the desired picture
   * @param width the width of the desired picture
   */
  public Picture(int height, int width)
  {
    // let the parent class handle this width and height
    super(width,height);
  }
  
  /**
   * Constructor that takes a picture and creates a 
   * copy of that picture
   * @param copyPicture the picture to copy
   */
  public Picture(Picture copyPicture)
  {
    // let the parent class do the copy
    super(copyPicture);
  }
  
  /**
   * Constructor that takes a buffered image
   * @param image the buffered image to use
   */
  public Picture(BufferedImage image)
  {
    super(image);
  }
  
  ////////////////////// methods ///////////////////////////////////////
  
  /**
   * Takes the images "IndoorHouseLibraryBackground.jpg", "mouse1GreenScreen.jpg"
   * and "kitten1GreenScreen.jpg" and removes the green in the images with
   * the cat and the mouse and places them in the picture with the couch 
   * after scaling the mouse down by 4
   * 
   * @return			An object of Picture that has the couch with 
   * 					the cat and the mouse in it
   * 
   */
  public Picture greenScreen()
  {
	  Picture couch = new Picture("GreenScreenCatMouse/IndoorHouseLibraryBackground.jpg");
	  Picture mouse = new Picture("GreenScreenCatMouse/mouse1GreenScreen.jpg");
	  Picture cat = new Picture("GreenScreenCatMouse/kitten1GreenScreen.jpg");
	  mouse.explore();
	  
	  Pixel[][] couchPix = couch.getPixels2D();
	  Pixel[][] mousePix = mouse.getPixels2D();
	  Pixel[][] catPix = cat.getPixels2D();
	  
	  Color greenCat = catPix[0][0].getColor();
	  Color greenMouse = mousePix[0][0].getColor();
	  
	  Picture smallMousePicture = new Picture(mousePix.length/2, mousePix[0].length/2);
	  Pixel[][] smallMouse = smallMousePicture.getPixels2D();
	  for(int i = 0; i<mousePix.length-1; i+=2)
	  {
		  for(int j = 0; j<mousePix[i].length-1; j+=2)
		  {
			  int[] avgColors = avgAmt(mousePix,i,j,4);
			  smallMouse[i/2][j/2].setBlue(avgColors[0]);
			  smallMouse[i/2][j/2].setRed(avgColors[1]);
			  smallMouse[i/2][j/2].setGreen(avgColors[2]);
		  }
	  }
	  
	  Picture smallCatPicture = new Picture(catPix.length/1, catPix[0].length/1);
	  Pixel[][] smallCat = smallCatPicture.getPixels2D();
	  for(int i = 0; i<catPix.length-1; i++)
	  {
		  for(int j = 0; j<catPix[i].length-1; j++)
		  {
			  int[] avgColors = avgAmt(catPix,i,j,1);
			  smallCat[i/1][j/1].setBlue(avgColors[0]);
			  smallCat[i/1][j/1].setRed(avgColors[1]);
			  smallCat[i/1][j/1].setGreen(avgColors[2]);
		  }
	  }
	  
	  
	  for(int i = 342; i < 342 + smallMouse.length; i++)
	  {
		  for(int j = 319; j <  319 + smallMouse[0].length; j++)
		  {
			  if(smallMouse[i-342][j-319].colorDistance(greenMouse) >50)
				couchPix[i][j].setColor(smallMouse[i-342][j-319].getColor());
		  }
	  }
	  
	  for(int i = 342; i < 342 + smallMouse.length; i++)
	  {
		  for(int j = 319; j <  319 + smallMouse[0].length; j++)
		  {
			  if(smallMouse[i-342][j-319].colorDistance(greenMouse) >50)
				couchPix[i][j].setColor(smallMouse[i-342][j-319].getColor());
		  }
	  }
	  
	  for(int i = 351; i < Math.min(couchPix.length, 351 + smallCat.length); i++)
	  {
		  for(int j = 466; j < Math.min(couchPix[i].length, 466 + smallCat[0].length); j++)
		  {
			  if(smallCat[i-351][j-466].colorDistance(greenCat) > 50
					&& smallCat[i-351][j-466].colorDistance(Color.WHITE) != 0)
				couchPix[i][j].setColor(smallCat[i-351][j-466].getColor());
		  }
	  }
	  
	  
	  
      return couch;
  }
  
  
  /**
   * Creates an outline by calling on the method colorDistance() and 
   * compares each pixel with the pixel below it. If the difference is 
   * more than the given threshold, then changes the selected pixel to black, 
   * otherwise changes it to white.
   * 
   * @param threshold				The minimum difference between pixels
   * 								to define an outline
   * 
   * @return						An object of Picture that shows an 
   * 								outline of the given picture
   */
  public Picture edgeDetectionBelow(int threshold)
  {
	  Pixel[][] pixels = this.getPixels2D();
	  Picture result = new Picture(pixels.length, pixels[0].length); 
	  Pixel[][] resultPixels = result.getPixels2D();
	  
	  Pixel upPixel = null;
	  Pixel downPixel = null;
      Color rightColor = null;
      for (int row = 0; row < pixels.length-1; row++)
      {
        for (int col = 0; col < pixels[0].length; col++)
        {
          upPixel = pixels[row][col];
          downPixel = pixels[row+1][col];
          rightColor = downPixel.getColor();
          if (upPixel.colorDistance(rightColor) > 
               threshold)
          resultPixels[row][col].setColor(Color.BLACK);
          else
             resultPixels[row][col].setColor(Color.WHITE);
        }
      }
      pixels = resultPixels;
      return result;
  }
  
  /**
   * For a given picture, scales the picture down by 4 (i.e. makes the
   * picture 1/4 of its size) and flips it by the x-axis, y-axis, and the 
   * line y=x to create a picture of the same size as the one given
   * 
   * @return				An object of Picture that shows the original
   * 						picture changed according to the ways specified
   * 						above
   */
  public Picture tileMirror()
  {
	  Pixel[][] pixels = this.getPixels2D();
	  Picture result = new Picture(pixels.length, pixels[0].length); 
	  Pixel[][] resultPixels = result.getPixels2D();
	  
	  for(int i = 0; i<pixels.length; i+=2)
	  {
		  for(int j = 0; j<pixels[i].length; j+=2)
		  {
				int[] avgColors = avgAmt(pixels, i, j, 4);
				resultPixels[i/2][j/2].setBlue(avgColors[0]);
				resultPixels[i/2][j/2].setRed(avgColors[1]);
				resultPixels[i/2][j/2].setGreen(avgColors[2]);
				
				resultPixels[pixels.length - 1 - i/2][j/2].setBlue(avgColors[0]);
				resultPixels[pixels.length - 1 - i/2][j/2].setRed(avgColors[1]);
				resultPixels[pixels.length - 1 - i/2][j/2].setGreen(avgColors[2]);
				
				resultPixels[i/2][pixels[i].length - 1 - j/2].setBlue(avgColors[0]);
				resultPixels[i/2][pixels[i].length - 1 - j/2].setRed(avgColors[1]);
				resultPixels[i/2][pixels[i].length - 1 - j/2].setGreen(avgColors[2]);
				
				resultPixels[pixels.length - 1 - i/2][pixels[i].length - 1 - j/2].setBlue(avgColors[0]);
				resultPixels[pixels.length - 1 - i/2][pixels[i].length - 1 - j/2].setRed(avgColors[1]);
				resultPixels[pixels.length - 1 - i/2][pixels[i].length - 1 - j/2].setGreen(avgColors[2]);
		  }
	  }
			  
	  
	  pixels = resultPixels;
	  return result;
	  
  }
  
  /**
   * For a given 2D array of pixels, and the position of a pixel, averages
   * the RGB values of the square of amt around the given pixel. 
   * 
   * @param pixels		The 2D array of pixels 
   * @param row			The row of the given pixel
   * @param col			The column of the given pixel
   * @param amt			The dimensions of the amt by amt square around the 
   * 					given pixel
   * 
   *
   * @return 			An array of 3 integers containing the blue, green
   * 					and red values respectivelu of the average around 
   * 					the given pixel
   * 
   */
  public int[] avgAmt(Pixel[][] pixels, int row, int col, int amt)
  {
	  int[] avgColors = new int[3];
	  for(int i = row; i<row+Math.sqrt(amt); i++)
	  {
		  for(int j = col; j<col+Math.sqrt(amt); j++)
		  {
			  if(i >= pixels.length || j >= pixels[i].length)
				break;
			  avgColors[0] += pixels[i][j].getBlue();
			  avgColors[1] += pixels[i][j].getRed();
			  avgColors[2] += pixels[i][j].getGreen();
		  }
	  }
	  
	  for(int i = 0; i<3; i++)
		avgColors[i]/=amt;
		
	 return avgColors;
			  
  }
  
  
  /**
   * Zooms into the upper left of a given square by enlarging each pixel
   * to become a 2 by 2 pixel and thus enlarging the picture by 4
   * 
   * @return				An object of Picture that is zoomed in to the
   * 						upper left
   */
  public Picture zoomUpperLeft()
  {
	  Pixel[][] pixels = this.getPixels2D();
	  Picture result = new Picture(pixels.length, pixels[0].length); 
	  Pixel[][] resultPixels = result.getPixels2D();
	  
	  for(int i = 0; i<pixels.length; i++)
	  {
		  for(int j = 0; j<pixels[0].length; j++)
		  {
			  resultPixels[i][j].setBlue(pixels[i/4][j/4].getBlue());
			  resultPixels[i][j].setRed(pixels[i/4][j/4].getRed());
			  resultPixels[i][j].setGreen(pixels[i/4][j/4].getGreen());
		  }
	  }
	  
	  pixels = resultPixels;
	  return result;
	  
  }

  
  public Picture turn90()
  {
	  Pixel[][] pixels = this.getPixels2D();
	  Picture result = new Picture(pixels[0].length, pixels.length); 
	  Pixel[][] resultPixels = result.getPixels2D();
	  for(int i = 0; i<pixels.length; i++)
	  {
		  for(int j = 0; j<pixels[i].length; j++)
		  {
			  resultPixels[j][pixels.length-1-i].setBlue(pixels[i][j].getBlue());
			  resultPixels[j][pixels.length-1-i].setRed(pixels[i][j].getRed());
			  resultPixels[j][pixels.length-1-i].setGreen(pixels[i][j].getGreen());
		  }
	  }
	  pixels = resultPixels;
	  return result;
  }
  
  
  
  public Picture stairStep(int shiftCount, int steps) 
  {
	  Pixel[][] pixels = this.getPixels2D();
	  Picture result = new Picture(pixels.length, pixels[0].length); 
	  Pixel[][] resultPixels = result.getPixels2D();
	  int count = 0;  
	  int change = 0;
	  
	  for(int k = 0; k<pixels.length; k++)
	  {
		  for(int l = 0; l<pixels[k].length; l++)
		  {
				change = l+(count*shiftCount);
				resultPixels[k][wrapAroundIfNeeded(change, pixels[k].length)].setBlue(pixels[k][l].getBlue());
				resultPixels[k][wrapAroundIfNeeded(change, pixels[k].length)].setRed(pixels[k][l].getRed());
				resultPixels[k][wrapAroundIfNeeded(change, pixels[k].length)].setGreen(pixels[k][l].getGreen());
				
		  }
		  if(k!=0 && k%(pixels.length/steps) == 0)
			  count++;
	  }
	  
	  pixels = resultPixels;
	  return result;
		  
  }
  
  public int wrapAroundIfNeeded(int change, int limit)
  {
	  while(change >= limit)
		  change -= limit;
	  return change;
  }
  
/**
 * Shifts the given picture right by a given percent
 * 
 * @param percent				The percent to shift the picture right by
 * 
 * @return						An object of Picture shifted by a given percent
 */ 
  public Picture shiftRight(int percent)
  {
	  Pixel[][] pixels = this.getPixels2D();
	  Picture result = new Picture(pixels.length, pixels[0].length); 
	  Pixel[][] resultPixels = result.getPixels2D();  
	  int change = (int)(pixels[0].length * ((double)percent/100));
	  for(int i = 0; i<pixels.length; i++)
	  {
		  for(int j = change; j<pixels[i].length; j++)
		  {
			  resultPixels[i][j].setBlue(pixels[i][j-change].getBlue());
			  resultPixels[i][j].setRed(pixels[i][j-change].getRed());
			  resultPixels[i][j].setGreen(pixels[i][j-change].getGreen());
			  
		  }
	  }
	  
	  for(int i = 0; i<pixels.length; i++)
	  {
		  for(int j = 0; j<change; j++)
		  {
			  resultPixels[i][j].setBlue(pixels[i][pixels[i].length - change + j].getBlue());
			  resultPixels[i][j].setRed(pixels[i][pixels[i].length - change + j].getRed());
			  resultPixels[i][j].setGreen(pixels[i][pixels[i].length - change + j].getGreen());
		  }
	  }
	  pixels = resultPixels;
	  return result;
	
			  
  }
	 

 
  
    /** To pixelate by dividing area into size x size
. 
   *  @param size    Side length of square area to pixelate. 
   */ 
  public void pixelate(int size) 
  {   
	  Pixel[][] pixels = this.getPixels2D();
	  int[][] avgGreen = new int[pixels.length/size+1][pixels[0].length/size+1];
	  int[][] avgBlue = new int[pixels.length/size+1][pixels[0].length/size+1];
	  int[][] avgRed = new int[pixels.length/size+1][pixels[0].length/size+1];
	  
	  for(int i = 0; i<pixels.length; i++)
	  {
		  for(int j = 0; j<pixels[0].length; j++)
		  {
			   avgRed[i/size][j/size] += pixels[i][j].getRed();
			   avgBlue[i/size][j/size] += pixels[i][j].getBlue();
			   avgGreen[i/size][j/size] += pixels[i][j].getGreen();
		  }
	  }
	  
	  for(int i = 0; i<avgGreen.length; i++)
	  {
		  for(int j = 0; j<avgGreen[i].length; j++)
		  {	
			  avgGreen[i][j]/=(size*size);
			  avgRed[i][j]/=(size*size);
			  avgBlue[i][j]/=(size*size);
		  }
	  }
	  
	  for(int i = 0; i<pixels.length; i++)
	  {
		  for(int j = 0; j<pixels[0].length; j++)
		  {
			    pixels[i][j].setRed(avgRed[i/size][j/size]);
				pixels[i][j].setBlue(avgBlue[i/size][j/size]);
				pixels[i][j].setGreen(avgGreen[i/size][j/size]);
				
		  }
	  }
	  
	  
  }
	  
/** Method that blurs the picture 
*  @param size    Blur size, greater is more blur 
*  @return   	  Blurred picture 
*/ 
  public Picture blur(int size) 
  { 
    Pixel[][] pixels = this.getPixels2D(); 
    Picture result = new Picture(pixels.length, pixels[0].length); 
    Pixel[][] resultPixels = result.getPixels2D();
     
    
    for(int i = 0; i<pixels.length; i++)
    {
		for(int j = 0; j<pixels[0].length; j++)
		{
			int[] avgColors = averageSquare(pixels, i, j, size);
			resultPixels[i][j].setBlue(avgColors[1]);
			resultPixels[i][j].setRed(avgColors[0]);
			resultPixels[i][j].setGreen(avgColors[2]);
		}
	}
	pixels = resultPixels;
	return result;
  }
  
 /** Method that enhances a picture by getting average Color around
*   a pixel then applies the following formula: 
* 
*    pixelColor <- 2 * currentValue - averageValue 
* 
*  size is the area to sample for blur. 
* 
*  @param size    Larger means more area to average around pixel
*                   and longer compute time. 
*  @return               enhanced picture 
*/ 
  public Picture enhance(int size)
  { 
    Pixel[][] pixels = this.getPixels2D(); 
    Picture result = new Picture(pixels.length, pixels[0].length); 
    Pixel[][] resultPixels = result.getPixels2D(); 
    for(int i = 0; i<pixels.length; i++)
    {
		for(int j = 0; j<pixels[0].length; j++)
		{
			int[] avgColors = averageSquare(pixels, i, j, size);
			resultPixels[i][j].setBlue(2*pixels[i][j].getBlue() - avgColors[1]);
			resultPixels[i][j].setRed(2*pixels[i][j].getRed() - avgColors[0]);
			resultPixels[i][j].setGreen(2*pixels[i][j].getGreen() - avgColors[2]);
		}
	}
	pixels = resultPixels;
	return result;
    
  }
  
 /** 
  * Method to average the RGB values of a square of pixels around a given
  * pixel and a given size
  * 
  * @param pixels			The 2D Array of pixels that makes up the picture
  * @param pixRow			The horizontal location of a given pixel
  * @param pixCol			The vertical location of a given pixel
  * @param size				The size of the square around it
  * 
  * @return 				An array of 3 integers which are the average 
  * 						Red, Blue and Green values respectively
  */
  public int[] averageSquare(Pixel[][] pixels, int pixRow, int pixCol, int size)
  {
	  int[] color = new int[3];
	  int startRow = pixRow-(size/2);
	  if(startRow<0)
		startRow = 0;
	  int endRow = pixRow + (size/2);
	  if(endRow >= pixels.length)
		endRow = pixels.length-1;
	
	  int startCol = pixCol-(size/2);
	  if(startCol < 0)
		startCol = 0;
	  
	  int endCol = pixCol + (size/2);
	  if(endCol >= pixels[0].length)
		endCol = pixels[0].length - 1;
	  
	  int count = 0;
	  for(int i = startRow; i <= endRow; i++)
	  {
		  for(int j = startCol; j<=endCol; j++)
		  {
			  color[0] += pixels[i][j].getRed();
			  color[1] += pixels[i][j].getBlue();
			  color[2] += pixels[i][j].getGreen();
			  count++;
		  }
	  }
	  
	  color[0]/= count;
	  color[1]/= count;
	  color[2]/= count;
	  
	  
	  return color;
  }
			  
	  
	   
	
	
  /**
   * Method to return a string with information about this picture.
   * @return a string with information about the picture such as fileName,
   * height and width.
   */
  public String toString()
  {
    String output = "Picture, filename " + getFileName() + 
      " height " + getHeight() 
      + " width " + getWidth();
    return output;
    
  }
  
  /** Method to set the blue to 0 */
  public void zeroBlue()
  {
    Pixel[][] pixels = this.getPixels2D();
    for (Pixel[] rowArray : pixels)
    {
      for (Pixel pixelObj : rowArray)
      {
        pixelObj.setBlue(0);
      }
    }
  }
  
  /**Method that sets red and green to 0 */
  public void keepOnlyBlue()
  {
	Pixel[][] pixels = this.getPixels2D();
    for (Pixel[] rowArray : pixels)
    {
      for (Pixel pixelObj : rowArray)
      {
        pixelObj.setRed(0);
        pixelObj.setGreen(0);
      }
    }
  }
  
  /**Method that sets red and blue to 0 */
  public void keepOnlyGreen()
  {
	Pixel[][] pixels = this.getPixels2D();
    for (Pixel[] rowArray : pixels)
    {
      for (Pixel pixelObj : rowArray)
      {
        pixelObj.setRed(0);
        pixelObj.setBlue(0);
      }
    }
  }
  
  /**Method that sets blue and green to 0 */
  public void keepOnlyRed()
  {
	Pixel[][] pixels = this.getPixels2D();
    for (Pixel[] rowArray : pixels)
    {
      for (Pixel pixelObj : rowArray)
      {
        pixelObj.setBlue(0);
        pixelObj.setGreen(0);
      }
    }
  }
  
  /**Method that inverts each color by doing 255-given color */
  public void negate()
  {
	  Pixel[][] pixels = this.getPixels2D();
    for (Pixel[] rowArray : pixels)
    {
      for (Pixel pixelObj : rowArray)
      {
		  pixelObj.setBlue(255-pixelObj.getBlue());
		  pixelObj.setGreen(255-pixelObj.getGreen());
		  pixelObj.setRed(255-pixelObj.getRed());
	  }
	}
  }
  
  /**Grayscales the picture by averaging each pixels RGB values and 
   * setting the RGB value to that*/
  public void grayScale()
  {
	  Pixel[][] pixels = this.getPixels2D();
	  int avg; 
	  for(Pixel[] rowArray: pixels)
	  {
		  for(Pixel obj : rowArray)
		  {
			  avg = obj.getBlue() + obj.getGreen() + obj.getRed();
			  avg/=3;
			  obj.setBlue(avg);
			  obj.setGreen(avg);
			  obj.setRed(avg);
		  }
	  }
  }
  
  
  /** Method that mirrors the picture around a 
    * vertical mirror in the center of the picture
    * from left to right */
  public void mirrorVertical()
  {
    Pixel[][] pixels = this.getPixels2D();
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    int width = pixels[0].length;
    for (int row = 0; row < pixels.length; row++)
    {
      for (int col = 0; col < width / 2; col++)
      {
        leftPixel = pixels[row][col];
        rightPixel = pixels[row][width - 1 - col];
        rightPixel.setColor(leftPixel.getColor());
      }
    } 
  }
  
  /** Mirror just part of a picture of a temple */
  public void mirrorTemple()
  {
    int mirrorPoint = 276;
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    int count = 0;
    Pixel[][] pixels = this.getPixels2D();
    
    // loop through the rows
    for (int row = 27; row < 97; row++)
    {
      // loop from 13 to just before the mirror point
      for (int col = 13; col < mirrorPoint; col++)
      {
        
        leftPixel = pixels[row][col];      
        rightPixel = pixels[row]                       
                         [mirrorPoint - col + mirrorPoint];
        rightPixel.setColor(leftPixel.getColor());
      }
    }
  }
  
  /** copy from the passed fromPic to the
    * specified startRow and startCol in the
    * current picture
    * @param fromPic the picture to copy from
    * @param startRow the start row to copy to
    * @param startCol the start col to copy to
    */
  public void copy(Picture fromPic, 
                 int startRow, int startCol)
  {
    Pixel fromPixel = null;
    Pixel toPixel = null;
    Pixel[][] toPixels = this.getPixels2D();
    Pixel[][] fromPixels = fromPic.getPixels2D();
    for (int fromRow = 0, toRow = startRow; 
         fromRow < fromPixels.length &&
         toRow < toPixels.length; 
         fromRow++, toRow++)
    {
      for (int fromCol = 0, toCol = startCol; 
           fromCol < fromPixels[0].length &&
           toCol < toPixels[0].length;  
           fromCol++, toCol++)
      {
        fromPixel = fromPixels[fromRow][fromCol];
        toPixel = toPixels[toRow][toCol];
        toPixel.setColor(fromPixel.getColor());
      }
    }   
  }

  /** Method to create a collage of several pictures */
  public void createCollage()
  {
    Picture flower1 = new Picture("flower1.jpg");
    Picture flower2 = new Picture("flower2.jpg");
    this.copy(flower1,0,0);
    this.copy(flower2,100,0);
    this.copy(flower1,200,0);
    Picture flowerNoBlue = new Picture(flower2);
    flowerNoBlue.zeroBlue();
    this.copy(flowerNoBlue,300,0);
    this.copy(flower1,400,0);
    this.copy(flower2,500,0);
    this.mirrorVertical();
    this.write("collage.jpg");
  }
  
  
  /** Method to show large changes in color 
    * @param edgeDist the distance for finding edges
    */
  public void edgeDetection(int edgeDist)
  {
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    Pixel[][] pixels = this.getPixels2D();
    Color rightColor = null;
    for (int row = 0; row < pixels.length; row++)
    {
      for (int col = 0; 
           col < pixels[0].length-1; col++)
      {
        leftPixel = pixels[row][col];
        rightPixel = pixels[row][col+1];
        rightColor = rightPixel.getColor();
        if (leftPixel.colorDistance(rightColor) > 
            edgeDist)
          leftPixel.setColor(Color.BLACK);
        else
          leftPixel.setColor(Color.WHITE);
      }
    }
  }

    
  
  
  /* Main method for testing - each class in Java can have a main 
   * method 
   */
  public static void main(String[] args) 
  {
	  
  }
  
} // this } is the end of class Picture, put all new methods before this
