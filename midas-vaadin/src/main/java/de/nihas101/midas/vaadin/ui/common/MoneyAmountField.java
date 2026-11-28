package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.ValueProvider;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.config.UIConfig;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Locale;

public class MoneyAmountField<T> extends BigDecimalField {

    public MoneyAmountField(
            final String label,
            final Locale locale,
            final UIConfig uiConfig
    ) {
        super(label);
        setLocale(locale);
        setSuffixComponent(new Span(uiConfig.getCurrencySymbol()));
    }

    public MoneyAmountField(
            final MessageSource messageSource,
            final Binder<T> binder,
            final String label,
            final Locale locale,
            final UIConfig uiConfig,
            final ValueProvider<T, MoneyAmount> amountSupplier,
            final Setter<T, MoneyAmount> amountConsumer
    ) {
        this(
                messageSource,
                binder,
                label,
                locale,
                uiConfig,
                amountSupplier,
                amountConsumer,
                false
        );
    }

    public MoneyAmountField(
            final MessageSource messageSource,
            final Binder<T> binder,
            final String label,
            final Locale locale,
            final UIConfig uiConfig,
            final ValueProvider<T, MoneyAmount> amountSupplier,
            final Setter<T, MoneyAmount> amountConsumer,
            final boolean allowZero
    ) {
        this(label, locale, uiConfig);

        final Binder.BindingBuilder<T, BigDecimal> bindingBuilder = binder.forField(this).asRequired();
        if (!allowZero) {
            bindingBuilder.withValidator((Validator<BigDecimal>) (value, context) ->
                    value != null && value.compareTo(BigDecimal.ZERO) != 0
                            ? ValidationResult.ok()
                            : ValidationResult.error(
                            messageSource.getMessage(
                                    "bookings.amount.error",
                                    null,
                                    locale
                            )
                    )
            );
        }
        bindingBuilder.withConverter(
                        MoneyAmount::of,
                        m -> m != null ? m.toBigDecimalForInput() : null
                )
                .bind(amountSupplier, amountConsumer);
    }
}
