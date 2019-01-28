package data_access;

import data_access.javaToPdf.Movimientos;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class Datos_deVenta {
    double combosYotros,bebidas,alimentos,total,fondostot, efectivo,grantotal,tarjeta, retiros,ventas_suc,ventas_rapi,ventas_uber,propTarjet,propEfect, cancel,descuents;
    ArrayList<ProdyCant> prodyCants;
    ArrayList<AsociadoyTotal> asociadoyTotals;
    ArrayList<Movimientos> movimientosSucurs;
    ArrayList<Movimientos> movimientosRappi;
    ArrayList<Movimientos> movimientosUber;
    ArrayList<Movimientos> movimientostotal;

    SortedSet<String> tickets=new TreeSet<>();


    int comens;

    public Datos_deVenta(double descuents,double combosYotros,double bebidas,double alimentos,double cancel ,int comens,double propTarjet,double propEfec,double total, double fondostot,double efectivo,double tarjeta, double grantotal,double retiros,double ventas_suc,double ventas_rapi,double ventas_uber, ArrayList<ProdyCant> prodyCants, ArrayList<AsociadoyTotal> asociadoyTotals,ArrayList<Movimientos> movimientosSucurs,ArrayList<Movimientos> movimientosRappi,ArrayList<Movimientos> movimientosUber, ArrayList<Movimientos> movimientostotal,  SortedSet<String> tickets) {
        this.total = total;
        this.fondostot = fondostot;
        this.prodyCants = prodyCants;
        this.asociadoyTotals = asociadoyTotals;
        this.efectivo=efectivo;
        this.tarjeta=tarjeta;
        this.grantotal=grantotal;
        this.retiros=retiros;
        this.movimientosRappi=movimientosRappi;
        this.movimientosSucurs=movimientosSucurs;
        this.movimientosUber=movimientosUber;
        this.ventas_suc=ventas_suc;
        this.ventas_rapi=ventas_rapi;
        this.ventas_uber=ventas_uber;
        this.propEfect=propEfec;
        this.propTarjet=propTarjet;
        this.comens=comens;
        this.cancel=cancel;
        this.combosYotros=combosYotros;
        this.bebidas=bebidas;
        this.alimentos=alimentos;
        this.descuents=descuents;
        this.movimientostotal=movimientostotal;
        this.tickets=tickets;
    }
}
