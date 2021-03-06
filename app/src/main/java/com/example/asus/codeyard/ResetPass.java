package com.example.asus.codeyard;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ResetPass extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    ProgressDialog progressDialog;

    public ResetPass() {

    }

    public static ResetPass newInstance(String param1, String param2) {
        ResetPass fragment = new ResetPass();
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
        final View v = inflater.inflate(R.layout.resetpass, container, false);
        final Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaLight.otf");
        Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");
        TextView forgot = v.findViewById(R.id.forgot);
        TextView recover = v.findViewById(R.id.recover);
        final EditText email = v.findViewById(R.id.email);
        final Button resbutton = v.findViewById(R.id.resbutton);
        TextView dhac = v.findViewById(R.id.dhac);
        TextView signup = v.findViewById(R.id.signup);

        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setProgressStyle(0);

        forgot.setTypeface(tf2);
        recover.setTypeface(tf);
        email.setTypeface(tf);
        resbutton.setTypeface(tf2);
        dhac.setTypeface(tf);
        signup.setTypeface(tf2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new SignUp()).commit();
            }
        });

        resbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String emailAddress = email.getText().toString();
                if (emailAddress.isEmpty()) {
                    email.setError("Please enter your email");
                } else {
                    progressDialog.show();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull final Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Check " + emailAddress + " to reset password", Toast.LENGTH_LONG).show();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.content, new Login()).commit();
                            } else {
                                progressDialog.dismiss();
                                final Snackbar snackbar = Snackbar.make(v, "Email not sent", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(v, "Please confirm email", Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                    }
                                });

                                snackbar.show();
                            }
                        }
                    });
                }
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onBackPressed() {

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
