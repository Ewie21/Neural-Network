package src;
import java.lang.Comparable;

public class Node implements Comparable<Node>{
    double bWeight = 1;
    //weights
    double[] linkWeights = new double[2];
    //values
    double[] linkVals = new double[2];
    double errSig = 1;
    String category;
    double correctAnswer;
    int sum = 0;

    public double input(){
        for(int i = 0; i<this.linkVals.length;i++){
            sum += (this.linkVals[i]*this.linkWeights[i]);
        }
        //System.out.print(sum+bWeight);
        return sum+bWeight;
    }

    public double output(){
        return sigmoid(input());
    } 

    public void computeAnswerErrSignal(){
        double actualAnswer = this.output();
        this.errSig = (this.correctAnswer - actualAnswer)*(actualAnswer)*(1-actualAnswer);
    }

    public void adjustWeights(double learningRate){
            this.bWeight += this.errSig*learningRate;
            for(int i = 0; i<this.linkVals.length;i++){ //loops through links
                //System.out.println("\nweight\n");
                //System.out.print(linkWeights[i]);
                //System.out.printf("\n val: %f \n", linkVals[i]);

                double weight = this.linkWeights[i] + (this.errSig * this.linkVals[i] * learningRate);//this is equal to 0 for some reason
                //System.out.println("\n Err: ");
                //System.out.print(errSig);
                this.linkWeights[i] = weight; //adjusts link weights
                //System.out.println("weight: \n");
                //System.out.print(linkWeights[i]);
            }
    }

    public double sigmoid(double x){
        return 1.0/(1.0+Math.pow(Math.E, -x));
    }

    @Override public int compareTo(Node node){
       double output = this.output();
       double oOutput = node.output();
        if(output == oOutput){
            return 0;
        }else if(output<oOutput){
            return 1;
        }else{
            return -1;
        }
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