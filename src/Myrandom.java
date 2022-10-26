package src;
import java.util.Random;

public class Myrandom{
    final static Random random = new Random(1234);

    public double doubleRange(double low, double high){
        return low + random.nextDouble()*(high-low);
    }
}