package com.example.resultbot.service.botservice;

import com.example.resultbot.entity.Transaction;
import com.example.resultbot.service.TransactionService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final TransactionService transactionService;

    public ReportService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public String generateMonthlyIncomeReport() {
        String filePath = "monthly_income_report.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Oylik Daromadlar");

            // Sarlavha qatori
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Sana");
            headerRow.createCell(1).setCellValue("Mijoz");
            headerRow.createCell(2).setCellValue("Daromad");
            headerRow.createCell(3).setCellValue("Valyuta");
            headerRow.createCell(4).setCellValue("Kategoriyasi");


            LocalDate today = LocalDate.now();
            int currentMonth = today.getMonthValue();
            int currentYear = today.getYear();

            List<Transaction> transactions = transactionService.fetchMonthlyTransactions(currentMonth, currentYear);

            // Ma'lumotlar qatorlari
            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getId());
                row.createCell(1).setCellValue(transaction.getClient().getFullName()); // Mijoz ismi
                row.createCell(2).setCellValue(transaction.getAmount());
                row.createCell(3).setCellValue(transaction.getCurrency().toString());

            }

            // Faylni saqlash
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Excel fayl yaratishda xatolik yuz berdi: " + e.getMessage(), e);
        }

        return filePath;
    }
}

