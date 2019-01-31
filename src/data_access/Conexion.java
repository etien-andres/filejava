package data_access;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import data_access.javaToPdf.Movimientos;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

import static data_access.JAva_to_pdf.user;

public class Conexion {
    public static ArrayList<String> datosFiscales=new ArrayList<>();



    public static String fechaventainit="31-01-19";
    public static String fechaventafinal="31-01-19";
    public static String sucursal="";

    public static void main(String[] args) {

//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet sheet = workbook.createSheet("Java Books");
//
//
//        Object[][] bookData = {
//                {"Head First Java", "Kathy Serria", 79},
//                {"Effective Java", "Joshua Bloch", 36},
//                {"Clean Code", "Robert martin", 42},
//                {"Thinking in Java", "Bruce Eckel", 35},
//        };
//
//        int rowCount = 0;
//
//        for (Object[] aBook : bookData) {
//            Row row = sheet.createRow(++rowCount);
//
//            int columnCount = 0;
//
//            for (Object field : aBook) {
//                Cell cell = row.createCell(++columnCount);
//                if (field instanceof String) {
//                    cell.setCellValue((String) field);
//                } else if (field instanceof Integer) {
//                    cell.setCellValue((Integer) field);
//                }
//            }
//
//        }
//
//
//        try (FileOutputStream outputStream = new FileOutputStream("JavaBooks.xls")) {
//            workbook.write(outputStream);
//        }
//        catch (Exception e) {
//        e.printStackTrace();
//        }
//




        Double total=0D,fondostot=0D,efectivon=0D,tarjeta=0D, retirosn=0D,cajatot=0D,propEfect=0d,propTarj=0d, ventasSucur=0d,ventasRappi=0d,ventasUber=0d,cancelaciones=0d,combosYotros=0d,bebidas=0d,alimentos=0d,descuentos=0d;

        int comens=0,cantcancel=0;

        ArrayList<String> fechs=new ArrayList<>();

        ArrayList<Movimientos> movimientosSucurs=new ArrayList<>();
        ArrayList<Movimientos> movimientosRappi=new ArrayList<>();
        ArrayList<Movimientos> movimientosUber=new ArrayList<>();
        ArrayList<Movimientos> movimientosCancel=new ArrayList<>();
        ArrayList<Movimientos> totalmovimientos=new ArrayList<>();

        SortedSet<String> productos=new TreeSet<>();
        SortedSet<String> usuarios=new TreeSet<>();
        SortedSet<String> tickets=new TreeSet<>();

        ArrayList<AsociadoyTotal> asociadoyTotals=new ArrayList<>();
        ArrayList<ProdyCant> prductos_concant=new ArrayList<>();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df =  new DecimalFormat("",otherSymbols);
        df.setMaximumFractionDigits(2);

        fechs.add(fechaventainit);
        fechs.add(fechaventafinal);








        try {
            Driver d=(Driver)Class.forName("com.filemaker.jdbc.Driver").newInstance();
        }catch (Exception e){
            System.out.println("error "+e.getMessage());
        }



        String jdburlcaja="jdbc:filemaker://localhost/caja";
        String jdburlprod="jdbc:filemaker://localhost/productos";

        Connection con=null;

        try {
            con = DriverManager.getConnection(jdburlcaja,"sirka","3v-S1r;k4");
            Statement statement=con.createStatement();
            //En este query se obtienen los datos de movimientos
            ResultSet rs=statement.executeQuery("select  \"Producto Descripcion\",\"Importe CDesc\",\"Cantidad Venta\", \"Nombre Asociado\", \"Fecha Venta Periodo Activo\",\"Hora Creacion\",\"ID movimiento\",\"Mesa Venta\",\"Importe Cancela\", \"Precio Unitario SDesc\",BanderaProductoImpresora, \"Cantidad Descuentos\",\"Cantidad Cancela\" from movimientos  where \"Producto Descripcion\" not in  ('Fondo de Caja','Botana de la Casa','Retiro Efectivo CParcial') and \"Fecha Venta Periodo Activo\" >='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' order by \"ID movimiento\"");

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

                    String idmov=rs.getString(7);

                    cancelaciones+=rs.getDouble(9);

                    String mesa=rs.getString(8);

                    if (impresora==null){
                        combosYotros+=importe;
                    }
                    else if (impresora.toUpperCase().contains("BARRA")){
                        bebidas+=importe;
                    }
                    else if (impresora.toUpperCase().contains("COCINA")){
                        alimentos+=importe;
                    }


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
            ResultSet resultadoDatosfisc=stst.executeQuery("SELECT \"Nombre Comercial\",\"Razon Social\",Sucursal,RFC,Domicilio,Colonia,Ciudad,\"Codigo Postal\",Estado,Telefono from registro_caja ");

            if (resultadoDatosfisc.first()){
                do {
                    for (int i = 1; i <11 ; i++) {
                        datosFiscales.add(resultadoDatosfisc.getString(i));
                        if (i==3) sucursal=resultadoDatosfisc.getString(3);
                    }
                }while (resultadoDatosfisc.next());
            }
            stst.close();
            resultadoDatosfisc.close();
            stst=con.createStatement();
            ResultSet fondostotal=stst.executeQuery("SELECT \"Importe CDesc\" from movimientos where \"Producto Descripcion\" ='Fondo de Caja' and \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' ");
            if (fondostotal.first()){
                do {
                    fondostot+=fondostotal.getDouble(1);
                }while (fondostotal.next());
            }
            fondostotal.close();
            ResultSet efectivo=stst.executeQuery("SELECT \"Pago Total\",\"Cambio\",\"Pago Tarjeta\",PropinaEfectivo,PropinaTarjeta,ComensalesCantidad from VentasCaja where \"Estatus Venta\"='Pagada' and \"Fecha Venta Periodo Activo\" >= '"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' ");
            if (efectivo.first()){
                do {
                    efectivon+=(efectivo.getDouble(1)-efectivo.getDouble(2)-efectivo.getDouble(3));
                    tarjeta+=efectivo.getDouble(3);
                    comens+=efectivo.getInt(6);

                }while (efectivo.next());
            }
            efectivo.close();
            ResultSet retiros=stst.executeQuery("SELECT \"Importe CDesc\" from movimientos  where \"Producto Descripcion\" ='Retiro Efectivo CParcial' and \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"'");
            if (retiros.first()){
                do {
                    retirosn+=retiros.getDouble(1);
                }while (retiros.next());
            }
            retiros.close();
            ResultSet propinas=stst.executeQuery("SELECT PropinaEfectivo,PropinaTarjeta from VentasCaja where \"Fecha Venta Periodo Activo\">='"+fechaventainit+"' and \"Fecha Venta Periodo Activo\" <='"+fechaventafinal+"' ");
            if (propinas.first()){
                do {
                    propEfect+=propinas.getDouble(1);
                    propTarj+=propinas.getDouble(2);
                }while (propinas.next());
            }


            cajatot+=+efectivon;



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




        Collections.sort(prductos_concant,new Sortcant());

        Collections.sort(asociadoyTotals,new SortAsoc());



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
