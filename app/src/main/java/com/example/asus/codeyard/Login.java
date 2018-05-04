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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText email;
    private EditText password;
    private Button login;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //todo added 5 below
    private static final String TAG = "Login";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

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
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaLight.otf");
        Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");
        this.mAuth = FirebaseAuth.getInstance();

        //todo added below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(), this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();//TODO App crashes here

        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setProgressStyle(0);

        TextView welcome = v.findViewById(R.id.welcomeback);
        TextView sitcontinue = v.findViewById(R.id.sitcontinue);
        TextView fpass = v.findViewById(R.id.fpass);
        login = v.findViewById(R.id.login);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        TextView dhac = v.findViewById(R.id.dhac);
        TextView signup = v.findViewById(R.id.signup);
        SignInButton signInButton = v.findViewById(R.id.sign_in_button);

        welcome.setTypeface(tf2);
        sitcontinue.setTypeface(tf);
        fpass.setTypeface(tf);
        login.setTypeface(tf2);
        email.setTypeface(tf);
        password.setTypeface(tf);
        dhac.setTypeface(tf);
        signup.setTypeface(tf2);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getActivity(), HomeActivity.class));

                }
            }
        };

        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new ResetPass()).commit();
            }
        });
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

        //todo new shit
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        return v;
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Toast.makeText(getActivity(), "Successful Login", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), HomeActivity.class));
            GoogleSignInAccount acct = result.getSignInAccount();
        } else {
            //  Toast.makeText(getActivity(), "Failed to login", Toast.LENGTH_LONG).show();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                // [START_EXCLUDE]
                //updateUI(false);
                // [END_EXCLUDE]
            }
        });
    }
    // [END revokeAccess]

    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void startSignIn() {
        String umail = email.getText().toString().trim();
        String upass = password.getText().toString().trim();

        if (umail.isEmpty()) {
            email.setError("Please enter your email");
        } else if (upass.isEmpty()) {
            password.setError("Please provide a password");
        } else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(umail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Login Failed!Invalid username or password", Toast.LENGTH_LONG).show();
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
