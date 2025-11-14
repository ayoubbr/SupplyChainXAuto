package ma.youcode.supplyChainX.unit.service;

import ma.youcode.supplyChainX.model.RawMaterial;
import ma.youcode.supplyChainX.model.SupplyOrderRawMaterial;
import ma.youcode.supplyChainX.repository.RawMaterialRepository;
import ma.youcode.supplyChainX.service.RawMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    private RawMaterial material;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        material = new RawMaterial(1L, "Fer", 50, 20, "kg",
                null, null, Collections.emptyList());
    }

    // save()
    @Test
    void shouldSaveRawMaterial() {
        when(rawMaterialRepository.existsByName("Fer")).thenReturn(false);
        when(rawMaterialRepository.save(material)).thenReturn(material);

        RawMaterial saved = rawMaterialService.save(material);

        assertEquals("Fer", saved.getName());
    }

    @Test
    void shouldThrowException_WhenNameExists() {
        when(rawMaterialRepository.existsByName("Fer")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> rawMaterialService.save(material));
    }

    // update()
    @Test
    void shouldUpdateRawMaterialSuccessfully() {
        RawMaterial updateData = new RawMaterial(null, "Acier", 100,
                30, "kg", null, null, Collections.emptyList());

        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(material);

        RawMaterial updated = rawMaterialService.update(updateData, 1L);

        assertEquals("Acier", updated.getName());
        assertEquals(100, updated.getStock());
    }

    @Test
    void shouldThrowException_WhenRawMaterialNotFoundForUpdate() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> rawMaterialService.update(material, 1L));
    }

    // deleteById()
    @Test
    void shouldDeleteRawMaterialSuccessfully() {
        material.setSupplyOrderRawMaterials(Collections.emptyList());

        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(material));

        rawMaterialService.deleteById(1L);

        verify(rawMaterialRepository, times(1)).delete(material);
    }

    @Test
    void shouldThrowException_WhenDeletingRawMaterialWithOrders() {
        material.setSupplyOrderRawMaterials(List.of(new SupplyOrderRawMaterial()));

        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(material));

        assertThrows(IllegalArgumentException.class,
                () -> rawMaterialService.deleteById(1L));
    }

    @Test
    void shouldThrowException_WhenRawMaterialNotFoundForDelete() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> rawMaterialService.deleteById(1L));
    }

    // getRawMaterialsBelowStock()
    @Test
    void shouldReturnRawMaterialsBelowStock() {
        RawMaterial low = new RawMaterial(2L, "Cuivre", 5,
                20, "kg", null, null, List.of());

        when(rawMaterialRepository.findAll()).thenReturn(List.of(material, low));

        List<RawMaterial> result = rawMaterialService.getRawMaterialsBelowStock();

        assertEquals(1, result.size());
        assertEquals("Cuivre", result.get(0).getName());
    }

    // findByName()
    @Test
    void shouldFindRawMaterialByName() {
        when(rawMaterialRepository.existsByName("Fer")).thenReturn(true);
        when(rawMaterialRepository.findByName("Fer")).thenReturn(List.of(material));

        List<RawMaterial> result = rawMaterialService.findByName("Fer");

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowException_WhenNameNotFound() {
        when(rawMaterialRepository.existsByName("Fer")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> rawMaterialService.findByName("Fer"));
    }
}

