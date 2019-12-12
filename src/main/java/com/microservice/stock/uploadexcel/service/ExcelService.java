package com.microservice.stock.uploadexcel.service;

import com.microservice.stock.uploadexcel.dao.StockPriceRepository;
import com.microservice.stock.uploadexcel.entity.StockPriceEntity;
import com.microservice.stock.uploadexcel.web.response.UploadExcelResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    StockPriceRepository stockPriceRepository;

    @Value("${excel.upload.path}")
    private String excelStoragePath;

    public void uploadExcel(MultipartFile file) throws Exception {
        try {
            File fileStoragePath = new File(excelStoragePath + file.getOriginalFilename());
            FileOutputStream out = new FileOutputStream(fileStoragePath);
            out.write(file.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new Exception("Upload Excel failed");
        }
    }

    public List<StockPriceEntity> importExcelData2DB(MultipartFile file) throws Exception {
        List<StockPriceEntity> stockPriceList = new ArrayList<>();
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = file.getInputStream();
            workbook = this.getWorkbook(inputStream, file.getOriginalFilename());
            if (null == workbook) {
                throw new Exception("workbook is empty！");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            for (Row row : workbook.getSheetAt(0)) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                StockPriceEntity stockPriceEntity = new StockPriceEntity();
                stockPriceEntity.setCompanyCode(row.getCell(0).getStringCellValue());
                stockPriceEntity.setStockExchange(row.getCell(1).getStringCellValue());
                stockPriceEntity.setPrice(row.getCell(2).getNumericCellValue());
                stockPriceEntity.setChangeDate(row.getCell(3).getDateCellValue());
                stockPriceEntity.setChangeTimestamp(sdf.parse(row.getCell(4).getStringCellValue()));
                stockPriceList.add(stockPriceEntity);
            }
            for (StockPriceEntity stockItem : stockPriceList) {
                stockPriceRepository.saveAndFlush(stockItem);
            }
        } catch (Exception e) {
            throw new Exception("Import Data to  DB failed");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
        return stockPriceList;
    }

    public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook workbook = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (".xls".equals(fileType)) {
            workbook = new HSSFWorkbook(inStr);
        } else if (".xlsx".equals(fileType)) {
            workbook = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("Invalid Excel！");
        }
        return workbook;
    }

    public UploadExcelResponse getUploadSummary(List<StockPriceEntity> stockPriceList){
        UploadExcelResponse response = new UploadExcelResponse();
        if (CollectionUtils.isNotEmpty(stockPriceList)) {
            response.setRecordNumber(stockPriceList.size());
            response.setCompanyName(stockPriceList.get(0).getCompanyCode());
            response.setStockExchange(stockPriceList.get(0).getStockExchange());
            stockPriceList.sort(Comparator.comparing(StockPriceEntity::getChangeDate));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            response.setFromDate(sdf.format(stockPriceList.get(0).getChangeDate()));
            response.setToDate(sdf.format(stockPriceList.get(stockPriceList.size()-1).getChangeDate()));
        }
        return response;
    }

}
