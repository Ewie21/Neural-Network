package src;

public class Sensor extends Node {

    public Sensor(){
        super(0);
    }

    public void setValue(double val){
        cachedOutput = val;
    }

    @Override
    public double output(){
        return cachedOutput;
    }
}
