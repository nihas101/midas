package de.nihas101.midas.core.export.pdf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CachedHeaderImageDataTest {

    @Mock
    private HeaderImageData headerImageData;

    @InjectMocks
    private CachedHeaderImageData cachedHeaderImageData;

    private static final String DATA_URI = "data:image/png;base64,abc123";

    @BeforeEach
    void setUp() {
        when(headerImageData.dataUri()).thenReturn(DATA_URI);
    }

    @Test
    void dataUri_returnsValueFromDelegate_onFirstCall() {
        assertEquals(DATA_URI, cachedHeaderImageData.dataUri());
    }

    @Test
    void dataUri_returnsCachedValue_onSubsequentCalls() {
        cachedHeaderImageData.dataUri();
        cachedHeaderImageData.dataUri();
        cachedHeaderImageData.dataUri();

        // Delegate must only be called once despite multiple invocations
        verify(headerImageData, times(1)).dataUri();
    }

    @Test
    void dataUri_returnsNull_whenDelegateReturnsNull() {
        when(headerImageData.dataUri()).thenReturn(null);

        assertNull(cachedHeaderImageData.dataUri());
    }

    @Test
    void dataUri_callsDelegateAgain_whenCachedValueIsNull() {
        // Delegate returns null — cache should NOT store null, so the delegate is called every time
        when(headerImageData.dataUri()).thenReturn(null);

        cachedHeaderImageData.dataUri();
        cachedHeaderImageData.dataUri();

        verify(headerImageData, times(2)).dataUri();
    }

    @Test
    void dataUri_callsDelegateAgain_whenCachedValueIsBlank() {
        // Delegate returns blank — cache should NOT store blank, so the delegate is called every time
        when(headerImageData.dataUri()).thenReturn("   ");

        cachedHeaderImageData.dataUri();
        cachedHeaderImageData.dataUri();

        verify(headerImageData, times(2)).dataUri();
    }
}