package de.nihas101.midas.ui.accountstatement;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.ui.common.SaveButton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Locale;

public class ManualRowDialog extends Dialog {

    public ManualRowDialog(
            final MessageSource messageSource,
            final AccountStatementService accountStatementService,
            final Shareholder shareholder,
            final Year year,
            Runnable afterSave,
            final Locale locale
    ) {
        super();
        this.setHeaderTitle(messageSource.getMessage("account-statements.add-manual-entry", null, locale));

        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        final TextField labelField = new TextField(
                messageSource.getMessage("bookings.type", null, locale)
        );
        labelField.setWidthFull();

        final BigDecimalField amountField = new BigDecimalField(
                messageSource.getMessage("bookings.amount", null, locale)
        );
        amountField.setLocale(locale);
        amountField.setSuffixComponent(new Span("€")); // TODO: currency from config?
        amountField.setWidthFull();

        layout.add(labelField, amountField);
        this.add(layout);

        final Button saveButton = new SaveButton(
                messageSource.getMessage("global.save", null, locale),
                e -> handleSave(
                        messageSource,
                        accountStatementService,
                        shareholder,
                        year,
                        afterSave,
                        locale,
                        labelField,
                        amountField
                )
        );

        final Button cancelBtn = new Button(
                messageSource.getMessage("global.cancel", null, locale),
                e -> this.close()
        );

        this.getFooter().add(saveButton, cancelBtn);
    }

    private void handleSave(
            final MessageSource messageSource,
            final AccountStatementService accountStatementService,
            final Shareholder shareholder,
            final Year year,
            final Runnable afterSave,
            final Locale locale,
            final TextField labelField,
            final BigDecimalField amountField
    ) {
        final String labelVal = labelField.getValue();
        if (StringUtils.isBlank(labelVal)) {
            labelField.setErrorMessage(messageSource.getMessage("account-statements.type.required", null, locale));
            labelField.setInvalid(true);
            return;
        }
        final BigDecimal moneyAmount = amountField.getValue();
        if (moneyAmount == null) {
            amountField.setErrorMessage(messageSource.getMessage("bookings.amount.error", null, locale));
            amountField.setInvalid(true);
            return;
        }
        final MoneyAmount newAmount = MoneyAmount.of(moneyAmount);
        accountStatementService.saveManualExtra(
                shareholder,
                year,
                labelVal,
                newAmount
        );
        this.close();
        afterSave.run();
    }
}
