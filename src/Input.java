//Object that stores the inputs and answer for a single training exmaple
package src;
import java.util.*;
public class Input {
    ArrayList<Double> inputs;
    String answer;
    //Inputs constructor
    public Input(ArrayList<Double> inputs, String answer){
        this.inputs = inputs;
        this.answer = answer;
    }
}