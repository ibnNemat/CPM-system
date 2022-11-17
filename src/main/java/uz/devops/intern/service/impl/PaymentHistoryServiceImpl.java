package uz.devops.intern.service.impl;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.repository.PaymentHistoryRepository;
import uz.devops.intern.service.PaymentHistoryService;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.service.mapper.PaymentHistoryMapper;

/**
 * Service Implementation for managing {@link PaymentHistory}.
 */
@Service
@Transactional
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

    private final Logger log = LoggerFactory.getLogger(PaymentHistoryServiceImpl.class);

    private final PaymentHistoryRepository paymentHistoryRepository;

    private final PaymentHistoryMapper paymentHistoryMapper;

    public PaymentHistoryServiceImpl(PaymentHistoryRepository paymentHistoryRepository, PaymentHistoryMapper paymentHistoryMapper) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentHistoryMapper = paymentHistoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryDTO> findAllForEmail() {
        log.debug("Request to get all PaymentHistories for email");
        List<PaymentHistoryDTO> historyList = paymentHistoryRepository
            .findAll()
            .stream()
            .map(paymentHistoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));

        String path = "src/main/resources/templates/files/histories.xlsx";
        File file = new File(path);

        XSSFWorkbook workbook = new XSSFWorkbook();

        try (FileOutputStream out = new FileOutputStream(file, true)) {

            XSSFSheet sheet = workbook.createSheet("All History");

            Row row1 = sheet.createRow(0);
            Cell cell = row1.createCell(1);
            cell.setCellValue("Show all payment history");
            sheet.addMergedRegion(new CellRangeAddress(0,0,1,4));

            XSSFRow headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("ORGANIZATION NAME");
            headerRow.createCell(2).setCellValue("SERVICE NAME");
            headerRow.createCell(3).setCellValue("GROUP NAME");
            headerRow.createCell(4).setCellValue("SUM");
            headerRow.createCell(5).setCellValue("CREAT AT");

            int rowIndex = 1;

            for (PaymentHistoryDTO historyDTO : historyList) {
                rowIndex++;
                XSSFRow row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue(historyDTO.getId());
                row.createCell(1).setCellValue(historyDTO.getOrganizationName());
                row.createCell(2).setCellValue(historyDTO.getServiceName());
                row.createCell(3).setCellValue(historyDTO.getGroupName());
                row.createCell(4).setCellValue(historyDTO.getSum());
                row.createCell(5).setCellValue(historyDTO.getCreatedAt());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return historyList;
    }

    @Override
    public PaymentHistoryDTO save(PaymentHistoryDTO paymentHistoryDTO) {
        log.debug("Request to save PaymentHistory : {}", paymentHistoryDTO);
        PaymentHistory paymentHistory = paymentHistoryMapper.toEntity(paymentHistoryDTO);
        paymentHistory = paymentHistoryRepository.save(paymentHistory);
        return paymentHistoryMapper.toDto(paymentHistory);
    }

    @Override
    public PaymentHistoryDTO update(PaymentHistoryDTO paymentHistoryDTO) {
        log.debug("Request to update PaymentHistory : {}", paymentHistoryDTO);
        PaymentHistory paymentHistory = paymentHistoryMapper.toEntity(paymentHistoryDTO);
        paymentHistory = paymentHistoryRepository.save(paymentHistory);
        return paymentHistoryMapper.toDto(paymentHistory);
    }

    @Override
    public Optional<PaymentHistoryDTO> partialUpdate(PaymentHistoryDTO paymentHistoryDTO) {
        log.debug("Request to partially update PaymentHistory : {}", paymentHistoryDTO);

        return paymentHistoryRepository
            .findById(paymentHistoryDTO.getId())
            .map(existingPaymentHistory -> {
                paymentHistoryMapper.partialUpdate(existingPaymentHistory, paymentHistoryDTO);

                return existingPaymentHistory;
            })
            .map(paymentHistoryRepository::save)
            .map(paymentHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryDTO> findAll() {
        log.debug("Request to get all PaymentHistories");
        return paymentHistoryRepository
            .findAll()
            .stream()
            .map(paymentHistoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentHistoryDTO> findOne(Long id) {
        log.debug("Request to get PaymentHistory : {}", id);
        return paymentHistoryRepository.findById(id).map(paymentHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PaymentHistory : {}", id);
        paymentHistoryRepository.deleteById(id);
    }
}
