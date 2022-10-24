package src;

public class Sensor extends Node {
    
    @Override
    public double output() {
        return linkVals[0];
    }
}
