package src;
import java.util.*;

public class NeuralNetwork{
    public static ArrayList<Node>[] nodeArray;
    public static int INPUT;
    public static int ANSWER;
    public NeuralNetwork(int inputNum, int hiddenNum, int answerNum, int hiddenLayers, Myrandom random){
        INPUT = 0;
        ANSWER = 1+hiddenLayers;

        nodeArray = new ArrayList[hiddenLayers+2];
        //an array of arrayl lists
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
            nodeArray[hiddenLayers+1].add(new Node(hiddenNum));
        }
        for(int layer = 1; layer<nodeArray.length; layer++){
            for(int node = 0; node<nodeArray[layer].size(); node++){
                nodeArray[layer].get(node).bWeight = random.doubleRange(-.05, .05);
                for(int link = 0; link<nodeArray[layer].get(node).links; link++){
                    nodeArray[layer].get(node).linkWeights[link] = random.doubleRange(-.05,.05);//weights init
                }
            }
        }
    }

    public static void learn(ArrayList<Node>[] nodeArray, ArrayList<Input> data, String[] categories, double learningRate, int hiddenLayers){
        int epochs = 0;
        //assigns categories to answer nodes
        for(int i = 0; i<categories.length; i++){
            nodeArray[ANSWER].get(i).category = categories[i];
        }
        while(epochs<100){ //read file in another function
            for(int l = 0; l<data.size(); l++){ //iterates through training examples
                //assigns correct answers to answer nodes based on their category
                for(int i = 0; i<nodeArray[ANSWER].size();i++){
                    if(nodeArray[ANSWER].get(i).category.equals(categories[i])){
                        nodeArray[ANSWER].get(i).correctAnswer = 1;
                    }else{
                        nodeArray[ANSWER].get(i).correctAnswer = 0;
                    }
                }
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
                            //if(layer == 2){
                            //    System.out.printf("ran output on answer %f\n", nodeArray[layer][node].cachedOutput);
                            //}
                        }
                    }
                }
                
                Node brightestNode = nodeArray[ANSWER].get(largestNode(nodeArray));
                double brightness = brightestNode.cachedOutput; //strength of the answer the network is giving us
                if(epochs % 10 == 0){
                    System.out.println("\n-------------------------\n");
                    System.out.printf("Epoch: %d\n", epochs);
                    System.out.printf("Non-Category: %s \nnon-Brightness: %f\n", nodeArray[ANSWER].get(1-largestNode(nodeArray)).category, nodeArray[ANSWER].get(1-largestNode(nodeArray)).cachedOutput);
                    System.out.printf("Category: %s \nBrightness: %f\n", brightestNode.category, brightness);
                    if(brightestNode.category.equals(data.get(l).answer)){
                        System.out.println("Yay ");
                    }
                }
                backPropogate(nodeArray, learningRate, hiddenLayers);
            }
            epochs++;
        }
        System.out.printf("finished with an errsig of %f after %d epochs \n", nodeArray[ANSWER].get(largestNode(nodeArray)).errSig, epochs);
    }

    public static void adjustHiddenWeights(ArrayList<Node>[] nodeArray, double learningRate, int hiddenLayers){
        //errsig and adjusting weights for hidden neurons
        for(int HIDDEN = 1; HIDDEN < hiddenLayers+1;HIDDEN++)
            for(int hidden = 0; hidden<nodeArray[HIDDEN].size();hidden++){ 
                nodeArray[HIDDEN].get(hidden).errSig = 0;//clears errSig from last training example
                for(int answer = 0; answer<nodeArray[ANSWER].size(); answer++){ //loops through answer neurons
                    //Initially nodeArray[ANSWER].get[hidden], changing it to this changed nothing
                    double answerWeight = nodeArray[ANSWER].get(answer).linkWeights[hidden];
                    nodeArray[HIDDEN].get(hidden).errSig += nodeArray[ANSWER].get(answer).errSig * answerWeight; //errSig initial summation for hidden nodeArray
                }
                double hiddenResult = nodeArray[HIDDEN].get(hidden).cachedOutput;
                nodeArray[HIDDEN].get(hidden).errSig *= (hiddenResult)*(1-hiddenResult);
                nodeArray[HIDDEN].get(hidden).adjustWeights(learningRate);
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
            nodeArray[ANSWER].get(answer).computeAnswerErrSignal();//errSig for answer neurons 
        }
        adjustHiddenWeights(nodeArray, learningRate, 1);
        //adjust weight for answer neurons
        for(int answer = 0; answer<nodeArray[ANSWER].size(); answer++){
            nodeArray[ANSWER].get(answer).adjustWeights(learningRate);
        }
    }

    public ArrayList<Node>[] getnodeArray(){
        return nodeArray;
    }
}