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
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUp.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText email;
    private EditText password2;
    private EditText password;
    private Button signup;
    private FirebaseAuth mAuth;

    private OnFragmentInteractionListener mListener;

    public SignUp() {
        // Required empty public constructor
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
        Typeface tf  = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaLight.otf");
        Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");

       TextView titlestart = v.findViewById(R.id.titlestart);
       TextView getstarted = v.findViewById(R.id.getstarted);
       Button signup = v.findViewById(R.id.signup);
       email = v.findViewById(R.id.email);
       password = v.findViewById(R.id.password);
       password2 = v.findViewById(R.id.password2);
       TextView logg = v.findViewById(R.id.logg);
       TextView ahac = v.findViewById(R.id.ahac);
       Button googlelog = v.findViewById(R.id.googlelog);

      getstarted.setTypeface(tf);
      titlestart.setTypeface(tf2);
      email.setTypeface(tf);
      password.setTypeface(tf);
      password2.setTypeface(tf);
      signup.setTypeface(tf2);
      ahac.setTypeface(tf);
      logg.setTypeface(tf2);
      googlelog.setTypeface(tf2);

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
      return  v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startSignIn(){
        String umail = email.getText().toString().trim();
        String upass = password.getText().toString().trim();
        String upass2 = password2.getText().toString().trim();

        if(umail.isEmpty()){
            email.setError("Enter a valid email or password");
        }else if(upass.isEmpty()){
            password.setError("Enter a password");
        }else if(!upass.equals(upass2)){
            password2.setError("Passwords do not match!");
        }else{
            mAuth.createUserWithEmailAndPassword(umail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getActivity(), "Failed to create account", Toast.LENGTH_LONG).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
