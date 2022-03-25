package com.mindhub.homebanking.models;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.TransactionDTO;


public class UserPDFExporter {
    private Account account;
    private List<TransactionDTO> listTransactions;
    private Client client;

    public UserPDFExporter(Account account, List<TransactionDTO> listTransactions, Client client) {
        this.account = account;
        this.listTransactions = listTransactions;
        this.client = client;
    }

    private PdfPTable createTable(Integer columns, float[] rowWidth) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100f);
        table.setWidths(rowWidth);
        table.setSpacingBefore(10);
        return table;
    }

    private void writeTableHeader(PdfPTable table, String[] titulos) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLACK);
        cell.setPadding(4);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        for (int i = 0; i < titulos.length; i++) {
            cell.setPhrase(new Phrase(String.valueOf(titulos[i]), font));
            table.addCell(cell);
        }
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setColor(Color.BLACK);

        Paragraph p = new Paragraph("Lista de movimientos para el usuario: " +
                client.getFirstName() + " " + client.getLastName(), font);
        //p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable tableAccount = createTable(3, new float[] {2.5f,3.0f,3.5f});
        writeTableHeader(tableAccount, new String[] {"Número de cuenta", "Saldo", "Fecha de cración"});
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        tableAccount.addCell(String.valueOf(account.getNumber()));
        tableAccount.addCell(String.valueOf(account.getBalance()));
        tableAccount.addCell(account.getCreationDate().format(formatter));

        PdfPTable tableTransactions = createTable(4, new float[] {3.0f, 2.0f, 3.0f, 3.0f});
        writeTableHeader(tableTransactions, new String[] {"Fecha", "Tipo", "Monto", "Descripción"});

        for (TransactionDTO transaction : listTransactions) {
            tableTransactions.addCell(transaction.getDate().format(formatter));
            tableTransactions.addCell(String.valueOf(transaction.getType()));
            tableTransactions.addCell(String.valueOf(transaction.getAmount()));
            tableTransactions.addCell(String.valueOf(transaction.getDescription()));
        }

        document.add(tableAccount);
        document.add(tableTransactions);

        document.close();

    }
}