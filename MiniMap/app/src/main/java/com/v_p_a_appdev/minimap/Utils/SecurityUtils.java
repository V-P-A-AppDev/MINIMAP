package com.v_p_a_appdev.minimap.Utils;

import android.util.Log;
import java.util.regex.Pattern;

/**
 * Security utilities for input validation and sanitization
 */
public class SecurityUtils {
    
    private static final String TAG = "SecurityUtils";
    
    // Patterns for input validation
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]+$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://[\\w\\-./?=&%]+$");
    
    /**
     * Sanitizes user input to prevent XSS and injection attacks
     * @param input The input to sanitize
     * @return Sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        // Remove potentially dangerous characters and patterns
        return input.replaceAll("[<>\"'&;()]", "")
                   .replaceAll("javascript:", "")
                   .replaceAll("on\\w+\\s*=", "")
                   .trim();
    }
    
    /**
     * Validates and sanitizes user name
     * @param name The name to validate
     * @return Sanitized name or null if invalid
     */
    public static String validateAndSanitizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        String sanitized = sanitizeInput(name.trim());
        if (sanitized.length() < 2 || sanitized.length() > 50) {
            return null;
        }
        
        if (!NAME_PATTERN.matcher(sanitized).matches()) {
            return null;
        }
        
        return sanitized;
    }
    
    /**
     * Validates and sanitizes phone number
     * @param phone The phone number to validate
     * @return Sanitized phone number or null if invalid
     */
    public static String validateAndSanitizePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        
        String sanitized = phone.replaceAll("[^0-9+\\-()\\s]", "").trim();
        if (sanitized.length() < 7 || sanitized.length() > 20) {
            return null;
        }
        
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            return null;
        }
        
        return sanitized;
    }
    
    /**
     * Validates URL format
     * @param url The URL to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        return URL_PATTERN.matcher(url.trim()).matches();
    }
    
    /**
     * Validates alphanumeric input
     * @param input The input to validate
     * @return true if valid, false otherwise
     */
    public static boolean isAlphanumeric(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        return ALPHANUMERIC_PATTERN.matcher(input.trim()).matches();
    }
    
    /**
     * Checks if input contains potentially dangerous content
     * @param input The input to check
     * @return true if dangerous content detected, false otherwise
     */
    public static boolean containsDangerousContent(String input) {
        if (input == null) return false;
        
        String lowerInput = input.toLowerCase();
        
        // Check for script tags
        if (lowerInput.contains("<script") || lowerInput.contains("javascript:")) {
            Log.w(TAG, "Dangerous content detected: script tags");
            return true;
        }
        
        // Check for SQL injection patterns
        if (lowerInput.contains("union select") || lowerInput.contains("drop table") || 
            lowerInput.contains("delete from") || lowerInput.contains("insert into")) {
            Log.w(TAG, "Dangerous content detected: SQL injection patterns");
            return true;
        }
        
        // Check for command injection patterns
        if (lowerInput.contains(";") && (lowerInput.contains("rm ") || lowerInput.contains("cat ") || 
            lowerInput.contains("ls ") || lowerInput.contains("pwd "))) {
            Log.w(TAG, "Dangerous content detected: command injection patterns");
            return true;
        }
        
        return false;
    }
    
    /**
     * Validates chat message content
     * @param message The message to validate
     * @return Sanitized message or null if invalid
     */
    public static String validateChatMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        
        if (containsDangerousContent(message)) {
            return null;
        }
        
        String sanitized = sanitizeInput(message.trim());
        if (sanitized.length() > 1000) { // Limit message length
            return null;
        }
        
        return sanitized;
    }
    
    /**
     * Validates location coordinates
     * @param latitude The latitude value
     * @param longitude The longitude value
     * @return true if valid coordinates, false otherwise
     */
    public static boolean isValidLocation(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
} 