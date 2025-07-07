package com.v_p_a_appdev.minimap.Utils;

import android.util.Log;
import java.util.regex.Pattern;

/**
 * Comprehensive input validation utility
 */
public class InputValidator {
    
    private static final String TAG = "InputValidator";
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-()]{7,20}$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s]{2,50}$"
    );
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s]{1,100}$"
    );
    
    /**
     * Validates email format
     * @param email Email to validate
     * @return Validation result
     */
    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();
        
        if (email == null || email.trim().isEmpty()) {
            result.addError("Email cannot be empty");
            return result;
        }
        
        String trimmedEmail = email.trim();
        
        if (trimmedEmail.length() > 254) { // RFC 5321 limit
            result.addError("Email is too long");
        }
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            result.addError("Invalid email format");
        }
        
        // Check for common email injection patterns
        if (trimmedEmail.contains("javascript:") || trimmedEmail.contains("<script")) {
            result.addError("Email contains invalid characters");
        }
        
        return result;
    }
    
    /**
     * Validates phone number
     * @param phone Phone number to validate
     * @return Validation result
     */
    public static ValidationResult validatePhone(String phone) {
        ValidationResult result = new ValidationResult();
        
        if (phone == null || phone.trim().isEmpty()) {
            result.addError("Phone number cannot be empty");
            return result;
        }
        
        String sanitized = phone.replaceAll("[^0-9+\\-()\\s]", "");
        
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            result.addError("Invalid phone number format");
        }
        
        if (sanitized.length() < 7) {
            result.addError("Phone number is too short");
        }
        
        return result;
    }
    
    /**
     * Validates user name
     * @param name Name to validate
     * @return Validation result
     */
    public static ValidationResult validateName(String name) {
        ValidationResult result = new ValidationResult();
        
        if (name == null || name.trim().isEmpty()) {
            result.addError("Name cannot be empty");
            return result;
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < 2) {
            result.addError("Name is too short");
        }
        
        if (trimmedName.length() > 50) {
            result.addError("Name is too long");
        }
        
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            result.addError("Name contains invalid characters");
        }
        
        return result;
    }
    
    /**
     * Validates general text input
     * @param text Text to validate
     * @param maxLength Maximum allowed length
     * @return Validation result
     */
    public static ValidationResult validateText(String text, int maxLength) {
        ValidationResult result = new ValidationResult();
        
        if (text == null || text.trim().isEmpty()) {
            result.addError("Text cannot be empty");
            return result;
        }
        
        String trimmedText = text.trim();
        
        if (trimmedText.length() > maxLength) {
            result.addError("Text is too long (max " + maxLength + " characters)");
        }
        
        if (SecurityUtils.containsDangerousContent(trimmedText)) {
            result.addError("Text contains potentially dangerous content");
        }
        
        return result;
    }
    
    /**
     * Validates location coordinates
     * @param latitude Latitude value
     * @param longitude Longitude value
     * @return Validation result
     */
    public static ValidationResult validateLocation(double latitude, double longitude) {
        ValidationResult result = new ValidationResult();
        
        if (latitude < -90 || latitude > 90) {
            result.addError("Invalid latitude value");
        }
        
        if (longitude < -180 || longitude > 180) {
            result.addError("Invalid longitude value");
        }
        
        return result;
    }
    
    /**
     * Validates rating value
     * @param rating Rating to validate
     * @return Validation result
     */
    public static ValidationResult validateRating(int rating) {
        ValidationResult result = new ValidationResult();
        
        if (rating < 0 || rating > 5) {
            result.addError("Rating must be between 0 and 5");
        }
        
        return result;
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean isValid = true;
        private StringBuilder errorMessages = new StringBuilder();
        
        public void addError(String error) {
            isValid = false;
            if (errorMessages.length() > 0) {
                errorMessages.append("\n");
            }
            errorMessages.append(error);
            Log.w(TAG, "Validation error: " + error);
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrorMessage() {
            return errorMessages.toString();
        }
        
        public boolean hasErrors() {
            return !isValid;
        }
    }
} 