package xyz.cfm.constructionflawmanagement;

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

public class RegisterActivity extends AppCompatActivity {

    public String id;
    public String pw;
    public String pwCheck;
    public boolean isIdUsable = false;

    private EditText registerIdEditText;
    private EditText registerPwEditText;
    private EditText registerPwCheckEditText;

    private Button registerIdCheckButton;
    private Button registerConfirmButton;

    private DatabaseReference userDatabase;

    private List<User> retrievedUsers;

    private TextWatcher registerIdTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldsForEmptyId();
        }
    };

    private TextWatcher registerConfirmWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldsForEmptyRegisterInfo();
        }
    };

    void checkFieldsForEmptyId() {
        id = registerIdEditText.getText().toString();

        if(id.equals("")) {
            registerIdCheckButton.setEnabled(false);
            registerIdCheckButton.setBackgroundResource(R.drawable.black_border_in_white_drawable);
            registerIdCheckButton.setTextColor(Color.parseColor("#000000"));
        } else {
            registerIdCheckButton.setEnabled(true);
            registerIdCheckButton.setBackgroundResource(R.drawable.button_activated_indigo_drawable);
            registerIdCheckButton.setTextColor(Color.parseColor("#FFFFFF"));
        }

    }
    void checkFieldsForEmptyRegisterInfo() {
        id = registerIdEditText.getText().toString();
        pw = registerPwEditText.getText().toString();
        pwCheck = registerPwCheckEditText.getText().toString();

        if(id.equals("") || pw.equals("") || pwCheck.equals("")) {
            registerConfirmButton.setEnabled(false);
            registerConfirmButton.setBackgroundResource(R.drawable.black_border_in_white_drawable);
            registerConfirmButton.setTextColor(Color.parseColor("#000000"));
        } else {
            registerConfirmButton.setEnabled(true);
            registerConfirmButton.setBackgroundResource(R.drawable.button_activated_indigo_drawable);
            registerConfirmButton.setTextColor(Color.parseColor("#FFFFFF"));
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
        setContentView(R.layout.activity_register);

        registerIdEditText = (EditText)findViewById(R.id.registerIdEditText);
        registerPwEditText = (EditText)findViewById(R.id.registerPwEditText);
        registerPwCheckEditText = (EditText)findViewById(R.id.registerPwCheckEditText);

        registerIdCheckButton = (Button)findViewById(R.id.registerIdCheckButton);
        registerConfirmButton = (Button)findViewById(R.id.registerConfirmButton);

        userDatabase = FirebaseDatabase.getInstance().getReference("users"); // user 에 대한 database 입니다.
        retrievedUsers = new ArrayList<>();

        // run once to disable if empty
        checkFieldsForEmptyId();
        checkFieldsForEmptyRegisterInfo();

        registerIdEditText.addTextChangedListener(registerIdTextWatcher);
        registerIdEditText.addTextChangedListener(registerConfirmWatcher);
        registerPwEditText.addTextChangedListener(registerConfirmWatcher);
        registerPwCheckEditText.addTextChangedListener(registerConfirmWatcher);

        /* 아이디 중복확인 버튼 누를 때 동작 */
        registerIdCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = registerIdEditText.getText().toString();
                int num_of_users = retrievedUsers.size();

                if(!isIdExist(id)){ // 아이디가 중복되지 않으면
                    isIdUsable = true;
                    Toast.makeText(getBaseContext(), "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "이미 사용중인 아이디 입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* 가입하기 버튼 누를 때 동작 */
        registerConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = registerIdEditText.getText().toString();
                pw = registerPwEditText.getText().toString();
                pwCheck = registerPwCheckEditText.getText().toString();

                if(pw.equals(pwCheck)) {
                    createUserAccount(id, pw);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "입력한 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /*
    * 새로운 계정을 만드는 매서드 입니다.
    * */
    private void createUserAccount(String id, String pw) {
        String userKey = userDatabase.push().getKey();
        User newUser = new User(userKey, id, pw);
        userDatabase.child(userKey).setValue(newUser);
        Toast.makeText(getBaseContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
    }

    /*
    * 서버를 통해 아이디 중복을 검사하는 매서드 입니다.
    * */
    private boolean isIdExist(String id) {
        for(int i = 0; i < retrievedUsers.size(); i++) {
            if(id.equals(retrievedUsers.get(i).getUserId())) {
                return true;
            }
        }
        return false;
    }
}
