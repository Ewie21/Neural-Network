package src;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.*;

import SimpleFile.SimpleFile;

import javax.swing.*;

import java.io.*;

import javax.imageio.*;

class ImageLearner
{
  final String[] categories = new String[] {
   "Not Elo", "Elo"
  };
  
  static final int cameraCropLeftX = 200;
  static final int cameraCropTopY = 100;
  static final int cameraCropWidth = 200;
  static final int cameraCropHeight = 200;
  
  static final int sensorWidth = 30;
  static final int sensorHeight = 30;
  
  static ArrayList<ImageSensor> inputs = new ArrayList<>();

  JFrame controlFrame;
  JLabel dirLabel;
  JLabel modelLabel = new JLabel("");
  JLabel categoryLabel;
  File dataDir = null;
  File modelDir;
  File rawDir = null;
  WebcamViewer videoCapture = null;
  SimpleFile indexFile = null;
  PrintStream indexStream = null;
  Node[][] net;
  
  ImageLearner()
  {
    JButton selectDirButton = new JButton("Select Directory");
    selectDirButton.addActionListener(new SelectDirListener());
    dirLabel = new JLabel("");
    
    Box controls = Box.createVerticalBox();
    controls.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    controls.add(selectDirButton);
    controls.add(dirLabel);
    controls.add(modelLabel);
    controls.add(Box.createVerticalStrut(50));
    
    for (String category : categories) {
      JButton button = new JButton(category);
      button.addActionListener(new CategoryListener(category));
      controls.add(button);
      controls.add(Box.createVerticalStrut(10));
    }
  
    controls.add(Box.createVerticalStrut(40));
    JButton modelButton = new JButton("Select Model");
    modelButton.addActionListener(new SelectModelListener());
    controls.add(modelButton);
    
    controls.add(Box.createVerticalStrut(40));
    JButton convertButton = new JButton("Convert Images");
    convertButton.addActionListener(new ConvertListener());
    controls.add(convertButton);
    
    controls.add(Box.createVerticalStrut(10));
    JButton learnButton = new JButton("Learn!");
    learnButton.addActionListener(new LearnListener());
    controls.add(learnButton);
    
    controls.add(Box.createVerticalStrut(10));
    JButton categorizeButton = new JButton("Categorize");
    categorizeButton.addActionListener(new CategorizeListener());
    controls.add(categorizeButton);
    
    categoryLabel = new JLabel("      ");
    controls.add(categoryLabel);
    
    controlFrame = new JFrame("Image Saver");
    controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    controlFrame.getContentPane().add(controls);
    controlFrame.pack();
    controlFrame.setLocation(25,50);
    controlFrame.setVisible(true);
    
    Point nwcorner = controlFrame.getLocation();
    Dimension size = controlFrame.getSize();
    
    videoCapture = new WebcamViewer();
    videoCapture.setLocation(nwcorner.x+size.width+25, nwcorner.y);
    
    controlFrame.toFront();
    videoCapture.toFront();
  }

  class SelectModelListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      JFileChooser chooser = new JFileChooser("./models");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int result = chooser.showOpenDialog(controlFrame);
      if(result == JFileChooser.APPROVE_OPTION){
        modelDir = chooser.getSelectedFile();
        System.out.println("Selected Model: " + modelDir.getAbsolutePath());
        modelLabel.setText(modelDir.getName()); 
      }
    }
  }


  class SelectDirListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      JFileChooser chooser = new JFileChooser("./");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int result = chooser.showOpenDialog(controlFrame);
      if (result == JFileChooser.APPROVE_OPTION) {
        dataDir = chooser.getSelectedFile();
        System.out.println("selected directory " + dataDir.getAbsolutePath());
        dirLabel.setText(dataDir.getName());
        indexFile = new SimpleFile(dataDir, "index.txt");
        indexFile.startAppending();
        indexStream = indexFile.getPrintStream();
        rawDir = new File(dataDir, "raw");
        if (! rawDir.exists()) {
          rawDir.mkdir();
        }
      }
    }
  }
  
  class CategoryListener implements ActionListener
  {
    String category;
    
    CategoryListener(String category)
    {
      this.category = category;
    }
    
    public void actionPerformed(ActionEvent event)
    {
      if (dataDir == null) {
        JOptionPane.showMessageDialog(controlFrame, "You must select a directory first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      BufferedImage image = videoCapture.getImage();
      String imageFileName = "image-" + System.currentTimeMillis();
      File imageFile = new File(rawDir, imageFileName + ".png");
      try {
        ImageIO.write(image, "png", imageFile);
        indexStream.println(imageFileName + "#" + category);
        indexStream.flush();
      } catch (IOException ioe) {
        JOptionPane.showMessageDialog(controlFrame, "Error: " + ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  
  class ConvertListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if (dataDir == null) {
        JOptionPane.showMessageDialog(controlFrame, "You must select a directory first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      convertImages(dataDir.getAbsolutePath());
    }
  }
  
  class LearnListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if (dataDir == null) {
        JOptionPane.showMessageDialog(controlFrame, "You must select a directory first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      java.util.List<ImageSensor> examples = ImageSensor.loadExamples(dataDir.getAbsolutePath());
      ArrayList<ImageSensor> examplesArrayList = new ArrayList<ImageSensor>();
      for(int i = 0;i<examples.size(); i++){
        examplesArrayList.add(examples.get(i));
      }
      int numInputSensors = sensorWidth * sensorHeight;
      int numHiddenNeurons = 600;
      double learningRate = 0.5;
      Node[][] net = new NeuralNetwork(numInputSensors, numHiddenNeurons, categories.length, 1, Main.random).getnodeArray();
      NeuralNetwork.learn(net, examplesArrayList, categories, learningRate);
    }
  }
  
  class CategorizeListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if (dataDir == null) {
        JOptionPane.showMessageDialog(controlFrame, "You must select a directory first.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if(modelDir == null){
        JOptionPane.showMessageDialog(controlFrame, "You must select a model first.", "Error", JOptionPane.ERROR_MESSAGE);
      }
      //how are we supposed to know the category to check if the answer is correct, if we don't know the right answer
      BufferedImage image = videoCapture.getImage();
      image = cookImage(image);
      //elo
      int width = image.getWidth();
      int height = image.getHeight();
      ArrayList<Double> inputsSensor = new ArrayList<>(width*height);
      for(int i = 0; i<width*height;i++){
        inputsSensor.add(0.00);
      }
      for (int x=0; x<width; x++) {
        for (int y=0; y<height; y++) {
          int rgb = image.getRGB(x, y);
          Color color = new Color(rgb);
          int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
          inputsSensor.set(y*width+x, gray/255.0);
        }
      }
    //
      ImageSensor example = new ImageSensor(inputsSensor, null);
      ArrayList<Input> examples = new ArrayList<Input>();
      examples.add(example);
      String modelName = "./models/" + modelDir.getName();
      String categoryName = NeuralNetwork.test(examples, categories, modelName);
      //tests the network on one example
      //String categoryName = ImageSensor.getCategoryName(category);
      System.out.println("Identified: " + categoryName);
      categoryLabel.setText(categoryName);
    }
  }
  
  void convertImages(String dataDirPath)
  {
    File dataDir = new File(dataDirPath);
    File rawDir = new File(dataDir, "raw");
    File cookedDir = new File(dataDir, "cooked");
    if (! cookedDir.exists()) 
      cookedDir.mkdir();
    
    SimpleFile indexFile = new SimpleFile(dataDir, "index.txt");
    indexFile.startReading();
    while (indexFile.hasMoreLines()) {
      String line = indexFile.readNextLine();
      String[] fields = line.split("#");
      String name = fields[0];
      String category = fields[1];
      String imageName = name + ".png";
      System.out.println("processing " + imageName);
      try {
        BufferedImage rawImage = ImageIO.read(new File(rawDir, imageName));
        BufferedImage cookedImage = cookImage(rawImage);
        ImageIO.write(cookedImage, "png", new File(cookedDir, imageName));
      }
      catch (IOException ioe) {
        System.err.println("IOException: " + ioe.getMessage());
      }
    }
    indexFile.stopReading();
  }
  
  BufferedImage cookImage(BufferedImage rawImage)
  {
    BufferedImage croppedImage = rawImage.getSubimage(cameraCropLeftX, cameraCropTopY, cameraCropWidth, cameraCropHeight);
    Image sampledImage = croppedImage.getScaledInstance(sensorWidth,sensorHeight,Image.SCALE_AREA_AVERAGING);
    
    BufferedImage finalImage = new BufferedImage(sensorWidth, sensorHeight, BufferedImage.TYPE_BYTE_GRAY);
    Graphics pen = finalImage.getGraphics();
    pen.drawImage(sampledImage, 0, 0, null);
    return finalImage;
  }
  
  public static void main(String[] args)
  {
    new ImageLearner();
  }
}