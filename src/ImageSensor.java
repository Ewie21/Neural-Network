package src;

import java.awt.*;
import java.awt.image.*;

import java.io.*;
import javax.imageio.*;

import java.util.*;

import SimpleFile.SimpleFile;

class ImageSensor extends Input
{
  // static fields to store category names and numbers
  private static Map<String,Integer> categoryNumbers;  // map category names to numbers
  private static String[] categoryNames;
  private static int numCategories = 0;
 
  private static ArrayList<Double> inputs;
  private static int Category = -1;

  public ArrayList<Double> getInputs(){
    return this.inputs;
  }

  public static String[] getCategoryNames(){
    return categoryNames;
  }
 
  ImageSensor(ArrayList<Double> inputs, String categoryName)
  {
    super(inputs, categoryName);
  }
 
  static int getNumCategories()
  {
    return numCategories;
  }
 
  static String getCategoryName(int categoryNumber)
  {
    if (categoryNumber == -1)
      return "unknown";
    else
      return categoryNames[categoryNumber];
  }
 
  public double getInputValue(int inputNumber)
  {
    if (inputNumber == 0)
      return 1.0;
    else  
      return inputs.get(inputNumber-1);
  }
 
  public double getOutputValue(int outputNumber)
  {
    if (outputNumber == Category)
      return 1.0;
    else
      return 0.0;
  }
 
  
 
  static java.util.List<ImageSensor> loadExamples(String dataDirPath)
  {
    LinkedList<ImageSensor> examples = new LinkedList<ImageSensor>();
    categoryNumbers = new HashMap<String,Integer>();
    categoryNames = new String[100];
    File dataDir = new File(dataDirPath);
    File cookedDir = new File(dataDir, "cooked");    
    SimpleFile indexFile = new SimpleFile(dataDir, "index.txt");
    indexFile.startReading();
    while (indexFile.hasMoreLines()) {
      String line = indexFile.readNextLine();
      String[] fields = line.split("#");
      String name = fields[0];
      String category = fields[1];
      //System.out.println("reading " + name);
      if (! categoryNumbers.containsKey(category)) {
        //System.out.println("Category " + category + " is number " + numCategories);
        categoryNumbers.put(category, numCategories);
        categoryNames[numCategories] = category;
        numCategories++;
     }
      String imageName = name + ".png";
     
      try {
        BufferedImage image = ImageIO.read(new File(cookedDir, imageName));
        //elo
        if (imageName != null)
          Category = categoryNumbers.get(imageName);
        int width = image.getWidth();
        int height = image.getHeight();
        inputs = new ArrayList<>(width*height);
        for (int x=0; x<width; x++) {
          for (int y=0; y<height; y++) {
            int rgb = image.getRGB(x, y);
            Color color = new Color(rgb);
            int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
            inputs.set(y*width+x, gray/255.0);
          }
        }
        //
        
        ImageSensor example = new ImageSensor(inputs, category);
        examples.add(example);
      }
      catch (IOException ioe) {
        System.err.println("IOException: " + ioe.getMessage());
      }
    }
    indexFile.stopReading();
    return examples;
  }
 
 
}