package de.nihas101.midas.ui.export;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.DatePickerI18nProvider;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

@Slf4j
@Route("export")
@PageTitle("Export")
public class ExportView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.DOWNLOAD;

    private final ShareholdersService shareholdersService;
    private final MessageSource messageSource;

    private MultiSelectComboBox<Shareholder> shareholderPicker;
    private Checkbox selectAllCheckbox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private CheckboxGroup<String> formatPicker;
    private Button exportButton;

    public ExportView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.messageSource = messageSource;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setSpacing(true);
        content.setPadding(true);

        content.add(new H2(messageSource.getMessage("export", null, getLocale())));

        setupShareholderSelection(content);
        setupDateSelection(content);
        setupFormatSelection(content);
        setupActionButton(content);

        updateExportButtonState();

        setContent(content);
    }

    private void updateExportButtonState() {
        if (shareholderPicker == null || formatPicker == null || startDatePicker == null || endDatePicker == null || exportButton == null) {
            return;
        }
        boolean shareholdersSelected = !shareholderPicker.getValue().isEmpty();
        shareholderPicker.setInvalid(!shareholdersSelected);
        shareholderPicker.setErrorMessage(shareholdersSelected ? "" : messageSource.getMessage("export.validation.no-shareholders", null, getLocale()));

        boolean formatsSelected = !formatPicker.getValue().isEmpty();
        formatPicker.setInvalid(!formatsSelected);
        formatPicker.setErrorMessage(formatsSelected ? "" : messageSource.getMessage("export.validation.no-formats", null, getLocale()));

        boolean datesValid = startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null &&
                !endDatePicker.getValue().isBefore(startDatePicker.getValue());
        endDatePicker.setInvalid(!datesValid);
        endDatePicker.setErrorMessage(datesValid ? "" : messageSource.getMessage("export.validation.invalid-dates", null, getLocale()));

        exportButton.setEnabled(shareholdersSelected && formatsSelected && datesValid);
    }

    private void setupShareholderSelection(VerticalLayout content) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);

        shareholderPicker = new MultiSelectComboBox<>(messageSource.getMessage("export.shareholders.label", null, getLocale()));
        final Set<Shareholder> allShareholders = Set.copyOf(shareholdersService.shareholders().toList());
        shareholderPicker.setItems(allShareholders);
        shareholderPicker.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName() + " (" + s.getId() + ")");
        shareholderPicker.setWidth("400px");

        selectAllCheckbox = new Checkbox(messageSource.getMessage("export.select-all.label", null, getLocale()));

        shareholderPicker.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                selectAllCheckbox.setValue(e.getValue().size() == allShareholders.size());
            }
            updateExportButtonState();
        });

        selectAllCheckbox.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                if (e.getValue()) {
                    shareholderPicker.setValue(allShareholders);
                } else {
                    shareholderPicker.deselectAll();
                }
            }
            updateExportButtonState();
        });

        // Set defaults
        shareholderPicker.setValue(allShareholders);
        selectAllCheckbox.setValue(true);

        layout.add(shareholderPicker, selectAllCheckbox);
        content.add(layout);
    }

    private void setupDateSelection(VerticalLayout content) {
        HorizontalLayout dateLayout = new HorizontalLayout();
        dateLayout.setSpacing(true);

        final DatePicker.DatePickerI18n i18n = DatePickerI18nProvider.datePickerI18n(
                messageSource,
                getLocale()
        );
        startDatePicker = new DatePicker(messageSource.getMessage("export.start-date.label", null, getLocale()));
        startDatePicker.setLocale(getLocale());
        startDatePicker.setI18n(i18n);
        startDatePicker.setValue(LocalDate.of(Year.now().getValue(), 1, 1));
        startDatePicker.addValueChangeListener(e -> updateExportButtonState());

        endDatePicker = new DatePicker(messageSource.getMessage("export.end-date.label", null, getLocale()));
        endDatePicker.setLocale(getLocale());
        endDatePicker.setI18n(i18n);
        endDatePicker.setValue(LocalDate.of(Year.now().getValue(), 12, 31));
        endDatePicker.addValueChangeListener(e -> updateExportButtonState());

        dateLayout.add(startDatePicker, endDatePicker);
        content.add(dateLayout);
    }

    private void setupFormatSelection(VerticalLayout content) {
        formatPicker = new CheckboxGroup<>(messageSource.getMessage("export.formats.label", null, getLocale()));
        formatPicker.setItems(
                messageSource.getMessage("export.format.xlsx", null, getLocale()),
                messageSource.getMessage("export.format.pdf", null, getLocale())
        );
        formatPicker.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        formatPicker.select(messageSource.getMessage("export.format.pdf", null, getLocale()));
        formatPicker.addValueChangeListener(e -> updateExportButtonState());

        content.add(formatPicker);
    }

    private void setupActionButton(VerticalLayout content) {
        exportButton = new Button(
                messageSource.getMessage("export.button", null, getLocale()),
                VaadinIcon.DOWNLOAD.create()
        );
        exportButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);

        // Mock click listener
        exportButton.addClickListener(e -> {
            log.info("Export triggered for {} shareholders, from {} to {}, formats: {}",
                    shareholderPicker.getValue().size(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue(),
                    formatPicker.getValue());
        });

        content.add(exportButton);
    }

    public static Icon icon() {
        return icon.create();
    }
}
