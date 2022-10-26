package src;
import java.lang.Comparable;

public class Node{
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
        //System.out.print(sum+bWeight);
        return sum+bWeight;
    }

    public double output(){
        cachedOutput = sigmoid(input());
        return cachedOutput;
    } 

    public void computeAnswerErrSignal(){
        double actualAnswer = this.cachedOutput;
        this.errSig = (this.correctAnswer - actualAnswer)*(actualAnswer)*(1-actualAnswer);
    }

    public void adjustWeights(double learningRate){
            this.bWeight += this.errSig*learningRate;
            for(int link = 0; link<links;link++){ //loops through links
                //System.out.println("\nweight\n");
                //System.out.print(linkWeights[i]);
                //System.out.printf("\n val: %f \n", linkVals[i]);
                double weight = this.errSig * this.linkVals[link] * learningRate;//this is equal to 0 for some reason
                //System.out.println("\n Err: ");
                //System.out.print(errSig);
                this.linkWeights[link] += weight; //adjusts link weights
                //System.out.println("weight: \n");
                //System.out.print(linkWeights[i]);
            }
    }

    public double sigmoid(double x){
        return 1.0/(1.0+Math.exp(-x));
    }


    public double getbWeight() {
        return bWeight;
    }

    public double[] getLinkWeights() {
        return linkWeights;
    }

    public double[] getLinkVals() {
        return linkVals;
    }

    public double getErrSig() {
        return errSig;
    }

    public String getCategory() {
        return category;
    }

    public double getCorrectAnswer() {
        return correctAnswer;
    }
}