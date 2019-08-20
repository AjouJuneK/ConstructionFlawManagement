package xyz.cfm.constructionflawmanagement;

import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout coopLinearLayout;
    private EditText coopNameEditText;
    private Button addCoopButton;

    private DatabaseReference coopDatabase;
    private List<Cooperation> retrievedCoops;

    @Override
    protected void onStart() {
        super.onStart();

        coopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retrievedCoops.clear();
                for(DataSnapshot coopSnapshot : dataSnapshot.getChildren()) {
                    Cooperation retrievedCoop = coopSnapshot.getValue(Cooperation.class);
                    drawCoop(retrievedCoop);
                    retrievedCoops.add(retrievedCoop);
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
        setContentView(R.layout.activity_setting);

        coopLinearLayout = (LinearLayout)findViewById(R.id.coopLinearLayout);
        coopNameEditText = (EditText)findViewById(R.id.coopNameEditText);
        coopNameEditText.setText("");
        addCoopButton = (Button)findViewById(R.id.addCoopButton);

        coopDatabase = FirebaseDatabase.getInstance().getReference("coops"); // 협렵업체에 대한 database 입니다.
        retrievedCoops = new ArrayList<>();

        addCoopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(coopNameEditText.getText().toString().equals(""))) {
                    addCoop(coopNameEditText.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(), "추가할 협력업체를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*
    * 새로운 협력업체를 등록하는 매서드 입니다.
    * */
    public void addCoop(final String coopName) {

        // 추가된 협력업체를 데이터베이스에 추가하기
        final String coopKey = coopDatabase.push().getKey();
        Toast.makeText(getBaseContext(), "\'" + coopName + "\' 협력업체가 추가되었습니다.", Toast.LENGTH_SHORT).show();
        coopNameEditText.setText("");
        final Cooperation currentCoop = new Cooperation(coopKey, coopName);
        coopDatabase.child(coopKey).setValue(currentCoop);

        // 화면에 view 를 추가한다.
        drawCoop(currentCoop);

    }

    /*
    * 동적으로 화면에 view를 생성해주는 매서드 입니다.
    * */
    public void drawCoop(final Cooperation coop) {

        final String coopName = coop.getCoopName();

        // convert dps to pixels using display scale factor
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;

        final LinearLayout currentCoopLayout = new LinearLayout(getApplicationContext());
        TextView currentCoopTextView = new TextView(getApplicationContext());
        Button currentCoopDeleteButton = new Button(getApplicationContext());

        // TextView set
        currentCoopTextView.setText(coopName);
        currentCoopTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        currentCoopTextView.setTextColor(Color.parseColor("#000000"));
        currentCoopTextView.setWidth((int)(170 * scale + 0.5f));
        currentCoopTextView.setHeight((int)(40 * scale + 0.5f));
        currentCoopTextView.setGravity(Gravity.CENTER);
        currentCoopTextView.setBackgroundResource(R.drawable.black_border_in_white_drawable);

        // TextView margin set
        LinearLayout.LayoutParams currentCoopTextViewLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        currentCoopTextViewLinearLayoutParams.setMargins((int)(60 * scale + 0.5f), (int)(8 * scale + 0.5f), (int)(16 * scale + 0.5f), (int)(8 * scale + 0.5f));
        currentCoopTextView.setLayoutParams(currentCoopTextViewLinearLayoutParams);
        currentCoopLayout.addView(currentCoopTextView);

        // Button set
        currentCoopDeleteButton.setText("삭제");
        currentCoopDeleteButton.setTextSize(14);
        currentCoopDeleteButton.setTextColor(Color.parseColor("#ffffff"));
        currentCoopDeleteButton.setGravity(Gravity.CENTER);
        currentCoopDeleteButton.setBackgroundResource(R.drawable.coop_setting_button);

        // Button margin set
        LinearLayout.LayoutParams currentCoopDeleteLinearLayoutParams = new LinearLayout.LayoutParams((int)(80 * scale + 0.5f), (int)(40 * scale + 0.5f));
        currentCoopDeleteLinearLayoutParams.setMargins((int)(8 * scale + 0.5f), (int)(8 * scale + 0.5f),(int)(8 * scale + 0.5f),(int)(8 * scale + 0.5f));
        currentCoopDeleteButton.setLayoutParams(currentCoopDeleteLinearLayoutParams);
        currentCoopDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "삭제 : " + coopName, Toast.LENGTH_SHORT).show();
                coopLinearLayout.removeView(currentCoopLayout);
                DatabaseReference deleteDatabaseReference = FirebaseDatabase.getInstance().getReference("coops").child(coop.getCoopKey());
                deleteDatabaseReference.removeValue();
            }
        });
        currentCoopLayout.addView(currentCoopDeleteButton);

        // Layout set
        LinearLayout.LayoutParams currentLayoutLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.HORIZONTAL);
        currentCoopLayout.setLayoutParams(currentLayoutLinearLayoutParams);
        coopLinearLayout.addView(currentCoopLayout);
    }

}
