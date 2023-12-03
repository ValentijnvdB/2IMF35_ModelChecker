import java.util.*;

public class StateSpaceParser {

    public static StateSpace parseStates(Scanner input) {

        // parse first line
        String line = input.nextLine();
        String[] parts = line.split("[^\\d]+");
        int initial = Integer.parseInt(parts[1]);
        int nrTransitions = Integer.parseInt(parts[2]);
        int nrStates = Integer.parseInt(parts[3]);

        HashMap<String, HashSet<Integer>[]> adj = new HashMap<>();
        HashMap<String, HashSet<Integer>[]> revAdj = new HashMap<>();

        int src, tgt;
        String lbl;
        while (input.hasNext()) {
            // get and parse next line
            line = input.nextLine();
            parts = line.split("[^\\d\\w]+");

            src = Integer.parseInt(parts[1]);
            lbl = parts[2];
            tgt = Integer.parseInt(parts[3]);

            // check if label exists, if not initialize array and sets
            if (!adj.containsKey(lbl)) {
                // adjacency list
                adj.put(lbl, new HashSet[nrStates]);
                for (int i = 0; i < nrStates; i++) {
                    adj.get(lbl)[i] = new HashSet<>();
                }

                // reverse adjacency list
                revAdj.put(lbl, new HashSet[nrStates]);
                for (int i = 0; i < nrStates; i++) {
                    revAdj.get(lbl)[i] = new HashSet<>();
                }
            }

            // add new edge
            adj.get(lbl)[src].add(tgt);
            revAdj.get(lbl)[tgt].add(src);
        }

        //printSets(initial, nrTransitions, nrStates, adj, revAdj);

        // convert sets to arrays
//        HashMap<String, int[][]> adjAsArray = toDblArray(nrStates, adj);
//        HashMap<String, int[][]> revAdjAsArray = toDblArray(nrStates, revAdj);

        //printArrays(initial, nrTransitions, nrStates, adjAsArray, revAdjAsArray);

        return new StateSpace(nrStates, initial, adj, revAdj);
    }


    /**
     * Convert array of sets to array of arrays
     */
    private static HashMap<String, int[][]> toDblArray(int nrStates, HashMap<String, Set<Integer>[]> in) {
        HashMap<String, int[][]> out = new HashMap<>();

        for (String lbl : in.keySet() ) {
            Set<Integer>[] trans = in.get(lbl);

            int[][] asArray = new int[nrStates][];
            out.put(lbl, asArray);

            for (int src = 0; src < trans.length; src++) {
                asArray[src] = new int[trans[src].size()];
                int index = 0;
                for (int tgt : trans[src]) {
                    asArray[src][index] = tgt;
                    index++;
                }

            }
        }

        return out;
    }

    //region DEBUG

    // Debug method
    private static void printSets(int initial, int nrTransitions, int nrStates,  HashMap<String, Set<Integer>[]> adj, HashMap<String, Set<Integer>[]> revAdj) {
        System.out.println("ADJACENCY LIST");
        System.out.println("des (" + initial + ", " + nrTransitions + ", " + nrStates + ")");

        for (String lbl : adj.keySet() ) {
            Set<Integer>[] trans = adj.get(lbl);

            for (int src = 0; src < trans.length; src++) {
                Set<Integer> tgts = trans[src];

                for (int tgt : tgts) {
                    System.out.println("(" + src + ", " + lbl + ", " + tgt + ")");
                }
            }
        }

        System.out.println("REVERSE ADJACENCY LIST");
        System.out.println("des (" + initial + ", " + nrTransitions + ", " + nrStates + ")");

        for (String lbl : revAdj.keySet() ) {
            Set<Integer>[] trans = revAdj.get(lbl);

            for (int tgt = 0; tgt < trans.length; tgt++) {
                Set<Integer> srcs = trans[tgt];

                for (int src : srcs) {
                    System.out.println("(" + src + ", " + lbl + ", " + tgt + ")");
                }
            }
        }
    }

    // Debug method
    private static void printArrays(int initial, int nrTransitions, int nrStates,  HashMap<String, int[][]> adj, HashMap<String, int[][]> revAdj) {
        System.out.println("ADJACENCY LIST");
        System.out.println("des (" + initial + ", " + nrTransitions + ", " + nrStates + ")");

        for (String lbl : adj.keySet() ) {
            int[][] trans = adj.get(lbl);

            for (int src = 0; src < trans.length; src++) {
                int[] tgts = trans[src];

                for (int tgt : tgts) {
                    System.out.println("(" + src + ", " + lbl + ", " + tgt + ")");
                }
            }
        }

        System.out.println("REVERSE ADJACENCY LIST");
        System.out.println("des (" + initial + ", " + nrTransitions + ", " + nrStates + ")");

        for (String lbl : revAdj.keySet() ) {
            int[][] trans = revAdj.get(lbl);

            for (int tgt = 0; tgt < trans.length; tgt++) {
                int[] srcs = trans[tgt];

                for (int src : srcs) {
                    System.out.println("(" + src + ", " + lbl + ", " + tgt + ")");
                }
            }
        }
    }

    //endregion

}
