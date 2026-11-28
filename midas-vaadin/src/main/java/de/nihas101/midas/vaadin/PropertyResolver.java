package de.nihas101.midas.vaadin;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

@RequiredArgsConstructor
public class PropertyResolver<T> {

    private final PropertyLoader propertyLoader;
    private final List<String> paramVariants;
    private final String envVariable;
    private final Function<String, T> parser;
    private final EnvironmentVariableReader environmentVariableReader;

    public PropertyResolver(
            final PropertyLoader propertyLoader,
            final List<String> paramVariants,
            final String envVariable,
            final Function<String, T> parser
    ) {
        this(
                propertyLoader,
                paramVariants,
                envVariable,
                parser,
                new EnvironmentVariableReader()
        );
    }

    public Optional<T> fromCommandLine(final String[] args) {
        if (args == null) {
            return Optional.empty();
        }

        for (String arg : args) {
            for (final String expectedArg : paramVariants) {
                final String prefix = "--" + expectedArg + "=";
                if (arg.startsWith(prefix)) {
                    return Optional.of(arg.substring(prefix.length()))
                            .map(parser);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<T> fromEnvironmentVariables() {
        if (envVariable == null) {
            return Optional.empty();
        }

        final String envLaunchBrowser = environmentVariableReader.getEnvironmentVariable(envVariable);
        return Optional.ofNullable(envLaunchBrowser).map(parser);
    }

    public Optional<T> fromProperties() {
        final Properties props = propertyLoader.loadCombinedProperties();
        for (final String paramVariant : paramVariants) {
            final String propValue = props.getProperty(paramVariant);
            if (propValue != null) {
                return Optional.of(propValue)
                        .map(parser);
            }
        }
        return Optional.empty();
    }

}