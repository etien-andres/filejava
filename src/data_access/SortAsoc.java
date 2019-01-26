package data_access;

import java.util.Comparator;

public class SortAsoc implements Comparator<AsociadoyTotal> {
    @Override
    public int compare(AsociadoyTotal o1, AsociadoyTotal o2) {
        return Math.toIntExact(Math.round(o2.total)-Math.round(o1.total));
    }
}
