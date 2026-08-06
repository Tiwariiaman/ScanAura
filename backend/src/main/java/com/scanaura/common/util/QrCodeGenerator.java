package com.scanaura.common.util;

import java.util.UUID;

public final class QrCodeGenerator {

    private QrCodeGenerator() {
    }

    /**
     * Generate Digital QR
     * Example:
     * SA-D-8AF2BC91
     */
    public static String generateDigitalQrCode() {

        return "SA-D-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    /**
     * Generate Physical QR
     * Example:
     * SA-P-000001
     */
    public static String generatePhysicalQrCode(long sequence) {

        return String.format("SA-P-%06d", sequence);
    }

}