package com.example.resultbot.service.botservice;

import com.example.resultbot.entity.Transaction;
import com.example.resultbot.service.TransactionService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final TransactionService transactionService;
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

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

            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getId());
                row.createCell(1).setCellValue(transaction.getClient().getFullName());
                row.createCell(2).setCellValue(transaction.getAmount());
                row.createCell(3).setCellValue(transaction.getCurrency().toString());

            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Excel fayl yaratishda xatolik yuz berdi: " + e.getMessage(), e);
        }

        return filePath;
    }
    public String generateMonthlyExpenseReport(int month, int year) {
        String filePath = "monthly_expense_report_" + month + "_" + year + "_" + System.currentTimeMillis() + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Oylik Xarajatlar");

            // Sarlavha qatori
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Sana");
            headerRow.createCell(1).setCellValue("Mijoz");
            headerRow.createCell(2).setCellValue("Xarajat Summasi");
            headerRow.createCell(3).setCellValue("Valyuta");
            headerRow.createCell(4).setCellValue("Kategoriya");
            headerRow.createCell(5).setCellValue("Izoh");

            // Ma'lumotlarni olish
            List<Transaction> expenses = transactionService.fetchMonthlyTransactions(month, year);

            // Ma'lumotlarni Excelga yozish
            int rowNum = 1;
            for (Transaction expense : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(expense.getCreatedAt().toString());
                row.createCell(1).setCellValue(expense.getClient().getFullName());
                row.createCell(2).setCellValue(expense.getAmount());
                row.createCell(3).setCellValue(expense.getCurrency().toString());
                row.createCell(4).setCellValue(expense.getExpenseCategory().getName());
                row.createCell(5).setCellValue(expense.getNote() != null ? expense.getNote() : "-");
            }

            // Faylni saqlash
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Excel fayl yaratishda xatolik yuz berdi", e);
            throw new RuntimeException("Excel fayl yaratishda xatolik yuz berdi: " + e.getMessage(), e);
        }

        return filePath;
    }
}

