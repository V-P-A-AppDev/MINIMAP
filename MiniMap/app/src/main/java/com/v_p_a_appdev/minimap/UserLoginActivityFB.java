package com.v_p_a_appdev.minimap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserLoginActivityFB {
    private FirebaseAuth entranceAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;
    private UserLoginActivity userLoginActivity;

    public UserLoginActivityFB(FirebaseAuth.AuthStateListener fireBaseAuthListener ,  UserLoginActivity userLoginActivity) {
        this.entranceAuth = FirebaseAuth.getInstance();
        this.fireBaseAuthListener = fireBaseAuthListener;
        this.userLoginActivity = userLoginActivity;
    }
    public void createUserWithEmailAndPassword(String email , String Password ){
        if (Password.length() < 6) {
            userLoginActivity.passwordInputError();
            return;
        }
        entranceAuth.createUserWithEmailAndPassword(email, Password).addOnCompleteListener( userLoginActivity, task -> {
            if (!task.isSuccessful()) {
                userLoginActivity.registrationError();
            } else {
                String userId = entranceAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userLoginActivity.userType).child(userId).child("name");
                String name = email.split("@")[0];
                currentUserDB.setValue(name);
            }
        });
    }

    public void signInWithEmailAndPassword(String email , String Password ){
        if (Password.length() < 6) {
            userLoginActivity.passwordInputError();
            return;
        }
        entranceAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(userLoginActivity, task -> {
            if (!task.isSuccessful()) {
                userLoginActivity.authenticationError();
            }
        });
    }

    public void addAuthStateListener() {
        entranceAuth.addAuthStateListener(fireBaseAuthListener);
    }

    public void removeAuthStateListener() {
        entranceAuth.removeAuthStateListener(fireBaseAuthListener);
    }
}
