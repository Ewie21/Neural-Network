package src;
import java.util.*;
import SimpleFile.SimpleFile;

//maybe the sorting of node[2] is screwing with what their links 
//because the links refer to the index of the nodes so switching around the indexes 4 times an epoch might screw with the links.

public class Main{
    final static Myrandom random = new Myrandom();
    final static int INPUT = 0;
    final static int HIDDEN = 1;
    final static int ANSWER = 2;
    public static void main(String[] args){
        Node[][] nodeArray = createNetwork(2,2,2);
        testNetworkAND(nodeArray, 1);
    }
    public static Node[][] createNetwork(int inputNum, int hiddenNum, int answerNum){
        Node[][] nodes = new Node[3][2];
        for(int i = 0; i<inputNum; i++){
            nodes[0][i] = new Sensor();
        }
        for(int i = 0; i<hiddenNum; i++){
            nodes[1][i] = new Node(inputNum);
        }
        for(int i = 0; i<answerNum; i++){
            nodes[2][i] = new Node(hiddenNum);
        }
        for(int layer = 1; layer<nodes.length; layer++){
            for(int node = 0; node<nodes[layer].length; node++){
                nodes[layer][node].bWeight = random.doubleRange(-.05, .05);
                for(int link = 0; link<nodes[layer][node].links; link++){
                    nodes[layer][node].linkWeights[link] = random.doubleRange(-.05,.05);//weights init
                }
            }
        }
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

    public static int largestNode(Node[][] nodeArray){
        int largestNode = 0;
                for(int node = 0; node<nodeArray[ANSWER].length;node++){
                    if(nodeArray[2][node].cachedOutput>nodeArray[2][largestNode].cachedOutput){
                        largestNode = node;
                    }
                }
                return largestNode;
    }

    public static void learn(Node[][] nodeArray, ArrayList<Input> data, String[] categories, double learningRate){
        int epochs = 0;
        double sum = 0;
        int n = 0;

        for(int i = 0; i<categories.length; i++){
            nodeArray[ANSWER][i].category = categories[i];
        }
        
        double averageErr = sum/n;
        while(epochs<100){ //read file in another function

            for(int l = 0; l<data.size(); l++){ //iterates through training examples
                
                //assigns correct answers to answer nodes based on their category
                for(int i = 0; i<nodeArray[ANSWER].length;i++){
                    if(nodeArray[ANSWER][i].category.equals(categories[i])){
                        nodeArray[ANSWER][i].correctAnswer = 1;
                    }else{
                        nodeArray[ANSWER][i].correctAnswer = 0;
                    }
                }
        
                //passes in data for input layer
                for(int i = 0; i<nodeArray[INPUT].length; i++){//i = current input index
                    double input = data.get(l).inputs.get(i);
                    nodeArray[INPUT][i].linkVals[INPUT] = input;
                }
                
                //feed-forward values for hidden and output layers
                 for(int layer = 1; layer<nodeArray.length; layer++){ //height of the array
                    for(int node = 0; node<nodeArray[layer].length; node++){ //length of the array
                        for(int prevNode = 0; prevNode<nodeArray[layer-1].length; prevNode++){ //previous layer
                            nodeArray[layer][node].linkVals[prevNode] = nodeArray[layer - 1][prevNode].cachedOutput;
                            nodeArray[layer][node].output();
                            //^^^ PROBLEM IS WE HAVE TO RECALC ANSWER OUTPUT FOR EVERY TIME WE UPDATE THE LINK WHICH WE TRY TO SOLVE HERE ^^^
                            //if(layer == 2){
                            //    System.out.printf("ran output on answer %f\n", nodeArray[layer][node].cachedOutput);
                            //}
                        }
                    }
                }
                
                Node brightestNode = nodeArray[ANSWER][largestNode(nodeArray)];
                double brightness = brightestNode.cachedOutput; //answer the network is giving us
                backPropogate(nodeArray, learningRate);
                if(epochs % 10 == 0){
                    System.out.println("\n-------------------------------\n");
                    System.out.printf("Epoch: %d\n", epochs);
                    System.out.printf("Category: %s \nBrightness: %f\n", brightestNode.category, brightness);
                    if(brightestNode.category.equals(data.get(l).answer)){
                        System.out.println("Yay ");
                    }
                }
            }
            epochs++;
        }
        System.out.printf("finished with an errsig of %f after %d epochs \n", nodeArray[ANSWER][largestNode(nodeArray)].errSig, epochs);
    }
        

    public static void testNetworkAND(Node[][] nodeArray, double learningRate){
        String[] categories = {"1", "0"};
        ArrayList<Input> data = ANDFile();
        learn(nodeArray, data, categories, learningRate);

    }

    public static void backPropogate(Node[][] nodeArray, double learningRate){
        //errsig for answer neurons
        for(int answer = 0; answer<nodeArray[ANSWER].length;answer++){
            nodeArray[ANSWER][answer].computeAnswerErrSignal();//errSig for answer neurons 
        }
        adjustHiddenWeights(nodeArray, learningRate);
        //adjusting weights for hidden neurons
        for(int answer = 0; answer<nodeArray[ANSWER].length; answer++){
            nodeArray[ANSWER][answer].adjustWeights(learningRate);
        }
    } 

    public static void adjustHiddenWeights(Node[][] nodeArray, double learningRate){
        //errsig and adjusting weights for hidden neurons
        for(int hidden = 0; hidden<nodeArray[HIDDEN].length;hidden++){ 
            nodeArray[HIDDEN][hidden].errSig = 0;
            for(int answer = 0; answer<nodeArray[ANSWER].length; answer++){ //loops through answer neurons
                double answerWeight = nodeArray[ANSWER][answer].linkWeights[hidden];
                nodeArray[HIDDEN][hidden].errSig += nodeArray[ANSWER][answer].errSig * answerWeight; //errSig initial summation for hidden nodes
            }   
            double hiddenResult = nodeArray[HIDDEN][hidden].cachedOutput;
            nodeArray[HIDDEN][hidden].errSig *= (hiddenResult)*(1-hiddenResult);
            nodeArray[HIDDEN][hidden].adjustWeights(learningRate);
        }
    }
}