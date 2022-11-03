package src;

import java.io.Serializable;
import java.util.*;

public class Model implements Serializable {
    public Model(int hiddenLayers, double[][][] linkWeights, double[][] bWeights){
        ArrayList<Node>[] nodeArray =  new ArrayList[hiddenLayers+2];
        for(int layer = 1; layer<nodeArray.length;layer++){
            for(int node = 0; node<nodeArray[layer].size();node++){
                nodeArray[layer].get(node).bWeight = bWeights[layer][node];
                nodeArray[layer].get(node).linkWeights = linkWeights[layer][node];
            }
        }
    }
}
