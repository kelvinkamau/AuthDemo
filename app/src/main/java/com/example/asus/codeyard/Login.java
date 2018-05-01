package com.example.asus.codeyard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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


public class Login extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText email;
    private EditText password;
    private Button login;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Login() {
        // Required empty public constructor
    }

    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
        View v = inflater.inflate(R.layout.login, container, false);
        Typeface tf  = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaLight.otf");
        Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");
        this.mAuth = FirebaseAuth.getInstance();
        TextView welcome = v.findViewById(R.id.welcomeback);
        TextView sitcontinue = v.findViewById(R.id.sitcontinue);
        TextView fpass = v.findViewById(R.id.fpass);
        login = v.findViewById(R.id.login);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        TextView dhac = v.findViewById(R.id.dhac);
        TextView signup = v.findViewById(R.id.signup);
        Button googlelog = v.findViewById(R.id.googlelog);

        welcome.setTypeface(tf2);
        sitcontinue.setTypeface(tf);
        fpass.setTypeface(tf);
        login.setTypeface(tf2);
        email.setTypeface(tf);
        password.setTypeface(tf);
        dhac.setTypeface(tf);
        signup.setTypeface(tf2);
        googlelog.setTypeface(tf2);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(getActivity(), HomeActivity.class));}
            }
        };

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new SignUp()).commit();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        return v;
    }
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void startSignIn(){
        String umail = email.getText().toString().trim();
        String upass = password.getText().toString().trim();

        if(umail.isEmpty()){
            email.setError("Please provide a username or email");
        }else if(upass.isEmpty()){
            password.setError("Please provide a password");
        }
        else{
            mAuth.signInWithEmailAndPassword(umail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), "Login Failed! Check username or password", Toast.LENGTH_LONG).show();
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
