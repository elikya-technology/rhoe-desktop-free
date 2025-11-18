/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.service;

import tech.elikya.apps.rhoe.desk.entity.Product;
import tech.elikya.apps.rhoe.desk.entity.ProductLog;
import tech.elikya.apps.rhoe.desk.entity.Sale;
import tech.elikya.apps.rhoe.desk.entity.SaleLine;
import tech.elikya.apps.rhoe.desk.repository.SaleLineRepository;
import tech.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleLineService {

    private SaleLineRepository saleLineRepository;
    private ProductService productService;
    private ProductLogService productLogService;

    @Autowired
    private void setSaleLineRepository(SaleLineRepository saleLineRepository) {
        this.saleLineRepository = saleLineRepository;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    public List<SaleLine> getFromSale(Sale sale) {
        List<SaleLine> saleLines = saleLineRepository.queryBySaleId(sale.getId());
        convertLinesPrice(saleLines);
        includeLinesProductsTaxes(saleLines);
        computeLinesUnitPrices(saleLines);
        convertLinesProductsUnitPrices(saleLines);
        return saleLines;
    }

    private void computeLinesUnitPrices(List<SaleLine> saleLines) {
        saleLines.forEach(it -> {
            BigDecimal unitPrice = it.getPrice().divide(BigDecimal
                    .valueOf(it.getQuantity()), 3);
            it.setUnitPrice(unitPrice);
        });
    }

    private void includeLinesProductsTaxes(List<SaleLine> saleLines) {
        List<Product> products = collectLinesProducts(saleLines);
        this.productService.includeTax(products);
    }

    private void convertLinesProductsUnitPrices(List<SaleLine> saleLines) {
        List<Product> products = collectLinesProducts(saleLines);
        this.productService.convertUnitPrice(products);
    }

    private List<Product> collectLinesProducts(List<SaleLine> saleLines) {
        return saleLines.stream().map(SaleLine::getProduct)
                .distinct().collect(Collectors.toList());
    }

    public List<SaleLine> getFromProduct(Product product, LocalDate from, LocalDate to) {
        List<SaleLine> saleLines = this.saleLineRepository.queryByProduct(product.getId(), from, to);
        convertLinesPrice(saleLines);
        return saleLines;
    }

    private void convertLinesPrice(List<SaleLine> saleLines) {
        saleLines.forEach(it -> {
            if (ApplicationCurrency.advancedOptionsAreEnabled()) {
                multiplyPrice(it);
            }
        });
    }

    private void multiplyPrice(SaleLine saleLine) {
        double rate = getConversionRate(saleLine);
        BigDecimal price = saleLine.getPrice().multiply(BigDecimal.valueOf(rate));
        saleLine.setPrice(price);
    }

    private double getConversionRate(SaleLine saleLine) {
        Sale sale = saleLine.getSale();
        if (sale.getCurrency().toLowerCase()
                .equals(ApplicationCurrency.getDefaultCurrency().toLowerCase()))
            return ApplicationCurrency.getActualRate();
        return sale.getRate();
    }

    public List<SaleLine> getFromProduct(Product product) {
        List<SaleLine> lines = this.saleLineRepository.queryByProduct(product.getId());
        convertLinesPrice(lines);
        return lines;
    }

    public boolean anyProductIsSold(List<Product> products) {
        Set<Integer> ids = getProductsIds(products);
        Optional<Integer> maxProductId = Optional.ofNullable(saleLineRepository.getMaxOfProductsIds(ids));
        return maxProductId.isPresent();
    }

    private Set<Integer> getProductsIds(List<Product> products) {
        return products.stream().map(Product::getId).collect(Collectors.toSet());
    }

    public void saveAll(List<SaleLine> saleLines) {
        convertPricesToDefaultCurrency(saleLines);
        this.saleLineRepository.saveAll(saleLines);
    }

    public void convertPricesToDefaultCurrency(List<SaleLine> saleLines) {
        if (ApplicationCurrency.advancedOptionsAreEnabled()) {
            saleLines.forEach(it -> {
                BigDecimal price = it.getProduct().getUnitPriceTax()
                        .multiply(BigDecimal.valueOf(it.getQuantity()));
                it.setPrice(price);
            });
        }
    }

    public void deleteFromSales(List<Sale> sales) {
        List<SaleLine> lines = extractSalesLines(sales);
        logProducts(lines);
        updateProductsQuantities(lines);
        List<Integer> salesIds = extractSalesIds(sales);
        this.saleLineRepository.deleteFromSalesIds(salesIds);
    }

    private void logProducts(List<SaleLine> lines) {
        List<ProductLog> productsLogs = getProductsDeletionLogs(lines);
        this.productLogService.saveAll(productsLogs);
    }

    private List<ProductLog> getProductsDeletionLogs(List<SaleLine> saleLines) {
        return saleLines.stream().map(it -> {
            Product product = getLoggableProduct(it);
            return ProductLog.builder().actionQty(it.getQuantity()).logAction("sale_deletion")
                    .logDate(LocalDate.now()).logTime(LocalTime.now()).product(product)
                    .stockQty(product.getStockQuantity() + it.getQuantity())
                    .unitPrice(product.getUnitPriceTax())
                    .actualCurrency(ApplicationCurrency.getActualCurrency())
                    .currencyRate(ApplicationCurrency.getActualRate())
                    .build();
        }).collect(Collectors.toList());
    }

    private Product getLoggableProduct(SaleLine it) {
        Optional<Product> product = this.productService.getFromId(it.getProduct().getId());
        if (!product.isPresent()) product = Optional.of(it.getProduct());
        return product.get();
    }

    private void updateProductsQuantities(List<SaleLine> lines) {
        List<Product> products = extractLinesProducts(lines);
        this.productService.updateProductsQty(products);
    }

    private List<Product> extractLinesProducts(List<SaleLine> saleLines) {
        return saleLines.stream().map(this::increaseProductQty).collect(Collectors.toList());
    }

    private List<SaleLine> extractSalesLines(List<Sale> sales) {
        return sales.stream().flatMap(it -> it.getLines()
                .stream()).collect(Collectors.toList());
    }

    private Product increaseProductQty(SaleLine saleLine) {
        Product product = saleLine.getProduct();
        product.increaseQuantity(saleLine.getQuantity());
        return product;
    }

    private List<Integer> extractSalesIds(List<Sale> sales) {
        return sales.stream().map(Sale::getId).distinct().collect(Collectors.toList());
    }

    public void deleteAll(List<SaleLine> saleLines) {
        this.saleLineRepository.deleteAll(saleLines);
    }

}
