package src;

public class Sensor extends Node{
    //Sensor constructor
    public Sensor(){
        super(0);
    }
    //Sets the cached output to the Input of the sensor
    public void setValue(double val){
        cachedOutput = val;
    }
    //Returns the cached output, so we can call the same output function for every neuron
    @Override
    public double output(){
        return cachedOutput;
    }
}
