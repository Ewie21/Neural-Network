package src;

public class Sensor extends Node {

    public Sensor(){
        super(1);
    }

    @Override
    public double output() {
        cachedOutput = linkVals[0];
        return cachedOutput;
    }
}
