package uz.devops.intern.file.io;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfBody;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.PaymentDTO;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.MAIL_MESSAGE_PDF_TITLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvertExcelToPDF {
    private final WriteCustomerDebtsToExcel writeCustomerDebtsToExcel;
    private final PaymentService paymentService;
//    @PostConstruct
//    private void createPDF() throws DocumentException, IOException {
//        Customers customer = new Customers().id(6L);
//        Services service = new Services().id(402L);
//        Groups group = new Groups().id(352L);
//
//        List<PaymentDTO> paymentDTOList = paymentService.findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(
//            customer, service, group, LocalDate.of(2022,9,1)
//        );
//        convertToPDFFromExcel(paymentDTOList, ResourceBundle.getBundle("message", new Locale("uz")));
//    }

    public void convertToPDFFromExcel(List<PaymentDTO> paymentDTOList, ResourceBundle resourceBundle) {
        writeCustomerDebtsToExcel.writeCustomerDebtsToExcelFile(paymentDTOList, resourceBundle);

        File file = new File("src/main/resources/templates/excel.xlsx");
        FileInputStream fileInputStream = null;
        Workbook workbook = null;
        try {
            fileInputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<String> headerList = getRow(0, sheet);

            Document document = new Document();
            String fileName = "src/main/resources/templates/customer_debts.pdf";

            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 32, Font.BOLD);
            Paragraph title = new Paragraph(resourceBundle.getString(MAIL_MESSAGE_PDF_TITLE) + "\n\n");
            title.setFont(boldFont);
            title.setIndentationLeft(30);

            document.open();
            document.addTitle("Info customer debts");
            document.top(50);
            document.add(title);

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

            document.close();
            file.delete();
//            workbook.close();
//            fileInputStream.close();
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
