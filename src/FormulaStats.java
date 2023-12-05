import MuFormula.BoundBy;

public class FormulaStats {

    private static int nd;

    private static int ad;
    private static BoundBy lastSeen;

    private static int dad;

    public static void reset() {
        nd = 0;
        ad = 0;
        dad = 0;
    }

    public static void setND(int newND) {
        nd = Integer.max(newND, nd);
    }

    public static void updateAD(BoundBy fp) {
        if (fp != lastSeen) {
            ad++;
        }
    }

}
