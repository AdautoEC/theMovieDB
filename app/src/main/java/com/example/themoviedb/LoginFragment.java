package com.example.themoviedb;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private Button mBtnLogin;
    private EditText mEtEmail, mEtPassword;
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        loadGE(v);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:error:" + error.getMessage());
            }
        });

        return v;
    }

    public void loadGE(View v){
        mBtnLogin = v.findViewById(R.id.btn_login);
        loginButton = v.findViewById(R.id.login_button);
        mEtEmail = v.findViewById(R.id.et_email);
        mEtPassword = v.findViewById(R.id.et_password);
    }

    public void login(){
        if(mEtEmail.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.login_error_title)
                    .setMessage(R.string.login_error_content_empty_email)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            builder.create().show();
            return;
        }
        if(mEtPassword.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.login_error_title)
                    .setMessage(R.string.login_error_content_empty_password)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            builder.create().show();
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), getString(R.string.login_success_title), getString(R.string.login_success_content), true);
        dialog.show();
        mAuth.signInWithEmailAndPassword(mEtEmail.getText().toString(), mEtPassword.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (!task.isSuccessful()) {
                    loginError(task);
                }else{
                    loginSuccess();
                }
            }
        });
    }

    public void loginError(Task<AuthResult> task){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            throw task.getException();
        }
        catch(FirebaseAuthInvalidCredentialsException e) {
            builder.setTitle(R.string.login_error_title)
                    .setMessage(R.string.login_error_content_default)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.create().show();
        }
        catch(Exception e) {
            Log.e("TAG", e.getMessage());
            builder.setTitle(R.string.login_error_title)
                    .setMessage(R.string.login_register_error_content_unnoted)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.create().show();
        }

        return;
    }

    public void loginSuccess(){
        startActivity(new Intent(getActivity(), ProfileActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loginSuccess();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }
}