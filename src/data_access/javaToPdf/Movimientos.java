package data_access.javaToPdf;

public class Movimientos {
    String fecha,descrip,asociado,hora,mesaventa, id_Mov;
    Double importe,precio,cancela;
    Integer cant,cantcanel;

    public Movimientos(String fecha, String descrip, String asociado, String hora, String mesaventa, Double importe, Double precio, Double cancela, String id_Mov, Integer cant) {
        this.fecha = fecha;
        this.descrip = descrip;
        this.asociado = asociado;
        this.hora = hora;
        this.mesaventa = mesaventa;
        this.importe = importe;
        this.precio = precio;
        this.cancela = cancela;
        this.id_Mov = id_Mov;
        this.cant = cant;
    }

    public Integer getCantcanel() {
        return cantcanel;
    }

    public void setCantcanel(Integer cantcanel) {
        this.cantcanel = cantcanel;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getAsociado() {
        return asociado;
    }

    public void setAsociado(String asociado) {
        this.asociado = asociado;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMesaventa() {
        return mesaventa;
    }

    public void setMesaventa(String mesaventa) {
        this.mesaventa = mesaventa;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getCancela() {
        return cancela;
    }

    public void setCancela(Double cancela) {
        this.cancela = cancela;
    }

    public String getId_Mov() {
        return id_Mov;
    }

    public void setId_Mov(String id_Mov) {
        this.id_Mov = id_Mov;
    }

    public Integer getCant() {
        return cant;
    }

    public void setCant(Integer cant) {
        this.cant = cant;
    }
}
