package com.v_p_a_appdev.minimap.FireBase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.v_p_a_appdev.minimap.Activities.UserLoginActivity;
import com.v_p_a_appdev.minimap.Utils.PasswordValidator;

import java.util.Objects;
import java.util.regex.Pattern;

public class UserLoginActivityFB {
    private FirebaseAuth entranceAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;
    private UserLoginActivity userLoginActivity;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public UserLoginActivityFB(FirebaseAuth.AuthStateListener fireBaseAuthListener ,  UserLoginActivity userLoginActivity) {
        this.entranceAuth = FirebaseAuth.getInstance();
        this.fireBaseAuthListener = fireBaseAuthListener;
        this.userLoginActivity = userLoginActivity;
    }

    public void createUserWithEmailAndPassword(String email , String Password ){
        // Validate and sanitize inputs
        if (!validateEmail(email)) {
            userLoginActivity.registrationError("Invalid email format");
            return;
        }
        
        PasswordValidator.PasswordValidationResult validationResult = PasswordValidator.validatePassword(Password);
        if (!validationResult.isValid()) {
            userLoginActivity.passwordInputError(validationResult.getErrorMessage());
            return;
        }
        
        // Sanitize inputs
        String sanitizedEmail = sanitizeEmail(email);
        String sanitizedPassword = Password; // Password is already validated
        
        entranceAuth.createUserWithEmailAndPassword(sanitizedEmail, sanitizedPassword).addOnCompleteListener( userLoginActivity, task -> {
            if (!task.isSuccessful()) {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                userLoginActivity.registrationError(errorMessage);
            } else {
                setInfo(sanitizedEmail);
            }
        });
    }

    public void signInWithEmailAndPassword(String email , String Password ){
        // Validate and sanitize inputs
        if (!validateEmail(email)) {
            userLoginActivity.authenticationError("Invalid email format");
            return;
        }
        
        if (Password == null || Password.isEmpty()) {
            userLoginActivity.passwordInputError("Password cannot be empty");
            return;
        }
        
        // Sanitize inputs
        String sanitizedEmail = sanitizeEmail(email);
        String sanitizedPassword = Password; // Password validation not needed for login
        
        entranceAuth.signInWithEmailAndPassword(sanitizedEmail, sanitizedPassword).addOnCompleteListener(userLoginActivity, task -> {
            if (!task.isSuccessful()) {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                userLoginActivity.authenticationError(errorMessage);
            }
        });
    }

    public void addAuthStateListener() {
        entranceAuth.addAuthStateListener(fireBaseAuthListener);
    }

    public void removeAuthStateListener() {
        entranceAuth.removeAuthStateListener(fireBaseAuthListener);
    }

    protected void setInfo(String email) {
        String userId = Objects.requireNonNull(this.entranceAuth.getCurrentUser()).getUid();
        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userLoginActivity.userType).child(userId).child("name");
        String name = email.split("@")[0];
        currentUserDB.setValue(name);
        if (userLoginActivity.userType.equals("Helpers")) {
            currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId).child("rating");
            currentUserDB.setValue(0);
        }
    }

    /**
     * Validates email format
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    private boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Sanitizes email input to prevent injection attacks
     * @param email The email to sanitize
     * @return Sanitized email
     */
    private String sanitizeEmail(String email) {
        if (email == null) return "";
        // Remove any potential script tags or dangerous characters
        return email.replaceAll("[<>\"']", "").trim();
    }
}
