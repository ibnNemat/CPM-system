package uz.devops.intern.file.io;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.PaymentDTO;

import java.io.*;
import java.util.List;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class WriteCustomerDebtsToExcel {

    public void writeCustomerDebtsToExcelFile(List<PaymentDTO> paymentDTOList, ResourceBundle resourceBundle) {
        File file = new File("src/main/resources/templates/excel.xlsx");
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        Sheet sheet = workbook.getSheetAt(0);
        Row subtitle = sheet.createRow(0);
        subtitle.createCell(0).setCellValue(resourceBundle.getString(MAIL_MESSAGE_NUMBER_DEBTS));
        subtitle.createCell(1).setCellValue(resourceBundle.getString(MAIL_MESSAGE_GROUP_NAME));
        subtitle.createCell(2).setCellValue(resourceBundle.getString(MAIL_MESSAGE_TYPE_SERVICE));
        subtitle.createCell(3).setCellValue(resourceBundle.getString(MAIL_MESSAGE_CUSTOMER_USERNAME));
        subtitle.createCell(4).setCellValue(resourceBundle.getString(MAIL_MESSAGE_SERVICE_PRICE));
        subtitle.createCell(5).setCellValue(resourceBundle.getString(MAIL_MESSAGE_PAID_MONEY));
        subtitle.createCell(6).setCellValue(resourceBundle.getString(MAIL_MESSAGE_STARTED_TIME_PAYMENT));
        subtitle.createCell(7).setCellValue(resourceBundle.getString(MAIL_MESSAGE_FINISHED_TIME_PAYMENT));

        int rowId = 1;
        for (PaymentDTO paymentDTO: paymentDTOList){
            Row row = sheet.createRow(rowId++);
            row.createCell(0).setCellValue(paymentDTO.getId());
            row.createCell(1).setCellValue(paymentDTO.getGroup().getName());
            row.createCell(2).setCellValue(paymentDTO.getService().getName());
            row.createCell(3).setCellValue(paymentDTO.getCustomer().getUsername());
            row.createCell(4).setCellValue(paymentDTO.getPaymentForPeriod());
            row.createCell(5).setCellValue(paymentDTO.getPaidMoney());
            row.createCell(6).setCellValue(paymentDTO.getStartedPeriod().toString());
            row.createCell(7).setCellValue(paymentDTO.getFinishedPeriod().toString());
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("successfully written customer debts to excel file. PaymentDTOList: {}", paymentDTOList);
    }

}
