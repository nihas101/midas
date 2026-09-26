package de.nihas101.midas.headless.config;

import de.nihas101.midas.commons.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

    private MoneyAmountValueSerializer moneyAmountValueSerializer;
    private MoneyAmountValueDeserializer moneyAmountValueDeserializer;

    @Bean
    public JsonMapper jsonMapper() {
        final SimpleModule moneyAmountModule = new SimpleModule("moneyAmountModule");
        moneyAmountModule.addSerializer(MoneyAmount.class, new MoneyAmountValueSerializer());
        moneyAmountModule.addDeserializer(MoneyAmount.class, new MoneyAmountValueDeserializer());

        return JsonMapper.builder()
                .addModule(moneyAmountModule)
                .build();
    }

}
