package com.example.asus.codeyard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private EditText email;
    private EditText password2;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private OnFragmentInteractionListener mListener;

    public SignUp() {
    }

    public static SignUp newInstance(String param1, String param2) {
        SignUp fragment = new SignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup, container, false);
        mAuth = FirebaseAuth.getInstance();
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaLight.otf");
        Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");

        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setProgressStyle(0);

        TextView titlestart = v.findViewById(R.id.titlestart);
        TextView getstarted = v.findViewById(R.id.getstarted);
        Button signup = v.findViewById(R.id.signup);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        password2 = v.findViewById(R.id.password2);
        TextView logg = v.findViewById(R.id.logg);
        TextView ahac = v.findViewById(R.id.ahac);

        getstarted.setTypeface(tf);
        titlestart.setTypeface(tf2);
        email.setTypeface(tf);
        password.setTypeface(tf);
        password2.setTypeface(tf);
        signup.setTypeface(tf2);
        ahac.setTypeface(tf);
        logg.setTypeface(tf2);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getActivity(), HomeActivity.class));

                }
            }
        };

        logg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new Login()).commit();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });
        return v;

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startSignIn() {
        final String umail = email.getText().toString().trim();
        String upass = password.getText().toString().trim();
        String upass2 = password2.getText().toString().trim();

        if (umail.isEmpty()) {
            email.setError("Enter a valid email or password");
        } else if (upass.isEmpty()) {
            password.setError("Enter a password");
        } else if (!upass.equals(upass2)) {
            password2.setError("Passwords do not match!");
        } else {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(umail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        //TODO This function is not working
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Check mail for verification", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "Could not send email verification", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getActivity(), "Account with email " + umail + " already exists!!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Account creation failed!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
