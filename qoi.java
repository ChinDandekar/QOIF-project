/* qoi.java
* Author: Chinmay Dandekar
*
* This program implements the Quite Ok Image Format (qoif) that compresses images 
* using the qoif format, found on https://qoiformat.org/qoi-specification.pdf 
* It has an encoder function that takes jpgs and converts them to qoif, and a
* decoder that takes a .qoif file and creates the image from it.
*
* This is the only class I have written, the rest are from an APCS package
* that I piggybacked off of to be able to read, create and manipulate pixels
* in a picture.
*/
import java.awt.Color;
import java.io.*;
import java.util.*;

public class qoi 
{


    public qoi()            //constructor
    {
    }

    private void fillHeader(ArrayList<Byte> byteArray, SimplePicture compressPicture)   //fills the header
    {                                                                                   //of the .qoif file
        byteArray.add((byte)'q');
        byteArray.add((byte)'o');
        byteArray.add((byte)'i');
        byteArray.add((byte)'f');
        byte[] widthBytes = bitwiseIntToByte(compressPicture.getWidth());
        for(byte widthByte: widthBytes)
        {
            byteArray.add(widthByte);
        }
        byte[] heighBytes = bitwiseIntToByte(compressPicture.getHeight());
        for(byte heighByte: heighBytes)
        {
            byteArray.add(heighByte);
        }
        byteArray.add((byte)3);
        byteArray.add((byte)1);
    }

    private byte[] bitwiseIntToByte(int num)    //converts an integer into an array of 4 bytes
    {
        byte[] dimensionArray = new byte[4];
        int bitwiseShifter;
        for(int i = 0; i<4; i++)
        {
            bitwiseShifter = 23-8*i;
            if(bitwiseShifter < 0)
                bitwiseShifter = 0;
            dimensionArray[i] = (byte)(num >> bitwiseShifter);
            num -= (num >> bitwiseShifter) << bitwiseShifter;
        }
        return dimensionArray;
    }

    private int hashFunction(Pixel p)       //outputs the hashfunction as specified in the
    {                                       //qoif specification
        return (p.getRed() * 3 + p.getGreen() * 5 + p.getBlue() * 7) % 64;
    }

    private boolean isSmallDiff(Pixel lastPixel, Pixel currentPixel)   //checks if the rgb value difference
    {                                                                  //between two pixels is small enough to qualify
                                                                        //as specified in the qoif specification
        boolean isDiff = ((currentPixel.getGreen() - lastPixel.getGreen()) <= 1) && ((currentPixel.getGreen() - lastPixel.getGreen()) >= -2);
        isDiff = isDiff && ((currentPixel.getRed() - lastPixel.getRed()) <= 1) && ((currentPixel.getRed() - lastPixel.getRed()) >= -2);
        isDiff = isDiff && ((currentPixel.getBlue() - lastPixel.getBlue()) <= 1) && ((currentPixel.getBlue() - lastPixel.getBlue()) >= -2);
        return isDiff;
    }

    private boolean isMediumDiff(Pixel lastPixel, Pixel currentPixel)//checks if the rgb value difference
    {                                                                  //between two pixels is medium enough to qualify
                                                                        //as specified in the qoif specification
        int dg = (currentPixel.getGreen() - lastPixel.getGreen());
        boolean isDiff = (dg <= 31) && (dg >= -32);
        isDiff = isDiff && ((currentPixel.getRed() - lastPixel.getRed()) - dg <= 7) && ((currentPixel.getRed() - lastPixel.getRed()) - dg >= 0);
        isDiff = isDiff && ((currentPixel.getBlue() - lastPixel.getBlue()) - dg <= 7) && ((currentPixel.getBlue() - lastPixel.getBlue()) >= 0);
        return isDiff;
    }

    private void compressImage(ArrayList<Byte> byteArray, SimplePicture compressPicture) //the method that
    {                                                                                   //actually compresses pixels into bytes
        Pixel[] pixels = compressPicture.getPixels();
        Pixel lastPixel = pixels[0];
        int lastTag;
        Pixel[] visitedPixels = new Pixel[64];
        byteArray.add((byte)254);                       //adding the first pixel in the picture to the byteArray
        lastTag = (byte)254;
        byteArray.add((byte)lastPixel.getRed());
        byteArray.add((byte)lastPixel.getGreen());
        byteArray.add((byte)lastPixel.getBlue());
        visitedPixels[hashFunction(lastPixel)] = lastPixel;


        for(int i = 1; i<pixels.length; i++)            //looping through all the pixels in the picture
        {
            if(pixels[i].equals(visitedPixels[hashFunction(pixels[i])]))
            {
                if(lastTag == 0 && lastPixel.equals(pixels[i]))     //compressing according to QOI_OP_RUN
                {
                    lastTag = (byte)192;
                    int counter = 0;
                    while(lastPixel.equals(pixels[i]) && i < pixels.length-1 && counter < 61)
                    {
                        counter++;
                        lastPixel = pixels[i];
                        i++;
                    }
                    i--;
                    byteArray.add((byte)(192+counter));
                }
                else                            //compressing according to QOI_OP_INDEX
                {   
                    byteArray.add((byte)hashFunction(pixels[i]));
                    lastPixel = pixels[i];
                    lastTag = 0;

                    
                }
            }
            else if(isSmallDiff(lastPixel, pixels[i]))      //compressing according to QOI_OP_DIFF
            {
                int value = 0;
                int db = pixels[i].getBlue() - lastPixel.getBlue()+2;
                value += db;
                int dg = (pixels[i].getGreen() - lastPixel.getGreen()+2) << 2;
                value += dg;
                int dr = (pixels[i].getRed() - lastPixel.getRed()+2) << 4;
                value += dr;
                
                lastTag = 64;
                value += lastTag;
                byteArray.add((byte)value);
                lastPixel = pixels[i];
                visitedPixels[hashFunction(pixels[i])] = pixels[i];

                
            }

            else if(isMediumDiff(lastPixel, pixels[i]))       //compressing according to QOI_OP_LUMA
            {
                int dg = (pixels[i].getGreen() - lastPixel.getGreen());
                int firstByte = dg + 32;
                lastTag = 128;
                firstByte += lastTag;
                byteArray.add((byte)firstByte);

                int db = pixels[i].getBlue() - lastPixel.getBlue() - dg + 8;
                int dr = (pixels[i].getRed() - lastPixel.getRed() - dg + 8 ) << 4;

                int secondByte = dr+db;
                byteArray.add((byte)secondByte);
                lastPixel = pixels[i];
                visitedPixels[hashFunction(pixels[i])] = pixels[i];

                
            }

            else                                //compressing according to QOI_OP_RGB
            {
                lastTag = 254;
                byteArray.add((byte)lastTag);
                byteArray.add((byte)pixels[i].getRed());
                byteArray.add((byte)pixels[i].getGreen());
                byteArray.add((byte)pixels[i].getBlue());

                visitedPixels[hashFunction(pixels[i])] = pixels[i];
                lastPixel = pixels[i];

                
            }


        }
        for(int i = 0; i<7; i++)        //adds the end marker
        {
            byteArray.add((byte)0);
        }
        byteArray.add((byte)1);



    }

    private void exectueCompressImage(Scanner obj) throws IOException       //calls all the code
    {                                                                   //that compresses the image
        System.out.println("Please input the filepath of the image you want to compress.");
        String filename = obj.nextLine();

        System.out.println("Please input the filename of the image you want to compress.");
        String name = obj.nextLine();

        filename += name;
        SimplePicture compressPicture = new SimplePicture(filename);
        ArrayList<Byte> byteArray = new ArrayList<Byte>();

        fillHeader(byteArray,compressPicture);
        compressImage(byteArray,compressPicture);

        name = name.substring(0, name.length()-4);

        System.out.println("Please input the filepath where you want to store the .qoif file.");
        String storeFilePath = obj.nextLine();

        File file = new File(storeFilePath + name + ".qoif");
            file.createNewFile();
            OutputStream writer = new FileOutputStream(file);
            byte[] array = new byte[byteArray.size()];
            for(int i = 0; i<byteArray.size(); i++)
            {
                array[i] = byteArray.get(i);
            }
            writer.write(array);
            writer.close();
    }

    private void decompressImage(Scanner obj) throws IOException        //calls all the code
    {                                                               //that decompresses the .qoif file

        System.out.println("Please input the filepath of the image you want to decompress.");
        String filename = obj.nextLine();

        System.out.println("Please input the filename of the image you want to decompress.");
        String name = obj.nextLine();
        
        while(name.length()<5 || !name.substring(name.length()-5).equals(".qoif"))
        {
            System.out.print("Incompatible file. The file " + name + " does not end with the .qoif ");
            System.out.println("extension and was not accepted. Please input a file that ends with the .qoif extension.");
            name = obj.nextLine();
        }
        filename += name;
        File file = new File(filename);
        InputStream reader = new FileInputStream(file);
        byte[] compressedImage = reader.readAllBytes();
        reader.close();
        SimplePicture picture = createImage(compressedImage, name);
        convertByteArrayToPicture(compressedImage, picture);
        //picture.write(picture.getFileName() + ".jpg");
        picture.show();
    }

    private void convertByteArrayToPicture(byte[] compressedImage, SimplePicture picture)   //decompresses
    {                                                                   //the .qoif file by converting into a picture
         
        int row = 0;
        int col = 0;
        Pixel lastPixel = new Pixel(picture,0,0); 
        Pixel[] visitedPixels = new Pixel[64];
        for(int i = 14; i<compressedImage.length-8; i++)        //loops through all the bytes in the .qoif file
        {

            int currentByte = (int)(compressedImage[i] & 0xFF);
            if(currentByte == 254)          //decompresses according to QOI_OP_RGB
            {                
                i++;
                picture.getPixel(col, row).setRed((int)(compressedImage[i] & 0xFF));
                i++;
                picture.getPixel(col, row).setGreen((int)(compressedImage[i] & 0xFF));
                i++;
                picture.getPixel(col, row).setBlue((int)(compressedImage[i] & 0xFF));
                
                visitedPixels[hashFunction(picture.getPixel(col, row))] = picture.getPixel(col, row);
                lastPixel = picture.getPixel(col, row);
                col++;
                if(col > picture.getWidth()-1)
                {
                    col = 0;
                    row++;
                }
            }
            else if(currentByte >> 6 == 0)          //decompresses according to QOI_OP_INDEX
            {
               
                if(visitedPixels[currentByte] == null)
                {
                    picture.getPixel(col,row).setColor(Color.white);
                }
                else
                    picture.getPixel(col, row).setColor(visitedPixels[currentByte].getColor());
                lastPixel = picture.getPixel(col, row);
                col++;
                if(col > picture.getWidth()-1)
                {
                    col = 0;
                    row++;
                }
                 
            }

            else if(currentByte >> 6 == 1)      //decompresses according to QOI_OP_DIFF
            {
                currentByte = currentByte - 64;
                int dr = (currentByte) >> 4;
                int dRbitShifted = (dr<<4);
                dr -= 2; 
                picture.getPixel(col, row).setRed(lastPixel.getRed() + dr);
                int dg = (currentByte - dRbitShifted);
                dg = dg>>2;
                int dGbitShifter = dg << 2;
                dg -= 2;
                picture.getPixel(col, row).setGreen(lastPixel.getGreen() + dg);
                int db = (currentByte - (dRbitShifted + dGbitShifter)) - 2;
                picture.getPixel(col, row).setBlue(lastPixel.getBlue() + db);

                
                visitedPixels[hashFunction(picture.getPixel(col, row))] = picture.getPixel(col, row);
                lastPixel = picture.getPixel(col, row);

                col++;
                if(col > picture.getWidth()-1)
                {
                    col = 0;
                    row++;
                }
            }

            else if(currentByte >> 6 == 2 )         //decompresses according to QOI_OP_LUMA
            {
                int dg = currentByte - 128 - 32;
                picture.getPixel(col, row).setGreen(lastPixel.getGreen() + dg);
                i++;
                currentByte = ((int)(compressedImage[i] & 0xFF ) );
                int dr = currentByte >> 4;
                int saver = dr << 4;
    
                int db = currentByte - saver + dg - 8;
                dr = dr + dg - 8;
                picture.getPixel(col, row).setRed(lastPixel.getRed() + dr);
                picture.getPixel(col, row).setBlue(lastPixel.getBlue() + db);

                visitedPixels[hashFunction(picture.getPixel(col, row))] = picture.getPixel(col, row);
                lastPixel = picture.getPixel(col, row);

                col++;
                if(col > picture.getWidth()-1)
                {
                    col = 0;
                    row++;
                }
            }

            else if(currentByte >> 6 == 3)      //decompresses according to QOI_OP_RUN
            {
                
                int run = currentByte - 192;
                
                for(int j = 0; j < run; j++)
                {
                    
                    picture.getPixel(col, row).setColor(lastPixel.getColor());
                    lastPixel = picture.getPixel(col, row);

                    col++;
                    if(col > picture.getWidth()-1)
                    {
                        col = 0;
                        row++;
                    }
                }
            }

        }


    }

    private Picture createImage(byte[] compressedImage, String name)    //creates the image
    {                           //that convertByteArrayToPicture() fills
        int width = 0;
        int bitwiseShifter;
        for(int i = 4; i<8; i++)
        {
            bitwiseShifter = 23-8*(i-4);
            if(bitwiseShifter < 0)
                bitwiseShifter = 0;
            width += (int)compressedImage[i] << bitwiseShifter;
        }
        int height = 0;
        for(int i = 8; i<12; i++)
        {
            bitwiseShifter = 23-8*(i-8);
            if(bitwiseShifter < 0)
                bitwiseShifter = 0;
            height += (int)compressedImage[i] << bitwiseShifter;
        }
        Picture picture = new Picture(height, width);
        String path = "/Users/chinmaydandekar/Desktop/-/College HW/Sophomore Year/Q1/CS 1L/Qoi/Decompressed Images/";
        name = path + name.substring(0,name.length()-5);
        picture.setFileName(name);
        return picture;
    }
    public static void main(String[] args) throws IOException   //main method
    {
        Scanner obj = new Scanner(System.in);
        System.out.println("Type c to compress an image, d to decompress it, and q to quit.");
        String response = "";

        response = obj.nextLine();
        response = response.toLowerCase();
        qoi quiteOk = new qoi();
        while(!response.equals("q"))
        {
            if(response.equals("c"))
            {
                quiteOk.exectueCompressImage(obj);
            }

            else if(response.equals("d"))
            {
                quiteOk.decompressImage(obj);
            }

            System.out.println("Type c to compress an image, d to decompress it, and q to quit.");
            response = obj.nextLine();
            response = response.toLowerCase();
        }   


    }
}