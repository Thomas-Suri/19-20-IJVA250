package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import com.example.demo.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private FactureService factureService;

    public LocalDate today = LocalDate.now();
    int todayYear = today.getYear();

    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();
        writer.println("Id;Nom;Prenom;Date de Naissance;Age");

        for(Client client : allClients){
            LocalDate dateN = client.getDateNaissance();

            int dateNYear = dateN.getYear();

            int age = todayYear - dateNYear;

            writer.println(client.getId()+";"
                    +client.getNom().replaceAll("[^\\w]","")+";"
                    +client.getPrenom().replaceAll("[^\\w]","")+";"
                    +client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"))+";"
                    +age);
        }
    }

    @GetMapping("/clients/xlsx")
    public void clientsXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.xlsx\"");
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");
        Row headerRow = sheet.createRow(0);

        Cell cellId = headerRow.createCell(0);
        cellId.setCellValue("Id");

        Cell cellNom = headerRow.createCell(1);
        cellNom.setCellValue("Nom");

        Cell cellPrenom = headerRow.createCell(2);
        cellPrenom.setCellValue("Prénom");

        Cell cellDateN = headerRow.createCell(3);
        cellDateN.setCellValue("Date de Naissance");


        Cell cellAge = headerRow.createCell(4);
        cellAge.setCellValue("Age");

        int iRow = 1;
        for(Client client : allClients){
            Row row = sheet.createRow(iRow);

            Cell id = row.createCell(0);
            id.setCellValue(client.getId());

            Cell nom = row.createCell(1);
            nom.setCellValue(client.getNom().replaceAll("[^\\w]",""));

            Cell prenom = row.createCell(2);
            prenom.setCellValue(client.getPrenom().replaceAll("[^\\w]",""));

            Cell dateN = row.createCell(3);
            dateN.setCellValue(client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));

            Cell age = row.createCell(4);
            LocalDate dateNa = client.getDateNaissance();

            int dateNYear = dateNa.getYear();

            int Age = todayYear - dateNYear;
            age.setCellValue(Age);

            iRow += 1;
        }

        sheet.autoSizeColumn(1, true);
        sheet.autoSizeColumn(2, true);
        sheet.autoSizeColumn(3, true);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/factures/xlsx")
    public void facturesClientXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=\"facturesClients.xlsx\"");
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Client");
        Row headerRow = sheet.createRow(0);

        Cell cellId = headerRow.createCell(0);
        cellId.setCellValue("Id");

        Cell cellNom = headerRow.createCell(1);
        cellNom.setCellValue("Nom");

        Cell cellPrenom = headerRow.createCell(2);
        cellPrenom.setCellValue("Prénom");

        Cell cellDateN = headerRow.createCell(3);
        cellDateN.setCellValue("Date de Naissance");


        Cell cellAge = headerRow.createCell(4);
        cellAge.setCellValue("Age");

        int iRow = 1;
        for(Client client : allClients){
            Row row = sheet.createRow(iRow);

            Cell id = row.createCell(0);
            id.setCellValue(client.getId());

            Cell nom = row.createCell(1);
            nom.setCellValue(client.getNom().replaceAll("[^\\w]",""));

            Cell prenom = row.createCell(2);
            prenom.setCellValue(client.getPrenom().replaceAll("[^\\w]",""));

            Cell dateN = row.createCell(3);
            dateN.setCellValue(client.getDateNaissance().format(ofPattern("dd/MM/YYYY")));

            Cell age = row.createCell(4);
            LocalDate dateNa = client.getDateNaissance();

            int dateNYear = dateNa.getYear();

            int Age = todayYear - dateNYear;
            age.setCellValue(Age);

            List<Facture> factures = factureService.findFacturesClient(client.getId());

            for (Facture facture : factures) {
                Sheet sheetFacture = workbook.createSheet("Facture n°" + facture.getId().toString() + " de " +client.getNom());
                Row headerRowFacture = sheetFacture.createRow(0);

                Cell cellNomProduit = headerRowFacture.createCell(0);
                cellNomProduit.setCellValue("Nom de produit");

                Cell cellPrixProduit = headerRowFacture.createCell(1);
                cellPrixProduit.setCellValue("Prix du produit");

                Cell cellQuantiteProduit = headerRowFacture.createCell(2);
                cellQuantiteProduit.setCellValue("Quantité");

                Cell cellSousTotal = headerRowFacture.createCell(3);
                cellSousTotal.setCellValue("Sous total");

                int jRow = 1;
                for (LigneFacture lignefacture : facture.getLigneFactures()) {
                    Row rowFact = sheetFacture.createRow(jRow);

                    Cell cellProduit = rowFact.createCell(0);
                    cellProduit.setCellValue(lignefacture.getArticle().getLibelle());

                    Cell cellPrix = rowFact.createCell(1);
                    cellPrix.setCellValue(lignefacture.getArticle().getPrix());

                    Cell cellQte = rowFact.createCell(2);
                    cellQte.setCellValue(lignefacture.getQuantite());

                    Cell cellSsTotal = rowFact.createCell(3);
                    cellSsTotal.setCellValue(lignefacture.getSousTotal());

                    jRow = jRow + 1;
                }

                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setColor(IndexedColors.RED.getIndex());
                font.setBold(true);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFont(font);

                CellStyle styleResultat = workbook.createCellStyle();
                Font fontResultat = workbook.createFont();
                fontResultat.setColor(IndexedColors.RED.getIndex());
                fontResultat.setBold(true);
                styleResultat.setFont(fontResultat);

                Row rowTotal = sheetFacture.createRow(jRow);

                Cell cellTotal = rowTotal.createCell(0);
                cellTotal.setCellValue("Total");

                cellTotal.setCellStyle(style);
                sheetFacture.addMergedRegion(new CellRangeAddress(jRow,jRow,0,2));
                Cell cellPrixTotal = rowTotal.createCell(3);
                cellPrixTotal.setCellValue(facture.getTotal());
                cellPrixTotal.setCellStyle(styleResultat);

                for (int j = 0; j < 5; j++) {
                    sheetFacture.autoSizeColumn(j);
                }

                iRow += 1;
            }
        }

        sheet.autoSizeColumn(1, true);
        sheet.autoSizeColumn(2, true);
        sheet.autoSizeColumn(3, true);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
