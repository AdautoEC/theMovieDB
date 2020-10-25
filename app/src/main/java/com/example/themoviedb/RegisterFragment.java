package com.example.themoviedb;

import android.accounts.AuthenticatorException;
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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterFragment extends Fragment {
    private FirebaseAuth mAuth;
    private Button mBtnRegister;
    private EditText mEtName, mEtEmail, mEtPassword;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.fragment_register, container, false);
        loadGE(v);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        return v;
    }

    public void loadGE(View v){
        mBtnRegister = v.findViewById(R.id.btn_register);
        mEtName = v.findViewById(R.id.et_name);
        mEtEmail = v.findViewById(R.id.et_email);
        mEtPassword = v.findViewById(R.id.et_password);
    }

    public void register(){
        if(mEtName.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.register_error_title)
                    .setMessage(R.string.register_error_content_empty_name)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            builder.create().show();
            return;
        }
        if(mEtEmail.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.register_error_title)
                    .setMessage(R.string.register_error_content_empty_email)
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

            builder.setTitle(R.string.register_error_content_empty_password)
                    .setMessage(R.string.register_error_content_empty_password)
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
        mAuth.createUserWithEmailAndPassword(mEtEmail.getText().toString(), mEtPassword.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (!task.isSuccessful()) {
                    RegisterError(task);
                }else{
                    RegisterSuccess();
                }
            }
        });
    }

    public void RegisterError(Task<AuthResult> task){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
        builder.setTitle(R.string.register_error_title)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        switch (errorCode) {

            case "ERROR_EMAIL_ALREADY_IN_USE":
                builder.setMessage(R.string.register_error_content_email_already_in_use);
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                builder.setMessage(R.string.register_error_content_credential_already_in_use);
                break;

            case "ERROR_WEAK_PASSWORD":
                builder.setMessage(R.string.register_error_content_password_weak);
                break;

        }
        builder.show();
        return;
    }

    public void RegisterSuccess(){
        startActivity(new Intent(getActivity(), ProfileActivity.class));
    }
}