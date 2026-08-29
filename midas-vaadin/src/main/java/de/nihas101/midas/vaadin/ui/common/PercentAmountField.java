package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.ValueProvider;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
public class PercentAmountField<T> extends BigDecimalField {

    public PercentAmountField(
            final String label,
            final Locale locale
    ) {
        super(label);
        setLocale(locale);
        setSuffixComponent(new Span("%"));
    }

    public PercentAmountField(
            final MessageSource messageSource,
            final Binder<T> binder,
            final String label,
            final Locale locale,
            final ValueProvider<T, MoneyAmount> amountSupplier,
            final Setter<T, MoneyAmount> amountConsumer
    ) {
        this(label, locale);

        binder.forField(this)
                .asRequired()
                .withValidator((Validator<BigDecimal>) (value, context) -> value != null && value.doubleValue() >= 0
                                ? ValidationResult.ok()
                                : ValidationResult.error(
                                messageSource.getMessage(
                                        "percent.amount.error",
                                        null,
                                        locale
                                )
                        )
                ).withConverter(
                        MoneyAmount::of,
                        m -> m != null ? m.toBigDecimalForInput() : null
                )
                .bind(
                        amountSupplier,
                        amountConsumer
                );
    }
}
