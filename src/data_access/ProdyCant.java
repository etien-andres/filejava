package data_access;

public class ProdyCant {

    String prod;
    int cant;
    double venta;

    public ProdyCant(String prod, int cant, double venta) {
        this.prod = prod;
        this.cant = cant;
        this.venta=venta;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public int getCant() {
        return cant;
    }

    public void setCant(int cant) {
        this.cant = cant;
    }


}
