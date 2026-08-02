package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.combobox.ComboBox;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class ShareholderPicker extends ComboBox<Shareholder> {

    public ShareholderPicker(
            final MessageSource messageSource,
            final Locale locale,
            final ShareholdersService shareholdersService,
            final QueryParameter<?, Shareholder> queryParameter
    ) {
        this(
                messageSource.getMessage("bookings.shareholder", null, locale),
                messageSource.getMessage("shareholder-picker.placeholder", null, locale),
                shareholdersService,
                queryParameter
        );
    }

    private ShareholderPicker(
            final String label,
            final String placeholder,
            final ShareholdersService shareholdersService,
            final QueryParameter<?, Shareholder> queryParameter
    ) {
        this(
                label,
                shareholdersService.shareholders(),
                queryParameter,
                placeholder
        );
    }

    private ShareholderPicker(
            final String label,
            final Shareholders shareholders,
            final QueryParameter<?, Shareholder> queryParameter,
            final String placeholder
    ) {
        super(label);
        this.setMinWidth("20em");
        this.setItems(shareholders.toList());
        this.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName() + " (" + s.getDisplayId() + ")");
        this.setPlaceholder(placeholder);
        this.setClearButtonVisible(true);
        this.addValueChangeListener(queryParameter);
    }
}
