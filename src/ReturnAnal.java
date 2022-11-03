package src;

public class ReturnAnal {
    double count;
    double sum;
    Node brightestNode;
    double brightness;
    public ReturnAnal(double count, double sum, Node brightestNode, double brightness){
        this.count = count;
        this.sum = sum;
        this.brightestNode = brightestNode;
        this.brightness = brightness;
    }

    public double getCount(){
        return count;
    }

    public double getSum(){
        return sum;
    }

    public Node getBrightestNode(){
        return brightestNode;
    }

    public double getBrightness(){
        return brightness;
    }
}
