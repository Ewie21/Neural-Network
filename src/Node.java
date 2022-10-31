package src;

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
                //System.out.println("\nweight\n");
                //System.out.println(linkWeights[link]);
                //System.out.printf("\n val: %f \n", linkVals[link]);
                //System.out.println("\n Err: ");
                //System.out.println(errSig);
                this.linkWeights[link] += this.errSig * this.linkVals[link] * learningRate;; //adjusts link weights
                //System.out.println("weight: \n");
                //System.out.println(linkWeights[link]);
            }
    }

    public double sigmoid(double x){
        return 1.0/(1.0+Math.exp(-x));
    }
}