package uz.devops.intern.file.io;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConvertExcelToPDF {
    public void convertToPDFFromExcel() throws IOException, DocumentException {
        FileInputStream fileInputStream = new FileInputStream(new File("src/main/resources/templates/excel.xlsx"));
        Workbook workbook = new XSSFWorkbook(fileInputStream);

        Sheet sheet = workbook.getSheetAt(0);
        List<String> headerList = getRow(0, sheet);

        Document document = new Document();
        String fileName = "src/main/resources/templates/test.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        document.open();
        PdfPTable table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
        addPDFData(true, headerList, table);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            List<String> rowList = getRow(i, sheet);
            addPDFData(false, rowList, table);
            document.add(table);
            table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
        }

        document.close();
    }

    public static List<String> getRow(int index, Sheet sheet) {
        List<String> list = new ArrayList<>();

        for (Cell cell : sheet.getRow(index)) {
            switch (cell.getCellType()) {
                case STRING:
                    list.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    list.add(String.valueOf(cell.getNumericCellValue()));
                    break;
                case BOOLEAN:
                    list.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                case FORMULA:
                    list.add(cell.getCellFormula().toString());
                    break;
            }
        }
        return list;
    }

    private static void addPDFData(boolean isHeader, List<String> list, PdfPTable table) {
        list.stream()
            .forEach(column -> {
                PdfPCell header = new PdfPCell();
                if (isHeader) {
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                }
                header.setPhrase(new Phrase(column));
                table.addCell(header);
            });
    }
}
