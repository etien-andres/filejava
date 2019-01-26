package data_access;

public class AsociadoyTotal {
    String asoc;
    Double total;

    public AsociadoyTotal(String asoc, Double total) {
        this.asoc = asoc;
        this.total = total;
    }

    public String getAsoc() {
        return asoc;
    }

    public void setAsoc(String asoc) {
        this.asoc = asoc;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
