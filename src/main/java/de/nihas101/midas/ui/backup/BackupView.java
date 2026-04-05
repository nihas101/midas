package de.nihas101.midas.ui.backup;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import de.nihas101.midas.backup.service.BackupFileNameProvider;
import de.nihas101.midas.backup.service.BackupService;
import de.nihas101.midas.backup.service.BackupStatusService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Slf4j
@Route("backup")
@PageTitle("Backup")
public class BackupView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.DATABASE;

    private final BackupService backupService;
    private final BackupStatusService backupStatusService;
    private final BackupFileNameProvider fileNameProvider;
    private final MessageSource messageSource;

    private final Span lastBackupLabel;
    private final Button backupButton;
    private final ProgressBar progressBar;
    private final Span statusText;
    private final VerticalLayout content;

    public BackupView(
            final MidasConfig midasConfig,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver,
            final BackupService backupService,
            final BackupStatusService backupStatusService,
            final BackupFileNameProvider fileNameProvider,
            final MessageSource messageSource
    ) {
        super(
                midasConfig,
                userConfigService,
                messageSource,
                midasLocaleResolver
        );
        this.backupService = backupService;
        this.backupStatusService = backupStatusService;
        this.fileNameProvider = fileNameProvider;
        this.messageSource = messageSource;

        content = new VerticalLayout();

        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);

        content.add(new H2(messageSource.getMessage("backup", null, getLocale())));

        lastBackupLabel = new Span();
        updateLastBackupLabel();
        content.add(lastBackupLabel);

        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        content.add(progressBar);

        statusText = new Span();
        statusText.setVisible(false);
        content.add(statusText);

        backupButton = new Button(messageSource.getMessage("backup.create", null, getLocale()), VaadinIcon.DOWNLOAD.create());
        backupButton.addClickListener(e -> runBackup());
        content.add(backupButton);

        setContent(content);
    }

    private void updateLastBackupLabel() {
        String lastBackup = backupStatusService.getLastSuccessAt()
                .map(dt -> dt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())))
                .orElse(messageSource.getMessage("backup.never", null, getLocale()));
        lastBackupLabel.setText(messageSource.getMessage("backup.last-success", new Object[]{lastBackup}, getLocale()));
    }

    private void runBackup() {
        try {
            final byte[] backupData = backupService.createBackup();
            final String fileName = fileNameProvider.getBackupFileName();

            final Anchor downloadAnchor = new Anchor(
                    DownloadHandler.fromInputStream(e -> new DownloadResponse(
                            new ByteArrayInputStream(backupData),
                            fileName,
                            "application/zip",
                            backupData.length
                    )),
                    ""
            );
            downloadAnchor.getElement().setAttribute("download", true);
            downloadAnchor.getElement().getStyle().set("display", "none");
            content.add(downloadAnchor);

            downloadAnchor.getElement().executeJs("this.click();");

            updateLastBackupLabel();
            Notification.show(messageSource.getMessage("backup.success", null, getLocale()));
        } catch (Exception e) {
            log.error("Backup failed", e);
            Notification.show(messageSource.getMessage("backup.error", new Object[]{e.getMessage()}, getLocale()), 5000, Notification.Position.MIDDLE);
        }
    }

    public static Icon icon() {
        return icon.create();
    }
}
