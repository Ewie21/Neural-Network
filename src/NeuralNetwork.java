package src;
import java.io.File;
import java.io.IOException;
import java.util.*;
import SimpleFile.SimpleFile;
import java.io.*;

public class NeuralNetwork{
    public static ArrayList<Node>[] nodeArray;
    public static int INPUT;
    public static int ANSWER;
    public NeuralNetwork(int inputNum, int hiddenNum, int answerNum, int hiddenLayers, Myrandom random){
        INPUT = 0;
        ANSWER = 1+hiddenLayers;

        nodeArray = new ArrayList[hiddenLayers+2];
        //an array of array lists
        for(int i = 0;i<nodeArray.length;i++){
            nodeArray[i] = new ArrayList<Node>();
        }
        //create sensors
        for(int i = 0; i<inputNum; i++){
            nodeArray[INPUT].add(new Sensor());
        }
        //create hidden layers
        for(int n = 1; n<hiddenLayers+1;n++){ //creates hidden layers
            for(int i = 0; i<hiddenNum; i++){ //creates nodeArray in the layers
                nodeArray[n].add(new Node(inputNum));
            }
        }
        
        for(int i = 0; i<answerNum; i++){
            nodeArray[ANSWER].add(new Node(hiddenNum));
        }
        for(int layer = 1; layer<nodeArray.length; layer++){
            for(int node = 0; node<nodeArray[layer].size(); node++){
                nodeArray[layer].get(node).bWeight = random.doubleRange(-.05, .05);
                if(Main.DEBUG) System.out.printf("Layer: %d Node: %d Bias Weight: %f\n", layer, node, nodeArray[layer].get(node).bWeight);
                for(int link = 0; link<nodeArray[layer].get(node).links; link++){
                    nodeArray[layer].get(node).linkWeights[link] = random.doubleRange(-.05,.05);//weights init
                    if(Main.DEBUG) System.out.printf("Layer: %d Node: %d Weight: %f\n", layer, node, nodeArray[layer].get(node).linkWeights[link]);
                }
            }
        }
    }

    public static void learn(ArrayList<Node>[] nodeArray, ArrayList<Input> data, String[] categories, double learningRate){
        int epochs = 0;
        double sum = 0;
        double count = 0;
        double errPercentage = 0;
        int hiddenLayers = nodeArray.length - 2;

        categorize(categories, nodeArray);
        while(errPercentage<99){ //read file in another function
            count = 0;
            sum = 0;
            errPercentage = 0;

            for(int lineNum = 0; lineNum<data.size(); lineNum++){ //iterates through training examples
                if(Main.DEBUG) System.out.println("made it");
                assignAnswers(data, lineNum);
                
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
            
            errPercentage = (sum/count)*100;
            System.out.println(errPercentage);
            epochs++;
            System.out.println(epochs);
        }
        //String name = writeModel(nodeArray);
        //test(data, categories, "models/model1.ser");
        System.out.printf("Training: Finished with an accuracy of %f/%f or %f percent after %d epochs \n", sum, count, errPercentage, epochs);
    }

    public static void test(ArrayList<Input> data, String[] categories, String modelName){
        int epochs = 1;
        double sum = 0;
        double count = 0;
        nodeArray = readModel(modelName);

        //tests model
        for(int l = 0;l<data.size();l++){
            assignAnswers(data, l);

            pushDownstream(nodeArray, data, l);
            
            selfAnalysis(epochs, sum, count, data, l, nodeArray);
            ReturnAnal selfAnal = selfAnalysis(epochs, sum, count, data, l, nodeArray);
            count = selfAnal.count;
            sum = selfAnal.sum;
        }
        double errPercentage = sum/count*100;
        System.out.printf("Testing: Finished with an accuracy of %f/%f or %f percent after %d epochs \n", sum, count, errPercentage, epochs);
    }

    public static void categorize(String[] categories, ArrayList<Node>[] nodeArray){
        for(int i = 0; i<categories.length; i++){
            nodeArray[ANSWER].get(i).category = categories[i];
        }
    }

    public static void assignAnswers(ArrayList<Input> data, int l){
        //assigns correct answers to answer nodes based on their category
        for(int node = 0; node<nodeArray[ANSWER].size();node++){
            if(nodeArray[ANSWER].get(node).category.equals(data.get(l).answer)){
                nodeArray[ANSWER].get(node).correctAnswer = 1;
            }else{
                nodeArray[ANSWER].get(node).correctAnswer = 0;
            }
        }
    }

    public static void pushDownstream(ArrayList<Node>[] nodeArray, ArrayList<Input> data, int l){
        //passes in data for input layer
        for(int i = 0; i<nodeArray[INPUT].size(); i++){//i = current input index
            double input = data.get(l).inputs.get(i);
            ((Sensor) nodeArray[INPUT].get(i)).setValue(input); 
        }
        //feed-forward values for hidden and output layers
        for(int layer = 1; layer<nodeArray.length; layer++){ //height of the array
            for(int node = 0; node<nodeArray[layer].size(); node++){ //length of the array
                for(int prevNode = 0; prevNode<nodeArray[layer-1].size(); prevNode++){ //previous layer
                    nodeArray[layer].get(node).linkVals[prevNode] = nodeArray[layer - 1].get(prevNode).cachedOutput;
                    nodeArray[layer].get(node).output();
                    //if(layer == ANSWER){
                    //   System.out.printf("ran output on answer %f\n", nodeArray[layer].get(node).cachedOutput);
                    //}
                }
            }
        }
    }

    public static ReturnAnal selfAnalysis(double epochs, double sum, double count, ArrayList<Input> data, int l, ArrayList<Node>[] nodeArray){
        Node brightestNode = nodeArray[ANSWER].get(largestNode(nodeArray));
        double brightness = brightestNode.cachedOutput; //strength of the answer the network is giving us
        
        if(epochs % 10 == 0){
            System.out.println("\n-------------------------\n");
            System.out.printf("Epoch: %f\n", epochs);
            if(Main.DEBUG) System.out.printf("Non-Category: %s \nnon-Brightness: %f\n", nodeArray[ANSWER].get(nodeArray[ANSWER].size()-1-largestNode(nodeArray)).category, nodeArray[ANSWER].get(nodeArray[ANSWER].size()-1-largestNode(nodeArray)).cachedOutput);
            System.out.printf("Category: %s \nBrightness: %f\n", brightestNode.category, brightness);
            if(brightestNode.category.equals(data.get(l).answer)){
                System.out.println("Yay ");
                //sum++;
            }
        }
        
        if(brightestNode.category.equals(data.get(l).answer)){
            //System.out.println("made it");
            sum++;
        }
        count++;
        ReturnAnal analData = new ReturnAnal(count, sum, brightestNode, brightness);
        return analData;
    }

    public static void adjustHiddenWeights(ArrayList<Node>[] nodeArray, double learningRate, int hiddenLayers){
        //errsig and adjusting weights for hidden neurons
        for(int HIDDEN = 1; HIDDEN < hiddenLayers+1;HIDDEN++){
            for(int hidden = 0; hidden<nodeArray[HIDDEN].size();hidden++){ 
                nodeArray[HIDDEN].get(hidden).errSig = 0;//clears errSig from last training example
                for(int nextLayer = 0; nextLayer<nodeArray[HIDDEN+1].size(); nextLayer++){ //loops through answer neurons
                    //Initially nodeArray[ANSWER].get[hidden], changing it to this changed nothing
                    double nextWeight = nodeArray[HIDDEN+1].get(nextLayer).linkWeights[hidden];
                    nodeArray[HIDDEN].get(hidden).errSig += nodeArray[HIDDEN+1].get(nextLayer).errSig * nextWeight; //errSig initial summation for hidden nodeArray
                }
                double hiddenResult = nodeArray[HIDDEN].get(hidden).cachedOutput;
                nodeArray[HIDDEN].get(hidden).errSig *= (hiddenResult)*(1-hiddenResult);
                if(Main.DEBUG) System.out.println("\nLayer: " + HIDDEN);
                if(Main.DEBUG) System.out.println("Node: " + hidden);
                nodeArray[HIDDEN].get(hidden).adjustWeights(learningRate);
            }
        }
    }

    public static int largestNode(ArrayList<Node>[] nodeArray){
        int largestNode = 0;
        for(int node = 0; node<nodeArray[ANSWER].size();node++){
            if(nodeArray[ANSWER].get(node).cachedOutput>nodeArray[ANSWER].get(largestNode).cachedOutput){
                largestNode = node;
            }
        }
        return largestNode;
    }


    public static void backPropogate(ArrayList<Node>[] nodeArray, double learningRate, int hiddenLayers){
        //errsig for answer neurons
        for(int answer = 0; answer<nodeArray[ANSWER].size();answer++){
            if(Main.DEBUG) System.out.println("Layer: "+ ANSWER);
            if(Main.DEBUG) System.out.println("Node: " + answer);
            nodeArray[ANSWER].get(answer).computeAnswerErrSignal();//errSig for answer neurons 
            if(Main.DEBUG) System.out.println("Error: " + nodeArray[ANSWER].get(answer).errSig);
        }
        adjustHiddenWeights(nodeArray, learningRate, hiddenLayers);
        //adjust weight for answer neurons
        for(int answer = 0; answer<nodeArray[ANSWER].size(); answer++){
            nodeArray[ANSWER].get(answer).adjustWeights(learningRate);
        }
    }
    //figure out how to save both 
    public static String writeModel(ArrayList<Node>[] nodeArray){
        try{
            int fileNum = Main.random.random.nextInt();
            String name = String.format("models/model1.ser", fileNum);
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

        public static ArrayList<Node>[] readModel(String fileName){
        ArrayList<Node>[] nodeArray = null;
        try{
            System.out.println("Loading Model");
            FileInputStream readFileModel = new FileInputStream(fileName);
            ObjectInputStream fileIn = new ObjectInputStream(readFileModel);
            nodeArray = (ArrayList<Node>[]) fileIn.readObject();
            fileIn.close();
            for(int layer = 1; layer<nodeArray.length;layer++){
                for(int node = 0; node<nodeArray[layer].size();node++){
                    nodeArray[layer].get(node).linkVals = null;
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

    public ArrayList<Node>[] getnodeArray(){
        return nodeArray;
    }
}