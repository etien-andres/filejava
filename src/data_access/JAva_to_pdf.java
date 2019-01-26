package data_access;

import com.itextpdf.text.*;
import com.itextpdf.*;

import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.scenario.effect.ImageData;

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

         double totalcaja=datos_deVenta.ventas_suc+datos_deVenta.fondostot+datos_deVenta.propTarjet+datos_deVenta.propEfect-datos_deVenta.retiros;





         Phrase titulo=Phrase.getInstance("Reporte Ventas EzBarNFood");
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
         caja.setFont(subFont);
         PdfPCell desc=new PdfPCell(caja);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Total Caja"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(totalcaja)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+getpercent(totalcaja,totalcaja)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_caja.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Fondos"));
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
         desc=new PdfPCell(new Phrase("Efectivo ventas:"));
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
         desc=new PdfPCell(new Phrase("Tarjeta ventas:"));
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
         desc=new PdfPCell(new Phrase("Retiros:"));
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

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Propinas Efectivo:"));
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
         desc=new PdfPCell(new Phrase("Propinas Tarjeta:"));
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
         document.add(tabla_caja);
         document.add(blanco);

         PdfPTable tabla_ventas=new PdfPTable(1);
         tabla_ventas.setHeaderRows(1);
         Phrase ventas=new Phrase("Ventas por tipo de servicio");
         ventas.setFont(subFont);
         desc=new PdfPCell(ventas);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Ventas totales:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.total+datos_deVenta.descuents)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents,datos_deVenta.total+datos_deVenta.descuents))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Ventas sucursal:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.ventas_suc)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents,datos_deVenta.ventas_suc))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Ventas Rappi:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.ventas_rapi)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents,datos_deVenta.ventas_rapi))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);

         contenedor=new PdfPTable(3);
         desc=new PdfPCell(new Phrase("Ventas Uber:"));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell(new Phrase("$"+Double.toString(datos_deVenta.ventas_uber)));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents,datos_deVenta.ventas_uber))));
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

         desc=new PdfPCell(new Phrase("% "+df.format(getpercent(datos_deVenta.total+datos_deVenta.descuents,datos_deVenta.descuents))));
         desc.setBorder(Rectangle.NO_BORDER);
         contenedor.addCell(desc);
         desc=new PdfPCell();
         desc.setBorder(Rectangle.NO_BORDER);
         desc.addElement(contenedor);

         tabla_ventas.addCell(desc);


         document.add(tabla_ventas);

         PdfPTable tabla_ventaporprod=new PdfPTable(1);
         tabla_ventas.setHeaderRows(1);
         Phrase ventas_porprod=new Phrase("Ventas por tipo de producto");
         ventas.setFont(subFont);
         desc=new PdfPCell(ventas);
         desc.setBackgroundColor(BaseColor.LIGHT_GRAY);
         desc.setBorder(Rectangle.NO_BORDER);

         tabla_ventas.addCell(desc);



         PdfPCell cant=new PdfPCell();






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

