package com.example.fernando.proyectodam.util.Ficheros;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;;

import com.example.fernando.proyectodam.pojo.ElementoLista;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Fecha.UtilFecha;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by Fernando on 15/10/2016.
 */

public class UtilPdfAdapter  {

    public static class ImprimirNota extends AsyncTask<Nota, Void, Uri> {

        private Context c;
        private MenuItem item;
        private boolean compartir;
        private AsyncResponse response;

        public ImprimirNota( Context c, MenuItem item, boolean compartir, AsyncResponse response ){

            this.c          = c;
            this.item       = item;
            this.compartir  = compartir;
            this.response   = response;

            item.setEnabled(false);
        }

        @Override
        protected Uri doInBackground(Nota... params) {

            try
            {
                File myFile = new File( c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                                        UtilFecha.formatDate(new Date()) + "_" + params[0].getTitulo() + ".pdf");
                myFile.createNewFile();
                OutputStream output = new FileOutputStream(myFile);

                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, output);

                //MetaData
                document.addCreationDate();
                document.setPageSize(PageSize.A4);
                document.addCreator("Quip");

                document.open();

                /* Inserting Image in PDF */
                //Si existe imagen la imprimimos
                Bitmap bmp = params[0].getImagen();

                if ( bmp != null ) {

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    Image myImg = Image.getInstance(bytes.toByteArray());
                    myImg.setAlignment(Image.MIDDLE);

                    //add image to document
                    document.add(myImg);


                }

                // left,right,top,bottom
                document.setMargins(36, 36, 36, 36);
                document.setMarginMirroring(true);

                /* Create Paragraph and Set Font */
                Paragraph p1 = new Paragraph(params[0].getTitulo());

                /* Create Set Font and its Size */
                Font paraFont= new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD);
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                p1.setFont(paraFont);

                //add paragraph to document
                document.add(p1);

                 /* Create Paragraph and Set Font */
                Paragraph p2 = new Paragraph(params[0].getNota());

                /* Create Set Font and its Size */
                Font paraFont2= new Font(Font.FontFamily.HELVETICA);
                paraFont2.setSize(16);
                p2.setAlignment(Paragraph.ALIGN_CENTER);
                p2.setFont(paraFont2);

                //add paragraph to document
                document.add(p2);

                document.close();

                return Uri.fromFile(myFile);
            }
            catch( IOException | DocumentException e ) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Uri file) {

            Toast.makeText(c, "Archivo PDF generado correctamente", Toast.LENGTH_LONG).show();

            //Habilitamos el item para que se pueda imprimir de nuevo
            item.setEnabled(true);

            if ( compartir ) response.enviarInformacion(file);

        }
    }

    public static class ImprimirLista extends AsyncTask<Lista, Void, Uri> {

        private Context c;
        private MenuItem item;
        private boolean compartir;
        private AsyncResponse response;

        public ImprimirLista( Context c, MenuItem item, boolean compartir, AsyncResponse response ){

            this.c          = c;
            this.item       = item;
            this.compartir  = compartir;
            this.response   = response;
            item.setEnabled(false);
        }

        private void createTable(Lista l, Section subCatPart ){

            PdfPTable table = new PdfPTable(2);

            PdfPCell c1 = new PdfPCell(new Phrase("Tarea Realizada"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Descripcion Tarea"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.setHeaderRows(1);

            for ( ElementoLista el: l.getItems() ) {

                boolean check   = el.isCheck();
                String desc     = el.getTexto();

                PdfPCell cE;

                if ( check ) {

                    cE = new PdfPCell(new Phrase("Realizada"));
                    cE.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cE);
                }
                else
                {
                    cE = new PdfPCell(new Phrase("No Realizada"));
                    cE.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cE);
                }

                cE = new PdfPCell(new Phrase(el.getTexto()));
                cE.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cE);
            }

            subCatPart.add(table);
        }

        private static void addEmptyLine(Paragraph paragraph, int number) {
            for (int i = 0; i < number; i++) {
                paragraph.add(new Paragraph(" "));
            }
        }

        @Override
        protected Uri doInBackground(Lista... params) {

            try
            {
                File myFile = new File( c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                                        UtilFecha.formatDate(new Date()) + "_" + params[0].getTitulo() + ".pdf");
                myFile.createNewFile();
                OutputStream output = new FileOutputStream(myFile);

                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, output);

                //MetaData
                document.addCreationDate();
                document.setPageSize(PageSize.A4);
                document.addCreator("Quip");

                document.open();

                // left,right,top,bottom
                document.setMargins(36, 36, 36, 36);
                document.setMarginMirroring(true);

                 /* Create Paragraph and Set Font */

                Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);
                Anchor anchor = new Anchor( params[0].getTitulo(), catFont);
                anchor.setName(params[0].getTitulo());

                // Second parameter is the number of the chapter
                Chapter catPart = new Chapter(new Paragraph(anchor), 1);

                //Espacios en Blando
                Paragraph paragraph = new Paragraph();
                addEmptyLine(paragraph, 2);
                catPart.add(paragraph);

                Paragraph subPara = new Paragraph("Elementos de la lista");
                Section subCatPart = catPart.addSection(subPara);

                Paragraph paragraph2 = new Paragraph();
                addEmptyLine(paragraph2, 2);
                subCatPart.add(paragraph2);

                // add a table
                createTable( params[0], subCatPart);

                // now add all this to the document
                document.add(catPart);

                document.close();

                return Uri.fromFile(myFile);
            }
            catch( IOException | DocumentException e ) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {

            Toast.makeText(c, "Archivo PDF generado correctamente", Toast.LENGTH_LONG).show();

            item.setEnabled(true);
            if ( compartir ) response.enviarInformacion(uri);
        }
    }
}
