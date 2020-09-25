package com.example.barberme.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.barberme.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignInFragment extends Fragment {

    TextInputEditText email;
    TextInputEditText password;
    Button signin;
    Button signup;
    SignInListener signInListener;

    interface SignInListener {
        void onSignInFragmentLoginClick(String email, String password);
        void onSignInFragmentRegisterClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        signInListener = (SignInListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        email = rootView.findViewById(R.id.email_et);
        password = rootView.findViewById(R.id.password_et);
        signin = rootView.findViewById(R.id.login_bt);
        signup = rootView.findViewById(R.id.signup_bt);

        signin.setOnClickListener(view -> {
            if(signInListener != null)
                signInListener.onSignInFragmentLoginClick(email.getText().toString(), password.getText().toString());
        });

        signup.setOnClickListener(view -> {
            if(signInListener != null)
                signInListener.onSignInFragmentRegisterClick();
        });

        return  rootView;
    }
}
