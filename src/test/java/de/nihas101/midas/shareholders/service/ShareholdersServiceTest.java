package de.nihas101.midas.shareholders.service;

import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.dto.Shareholders;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareholdersServiceTest {

    @Mock
    private ShareholdersRepository repository;

    @InjectMocks
    private ShareholdersService service;

    @Test
    void shareholders() {
        ShareholderEntity entity = new ShareholderEntity(1, 100, "Max", "Mustermann");
        when(repository.findAll()).thenReturn(List.of(entity));

        Shareholders result = service.shareholders();

        final List<Shareholder> shareholders = result.toList();
        assertEquals(1, shareholders.size());
        Shareholder dto = shareholders.getFirst();
        assertEquals(1, dto.getId());
        assertEquals("Max", dto.getFirstName());
    }

    @Test
    void create_success() {
        Shareholder dto = new Shareholder(null, 0, "Max", "Mustermann");

        service.create(dto);

        ArgumentCaptor<ShareholderEntity> captor = ArgumentCaptor.forClass(ShareholderEntity.class);
        verify(repository).save(captor.capture());
        ShareholderEntity saved = captor.getValue();
        assertNull(saved.getId());
        assertEquals("Max", saved.getFirstName());
    }

    @Test
    void create_withNullFails() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_withIdFails() {
        Shareholder dto = new Shareholder(1, 0, "Max", "Mustermann");
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void update_success() {
        Shareholder dto = new Shareholder(1, 100, "Max", "Mustermann");

        service.update(dto);

        ArgumentCaptor<ShareholderEntity> captor = ArgumentCaptor.forClass(ShareholderEntity.class);
        verify(repository).save(captor.capture());
        ShareholderEntity saved = captor.getValue();
        assertEquals(1, saved.getId());
        assertEquals("Max", saved.getFirstName());
    }

    @Test
    void update_withoutNullFails() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void update_withoutIdFails() {
        Shareholder dto = new Shareholder(null, 0, "Max", "Mustermann");
        assertThrows(IllegalArgumentException.class, () -> service.update(dto));
    }

    @Test
    void delete() {
        Shareholder dto = new Shareholder(1, 100, "Max", "Mustermann");

        service.delete(dto);

        ArgumentCaptor<ShareholderEntity> captor = ArgumentCaptor.forClass(ShareholderEntity.class);
        verify(repository).delete(captor.capture());
        assertEquals(1, captor.getValue().getId());
    }

    @Test
    void delete_withNullFails() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }
}
