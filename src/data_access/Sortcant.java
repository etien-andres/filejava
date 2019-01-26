package data_access;

import java.util.Comparator;

public class Sortcant implements Comparator<ProdyCant> {

    @Override
    public int compare(ProdyCant o1, ProdyCant o2) {
        return o2.cant-o1.cant;
    }
}
