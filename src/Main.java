package src;
import java.util.*;
import SimpleFile.SimpleFile;

//maybe the sorting of node[2] is screwing with what their links 
//because the links refer to the index of the nodes so switching around the indexes 4 times an epoch might screw with the links.

public class Main{
    final static Myrandom random = new Myrandom();
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
            nodes[1][i] = new Node();
        }
        for(int i = 0; i<answerNum; i++){
            nodes[2][i] = new Node();
        }
        for(int i = 1; i<nodes.length; i++){
            for(int j = 1; j<nodes[i].length; j++){
                for(int k = 0; k<2; k++){
                    //weights init
                    nodes[i][j].linkWeights[k] = random.doubleRange(-.5,.5);
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
            ArrayList<Integer> lineInputs = new ArrayList<>(Arrays.asList(Integer.parseInt(initInput.get(0).split(" ")[0]), Integer.parseInt(initInput.get(0).split(" ")[1])));
            Input input = new Input(lineInputs, Double.parseDouble(initInput.get(1)));
            inputs.add(input);
        }
        return inputs;
    }

    public static void learn(Node[][] nodeArray, ArrayList<Input> data, String[] categories, int[] answers, double learningRate){
        double actualAnswer;
        int epochs = 0;
        double sum = 0;
        int n = 0;
        for(int i = 0; i<nodeArray[2].length; i++){
            sum+=nodeArray[2][i].errSig;
            n++;
        }
        double averageErr = sum/n;
        while(epochs<100){ //read file in another function
            for(int l = 0; l<data.size(); l++){
                for(int i = 0; i<categories.length; i++){
                    nodeArray[2][i].category = categories[i];
                }
                if(answers[0] == data.get(l).answer){
                    nodeArray[2][0].correctAnswer = answers[0];
                    for(int i = 1; i<nodeArray[2].length;i++){
                        nodeArray[2][i].correctAnswer = answers[i];
                    }
                }else{
                    for(int i = 0; i<nodeArray[2].length;i++){
                        nodeArray[2][i].correctAnswer = answers[nodeArray[2].length - 1 - i];
                    }
                }
                //input layers
                for(int i = 0; i<nodeArray[0].length; i++){//i = current input index
                    //if(nodeArray[0][i].linkVals.length == nodeArray[0][i].linkWeights.length){
                    //    break;
                    //}
                    Double input = Double.valueOf(data.get(l).inputs.get(i).intValue());
                    nodeArray[0][i].linkVals[0] = input;
                }
                
                //hidden and output layers
                 for(int layer = 1; layer<nodeArray.length; layer++){ //height of the array
                    //if(nodeArray[2][1].linkVals.length == nodeArray[2][1].linkWeights.length){
                    //    break;
                    //}
                    for(int j = 0; j<nodeArray[layer].length; j++){ // length of the array
                        //if(nodeArray[2][1].linkVals.length == nodeArray[2][1].linkWeights.length){
                        //    break;
                        //}
                        for(int k = 0; k<nodeArray[layer-1].length; k++){ //previous layer
                            nodeArray[layer][j].linkVals[k] = nodeArray[layer - 1][k].output();
                        }
                    }
                 }

                Arrays.sort(nodeArray[2]);
                actualAnswer = nodeArray[2][1].output(); //answer the network is giving us
                backPropogate(actualAnswer, nodeArray, learningRate);
                if(epochs % 10 == 0){
                    System.out.printf("Epoch: %d\n", epochs);
                    System.out.printf("Category: %s \nPrediction: %f\n", nodeArray[2][1].category, actualAnswer);
                    if(nodeArray[2][1].category == String.valueOf(data.get(l).answer)){
                        System.out.println("Yay ");
                    }
                }
            }
            epochs++;
        }
        System.out.printf("finished with an errsig of %f after %d epochs \n", nodeArray[2][1].errSig, epochs);
    }
        

    public static void testNetworkAND(Node[][] nodeArray, double learningRate){
        String[] categories = {"1", "0"};
        ArrayList<Input> data = ANDFile();
        int[] answers = {1, 0};
        learn(nodeArray, data, categories, answers, learningRate);

    }

    public static void backPropogate(double actualAnswer, Node[][] nodeArray, double learningRate){
        for(int i = 0; i<nodeArray[2].length;i++){
            nodeArray[2][i].computeAnswerErrSignal();//errSig for answer neurons 
           }
           
        for(int h = 0; h<nodeArray[1].length;h++){ //loops through hidden neurons; variable names
            for(int a = 0; a<nodeArray[2].length; a++){ //loops through answer neurons
                nodeArray[2][a].adjustWeights(learningRate);
                double answerWeight = nodeArray[2][a].linkWeights[h];
                nodeArray[1][h].errSig += nodeArray[2][a].errSig * answerWeight; //errSig initial summation for hidden nodes
                
            }   
            double hiddenResult = nodeArray[1][h].output();
            nodeArray[1][h].errSig *= (hiddenResult)*(1-hiddenResult);
            nodeArray[1][h].adjustWeights(learningRate);
        }
    } 
}