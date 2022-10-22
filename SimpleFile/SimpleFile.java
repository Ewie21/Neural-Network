package SimpleFile;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is a simple class for reading or writing text files in Java.
 * 
 @author Andrew Merrill, Spring 2004, last updated January 2017
 @version 2.0
 
 <pre>
 Example of writing to a file:
 
 // create a new SimpleFile object
 SimpleFile file = new SimpleFile("mytestfile.txt");
 
 // tell it that you want to start writing
 file.startWriting();
 // or use file.startAppending() to write to the end of the file
 
 // get a PrintStream from the file
 PrintStream stream = file.getPrintStream();
 
 // Use the println method from the PrintStream class to write output to the file
 stream.println("line 1");
 stream.println("line 2");
 
 // tell the file that you are done writing
 file.stopWriting();
 
 ///////////////////////////////////////////////////////////////////////////
 Example of reading from a file:
 
 // create a new SimpleFile object
 SimpleFile file = new SimpleFile("mytestfile.txt");
 
 for (String line : file) {
    // do stuff with the line of text
 }
 
 // OR, if you want to do it the long way, you can do this:
 
 // tell it that you want to start reading
 file.startReading();
 
 // the hasMoreLines() method returns true if there are more lines to read
 while (file.hasMoreLines()) 
 {
 // the readNextLine() method reads the next line and returns it as a String
 String line = file.readNextLine();
 
 // now do whatever you want with the line you just read
 // ...
 }
 
 // tell the file that you are done reading
 file.stopReading();
 </pre>
 */



public class SimpleFile extends File implements Iterable<String>
{
  private PrintStream outputStream = null;
  private BufferedReader inputReader = null;
  private String line = null;

  /*************************************************************************
   * Construct a new SimpleFile from a filename.
   * 
   * A SimpleFile may be used for either reading or writing.
   * 
   * @param filename The filename of the file.
   */
  
  public SimpleFile(String filename)
  {
    super(filename);
  }
  
  
  /*************************************************************************
   * Construct a new SimpleFile from a parent directory and a filename.
   * 
   * A SimpleFile may be used for either reading or writing.
   * 
   * @param parent The directory in which to create this file.
   * @param filename The filename of the file.
   */
  public SimpleFile(String parent, String filename)
  {
    super(parent, filename);
  }
  
  
  /*************************************************************************
   * Construct a new SimpleFile from a parent directory and a filename.
   * 
   * A SimpleFile may be used for either reading or writing.
   * 
   * @param parent The directory in which to create this file.
   * @param filename The filename of the file.
   */
  public SimpleFile(File parent, String filename)
  {
    super(parent, filename);
  }
  
  
  /*************************************************************************
   Prepare the file for writing.
   New lines will be written at the beginning of the file, erasing the old contents.
   */
  
  public void startWriting()
  {
    startWriting(false);
  }
  
  /*************************************************************************
   Prepare the file for appending.
   This can be used instead of startWriting if you want
   new lines to be written at the end of the file and to preserve the old contents.
   */
  
  public void startAppending()
  {
    startWriting(true);
  }
  
  ///////////////////////////////////////////////////////////////////////////
  // private method that really prepares the file for writing 
  //   append is true if we should append to the file, and false if we should erase it
  
  private void startWriting(boolean append)
  {
    if (inputReader != null) {
      throw new SimpleFileException("Cannot write to a file while reading it");
    }
    if (outputStream != null) {
      throw new SimpleFileException("You need to stop writing first");
    }
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(this, append);
      outputStream = new PrintStream(fileOutputStream);
    }
    catch (IOException exception)
    {
      outputStream = null;
      throw new SimpleFileException("Failed to start writing: " + exception.getMessage());
    }
  }
  
  /*************************************************************************
   * Returns a PrintStream that can be used to write to the file.
   *  You must call startWriting or startAppending before calling this method.
   */
  
  public PrintStream getPrintStream()
  {
    if (outputStream == null) {
      throw new SimpleFileException("You need to start writing first");
    }  
    return outputStream;
  }
  
  /*************************************************************************
   * Closes the file when you are done writing to it.
   */
  
  public void stopWriting()
  {
    if (outputStream != null) {
      outputStream.close();
      outputStream = null;
    }
  }
  
  /*************************************************************************/
  
  /**
   * Prepare the file for reading.
   */
  
  public void startReading()
  {
    if (inputReader != null) {
      throw new SimpleFileException("You need to stop reading first");
    }
    if (outputStream != null) {
      throw new SimpleFileException("You cannot read a file while you are writing it");
    }
    try {
      FileReader fileReader = new FileReader(this);
      inputReader = new BufferedReader(fileReader);
      // a BufferedReader can read a file one line at a time
      line = inputReader.readLine();
    } 
    catch (IOException exception)
    {
      inputReader = null;
      throw new SimpleFileException("Failed to start reading: " + exception.getMessage());
    }
  }
  
  /*************************************************************************
   * Returns true if there are more lines to read in the file.
   * Returns false if we have reached the end of the file.
   */
  
  public boolean hasMoreLines()
  {
    if (inputReader == null) {
      throw new SimpleFileException("You need to start reading first");
    }
    if (line != null)
      return true;
    else
      return false;
  }
  
  /*************************************************************************
   * Returns the next line from the file, as a String.
   */
  
  public String readNextLine()
  {
    if (inputReader == null) {
      throw new SimpleFileException("You need to start reading first");
    }
    if (line == null) {
      throw new SimpleFileException("There are no more lines to read");
    }
    String nextLine = line;
    try {
      line = inputReader.readLine();
    } 
    catch (IOException exception)
    {
      throw new SimpleFileException("Failed while reading: " + exception.getMessage());
    }
    return nextLine;
  }
  
  /*************************************************************************
   * Closes the file when you are done reading it.
   */
  
  public void stopReading()
  {
    if (inputReader != null) {
      try {
        inputReader.close();
        inputReader = null;
      }
      catch (IOException exception)
      {
        inputReader = null;
        throw new SimpleFileException("Error when trying to close file: " + exception.getMessage());
      }
    }
  }
  
  /////////////////////////////////////////////////////////////////
  
  public Iterator<String> iterator()
  {
    return new SimpleFileIterator();
  }
  
  class SimpleFileIterator implements Iterator<String>
  {
    SimpleFileIterator()
    {
      startReading();
    }
    
    public boolean hasNext()
    {
      boolean moreLines = hasMoreLines();
      if (! moreLines) stopReading();
      return moreLines;
    }
    
    public String next()
    {
      return readNextLine();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("cannot remove from simple file iterator");
    }
  }
  
  
//////////////////////////////////////////////////////////////////////////////////////////
  
  public class SimpleFileException extends RuntimeException
  {
    SimpleFileException(String message)
    {
      super(message + " with file " + SimpleFile.this.getName());
    }
  }
}