package com.microservice.stock.uploadexcel.dao;

import com.microservice.stock.uploadexcel.entity.StockPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPriceEntity, Long> {
}
