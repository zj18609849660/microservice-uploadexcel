package com.microservice.stock.uploadexcel.web;

import com.microservice.stock.uploadexcel.entity.StockPriceEntity;
import com.microservice.stock.uploadexcel.service.ExcelService;
import com.microservice.stock.uploadexcel.web.response.UploadExcelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/excel")
public class UploadExcelController {
    @Autowired
    ExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<UploadExcelResponse> uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        excelService.uploadExcel(file);
        List<StockPriceEntity> stockPriceList = excelService.importExcelData2DB(file);
        return new ResponseEntity<>(excelService.getUploadSummary(stockPriceList), HttpStatus.OK);
    }

}
