package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.combobox.ComboBox;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.dto.Shareholders;
import de.nihas101.midas.shareholders.service.ShareholdersService;

public class ShareholderPicker extends ComboBox<Shareholder> {

    public ShareholderPicker(
            final String label,
            final ShareholdersService shareholdersService,
            final ValueChangeListener<ComponentValueChangeEvent<ComboBox<Shareholder>, Shareholder>> changeListener
    ) {
        this(
                label,
                shareholdersService.shareholders(),
                changeListener
        );
    }

    public ShareholderPicker(
            final String label,
            final Shareholders shareholders,
            final ValueChangeListener<ComponentValueChangeEvent<ComboBox<Shareholder>, Shareholder>> changeListener
    ) {
        super(label);
        this.setMinWidth("20em");
        this.setItems(shareholders.toList());
        this.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName() + " (" + s.getDisplayId() + ")");
        this.setPlaceholder("Search by name or ID..."); // TODO: i18n
        this.setClearButtonVisible(true);
        this.addValueChangeListener(changeListener);
    }
}
