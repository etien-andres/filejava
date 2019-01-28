package data_access;

import com.itextpdf.text.*;
import com.itextpdf.*;

import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.scenario.effect.ImageData;
import data_access.javaToPdf.Movimientos;

import javax.imageio.ImageIO;
import javax.xml.soap.Text;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class JAva_to_pdf {


    static String user=System.getProperty("user.name");

     static String FILE = "C:/Users/"+user+"/Desktop/Reportes/Reporte Ventas.pdf";
     static Font catFont = new Font(Font.FontFamily.UNDEFINED, 18,
            Font.BOLD);
     static Font redFont = new Font(Font.FontFamily.UNDEFINED, 12,
            Font.NORMAL, BaseColor.RED);
     static Font subFont = new Font(Font.FontFamily.UNDEFINED, 16,
            Font.BOLD);
    static Font white = new Font(Font.FontFamily.UNDEFINED, 16,
            Font.BOLD);

     static Font smallBold = new Font(Font.FontFamily.UNDEFINED, 5,
            Font.BOLD);
    static Font great = new Font(Font.FontFamily.UNDEFINED, 24,
            Font.BOLD);



    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
     static void addMetaData(Document document) {
        document.addTitle("Reporte Ventas EzbarNFood");
        document.addAuthor("EzBarNFood ");
    }

    static double getpercent(double total,double parcial){
         return (parcial/total)*100;
    }

     static void addContent(Document document, ArrayList<String> DatosSucur,ArrayList<String> fechass, Datos_deVenta datos_deVenta) throws DocumentException {
         DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
         otherSymbols.setDecimalSeparator('.');
         NumberFormat nf = NumberFormat.getNumberInstance();

         DecimalFormat df =  new DecimalFormat("####.##",otherSymbols);
         df.setMaximumFractionDigits(2);

         double totalcaja=datos_deVenta.efectivo+datos_deVenta.tarjeta+datos_deVenta.propEfect-datos_deVenta.retiros;





         Phrase titulo=Phrase.getInstance("Reporte de Caja y Ventas EzBarNFood");
         Paragraph parrafo_titulo = new Paragraph();
         addEmptyLine(parrafo_titulo,1);
        parrafo_titulo.setFont(great);
         parrafo_titulo.add(titulo);
         addEmptyLine(parrafo_titulo,3);

         parrafo_titulo.setAlignment(Element.ALIGN_CENTER);
         document.add(parrafo_titulo);

         Paragraph parrafo_datos=new Paragraph();

         PdfPTable tabla_datos=new PdfPTable(2);
         PdfPCell celdaimagen=new PdfPCell();
         Image logoCliente=null;
         try {
             logoCliente=Image.getInstance("Files/EzBar&Food/LogoColor.png");
             logoCliente.scaleToFit(150,150);
             celdaimagen.addElement(logoCliente);
             celdaimagen.setBorder(Rectangle.NO_BORDER);
             tabla_datos.addCell(celdaimagen);



         } catch (IOException e) {
             e.printStackTrace();
         }
         for (int i = 0; i <DatosSucur.size() ; i++) {
             if (DatosSucur.get(i)!=null){
                 Phrase phrase=Phrase.getInstance(DatosSucur.get(i)+"\n");
                 parrafo_datos.add(phrase);
             }


         }
         parrafo_datos.setFont(smallBold);
         PdfPCell celdaDAtos=new PdfPCell(parrafo_datos);
         celdaDAtos.setBorder(Rectangle.NO_BORDER);
         celdaDAtos.setBackgroundColor(BaseColor.LIGHT_GRAY);
         tabla_datos.addCell(celdaDAtos);
         tabla_datos.setHorizontalAlignment(Element.ALIGN_CENTER);

         document.add(tabla_datos);
         Paragraph fechas=new Paragraph();
         addEmptyLine(fechas,1);
         Phrase fech=Phrase.getInstance("Reporte del "+fechass.get(0)+" al "+fechass.get(1));
         fechas.add(fech);
         addEmptyLine(fechas,2);
         fechas.setAlignment(Element.ALIGN_CENTER);


         document.add(fechas);
         Paragraph blanco=new Paragraph();
         addEmptyLine(blanco,1);

         PdfPTable contenedor=null;
         PdfPTable tabla_caja=new PdfPTable(1);
         tabla_caja.setHeaderRows(1);
         Phrase caja=new Phrase("Caja");

         caja.setFont(catFont);
         PdfPCell desc=new PdfPCell(caja);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);

         tabla_caja.addCell(desc);



         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Fondos"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.fondostot)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.fondostot))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Ventas efectivo:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.efectivo)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.efectivo))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Propinas efectivo:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.propEfect)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.propEfect))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Total voucher:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.tarjeta)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.tarjeta))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Ventas tarjeta:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.tarjeta-datos_deVenta.propTarjet)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.tarjeta))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);
         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Propinas tarjeta:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.propTarjet)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.propTarjet))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("-Retiros:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.retiros)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(totalcaja,datos_deVenta.retiros))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);





         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Total Caja"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(totalcaja)));
         desc.setHorizontalAlignment(Rectangle.LEFT);
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);


         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         document.add(tabla_caja);
         document.add(blanco);

         PdfPTable tabla_ventas=new PdfPTable(1);
         tabla_ventas.setHeaderRows(1);
         Phrase ventas=new Phrase("Ventas por tipo de servicio");
         ventas.setFont(subFont);
         desc=new PdfPCell(ventas);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Ventas totales:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel,datos_deVenta.total+datos_deVenta.cancel+datos_deVenta.descuents))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Ventas sucursal:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.ventas_suc)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel,datos_deVenta.ventas_suc))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Ventas Rappi:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.ventas_rapi)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel,datos_deVenta.ventas_rapi))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("+Ventas Uber:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.ventas_uber)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel,datos_deVenta.ventas_uber))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("-Descuentos:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.descuents)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel,datos_deVenta.descuents))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("-Cancelaciones:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.cancel)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents+datos_deVenta.cancel,datos_deVenta.cancel))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Venta Neta:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.total)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);


         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);


         document.add(tabla_ventas);

         document.add(blanco);


         PdfPTable tabla_ventaporprod=new PdfPTable(1);
         //tabla_ventaporprod.setHeaderRows(1);
         Phrase ventas_porprod=new Phrase("Ventas por tipo de producto");
         ventas_porprod.setFont(subFont);
         desc=new PdfPCell(ventas_porprod);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);


         tabla_ventaporprod.addCell(desc);


         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Alimentos:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.alimentos)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent((datos_deVenta.alimentos+datos_deVenta.bebidas+datos_deVenta.combosYotros),datos_deVenta.alimentos))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventaporprod.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Bebidas:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.bebidas)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.alimentos+datos_deVenta.bebidas+datos_deVenta.combosYotros,datos_deVenta.bebidas))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventaporprod.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Combos y otros:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.combosYotros)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.alimentos+datos_deVenta.bebidas+datos_deVenta.combosYotros,datos_deVenta.combosYotros))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventaporprod.addCell(desc);


         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Total:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.combosYotros+datos_deVenta.bebidas+datos_deVenta.alimentos)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventaporprod.addCell(desc);

         document.add(tabla_ventaporprod);

         document.add(blanco);
         document.newPage();

         PdfPTable tabla_cuentas=new PdfPTable(1);
         //tabla_ventaporprod.setHeaderRows(1);
         Phrase resmn=new Phrase("Resumen de cuentas");
         resmn.setFont(subFont);
         desc=new PdfPCell(resmn);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);

         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);


         tabla_cuentas.addCell(desc);

         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Cuentas totales:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase(Integer.toString(datos_deVenta.tickets.size())));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_cuentas.addCell(desc);
         ArrayList<String> ticks=new ArrayList<>();
         for (String a:datos_deVenta.tickets){
             ticks.add(a);
         }

         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Ticket inicial/Ticket final:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase(ticks.get(0)+" / "+ticks.get(ticks.size()-1)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_cuentas.addCell(desc);

         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Comensales:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase(Integer.toString(datos_deVenta.comens)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_cuentas.addCell(desc);

         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Promedio  de venta por ticket:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+df.format(datos_deVenta.total/ticks.size())));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_cuentas.addCell(desc);


         contenedor=new PdfPTable(2);
         desc=new PdfPCell(new Phrase("Promedio  de venta por comensal:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+df.format(datos_deVenta.total/datos_deVenta.comens)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_cuentas.addCell(desc);


         document.add(tabla_cuentas);

         document.add(blanco);


         PdfPTable tabla_asociad=new PdfPTable(1);
         //tabla_ventaporprod.setHeaderRows(1);
         Phrase asoc=new Phrase("Ventas por Asociado");
         asoc.setFont(subFont);
         desc=new PdfPCell(asoc);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);

         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);


         tabla_asociad.addCell(desc);
         for (AsociadoyTotal asociadoyTotal:datos_deVenta.asociadoyTotals){
             contenedor=new PdfPTable(3);
             desc=new PdfPCell(new Phrase(asociadoyTotal.asoc));
             desc.setBorder(Rectangle.NO_BORDER);
             contenedor.addCell(desc);
             desc=new PdfPCell(new Phrase("$"+Double.toString(asociadoyTotal.total)));
             desc.setBorder(Rectangle.NO_BORDER);
             contenedor.addCell(desc);

             desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total,asociadoyTotal.total))));
             desc.setBorder(Rectangle.NO_BORDER);
             contenedor.addCell(desc);
             desc=new PdfPCell();
             desc.setBorder(Rectangle.NO_BORDER);
             desc.addElement(contenedor);

             tabla_asociad.addCell(desc);
         }




         document.add(tabla_asociad);

         document.add(blanco);


         PdfPTable tabla_productos=new PdfPTable(1);
         //tabla_ventaporprod.setHeaderRows(1);
         Phrase prods=new Phrase("Productos Vendidos");
         prods.setFont(subFont);
         desc=new PdfPCell(prods);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);

         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);


         tabla_productos.addCell(desc);


         contenedor=new PdfPTable(4);

         desc=new PdfPCell(new Phrase("Producto"));
         desc.setBorder(Rectangle.NO_BORDER);
         desc.setHorizontalAlignment(Element.ALIGN_LEFT);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("Cantidad"));
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);


         desc=new PdfPCell(new Phrase("Venta"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("%"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         tabla_productos.addCell(desc);

        for (ProdyCant prodyCant:datos_deVenta.prodyCants){
            contenedor=new PdfPTable(4);

            desc=new PdfPCell(new Phrase(prodyCant.prod));
            desc.setBorder(Rectangle.NO_BORDER);
            desc.setHorizontalAlignment(Element.ALIGN_LEFT);
            contenedor.addCell(desc);

            desc=new PdfPCell(new Phrase(Integer.toString(prodyCant.cant)));
            desc.setHorizontalAlignment(Element.ALIGN_CENTER);
            desc.setBorder(Rectangle.NO_BORDER);
            contenedor.addCell(desc);


            desc=new PdfPCell(new Phrase("$"+prodyCant.venta));
            desc.setBorder(Rectangle.NO_BORDER);
            contenedor.addCell(desc);

            desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total,prodyCant.venta))));
            desc.setBorder(Rectangle.NO_BORDER);
            contenedor.addCell(desc);
            desc=new PdfPCell();
            desc.setBorder(Rectangle.NO_BORDER);
            desc.addElement(contenedor);

            tabla_productos.addCell(desc);

        }

         document.add(tabla_productos);

        document.add(blanco);
        document.newPage();

         PdfPTable tabla_movimients=new PdfPTable(1);
         //tabla_ventaporprod.setHeaderRows(1);
         Phrase movs=new Phrase("Movimientos:");
         movs.setFont(subFont);
         desc=new PdfPCell(movs);
         desc.setHorizontalAlignment(Element.ALIGN_CENTER);

         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);


         tabla_movimients.addCell(desc);

         contenedor=new PdfPTable(7);

         desc=new PdfPCell(new Phrase("Ticket"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("Fecha"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("Hora"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);


         desc=new PdfPCell(new Phrase("Desc."));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("Importe"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("Mesa"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("Usuario"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         tabla_movimients.addCell(desc);

         for (Movimientos mov:datos_deVenta.movimientostotal){
             contenedor=new PdfPTable(7);

             desc=new PdfPCell(new Phrase(mov.getId_Mov()));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);

             desc=new PdfPCell(new Phrase(mov.getFecha()));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);

             desc=new PdfPCell(new Phrase(mov.getHora()));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);


             desc=new PdfPCell(new Phrase(mov.getDescrip()));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);

             desc=new PdfPCell(new Phrase(Double.toString(mov.getImporte())));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);

             desc=new PdfPCell(new Phrase(mov.getMesaventa()));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);

             desc=new PdfPCell(new Phrase(mov.getAsociado()));
             desc.setBorder(Rectangle.NO_BORDER);
             desc.setHorizontalAlignment(Element.ALIGN_LEFT);

             contenedor.addCell(desc);

             desc=new PdfPCell();
             desc.setBorder(Rectangle.NO_BORDER);
             desc.addElement(contenedor);
             desc.setHorizontalAlignment(Rectangle.LEFT);
             tabla_movimients.addCell(desc);

         }

         document.add(tabla_movimients);




     }




     static void createTable(Section subCatPart)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);
        PdfPTable subtable=new PdfPTable(2);
        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);
        PdfPCell c1=null;
        PdfPCell c3=null;


        c1 = new PdfPCell(new Phrase("Table Header 2"));
        c3=new PdfPCell(new Phrase("ho"));
        c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c3.setBorder(Rectangle.NO_BORDER);
       subtable.addCell(c3);
        subtable.addCell(c3);
        c1.addElement(subtable);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        c1 = new PdfPCell(new Phrase("Table Header 1"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        c1 = new PdfPCell(new Phrase("Table Header 3"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        for (int i = 0; i <9 ; i++) {
            c1=new PdfPCell(new Phrase("Hi"+i));

            c1.setBorder(Rectangle.NO_BORDER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

        }

        subCatPart.add(table);

    }

     static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

     static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}

