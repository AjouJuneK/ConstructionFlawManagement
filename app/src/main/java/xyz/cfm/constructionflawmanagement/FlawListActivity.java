package xyz.cfm.constructionflawmanagement;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FlawListActivity extends AppCompatActivity {

    public boolean isFromAttachPhotoIntent = false;
    private byte[] byteArray;

    public String dong;
    public String ho;
    public String room;
    public String flawInfo;
    public String coopName;

    private TextView dongTextView;
    private TextView hoTextView;
    private TextView roomTextView;
    private TextView flawInfoTextView;
    private Button addFlawByVoiceButton;
    private Button addFlawListButton;

    private TextWatcher flawTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            dong = dongTextView.getText().toString();
            ho = hoTextView.getText().toString();
            room = roomTextView.getText().toString();
            flawInfo = flawInfoTextView.getText().toString();

            if (dong.equals("") || ho.equals("") || room.equals("") || flawInfo.equals("")) {
                addFlawListButton.setEnabled(false);
                addFlawListButton.setTextColor(Color.parseColor("#bbbbbb"));
                addFlawListButton.setBackgroundResource(R.drawable.rounded_corner_title);
            } else {
                addFlawListButton.setEnabled(true);
                addFlawListButton.setTextColor(Color.parseColor("#000000"));
                addFlawListButton.setBackgroundResource(R.drawable.rounder_corner);
            }
        }
    };

    private Spinner coopSpinner;
    private ArrayAdapter coopAdapter;

    private DatabaseReference coopDatabase;
    private DatabaseReference flawInfoListDatabase;
    private DatabaseReference flawInfoWithPhotoListDatabase;
    private List<Cooperation> retrievedCoops;
    private ArrayList<String> coopNameArrayList;

    @Override
    protected void onStart() {
        super.onStart();

        coopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coopNameArrayList.clear();
                for(DataSnapshot coopSnapshot : dataSnapshot.getChildren()) {
                    Cooperation retrievedCoop = coopSnapshot.getValue(Cooperation.class);
                    retrievedCoops.add(retrievedCoop);
                    coopNameArrayList.add(retrievedCoop.getCoopName());
                }
                coopSpinner.setAdapter(coopAdapter);
                if(retrievedCoops.size() >= 1) {
                    coopName = retrievedCoops.get(0).getCoopName();
                }
                // 협력업체가 선택되는 순간 작동되는 매서드
                coopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        coopName = coopSpinner.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
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
        setContentView(R.layout.activity_flaw_list);

        Intent intentFromAttachPhotoActivity = getIntent();
        Bundle extras = intentFromAttachPhotoActivity.getExtras();
        if (extras != null) {
            if (extras.containsKey("bm")) {
                isFromAttachPhotoIntent = true;
                byteArray = getIntent().getByteArrayExtra("bm");
            }
        }

        dongTextView = (TextView)findViewById(R.id.dongTextView);
        hoTextView = (TextView)findViewById(R.id.hoTextView);
        roomTextView = (TextView)findViewById(R.id.roomTextView);
        flawInfoTextView = (TextView)findViewById(R.id.flawInfoTextView);

        dongTextView.addTextChangedListener(flawTextWatcher);
        hoTextView.addTextChangedListener(flawTextWatcher);
        roomTextView.addTextChangedListener(flawTextWatcher);
        flawInfoTextView.addTextChangedListener(flawTextWatcher);

        addFlawByVoiceButton = (Button)findViewById(R.id.addFlawByVoiceButton);
        addFlawListButton = (Button)findViewById(R.id.addFlawListButton);
        coopSpinner = (Spinner)findViewById(R.id.coopSpinner);


        coopDatabase = FirebaseDatabase.getInstance().getReference("coops"); // 협력업체에 대한 database 입니다.
        retrievedCoops = new ArrayList<>();
        coopNameArrayList = new ArrayList<String>();
        coopAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, coopNameArrayList);

        flawInfoListDatabase = FirebaseDatabase.getInstance().getReference("flawInfoList"); // 하자 리스트에 대한 database 입니다.
        flawInfoWithPhotoListDatabase = FirebaseDatabase.getInstance().getReference("flawInfoWithPhotoList"); // 사진을 첨부한 하자 리스트에 대한 database 입니다.

        addFlawByVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFlawByVoiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                addFlawByVoiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                addFlawByVoiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
                try {
                    startActivityForResult(addFlawByVoiceIntent, 200);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getBaseContext(), "음성인식 모듈 불러오기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addFlawListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // database에 추가한다.
                dong = dongTextView.getText().toString();
                ho = hoTextView.getText().toString();
                room = roomTextView.getText().toString();
                flawInfo = flawInfoTextView.getText().toString();

                if (isFromAttachPhotoIntent) {
                    final String flawInfoWithPhotoKey = flawInfoWithPhotoListDatabase.push().getKey();
                    final String photoInString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    final FlawInfoWithPhoto currentFlawInfoWithPhoto = new FlawInfoWithPhoto(flawInfoWithPhotoKey, dong, ho, room, flawInfo, coopName, photoInString, false);
                    flawInfoWithPhotoListDatabase.child(flawInfoWithPhotoKey).setValue(currentFlawInfoWithPhoto);
                } else {
                    final String flawInfoKey = flawInfoListDatabase.push().getKey();
                    final FlawInfo currentFlawInfo = new FlawInfo(flawInfoKey, dong, ho, room, flawInfo, coopName, false);
                    flawInfoListDatabase.child(flawInfoKey).setValue(currentFlawInfo);
                }
                Toast.makeText(getBaseContext(), "새로운 하자가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200) {
            if(resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String flawInfoString = result.get(0);
                String[] dongStrings = flawInfoString.split("동", 2);
                String[] hoStrings = dongStrings[1].split("호", 2);
                String[] roomStrings = hoStrings[1].split(" ", 3);

                // TODO 에러 처리 필요

                dongTextView.setText(dongStrings[0]);
                hoTextView.setText(hoStrings[0]);
                roomTextView.setText(roomStrings[1]);
                flawInfoTextView.setText(roomStrings[2]);

            }
        }
    }

}
