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

    public Node(int links){
        this.links = links;
        linkWeights = new double[links];
        linkVals = new double[links];
    }

    public double input(){
        double sum = 0;
        for(int i = 0; i<links;i++){
            sum += (this.linkVals[i]*this.linkWeights[i]);
        }
        return sum+bWeight;
    }

    public double output(){
        cachedOutput = sigmoid(input());
        return cachedOutput;
    } 

    public void computeAnswerErrSignal(){
        //System.out.println(errSig);
        this.errSig = (this.correctAnswer - cachedOutput)*(cachedOutput)*(1-cachedOutput);
        //System.out.println(errSig);
    }

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

    public double sigmoid(double x){
        return 1.0/(1.0+Math.exp(-x));
    }
}