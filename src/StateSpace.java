import java.util.HashMap;

public class StateSpace {

    // map from transition labels to adjacency lists.
    public final HashMap<String, int[][]> adj;
    public final HashMap<String, int[][]> revAdj;

    public final int initial;

    public StateSpace(int initial, HashMap<String, int[][]> adj, HashMap<String, int[][]> revAdj) {

        this.initial = initial;
        this.adj = adj;
        this.revAdj = revAdj;

    }


}
