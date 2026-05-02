package de.nihas101.midas.ui.export;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.export.ExportFactory;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.DatePickerI18nProvider;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

@Slf4j
@Route("export")
@PageTitle("Export")
public class ExportView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.DOWNLOAD;

    private final ShareholdersService shareholdersService;
    private final MessageSource messageSource;
    private final ExportFactory exportFactory;

    private MultiSelectComboBox<Shareholder> shareholderPicker;
    private Checkbox selectAllCheckbox;
    private CheckboxGroup<String> viewPicker;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private CheckboxGroup<String> formatPicker;
    private Button exportButton;
    private final VerticalLayout mainContent;

    public ExportView(
            final ShareholdersService shareholdersService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver,
            final ExportFactory exportFactory
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.messageSource = messageSource;
        this.exportFactory = exportFactory;

        mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.setSpacing(true);
        mainContent.setPadding(true);
        mainContent.setAlignItems(FlexComponent.Alignment.START);

        mainContent.add(new H2(messageSource.getMessage("export", null, getLocale())));

        // Form container to keep elements left-aligned but centered as a group
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("600px");
        formContainer.setPadding(false);
        formContainer.setSpacing(true);
        formContainer.setAlignItems(FlexComponent.Alignment.START);

        setupShareholderSelection(formContainer);
        setupViewSelection(formContainer);
        setupDateSelection(formContainer);
        setupFormatSelection(formContainer);

        mainContent.add(formContainer);
        mainContent.setAlignSelf(FlexComponent.Alignment.CENTER, formContainer);

        updateExportButtonState();

        setContent(mainContent);
    }

    private void updateExportButtonState() {
        if (shareholderPicker == null || viewPicker == null || formatPicker == null || startDatePicker == null || endDatePicker == null || exportButton == null) {
            return;
        }
        boolean shareholdersSelected = !shareholderPicker.getValue().isEmpty();
        shareholderPicker.setInvalid(!shareholdersSelected);
        shareholderPicker.setErrorMessage(shareholdersSelected ? "" : messageSource.getMessage("export.validation.no-shareholders", null, getLocale()));

        boolean viewsSelected = !viewPicker.getValue().isEmpty();
        viewPicker.setInvalid(!viewsSelected);
        viewPicker.setErrorMessage(viewsSelected ? "" : messageSource.getMessage("export.validation.no-views", null, getLocale()));

        boolean formatsSelected = !formatPicker.getValue().isEmpty();
        formatPicker.setInvalid(!formatsSelected);
        formatPicker.setErrorMessage(formatsSelected ? "" : messageSource.getMessage("export.validation.no-formats", null, getLocale()));

        boolean datesValid = startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null &&
                !endDatePicker.getValue().isBefore(startDatePicker.getValue());
        endDatePicker.setInvalid(!datesValid);
        endDatePicker.setErrorMessage(datesValid ? "" : messageSource.getMessage("export.validation.invalid-dates", null, getLocale()));

        exportButton.setEnabled(shareholdersSelected && viewsSelected && formatsSelected && datesValid);
    }

    private void setupShareholderSelection(VerticalLayout content) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.setWidthFull();

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
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.add(layout);
    }

    private void setupViewSelection(VerticalLayout content) {
        final List<String> allViews = List.of("bookings", "interest", "account-statements");

        viewPicker = new CheckboxGroup<>(messageSource.getMessage("export.views.label", null, getLocale()));
        viewPicker.setItems(allViews);
        viewPicker.setItemLabelGenerator(key -> messageSource.getMessage("export.view." + key, null, getLocale()));
        viewPicker.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        viewPicker.addValueChangeListener(e -> updateExportButtonState());

        // Set defaults
        viewPicker.select(allViews);

        content.add(viewPicker);
    }

    private void setupDateSelection(VerticalLayout content) {
        HorizontalLayout dateLayout = new HorizontalLayout();
        dateLayout.setSpacing(true);
        dateLayout.setWidthFull();
        dateLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

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
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.END);

        final List<String> allFormats = List.of("xlsx", "pdf");

        formatPicker = new CheckboxGroup<>(messageSource.getMessage("export.formats.label", null, getLocale()));
        formatPicker.setItems(allFormats);
        formatPicker.setItemLabelGenerator(key -> messageSource.getMessage("export.format." + key, null, getLocale()));
        formatPicker.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        formatPicker.select("pdf");
        formatPicker.addValueChangeListener(e -> updateExportButtonState());

        exportButton = new Button(
                messageSource.getMessage("export.button", null, getLocale()),
                VaadinIcon.DOWNLOAD.create()
        );
        exportButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
        exportButton.addClickListener(e -> runExport());

        layout.add(formatPicker, exportButton);
        layout.setFlexGrow(1, formatPicker);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        content.add(layout);
    }

    private void runExport() {
        try {
            final LocalDate from = startDatePicker.getValue();
            final LocalDate to = endDatePicker.getValue();
            final ExportRequest request = new ExportRequest(
                    List.copyOf(shareholderPicker.getValue()),
                    viewPicker.getValue(),
                    from,
                    to,
                    formatPicker.getValue()
            );

            if (request.formats().contains("xlsx")) {
                runXlsxExport(request, from, to);
            }
            if (request.formats().contains("pdf")) {
                runPdfExport(request, from, to);
            }

            Notification.show(messageSource.getMessage("export.notification.success", null, getLocale()));
        } catch (Exception e) {
            log.error("Export failed", e);
            Notification.show(messageSource.getMessage("export.notification.error", new Object[]{e.getMessage()}, getLocale()), 5000, Notification.Position.MIDDLE);
        }
    }

    private void runXlsxExport(ExportRequest request, LocalDate from, LocalDate to) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        exportFactory.createXlsxExport(request, out, getLocale())
                .trigger();

        final byte[] data = out.toByteArray();
        final String fileName = "export_" + from + "_" + to + ".xlsx";

        triggerDownload(data, fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void runPdfExport(ExportRequest request, LocalDate from, LocalDate to) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        exportFactory.createPdfExport(request, out, getLocale())
                .trigger();

        final byte[] data = out.toByteArray();

        int totalFiles = request.shareholders().size() * request.views().size();
        String fileName;
        String mimeType;

        if (totalFiles > 1) {
            fileName = "export_" + from + "_" + to + ".zip";
            mimeType = "application/zip";
        } else {
            fileName = "export_" + from + "_" + to + ".pdf";
            mimeType = "application/pdf";
        }

        triggerDownload(data, fileName, mimeType);
    }

    private void triggerDownload(byte[] data, String fileName, String mimeType) {
        final Anchor downloadAnchor = new Anchor(
                DownloadHandler.fromInputStream(event -> new DownloadResponse(
                        new ByteArrayInputStream(data),
                        fileName,
                        mimeType,
                        data.length
                )),
                ""
        );
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.getElement().getStyle().set("display", "none");
        mainContent.add(downloadAnchor);

        downloadAnchor.getElement().executeJs("this.click();");
    }

    public static Icon icon() {
        return icon.create();
    }
}
