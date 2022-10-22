package src;
import java.util.*;
import SimpleFile.SimpleFile;
//CHECK SORTING!!! PERHAPS MESSING UP THERE!!!



public class Main{
    final static Myrandom random = new Myrandom();
    public static void main(String[] args){
        Node[][] nodeArray = createNetwork(2,2,2);
        testNetworkAND(nodeArray, 1.5);
    }
    public static Node[][] createNetwork(int inputNum, int hiddenNum, int answerNum){
        Node[][] nodes = new Node[3][2];
        for(int i = 0; i<inputNum; i++){
            nodes[0][i] = new Node();
        }
        for(int i = 0; i<hiddenNum; i++){
            nodes[1][i] = new Node();
        }
        for(int i = 0; i<answerNum; i++){
            nodes[2][i] = new Node();
        }
        for(int i = 0; i<nodes.length; i++){
            for(int j = 0; j<nodes[i].length; j++){
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
        while(averageErr>0.1){ //read file in another function
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
                for(int i = 0; i<nodeArray[0].length; i++){
                    if(nodeArray[0][i].linkVals.length == nodeArray[0][i].linkWeights.length){
                        break;
                    }
                    for(int j = 0; j<data.get(l).inputs.size(); j++){
                        Double input = Double.valueOf(data.get(l).inputs.get(j).intValue());
                        nodeArray[0][i].linkVals[j] = input;
                    }
                }
                //HARD CODED
                //hidden layer
                for(int i = 0; i<nodeArray[1].length;i++){//iterate through nodes
                    for(int j = 0; j<nodeArray[0].length;j++){//iterate through links
                        nodeArray[1][i].linkVals[j] = nodeArray[0][j].output();
                    }
                }
                //output layer
                for(int i = 0; i<nodeArray[2].length;i++){
                    for(int j = 0; j<nodeArray[1].length;j++){
                        nodeArray[2][i].linkVals[j] = nodeArray[1][j].output();
                    }
                }

                /* DYNAMIC
                 for(int i = 1; i<3; i++){ //height of the array
                    if(nodeArray[2][1].linkVals.length == nodeArray[2][1].linkWeights.length){
                        break;
                    }
                    for(int j = 0; j<nodeArray[i].length; j++){ // length of the array
                        if(nodeArray[2][1].linkVals.length == nodeArray[2][1].linkWeights.length){
                            break;
                        }
                        for(int k = 0; k<nodeArray[i-1].length; k++){ //previous layer
                            nodeArray[i][j].linkVals[k] = nodeArray[i - 1][k].output();
                        }
                    }
                }
                */
                //hidden and output layers
                

                Arrays.sort(nodeArray[2]);
                actualAnswer = nodeArray[2][0].output(); //answer the network is giving us
                backPropogate(actualAnswer, nodeArray, learningRate);
                if(epochs % 100 == 0){
                    System.out.printf("Epoch: %d\n", epochs);
                    System.out.printf("Category: %s \nPrediction: %f\n", nodeArray[2][0].category, actualAnswer);
                    if(Math.abs((actualAnswer - nodeArray[2][0].correctAnswer)) < .01){
                        System.out.println("Yay ");
                    }
                }
                
            }
            epochs++;
        }
        System.out.printf("finished with an errsig of %f after %d epochs \n", nodeArray[2][0].errSig, epochs);
    }

    public static void testNetworkAND(Node[][] nodeArray, double learningRate){
        String[] categories = {"1", "0"};
        ArrayList<Input> data = ANDFile();
        int[] answers = {1, 0};
        learn(nodeArray, data, categories, answers, learningRate);

    }

    public static void backPropogate(double actualAnswer, Node[][] nodeArray, double learningRate){
        for(int i = 0; i<nodeArray[2].length;i++){
            nodeArray[2][i].computeAnswerErrSignal();//errSig for answer neurons; the actual anser is either the output of the individual neuron or the highest neuron
           }
        //this code assumes that the link values and the hidden nodes are in the same order
        for(int h = 0; h<nodeArray[1].length;h++){ //loops through hidden neurons; variable names
            for(int a = 0; a<nodeArray[2].length; a++){ //loops through answer neurons
                double answerWeight = nodeArray[2][a].linkWeights[h];
                nodeArray[1][h].errSig += nodeArray[2][a].errSig * answerWeight; //errSig modification for answer neurons
                nodeArray[2][a].adjustWeights(learningRate);
            }
            double hiddenResult = nodeArray[1][h].output();
            nodeArray[1][h].errSig *= (hiddenResult)*(1-hiddenResult);
            nodeArray[1][h].adjustWeights(learningRate);
        }

    } 
}