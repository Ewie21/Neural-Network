package src;

import java.io.Serializable;

public class Node implements Serializable{
    double bWeight;
    //weights
    int links;
    double[] linkWeights;
    //values
    double[] linkVals;
    double errSig;
    String category;
    double correctAnswer;
    double cachedOutput;
    //Node constructor
    public Node(int links){
        this.links = links;
        linkWeights = new double[links];
        linkVals = new double[links];
    }
    //Finds the total input of a neuron
    public double input(){
        double sum = 0;
        for(int i = 0; i<links;i++){
            sum += (this.linkVals[i]*this.linkWeights[i]);
        }
        return sum+bWeight;
    }
    //Runs the total input through an activation function
    public double output(){
        cachedOutput = sigmoid(input());
        return cachedOutput;
    } 
    //Computes the error signal for an answers neuron
    public void computeAnswerErrSignal(){
        if(Main.DEBUG) System.out.println(errSig);
        this.errSig = (this.correctAnswer - cachedOutput)*(cachedOutput)*(1-cachedOutput);
        //these last two terms are the derivative of the sigmoid function.
        //this is important to us because it makes the errsig higher the closer our output is to 1, making it choose a side(-,+).
        if(Main.DEBUG) System.out.println(errSig);
    }
    //Adjusts the weights of a neuron
    public void adjustWeights(double learningRate){
            this.bWeight += this.errSig*learningRate;
            for(int link = 0; link<links;link++){ //loops through links
                if(Main.DEBUG) System.out.println("\nInitial Weight\n" + linkWeights[link]);
                if(Main.DEBUG) System.out.printf("\n Link Value: %f \n", linkVals[link]);
                if(Main.DEBUG) System.out.println("\n Err: " + errSig);
                this.linkWeights[link] += (this.errSig * this.linkVals[link] * learningRate); //adjusts link weights
                if(Main.DEBUG) System.out.println("Adjusted Weight: \n" + linkWeights[link]);
            }
    }
    //Sigmoid activation function
    public double sigmoid(double x){
        return 1.0/(1.0+Math.exp(-x));
    }
}