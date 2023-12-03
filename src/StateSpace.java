import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StateSpace {

    // map from transition labels to adjacency lists.
    public final HashMap<String, int[][]> adj;
    public final HashMap<String, int[][]> revAdj;

    public final int initial;

    private final HashSet<Integer> S;

    public StateSpace(int nrStates, int initial, HashMap<String, int[][]> adj, HashMap<String, int[][]> revAdj) {

        this.initial = initial;
        this.adj = adj;
        this.revAdj = revAdj;

        S = (HashSet<Integer>) IntStream.rangeClosed(0, nrStates - 1) .boxed() .collect(Collectors.toSet());

    }

    public HashSet<Integer> getS() {
        return (HashSet<Integer>) S.clone();
    }


}
