package ma.youcode.supplyChainX.unit.service;

import ma.youcode.supplyChainX.model.Supplier;
import ma.youcode.supplyChainX.model.SupplyOrder;
import ma.youcode.supplyChainX.repository.SupplierRepository;
import ma.youcode.supplyChainX.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        supplier = new Supplier(1L, "DHL", "0661000000", 4.5, 5, null);
    }

    // save()
    @Test
    void shouldSaveSupplierSuccessfully() {
        when(supplierRepository.existsByName("DHL")).thenReturn(false);
        when(supplierRepository.existsByContact("0661000000")).thenReturn(false);
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        Supplier saved = supplierService.save(supplier);

        assertEquals("DHL", saved.getName());
    }

    @Test
    void shouldThrowException_WhenSupplierNameExists() {
        when(supplierRepository.existsByName("DHL")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> supplierService.save(supplier));
    }

    @Test
    void shouldThrowException_WhenSupplierContactExists() {
        when(supplierRepository.existsByName("DHL")).thenReturn(false);
        when(supplierRepository.existsByContact("0661000000")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> supplierService.save(supplier));
    }

    // update()
    @Test
    void shouldUpdateSupplierSuccessfully() {
        Supplier updatedData = new Supplier(null, "NewName", "0777888999",
                5, 3, null);

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByName("NewName")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        Supplier updated = supplierService.update(updatedData, 1L);

        assertEquals("NewName", updated.getName());
        assertEquals("0777888999", updated.getContact());
    }

    @Test
    void shouldThrowException_WhenUpdatingWithExistingName() {
        Supplier updatedData = new Supplier(null, "Existing", "000",
                5, 4, null);

        Supplier conflictSupplier = new Supplier(2L, "Existing", "000",
                5, 4, null);

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByName("Existing")).thenReturn(true);
        when(supplierRepository.findByName("Existing")).thenReturn(conflictSupplier);

        assertThrows(IllegalArgumentException.class,
                () -> supplierService.update(updatedData, 1L));
    }

    // findAll()
    @Test
    void shouldReturnSuppliersList() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));

        List<Supplier> result = supplierService.findAll();

        assertEquals(1, result.size());
    }

    // deleteById()
    @Test
    void shouldDeleteSupplierSuccessfully() {
        supplier.setSupplyOrders(List.of()); // empty orders

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        Supplier deleted = supplierService.deleteById(1L);

        assertEquals("DHL", deleted.getName());
        verify(supplierRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowException_WhenDeletingSupplierWithOrders() {
        supplier.setSupplyOrders(List.of(new SupplyOrder())); // simulate existing orders

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        assertThrows(IllegalStateException.class,
                () -> supplierService.deleteById(1L));
    }

    @Test
    void shouldThrowException_WhenSupplierToDeleteNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> supplierService.deleteById(1L));
    }

    // findByName()
    @Test
    void shouldFindSupplierByName() {
        when(supplierRepository.existsByName("DHL")).thenReturn(true);
        when(supplierRepository.findByName("DHL")).thenReturn(supplier);

        Supplier found = supplierService.findByName("DHL");

        assertEquals("DHL", found.getName());
    }

    @Test
    void shouldThrowException_WhenSupplierNameNotFound() {
        when(supplierRepository.existsByName("DHL")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> supplierService.findByName("DHL"));
    }
}
