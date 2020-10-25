package com.example.themoviedb;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private Button mBtnLogin;
    private EditText mEtEmail, mEtPassword;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        loadGE(v);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return v;
    }

    public void loadGE(View v){
        mBtnLogin = v.findViewById(R.id.btn_login);
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
}