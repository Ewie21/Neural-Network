//Object that serves purely as a single purpose tuple
package src;

public class ReturnAnal {
    double count;
    double sum;
    Node brightestNode;
    double brightness;
    //Return Analysis constructor
    public ReturnAnal(double count, double sum, Node brightestNode, double brightness){
        this.count = count;
        this.sum = sum;
        this.brightestNode = brightestNode;
        this.brightness = brightness;
    }
    //Count getter
    public double getCount(){
        return count;
    }
    //Sum getter
    public double getSum(){
        return sum;
    }
    //Brightest node getter
    public Node getBrightestNode(){
        return brightestNode;
    }
    //Brightness getter
    public double getBrightness(){
        return brightness;
    }
}
