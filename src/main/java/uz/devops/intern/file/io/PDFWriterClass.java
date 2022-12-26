package uz.devops.intern.file.io;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.attoparser.dom.Text;
import org.springframework.stereotype.Service;
import uz.devops.intern.service.dto.PaymentDTO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFWriterClass {
    private final WriterCustomerDebtsToWorkbook writerCustomerDebtsToWorkbook;
    public void convertToPDFFromExcel(List<PaymentDTO> paymentDTOList, ResourceBundle resourceBundle) throws NotOfficeXmlFileException {
        XSSFWorkbook workbook = writerCustomerDebtsToWorkbook.writeCustomerDebtsToExcelFile(paymentDTOList, resourceBundle);
        log.info("current thread while writing to PDF: {}" ,Thread.currentThread().getName());

        try {
            Sheet sheet = workbook.getSheetAt(0);
            List<String> headerList = getRow(0, sheet);

            Document document = new Document();
            document.addLanguage("ru");
            String fileName = "src/main/resources/templates/customer_debts.pdf";

            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            Paragraph title = new Paragraph(resourceBundle.getString(MAIL_MESSAGE_PDF_TITLE), new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD));
            title.setIndentationLeft(30);

            document.open();
            document.addTitle("Info customer debts");
            document.top(50);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
            table.setWidthPercentage(100);
            addPDFData(true, headerList, table);
            document.add(table);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                List<String> rowList = getRow(i, sheet);
                table = new PdfPTable(sheet.getRow(i).getPhysicalNumberOfCells());
                table.setWidthPercentage(100);
                addPDFData(false, rowList, table);
                document.add(table);
            }
            document.add(new Paragraph(" "));
            document.add(new Paragraph(resourceBundle.getString(MAIL_MESSAGE_FOR_COMMUNICATION) + "  +998950645097"));
            document.add(new Paragraph(resourceBundle.getString(MAIL_MESSAGE_RESPONSIBLE_PERSON) + "  D.To'lqinjonov"));

            document.close();
            workbook.close();
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage());
        }
        log.info("Successfully successfully converting excel file to PDF");
    }

    public static List<String> getRow(int index, Sheet sheet) {
        List<String> list = new ArrayList<>();

        for (Cell cell : sheet.getRow(index)) {
            switch (cell.getCellType()) {
                case STRING -> list.add(cell.getStringCellValue());
                case NUMERIC -> list.add(String.valueOf(cell.getNumericCellValue()));
                case BOOLEAN -> list.add(String.valueOf(cell.getBooleanCellValue()));
                case FORMULA -> list.add(cell.getCellFormula().toString());
            }
        }
        return list;
    }

    private static void addPDFData(boolean isHeader, List<String> list, PdfPTable table) {
        list.forEach(column -> {
                PdfPCell header = new PdfPCell();
                if (isHeader) {
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setFixedHeight(34);
                }
                header.setPhrase(new Phrase(column));
                table.addCell(header);
            });
    }
}
