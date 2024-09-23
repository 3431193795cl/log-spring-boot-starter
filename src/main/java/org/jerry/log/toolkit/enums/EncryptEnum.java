package org.jerry.log.toolkit.enums;

import lombok.Getter;

@Getter
public enum EncryptEnum {
    DES("DES"),
    DES3("DES3"),
    AES("AES");

    private final String value;

    EncryptEnum(String value) {
        this.value = value;
    }

}
