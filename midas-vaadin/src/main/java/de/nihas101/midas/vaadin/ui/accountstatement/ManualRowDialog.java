package de.nihas101.midas.vaadin.ui.accountstatement;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import de.nihas101.midas.api.accountstatement.AccountStatementService;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.vaadin.ui.common.MoneyAmountField;
import de.nihas101.midas.vaadin.ui.common.SaveButton;
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
            final UIConfig uiConfig,
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

        final Binder<Booking> binder = new Binder<>();
        final MoneyAmountField<Booking> moneyAmountField = new MoneyAmountField<>(
                messageSource,
                binder,
                messageSource.getMessage("bookings.amount", null, locale),
                locale,
                uiConfig,
                Booking::getAmount,
                Booking::setAmount
        );
        moneyAmountField.setWidthFull();

        layout.add(labelField, moneyAmountField);
        this.add(layout);

        final Button saveButton = new SaveButton(
                messageSource.getMessage("global.save", null, locale),
                e -> {
                    final BinderValidationStatus<Booking> validationStatus = binder.validate();
                    if (!validationStatus.isOk()) {
                        validationStatus.getValidationErrors()
                                .stream()
                                .map(ValidationResult::getErrorMessage)
                                .filter(StringUtils::isNotBlank)
                                .findFirst()
                                .ifPresent(Notification::show);
                        return;
                    }

                    handleSave(
                            messageSource,
                            accountStatementService,
                            shareholder,
                            year,
                            afterSave,
                            locale,
                            labelField,
                            moneyAmountField
                    );
                }
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
            final MoneyAmountField<Booking> amountField
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
                null,
                shareholder,
                year,
                labelVal,
                newAmount
        );
        this.close();
        afterSave.run();
    }
}
