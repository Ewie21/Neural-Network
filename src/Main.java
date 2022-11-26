package src;
import java.util.*;

import SimpleFile.SimpleFile;

public class Main{
    final static boolean DEBUG = false;
    final static Myrandom random = new Myrandom();
    static int INPUT = 0;
    static int ANSWER;
    public static void main(String[] args){
        //trainDigits(.05);
        //testDigits("/models/modelbest.ser"); 
        ImageLearner.main(args);;
    }
    //Creates the network 
    public static Node[][] createNetwork(int inputNum, int hiddenNum, int answerNum, int hiddenLayers){
        Node[][] nodes = new NeuralNetwork(inputNum, hiddenNum, answerNum, hiddenLayers, random).getnodeArray();
        return nodes;
    }   
    //Reads the AND data file and writes it to an Input object of answers and inputs
    public static ArrayList<StandardInput> ANDFile(){
        SimpleFile file = new SimpleFile("AND.txt");
        ArrayList<StandardInput> inputs = new ArrayList<StandardInput>();
        for(String line:file){
            ArrayList<String> initInput = new ArrayList<>(Arrays.asList(line.split(";")));
            ArrayList<Double> lineInputs = new ArrayList<>(Arrays.asList(Double.parseDouble(initInput.get(0).split(" ")[0]), Double.parseDouble(initInput.get(0).split(" ")[1])));
            StandardInput input = new StandardInput(lineInputs, initInput.get(1));
            inputs.add(input);
        }
        return inputs;
    }
    //Trains the neural network into an AND gate, using the AND dataset
    public static void trainNetworkAND(double learningRate){
        String[] categories = {"0", "1"};
        ArrayList<StandardInput> data = ANDFile();
        Node[][] nodeArray = createNetwork(2,2,2, 1);
        NeuralNetwork.learn(nodeArray, data, categories, learningRate);
    }
    //Reads the XOR data file and writes it to an Input object of answer and inputs
    public static ArrayList<StandardInput> XORFile(){
        SimpleFile file = new SimpleFile("XOR.txt");
        ArrayList<StandardInput> inputs = new ArrayList<StandardInput>();
        for(String line:file){
            ArrayList<String> initInputs = new ArrayList<>(Arrays.asList(line.split(";")));
            ArrayList<Double> lineInputs = new ArrayList<>(Arrays.asList(Double.parseDouble(initInputs.get(0).split(" ")[0]), Double.parseDouble(initInputs.get(0).split(" ")[1])));
            StandardInput input = new StandardInput(lineInputs, initInputs.get(1));
            inputs.add(input);
        }
        return inputs;
    }
    //Trains the neural network into an XOR gate, using the XOR dataset
    public static void trainNetworkXOR(double learningRate){
        String[] categories = {"1", "0"};
        ArrayList<StandardInput> data = XORFile();
        Node[][] nodeArray = createNetwork(2,2,2, 1);
        NeuralNetwork.learn(nodeArray, data, categories, learningRate);
    }

    //Reads digits-train file and writes it to an Input object of answers and inputs
    public static ArrayList<StandardInput> trainDigitsFile(){
        SimpleFile file = new SimpleFile("digits-train.txt");
        ArrayList<StandardInput> inputs = new ArrayList<StandardInput>();
        for(String line:file){
            ArrayList<String> initInputs = new ArrayList<>(Arrays.asList(line.split(",")));
            ArrayList<Double> doubleInputs = new ArrayList<>();
            for(int i = 0;i<initInputs.size()-1;i++){
                doubleInputs.add(Double.parseDouble(initInputs.get(i)));
            }
            StandardInput input = new StandardInput(doubleInputs, initInputs.get(initInputs.size()-1));
            inputs.add(input);
        }
        return inputs;
    }
    //Reads digits-test file and writes it to an Input object of answers and inputs
    public static ArrayList<StandardInput> testDigitsFile(){
        SimpleFile file = new SimpleFile("digits-test.txt");
        ArrayList<StandardInput> inputs = new ArrayList<StandardInput>();
        for(String line:file){
            ArrayList<String> initInputs = new ArrayList<>(Arrays.asList(line.split(",")));
            ArrayList<Double> doubleInputs = new ArrayList<>();
            for(int i = 0;i<initInputs.size()-1;i++){
                doubleInputs.add(Double.parseDouble(initInputs.get(i)));
            }
            StandardInput input = new StandardInput(doubleInputs, initInputs.get(initInputs.size()-1));
            inputs.add(input);
        }
        return inputs;
    }
    //Trains the neural network to recognize digiits by reading the train-digits file
    //Each line of the file contains 64 integers from 0 to 16 representing greyscale values
    //Each 8 integers represents a different line of pixels, making the image 16x16
    //The greyscale values are the average of the greyscale of a higher resolution image
    public static Node[][] trainDigits(double learningRate){
        String[] categories = {"0","1","2","3","4","5","6","7","8","9"};
        ArrayList<StandardInput> data = trainDigitsFile();
        Node[][] nodeArray = createNetwork(64,128,10,1);
        String name = NeuralNetwork.learn(nodeArray, data, categories, learningRate);
        //testDigits("src/models/model1.ser");
        testDigits(name);
        return nodeArray;
    }
    //Tests a pre-trained model on the digits-test data set
    public static void testDigits(String modelPath){
        String[] categories = {"0","1","2","3","4","5","6","7","8","9"};
        ArrayList<StandardInput> data = testDigitsFile();
        NeuralNetwork.test(data, categories, modelPath);
    }
}