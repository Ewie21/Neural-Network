package src;
import java.util.*;
import SimpleFile.SimpleFile;

public class Main{
    final static boolean DEBUG = false;
    final static Myrandom random = new Myrandom();
    static int INPUT = 0;
    static int ANSWER = 2;
    public static void main(String[] args){
        //trainDigits(.05);
        testDigits("src/models/model322715912.ser");
    }

    public static ArrayList<Node>[] createNetwork(int inputNum, int hiddenNum, int answerNum, int hiddenLayers){
        ArrayList<Node>[] nodes = new NeuralNetwork(inputNum, hiddenNum, answerNum, hiddenLayers, random).getnodeArray();
        return nodes;
    }   

    public static ArrayList<Input> ANDFile(){
        SimpleFile file = new SimpleFile("AND.txt");
        ArrayList<Input> inputs = new ArrayList<Input>();
        for(String line:file){
            ArrayList<String> initInput = new ArrayList<>(Arrays.asList(line.split(";")));
            ArrayList<Double> lineInputs = new ArrayList<>(Arrays.asList(Double.parseDouble(initInput.get(0).split(" ")[0]), Double.parseDouble(initInput.get(0).split(" ")[1])));
            Input input = new Input(lineInputs, initInput.get(1));
            inputs.add(input);
        }
        return inputs;
    }

    public static void trainNetworkAND(double learningRate){
        String[] categories = {"0", "1"};
        ArrayList<Input> data = ANDFile();
        ArrayList<Node>[] nodeArray = createNetwork(2,2,2, 1);
        NeuralNetwork.learn(nodeArray, data, categories, learningRate);
    }

    public static ArrayList<Input> XORFile(){
        SimpleFile file = new SimpleFile("XOR.txt");
        ArrayList<Input> inputs = new ArrayList<Input>();
        for(String line:file){
            ArrayList<String> initInputs = new ArrayList<>(Arrays.asList(line.split(";")));
            ArrayList<Double> lineInputs = new ArrayList<>(Arrays.asList(Double.parseDouble(initInputs.get(0).split(" ")[0]), Double.parseDouble(initInputs.get(0).split(" ")[1])));
            Input input = new Input(lineInputs, initInputs.get(1));
            inputs.add(input);
        }
        return inputs;
    }

    public static void trainNetworkXOR(double learningRate){
        String[] categories = {"1", "0"};
        ArrayList<Input> data = XORFile();
        ArrayList<Node>[] nodeArray = createNetwork(2,2,2, 1);
        NeuralNetwork.learn(nodeArray, data, categories, learningRate);
    }

    //Arraylist to hold arraylists of inputs
    public static ArrayList<Input> trainDigitsFile(){
        SimpleFile file = new SimpleFile("digits-train.txt");
        //arraylist of Inputs
        ArrayList<Input> inputs = new ArrayList<Input>();
        for(String line:file){
            ArrayList<String> initInputs = new ArrayList<>(Arrays.asList(line.split(",")));
            ArrayList<Double> doubleInputs = new ArrayList<>();
            for(int i = 0;i<initInputs.size()-1;i++){
                doubleInputs.add(Double.parseDouble(initInputs.get(i)));
            }
            Input input = new Input(doubleInputs, initInputs.get(initInputs.size()-1));
            inputs.add(input);
        }
        return inputs;
    }

    public static ArrayList<Input> testDigitsFile(){
        SimpleFile file = new SimpleFile("digits-test.txt");
        ArrayList<Input> inputs = new ArrayList<Input>();
        for(String line:file){
            ArrayList<String> initInputs = new ArrayList<>(Arrays.asList(line.split(",")));
            ArrayList<Double> doubleInputs = new ArrayList<>();
            for(int i = 0;i<initInputs.size()-1;i++){
                doubleInputs.add(Double.parseDouble(initInputs.get(i)));
            }
            Input input = new Input(doubleInputs, initInputs.get(initInputs.size()-1));
            inputs.add(input);
        }
        return inputs;
    }

    public static ArrayList<Node>[] trainDigits(double learningRate){
        String[] categories = {"0","1","2","3","4","5","6","7","8","9"};
        ArrayList<Input> data = trainDigitsFile();
        ArrayList<Node>[] nodeArray = createNetwork(64,128,10,1);
        String name = NeuralNetwork.learn(nodeArray, data, categories, learningRate);
        //testDigits("src/models/model1.ser");
        testDigits(name);
        return nodeArray;
    }

    public static void testDigits(String modelPath){
        String[] categories = {"0","1","2","3","4","5","6","7","8","9"};
        ArrayList<Input> data = testDigitsFile();
        NeuralNetwork.test(data, categories, modelPath);
    }
    
}