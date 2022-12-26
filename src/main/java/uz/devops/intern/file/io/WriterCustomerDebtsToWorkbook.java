package uz.devops.intern.file.io;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import uz.devops.intern.service.dto.PaymentDTO;
import java.util.List;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class WriterCustomerDebtsToWorkbook {
    public XSSFWorkbook writeCustomerDebtsToExcelFile(List<PaymentDTO> paymentDTOList, ResourceBundle resourceBundle) {
        log.info("current thread while writing to workbook {}" ,Thread.currentThread().getName());
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
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

        log.info("successfully written customer debts to Workbook with PaymentDTOList: {}", paymentDTOList);
        return workbook;
    }
}
