package src;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.*;

public class NeuralNetwork{
    public static Node[][] nodeArray;
    public static int INPUT;
    public static int ANSWER;
    //Neural network constructor; An Array of Arraylists of Nodes
    public NeuralNetwork(int inputNum, int hiddenNum, int answerNum, int hiddenLayers, Myrandom random){
        INPUT = 0;
        ANSWER = 1+hiddenLayers;

        nodeArray = new Node[hiddenLayers+2][];
        //an array of array lists
        //initialize input sub array
        nodeArray[INPUT] = new Node[inputNum];
        //initialize hidden sub arrays
        for(int i = 1; i<hiddenLayers+1;i++){
            nodeArray[i] = new Node[hiddenNum];
        }
        //initialize answer sub array
        nodeArray[ANSWER] = new Node[answerNum];
        //create sensors
        for(int i = 0; i<inputNum; i++){ 
            nodeArray[INPUT][i] = new Sensor();
        }
        //create hidden layers
        for(int n = 1; n<hiddenLayers+1;n++){ //creates hidden layers
            for(int i = 0; i<hiddenNum; i++){ //creates nodeArray in the layers
                nodeArray[n][i] = new Node(inputNum);
            }
        }
        
        for(int i = 0; i<answerNum; i++){
            nodeArray[ANSWER][i] = new Node(hiddenNum);
        }
        for(int layer = 1; layer<nodeArray.length; layer++){
            for(int node = 0; node<nodeArray[layer].length; node++){
                nodeArray[layer][node].bWeight = random.doubleRange(-.05, .05);
                if(Main.DEBUG) System.out.printf("Layer: %d Node: %d Bias Weight: %f\n", layer, node, nodeArray[layer][node].bWeight);
                for(int link = 0; link<nodeArray[layer][node].links; link++){
                    nodeArray[layer][node].linkWeights[link] = random.doubleRange(-.05,.05);//weights init
                    if(Main.DEBUG) System.out.printf("Layer: %d Node: %d Weight: %f\n", layer, node, nodeArray[layer][node].linkWeights[link]);
                }
            }
        }
    }
    //Trains the network
    public static String learn(Node[][] nodeArray, ArrayList<Input> data, String[] categories, double learningRate){
        int epochs = 0;
        double sum = 0;
        double count = 0;
        double errPercentage = 0;
        int hiddenLayers = nodeArray.length - 2;

        categorize(categories, nodeArray);

        while(errPercentage<99.99){ //read file in another function
            count = 0;
            sum = 0;
            errPercentage = 0;

            for(int lineNum = 0; lineNum<data.size(); lineNum++){ //iterates through training examples
                if(Main.DEBUG) System.out.println("training checkpoint one passed");
                assignAnswers(data, lineNum, nodeArray, ANSWER);
                
                pushDownstream(nodeArray, data, lineNum);
                
                ReturnAnal selfAnal = selfAnalysis(epochs, sum, count, data, lineNum, nodeArray);
                count = selfAnal.getCount();
                sum = selfAnal.getSum();
                Node brightestNode = selfAnal.getBrightestNode();
                double brightness = selfAnal.getBrightness();
                //System.out.println(sum);
                //System.out.println(count);
                backPropogate(nodeArray, learningRate, hiddenLayers);
            }
            double oldErrPercentage = errPercentage;
            errPercentage = (sum/count)*100;
            System.out.println(errPercentage);
            epochs++;
            System.out.println(epochs);
            if(errPercentage - oldErrPercentage <0.0001){
                break;
            }
        }
        String name = writeModel(nodeArray);
        System.out.printf("Training: Finished with an accuracy of %f/%f or %f percent after %d epochs \n", sum, count, errPercentage, epochs);
        return name;
    }
    //Tests a model on a dataset
    public static String test(ArrayList<Input> data, String[] categories, String modelName){
        int epochs = 0;
        double sum = 0;
        double count = 0;
        String cCategory = "";
        nodeArray = readModel(modelName);
        ANSWER = nodeArray.length - 1;

        for(int node = 0; node<nodeArray[ANSWER].length; node++){
            nodeArray[ANSWER][node].category = categories[node];
            if(Main.DEBUG) System.out.println(nodeArray[ANSWER][node].category);
        }

        //tests model
        for(int l = 0;l<data.size();l++){
            assignAnswers(data, l, nodeArray, ANSWER);

            pushDownstream(nodeArray, data, l);
            
            selfAnalysis(epochs, sum, count, data, l, nodeArray);
            ReturnAnal selfAnal = selfAnalysis(epochs, sum, count, data, l, nodeArray);
            cCategory = selfAnal.brightestNode.category;
            count = selfAnal.count;
            sum = selfAnal.sum;
        }
        double errPercentage = sum/count*100;
        System.out.printf("Testing: Finished with an accuracy of %f/%f or %f percent after %d epochs \n", sum, count, errPercentage, epochs);
        return cCategory;
    }
    //Assigns categories to each answer neuron
    public static void categorize(String[] categories, Node[][] nodeArray){
        for(int i = 0; i<categories.length; i++){
            nodeArray[ANSWER][i].category = categories[i];
        }
    }
    //Assigns correct answers to each answer neuron by checking their category against the given answer
    public static void assignAnswers(ArrayList<Input> data, int l, Node[][] nodeArray, int ANSWER){
        for(int node = 0; node<nodeArray[ANSWER].length;node++){
            if(nodeArray[ANSWER][node].category.equals(data.get(l).answer)){
                nodeArray[ANSWER][node].correctAnswer = 1;
            }else{
                nodeArray[ANSWER][node].correctAnswer = 0;
            }
        }
    }
    //Passes in data to to the sensors, pushs data 'downstream' through the network
    public static void pushDownstream(Node[][] nodeArray, ArrayList<Input> data, int l){
        //passes in data for input layer
        for(int i = 0; i<nodeArray[INPUT].length; i++){//i = current input index
            double input = data.get(l).inputs.get(i);
            ((Sensor) nodeArray[INPUT][i]).setValue(input); 
        }
        //feed-forward values for hidden and output layers
        for(int layer = 1; layer<nodeArray.length; layer++){ //height of the array
            for(int node = 0; node<nodeArray[layer].length; node++){ //length of the array
                for(int prevNode = 0; prevNode<nodeArray[layer-1].length; prevNode++){ //previous layer
                    nodeArray[layer][node].linkVals[prevNode] = nodeArray[layer - 1][prevNode].cachedOutput;
                    nodeArray[layer][node].output();
                    if(Main.DEBUG) if(layer == ANSWER){
                       System.out.printf("Ran output on answer %f\n", nodeArray[layer][node].cachedOutput);
                    }
                }
            }
        }
    }
    //Analyses chosen answer neuron's result and prints 'Yay' if the network chose correct; also increments sum and count
    public static ReturnAnal selfAnalysis(double epochs, double sum, double count, ArrayList<Input> data, int l, Node[][] nodeArray){
        Node brightestNode = nodeArray[ANSWER][largestNode(nodeArray)];
        double brightness = brightestNode.cachedOutput; //strength of the answer the network is giving us
        
        if(epochs % 10 == 0){
            System.out.println("\n-------------------------\n");
            System.out.printf("Epoch: %f\n", epochs);
            if(Main.DEBUG) System.out.printf("Non-Category: %s \nnon-Brightness: %f\n", nodeArray[ANSWER][nodeArray[ANSWER].length-1-largestNode(nodeArray)].category, nodeArray[ANSWER][nodeArray[ANSWER].length-1-largestNode(nodeArray)].cachedOutput);
            System.out.printf("Category: %s \nBrightness: %f\n", brightestNode.category, brightness);
            if(brightestNode.category.equals(data.get(l).answer)){
                System.out.println("Yay ");
                //sum++;
            }
        }
        
        if(brightestNode.category.equals(data.get(l).answer)){
            if(Main.DEBUG) System.out.println("Sum++");
            sum++;
        }
        count++;
        ReturnAnal analData = new ReturnAnal(count, sum, brightestNode, brightness);
        return analData;
    }
    //Adjusts the weights of all the hidden neurons in a network
    public static void adjustHiddenWeights(Node[][] nodeArray, double learningRate, int hiddenLayers){
        //errsig and adjusting weights for hidden neurons
        for(int HIDDEN = 1; HIDDEN < hiddenLayers+1;HIDDEN++){
            for(int hidden = 0; hidden<nodeArray[HIDDEN].length;hidden++){ 
                nodeArray[HIDDEN][hidden].errSig = 0;//clears errSig from last training example
                for(int nextLayer = 0; nextLayer<nodeArray[HIDDEN+1].length; nextLayer++){ //loops through answer neurons
                    //Initially nodeArray[ANSWER].get[hidden], changing it to this changed nothing
                    double nextWeight = nodeArray[HIDDEN+1][nextLayer].linkWeights[hidden];
                    nodeArray[HIDDEN][hidden].errSig += nodeArray[HIDDEN+1][nextLayer].errSig * nextWeight;//errSig initial summation for hidden nodeArray
                }
                double hiddenResult = nodeArray[HIDDEN][hidden].cachedOutput;
                nodeArray[HIDDEN][hidden].errSig *= (hiddenResult)*(1-hiddenResult);
                if(Main.DEBUG) System.out.println("\nLayer: " + HIDDEN);
                if(Main.DEBUG) System.out.println("Node: " + hidden);
                nodeArray[HIDDEN][hidden].adjustWeights(learningRate);
            }
        }
    }
    //Chooses the largest node and returns its index
    public static int largestNode(Node[][] nodeArray){
        int largestNode = 0;
        for(int node = 0; node<nodeArray[ANSWER].length;node++){
            if(nodeArray[ANSWER][node].cachedOutput>nodeArray[ANSWER][largestNode].cachedOutput){
                largestNode = node;
            }
        }
        return largestNode;
    }

    //Goes back through the network adjusting the weights of the all the neurons based on their error signal
    public static void backPropogate(Node[][] nodeArray, double learningRate, int hiddenLayers){
        //errsig for answer neurons
        for(int answer = 0; answer<nodeArray[ANSWER].length;answer++){
            if(Main.DEBUG) System.out.println("Layer: "+ ANSWER);
            if(Main.DEBUG) System.out.println("Node: " + answer);
            nodeArray[ANSWER][answer].computeAnswerErrSignal();//errSig for answer neurons 
            if(Main.DEBUG) System.out.println("Error: " + nodeArray[ANSWER][answer].errSig);
        }
        adjustHiddenWeights(nodeArray, learningRate, hiddenLayers);
        //adjust weight for answer neurons
        for(int answer = 0; answer<nodeArray[ANSWER].length; answer++){
            nodeArray[ANSWER][answer].adjustWeights(learningRate);
        }
    }
    //Serializes a trained model so it can be used later, returns the name of the model
    public static String writeModel(Node[][] nodeArray){
        try{
            int fileNum = Main.random.random.nextInt();
            String name = String.format("src/models/modelBest.ser", fileNum);
            File file = new File(name);
            if(file.createNewFile()){
                FileOutputStream fileModel = new FileOutputStream(name);
                ObjectOutputStream out = new ObjectOutputStream(fileModel); 
                out.writeObject(nodeArray);
                out.close();
                fileModel.close();
                System.out.printf("Model %d Saved\n", fileNum);
                return name;
            }else{
                System.out.println("Houston, this file already exists");
                return writeModel(nodeArray);
            }
            
        }catch(IOException e){
            System.out.print("Houston, we have a problem: ");
            e.printStackTrace();
            return "";
        }
    }
    //Deserializes a model into a neural network, returns the neural network
    public static Node[][] readModel(String fileName){
        try{
            Node[][] nodeArray = null;
            System.out.println("Loading Model");
            FileInputStream readFileModel = new FileInputStream(fileName);
            ObjectInputStream fileIn = new ObjectInputStream(readFileModel);
            nodeArray = ((Node[][]) fileIn.readObject());
            fileIn.close();
            for(int layer = 1; layer<nodeArray.length;layer++){
                for(int node = 0; node<nodeArray[layer].length;node++){
                    for(int link = 0; link<nodeArray[layer][node].linkVals.length; link++){
                        nodeArray[layer][node].linkVals[link] = 0;
                    }
                }
            }
            System.out.println("Model Loaded");
            return nodeArray;
        }catch(IOException i){
            System.out.println("Houston, we have a problem: ");
            i.printStackTrace();
            return nodeArray;
        }catch(ClassNotFoundException c){
            System.out.println("Houston, we have a problem. Wrong File Loaded!");
            c.printStackTrace();
            return nodeArray;
        }
    }
    //Getter function for the neural network
    public Node[][] getnodeArray(){
        return nodeArray;
    }
}