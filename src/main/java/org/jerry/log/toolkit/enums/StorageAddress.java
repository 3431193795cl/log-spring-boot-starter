package org.jerry.log.toolkit.enums;

import lombok.Getter;

@Getter
public enum StorageAddress {
    COOKIE("Cookie"),
    SESSION("session");

    private String value;

    StorageAddress(String value) {
        this.value = value;
    }

}
