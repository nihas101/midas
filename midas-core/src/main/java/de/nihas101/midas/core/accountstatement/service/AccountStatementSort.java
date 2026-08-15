package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrderEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountStatementSort {

    public static final int MANUAL_STATEMENT_OFFSET = 1000;
    private final AccountStatementOrdersRepository accountStatementOrdersRepository;

    public List<LabeledAccountStatement> sort(
            final List<LabeledAccountStatement> allStatements,
            final Shareholder shareholder,
            final Year year
    ) {
        final List<LabeledAccountStatement> sortableList = new ArrayList<>(allStatements);
        final List<AccountStatementOrderEntity> persistedOrders = accountStatementOrdersRepository
                .findByShareholderIdAndYearOrderByPositionAsc(shareholder.getId(), year.getValue());
        if (persistedOrders.isEmpty()) {
            sortableList.sort(Comparator.comparing(this::getDefaultOrderIndex));
        }

        final Map<String, Integer> positionMap = new HashMap<>();
        for (final AccountStatementOrderEntity accountStatementOrderEntity : persistedOrders) {
            positionMap.put(accountStatementOrderEntity.getRowKey(), accountStatementOrderEntity.getPosition());
        }

        sortableList.sort((a, b) -> {
            final String keyA = a.rowKey();
            final String keyB = b.rowKey();
            final Integer posA = positionMap.get(keyA);
            final Integer posB = positionMap.get(keyB);

            if (posA != null && posB != null) {
                final int result = posA.compareTo(posB);
                if (result == 0) {
                    return this.getDefaultOrderIndex(a).compareTo(this.getDefaultOrderIndex(b));
                }
                return result;
            } else if (posA != null) {
                return -1;
            } else if (posB != null) {
                return 1;
            } else {
                return this.getDefaultOrderIndex(a).compareTo(this.getDefaultOrderIndex(b));
            }
        });
        return sortableList;
    }

    private Integer getDefaultOrderIndex(final LabeledAccountStatement statement) {
        return statement.type() != null
                ? statement.type().getSortKey()
                : MANUAL_STATEMENT_OFFSET + (statement.id() != null ? statement.id() : 0);
    }
}