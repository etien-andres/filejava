package data_access;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import data_access.javaToPdf.Movimientos;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class Conexion {
    public static ArrayList<String> datosFiscales=new ArrayList<>();


    public static JPanel panel=new JPanel();
    public static JTextField fecha_inittxt=new JTextField();
    public static JTextField fecha_fintxt=new JTextField();


    public static void main(String[] args) {
        Double total=0D,fondostot=0D,efectivon=0D,tarjeta=0D, retirosn=0D,cajatot=0D,propEfect=0d,propTarj=0d, ventasSucur=0d,ventasRappi=0d,ventasUber=0d,cancelaciones=0d,combosYotros=0d,bebidas=0d,alimentos=0d,descuentos=0d;
        int comens=0,cantcancel=0;
        ArrayList<String> fechs=new ArrayList<>();

        panel.add(fecha_inittxt);


        ArrayList<Movimientos> movimientosSucurs=new ArrayList<>();
        ArrayList<Movimientos> movimientosRappi=new ArrayList<>();
        ArrayList<Movimientos> movimientosUber=new ArrayList<>();
        ArrayList<Movimientos> movimientosCancel=new ArrayList<>();
        ArrayList<Movimientos> totalmovimientos=new ArrayList<>();



        String fechaventainit="11/11/18";
        String fechaventafinal="11/11/18";
        fechs.add(fechaventainit);
        fechs.add(fechaventafinal);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        NumberFormat nf = NumberFormat.getNumberInstance();

        DecimalFormat df =  new DecimalFormat("",otherSymbols);
        df.setMaximumFractionDigits(2);



        ArrayList<AsociadoyTotal> asociadoyTotals=new ArrayList<>();

        ArrayList<ProdyCant> prductos_concant=new ArrayList<>();
        try {
            Driver d=(Driver)Class.forName("com.filemaker.jdbc.Driver").newInstance();
        }catch (Exception e){
            System.out.println("error "+e.getMessage());
        }
        SortedSet<String> productos=new TreeSet<>();
        SortedSet<String> usuarios=new TreeSet<>();
        SortedSet<String> tickets=new TreeSet<>();


        String jdburlcaja="jdbc:filemaker://localhost/caja";
        String jdburlprod="jdbc:filemaker://localhost/productos";

        Connection con=null;
        try {
            con = DriverManager.getConnection(jdburlcaja,"sirka","3v-S1r;k4");
            Statement statement=con.createStatement();

            ResultSet rs=statement.executeQuery("select  \"Producto Descripcion\",\"Importe CDesc\",\"Cantidad Venta\", \"Nombre Asociado\", \"Fecha Venta Periodo Activo\",\"Hora Creacion\",\"ID movimiento\",\"Mesa Venta\",\"Importe Cancela\", \"Precio Unitario SDesc\",BanderaProductoImpresora, \"Descuento Pesos\",\"Cantidad Cancela\" from movimientos  where \"Producto Descripcion\" not in  ('Fondo de Caja','Botana de la Casa','Retiro Efectivo CParcial') and \"Fecha Venta Periodo Activo\" >='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' order by \"ID movimiento\"");

            if (rs.first()){
                do {

                    String impresora="";
                    String nombre=rs.getString(4);
                    String Descrip=rs.getString(1);
                    Double importe=rs.getDouble(2);
                    impresora=rs.getString(11);
                    double descs=rs.getDouble(12);
                    double cance=rs.getDouble(9);
                    Movimientos mov=new Movimientos(rs.getDate(5).toString(),Descrip,nombre,rs.getTime(6).toString(),rs.getString(8),importe,rs.getDouble(10),rs.getDouble(9),rs.getString(7),rs.getInt(3));
                    totalmovimientos.add(mov);
                    if (cance>0){
                        mov.setCantcanel(rs.getInt(13));
                        movimientosCancel.add(mov);
                    }
                    cantcancel+=rs.getInt(13);

                    descuentos+=descs;
                    total+=importe;
                    Integer cant=rs.getInt(3);
                    String idmov=rs.getString(7);

                    if (impresora==null){
                        combosYotros+=importe;
                    }
                    else if (impresora.toUpperCase().contains("BARRA")){
                        bebidas+=importe;
                    }
                    else if (impresora.toUpperCase().contains("COCINA")){
                        alimentos+=importe;
                    }
                    cancelaciones+=rs.getDouble(9);
                    String mesa=rs.getString(8);

                    if (mesa.toUpperCase().contains("UBER")){
                        ventasUber+=importe;
                        movimientosUber.add(mov);
                    }
                    else if (mesa.toUpperCase().contains("RAPPI")){
                        ventasRappi+=importe;
                        movimientosRappi.add(mov);
                    }else {
                        ventasSucur+=importe;
                        movimientosSucurs.add(mov);
                    }

                    tickets.add(idmov);




                    productos.add(Descrip);
                    usuarios.add(nombre);


                }while (rs.next());
                //statement.close();
                //rs.close();
            }
            statement.close();
            rs.close();

            for (String a:productos){
                Statement sts=con.createStatement();
                ResultSet rss=sts.executeQuery("SELECT \"Cantidad Venta\",\"Importe CDesc\" from movimientos where  \"Producto Descripcion\"='"+a+"' and \"Fecha Venta Periodo Activo\">='"+fechaventainit+"'  and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' order by \"Producto Descripcion\"");
                int x=0;
                double y=0D;
                if (rss.first()){
                    do {
                       x+=rss.getInt(1);
                       y+=rss.getDouble(2);
                    }while (rss.next());
                }
                //System.out.println(a+" "+x);
                ProdyCant prodycant=new ProdyCant(a,x,y);
                prductos_concant.add(prodycant);
                sts.close();
                rss.close();
            }


            for (String b:usuarios){
                Statement sts=con.createStatement();
                ResultSet rss=sts.executeQuery("SELECT \"Importe CDesc\" from movimientos where  \"Nombre Asociado\"='"+b+"' and \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' order by \"Producto Descripcion\"");
                double y=0D;
                if (rss.first()){
                    do {
                        y+=rss.getDouble(1);
                    }while (rss.next());
                }
                //System.out.println(b);

                AsociadoyTotal asociadoyTotal=new AsociadoyTotal(b,y);
                asociadoyTotals.add(asociadoyTotal);

                sts.close();
                rss.close();
            }

            Statement stst=con.createStatement();
            ResultSet resultadoDatosfisc=stst.executeQuery("select \"Nombre Comercial\",\"Razon Social\",Sucursal,RFC,Domicilio,Colonia,Ciudad,\"Codigo Postal\",Estado,Telefono from registro_caja ");

            if (resultadoDatosfisc.first()){
                do {
                    for (int i = 1; i <11 ; i++) {
                        datosFiscales.add(resultadoDatosfisc.getString(i));
                        System.out.println(resultadoDatosfisc.getString(i));
                    }
                }while (resultadoDatosfisc.next());
            }
            stst.close();
            resultadoDatosfisc.close();
            stst=con.createStatement();
            ResultSet fondostotal=stst.executeQuery("select \"Importe CDesc\" from movimientos where \"Producto Descripcion\" ='Fondo de Caja' and \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' ");
            if (fondostotal.first()){
                do {
                    fondostot+=fondostotal.getDouble(1);
                }while (fondostotal.next());
            }
            //System.out.println("fondos tot $"+fondostot);
            fondostotal.close();
            ResultSet efectivo=stst.executeQuery("select \"Pago Total\",\"Cambio\",\"Pago Tarjeta\",PropinaEfectivo,PropinaTarjeta,ComensalesCantidad from VentasCaja where \"Estatus Venta\"='Pagada' and \"Fecha Venta Periodo Activo\" >= '"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' ");
            if (efectivo.first()){
                do {
                    System.out.println(efectivo.getDouble(1)+" "+efectivo.getDouble(2)+" "+efectivo.getDouble(3));
                    efectivon+=(efectivo.getDouble(1)-efectivo.getDouble(2)-efectivo.getDouble(3));
                    tarjeta+=efectivo.getDouble(3);
                    comens+=efectivo.getInt(6);

                }while (efectivo.next());
            }
            efectivo.close();
            ResultSet retiros=stst.executeQuery("select \"Importe CDesc\" from movimientos  where \"Producto Descripcion\" ='Retiro Efectivo CParcial' and \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"'");
            if (retiros.first()){
                do {
                    retirosn+=retiros.getDouble(1);
                }while (retiros.next());
            }
            retiros.close();
            ResultSet propinas=stst.executeQuery("select PropinaEfectivo,PropinaTarjeta from VentasCaja where \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' ");
            if (propinas.first()){
                do {
                    propEfect+=propinas.getDouble(1);
                    propTarj+=propinas.getDouble(2);
                }while (propinas.next());
            }



            cajatot+=+efectivon;
            System.out.println("Efectivo en caja "+(cajatot+propEfect));
            System.out.println("fondo "+fondostot);
            System.out.println("ventas Efectivo: "+ efectivon);
            System.out.println("Tarjeta: "+ tarjeta);
            System.out.println("Propinas "+propTarj+" "+propEfect);
            System.out.println("Retiros: "+ retirosn);
            System.out.println("Voucher Total: "+(tarjeta+propTarj));

            System.out.println("ventas Rapi "+ventasRappi);



        } catch(Exception e) {
            System.out.println(e);
        }
        finally {
            if (con!=null){
                try {

                    con.close();
                } catch (SQLException e) {
                   // e.printStackTrace();
                }

            }
        }

        System.out.println("Ventas Totales: $"+total);
        System.out.println("Top 10 Productos: ");
        System.out.print("Descripcion");
        for (int j = 0; j <49; j++) {
            System.out.print(" ");
        }
        System.out.print("|Qty");
        System.out.println(" |venta | %");
        for (int i = 0; i <80 ; i++) {
            System.out.print("-");
        }
        System.out.println();
        Collections.sort(prductos_concant,new Sortcant());

            if (prductos_concant.size()>10){
                for (int i = 0; i <10; i++) {
                    String str=prductos_concant.get(i).getProd();
                    for (int j = str.length(); j <60 ; j++) {
                        str+=" ";
                    }
                    double percent=(prductos_concant.get(i).venta/total)*100;

                    System.out.println(str +" "+prductos_concant.get(i).getCant()+" "+" $"+prductos_concant.get(i).venta+" |"+df.format(percent)+"%");
                }

            }
            else {
                for (int i = 0; i <prductos_concant.size(); i++) {
                    String str=prductos_concant.get(i).getProd();
                    for (int j = str.length(); j <60 ; j++) {
                        str+=" ";
                    }
                    double percent=(prductos_concant.get(i).venta/total)*100;

                    System.out.println(str +" "+prductos_concant.get(i).getCant()+" "+" $"+prductos_concant.get(i).venta+" |"+df.format(percent)+"%");
                }
            }


        System.out.println(bebidas+" "+alimentos+" "+combosYotros);
        System.out.println(cancelaciones+" "+cantcancel   );

        System.out.println();
        System.out.println("Ventas por asociado");
        Collections.sort(asociadoyTotals,new SortAsoc());
        for (AsociadoyTotal x:asociadoyTotals){
            String str=x.asoc;
            for (int i = str.length(); i <40 ; i++) {
                str+=" ";
            }
            double percent=(x.total/total)*100;
            System.out.println(str+"$"+x.total+" - "+df.format(percent)+"%");
        }

        Datos_deVenta datos_deVenta=new Datos_deVenta(descuentos,combosYotros,bebidas,alimentos,cancelaciones,comens,propTarj,propEfect,total,fondostot,efectivon,tarjeta,cajatot,retirosn,ventasSucur,ventasRappi,ventasUber,prductos_concant,asociadoyTotals,movimientosSucurs,movimientosRappi,movimientosUber,totalmovimientos,tickets);

        try {
            String usr=System.getProperty("user.name");
            new File("C:/Users/"+usr+"/Desktop/Reportes/").mkdirs();

            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(JAva_to_pdf.FILE));
            document.open();
            JAva_to_pdf.addMetaData(document);
            JAva_to_pdf.addContent(document,datosFiscales,fechs,datos_deVenta);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }






}
