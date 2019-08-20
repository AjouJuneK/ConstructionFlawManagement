package xyz.cfm.constructionflawmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public String id;
    public String pw;

    private EditText loginEditText;
    private EditText pwEditText;
    private Button loginButton;
    private Button registerButton;

    private DatabaseReference userDatabase;
    private List<User> retrievedUsers;

    private TextWatcher cfmTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues() {

        id = loginEditText.getText().toString();
        pw = pwEditText.getText().toString();

        if(id.equals("") || pw.equals("")){
            loginButton.setEnabled(false);
            loginButton.setBackgroundResource(R.drawable.black_border_in_white_drawable);
            loginButton.setTextColor(Color.parseColor("#000000"));
        } else {
            loginButton.setEnabled(true);
            loginButton.setBackgroundResource(R.drawable.button_activated_indigo_drawable);
            loginButton.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retrievedUsers.clear();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User retrievedUser = userSnapshot.getValue(User.class);

                    retrievedUsers.add(retrievedUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginEditText = (EditText)findViewById(R.id.LoginEditText);
        pwEditText = (EditText)findViewById(R.id.PWEditText);
        loginButton = (Button)findViewById(R.id.login_button);
        registerButton = (Button)findViewById(R.id.register_button);

        // 로그인 버튼을 아이디와 비밀번호를 쳤을 때만 활성화되도록 한다.
        loginEditText.addTextChangedListener(cfmTextWatcher);
        pwEditText.addTextChangedListener(cfmTextWatcher);

        userDatabase = FirebaseDatabase.getInstance().getReference("users"); // user 에 대한 database 입니다.
        retrievedUsers = new ArrayList<>();

        // run once to disable if empty
        checkFieldsForEmptyValues();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = loginEditText.getText().toString();
                pw = pwEditText.getText().toString();

                boolean loginable = authenticate(id, pw);

                if(loginable) {
                    moveToHomeActivity();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "로그인 정보가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegisterActivity();
            }
        });
    }

    /*
    * 회원가입 버튼을 누르면 회원가입 화면으로 넘어가는 매서드 입니다.
    * */
    private void moveToRegisterActivity() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    /*
    * 로그인이 되면, 홈 화면으로 넘어가는 매서드 입니다.
    * */
    private void moveToHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    /*
    * 로그인 인증 매서드 입니다.
    * */
    private boolean authenticate(String id, String pw) {
        for(int i = 0; i < retrievedUsers.size(); i++) {
            if(id.equals(retrievedUsers.get(i).getUserId()) && pw.equals(retrievedUsers.get(i).getUserPw())) {
                return true;
            }
        }
        return false;
    }
}
