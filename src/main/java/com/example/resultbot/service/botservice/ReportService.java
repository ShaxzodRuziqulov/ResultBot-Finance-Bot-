package com.example.resultbot.service.botservice;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class ReportService {
    public ByteArrayOutputStream generateReport(String reportType, Map<String, String> filters) {
        // Excel faylni yaratish
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Hisobot");

        // Dummy ma'lumotlarni to‘ldirish
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Sarlavha");
        headerRow.createCell(1).setCellValue("Qiymat");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Misol");
        dataRow.createCell(1).setCellValue("12345");

        // Faylni ByteArrayOutputStream-ga yozish
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Excel fayl yaratishda xatolik yuz berdi", e);
        }

        return outputStream;
    }
}

