package com.v_p_a_appdev.minimap.Utils;

import java.util.regex.Pattern;

/**
 * Utility class for password validation with enhanced security requirements
 */
public class PasswordValidator {
    
    // Minimum password requirements
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    // Regex patterns for password strength
    private static final Pattern HAS_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL_CHAR = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    private static final Pattern HAS_WHITESPACE = Pattern.compile("\\s");
    
    /**
     * Validates password strength according to security requirements
     * @param password The password to validate
     * @return PasswordValidationResult containing validation status and error messages
     */
    public static PasswordValidationResult validatePassword(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        
        if (password == null || password.isEmpty()) {
            result.addError("Password cannot be empty");
            return result;
        }
        
        // Check length requirements
        if (password.length() < MIN_LENGTH) {
            result.addError("Password must be at least " + MIN_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_LENGTH) {
            result.addError("Password cannot exceed " + MAX_LENGTH + " characters");
        }
        
        // Check character requirements
        if (!HAS_UPPERCASE.matcher(password).find()) {
            result.addError("Password must contain at least one uppercase letter");
        }
        
        if (!HAS_LOWERCASE.matcher(password).find()) {
            result.addError("Password must contain at least one lowercase letter");
        }
        
        if (!HAS_NUMBER.matcher(password).find()) {
            result.addError("Password must contain at least one number");
        }
        
        if (!HAS_SPECIAL_CHAR.matcher(password).find()) {
            result.addError("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)");
        }
        
        // Check for whitespace
        if (HAS_WHITESPACE.matcher(password).find()) {
            result.addError("Password cannot contain whitespace characters");
        }
        
        // Check for common weak passwords
        if (isCommonPassword(password)) {
            result.addError("Password is too common, please choose a stronger password");
        }
        
        return result;
    }
    
    /**
     * Checks if the password is a common weak password
     * @param password The password to check
     * @return true if it's a common password, false otherwise
     */
    private static boolean isCommonPassword(String password) {
        String[] commonPasswords = {
            "password", "123456", "123456789", "qwerty", "abc123", "password123",
            "admin", "letmein", "welcome", "monkey", "dragon", "master", "hello",
            "freedom", "whatever", "qwerty123", "trustno1", "jordan", "harley",
            "ranger", "iwantu", "jennifer", "hunter", "buster", "soccer", "baseball",
            "tiger", "charlie", "andrew", "michelle", "love", "sunshine", "jessica",
            "asshole", "696969", "amanda", "apple", "orange", "654321", "michael",
            "qwertyuiop", "superman", "batman", "shadow", "master", "jordan23"
        };
        
        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.equals(common)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Result class for password validation
     */
    public static class PasswordValidationResult {
        private boolean isValid = true;
        private StringBuilder errorMessages = new StringBuilder();
        
        public void addError(String error) {
            isValid = false;
            if (errorMessages.length() > 0) {
                errorMessages.append("\n");
            }
            errorMessages.append(error);
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