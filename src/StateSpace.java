import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StateSpace {

    // map from transition labels to adjacency lists.
    public final HashMap<String, HashSet<Integer>[]> adj;
    public final HashMap<String, HashSet<Integer>[]> revAdj;

    public final int initial;

    private final int nrStates;

    public StateSpace(int nrStates, int initial, HashMap<String, HashSet<Integer>[]> adj, HashMap<String, HashSet<Integer>[]> revAdj) {

        this.initial = initial;
        this.adj = adj;
        this.revAdj = revAdj;
        this.nrStates = nrStates;

    }

    public HashSet<Integer> getS() {
        return (HashSet<Integer>) IntStream.rangeClosed(0, nrStates - 1) .boxed() .collect(Collectors.toSet());
    }


}
