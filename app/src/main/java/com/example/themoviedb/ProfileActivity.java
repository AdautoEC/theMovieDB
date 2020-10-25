package com.example.themoviedb;


import androidx.appcompat.app.AppCompatActivity;


import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        //Log.d("TAG", mUser.getDisplayName());


        //mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }
}