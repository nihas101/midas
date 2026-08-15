package de.nihas101.midas.api.accountstatement;

public interface LabeledAccountStatement extends AccountStatement {

    String label();

    boolean isManualExtra();

    boolean isHidden();

    default String rowKey() {
        return new RowKey(type(), id()).toString();
    }
}
