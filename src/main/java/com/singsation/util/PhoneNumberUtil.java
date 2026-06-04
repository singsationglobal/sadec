package com.singsation.util;

public class PhoneNumberUtil {
    
    public static String normalizeSouthAfricanPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }
        
        String cleaned = phoneNumber.replaceAll("\\s+", "");
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }
        if (cleaned.startsWith("27") && cleaned.length() == 11) {
            cleaned = "0" + cleaned.substring(2);
        }
        if (!cleaned.matches("^0[0-9]{9}$")) {
            return null;
        }
        return cleaned;
    }
    
    public static String toInternationalFormat(String normalizedNumber) {
        if (normalizedNumber == null || !normalizedNumber.startsWith("0")) {
            return normalizedNumber;
        }
        return "27" + normalizedNumber.substring(1);
    }
    
    public static boolean isEmail(String input) {
        return input != null && input.contains("@");
    }
}