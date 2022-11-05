package src;
import java.util.Random;

public class Myrandom{
    final static Random random = new Random();
    //Returns a double in range between high and low
    public double doubleRange(double low, double high){
        return low + random.nextDouble()*(high-low);
    }
}