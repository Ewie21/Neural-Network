package src;
import java.lang.reflect.Array;
import java.util.*;
import SimpleFile.SimpleFile;
//ErrSig is still 0, fix this!!!

public class Main{  
    final static Myrandom random = new Myrandom();
    static int INPUT = 0;
    static int HIDDEN;
    static int ANSWER = 2;
    public static void main(String[] args){
        ArrayList<Node>[] nodeArray = createNetwork(2,2,2, 1);
        testNetworkAND(nodeArray, 1);
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

    public static void testNetworkAND(ArrayList<Node>[] nodeArray, double learningRate){
        String[] categories = {"1", "0"};
        ArrayList<Input> data = ANDFile();
        NeuralNetwork.learn(nodeArray, data, categories, learningRate, 1);
    }
}