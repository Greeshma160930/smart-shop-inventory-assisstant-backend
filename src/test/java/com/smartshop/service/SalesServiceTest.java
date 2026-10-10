package com.smartshop.service;

import com.smartshop.dto.SaleRequest;
import com.smartshop.entity.InventoryLog;
import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import com.smartshop.entity.User;
import com.smartshop.exception.InsufficientStockException;
import com.smartshop.repository.InventoryLogRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryLogRepository inventoryLogRepository;

    @Mock
    private AlertService alertService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private SalesService salesService;

    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Basmati Rice 5kg")
                .sku("SKU-RICE-001")
                .price(BigDecimal.valueOf(550.00))
                .stockQuantity(30)
                .minStockLevel(10)
                .build();

        testUser = User.builder()
                .id(1L)
                .name("Demo User")
                .email("demo@smartshop.com")
                .build();

        lenient().when(authService.getCurrentUser()).thenReturn(testUser);
        lenient().when(saleRepository.save(any(Sale.class))).thenAnswer(i -> {
            Sale s = i.getArgument(0);
            s.setId(10L);
            return s;
        });
        lenient().when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        lenient().when(inventoryLogRepository.save(any(InventoryLog.class))).thenAnswer(i -> {
            InventoryLog log = i.getArgument(0);
            log.setId(20L);
            return log;
        });
    }

    @Test
    @DisplayName("Test successful sale records sale, reduces stock, logs inventory, and checks alerts")
    void testRecordSaleSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        SaleRequest request = SaleRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();

        Sale recordedSale = salesService.recordSale(request);

        assertNotNull(recordedSale);
        assertEquals(5, recordedSale.getQuantitySold());
        assertEquals(BigDecimal.valueOf(2750.00), recordedSale.getTotalAmount());
        assertEquals(25, testProduct.getStockQuantity(), "Stock should be reduced from 30 to 25");

        // Verify product was saved with reduced stock
        verify(productRepository).save(testProduct);

        // Verify sale was saved
        verify(saleRepository).save(any(Sale.class));

        // Verify inventory log was saved with SALE type and -5 change
        ArgumentCaptor<InventoryLog> logCaptor = ArgumentCaptor.forClass(InventoryLog.class);
        verify(inventoryLogRepository).save(logCaptor.capture());
        InventoryLog savedLog = logCaptor.getValue();
        assertEquals("SALE", savedLog.getChangeType());
        assertEquals(-5, savedLog.getQuantityChange());
        assertEquals(25, savedLog.getResultingStock());

        // Verify alert check was triggered
        verify(alertService).checkAndCreateAlerts(testProduct);
    }

    @Test
    @DisplayName("Test sale is rejected when requested quantity exceeds available stock")
    void testRecordSaleInsufficientStock() {
        testProduct.setStockQuantity(3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        SaleRequest request = SaleRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> salesService.recordSale(request)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(saleRepository, never()).save(any());
        verify(inventoryLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test sale is rejected when quantity is non-positive")
    void testRecordSaleInvalidQuantity() {
        SaleRequest request = SaleRequest.builder()
                .productId(1L)
                .quantity(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> salesService.recordSale(request));
        verify(saleRepository, never()).save(any());
    }
}
