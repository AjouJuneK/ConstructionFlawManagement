package xyz.cfm.constructionflawmanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final int Take_CAMERA = 1; // 카메라 호출 요청 코드
    private Button settingButton;
    private Button checkListButton;
    private Button flawListButton;
    private Button attachPhotoButton;
    private Button analysisButton;

    private Bitmap bm;
    private List<FlawInfo> flawInfoList;
    private List<FlawInfoWithPhoto> flawInfoWithPhotoList;

    private DatabaseReference flawInfoListDatabase;
    private DatabaseReference flawInfoWithPhotoListDatabase;

    @Override
    protected void onStart() {
        super.onStart();

        flawInfoListDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flawInfoList.clear();
                for(DataSnapshot flawInfoListSnapshot : dataSnapshot.getChildren()) {
                    FlawInfo retrievedFlawInfo = flawInfoListSnapshot.getValue(FlawInfo.class);
                    flawInfoList.add(retrievedFlawInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        flawInfoWithPhotoListDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flawInfoWithPhotoList.clear();
                for(DataSnapshot flawInfoWithPhotoListSnapShot : dataSnapshot.getChildren()) {
                    FlawInfoWithPhoto retrievedFlawInfoWithPhoto = flawInfoWithPhotoListSnapShot.getValue(FlawInfoWithPhoto.class);
                    flawInfoWithPhotoList.add(retrievedFlawInfoWithPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        flawInfoListDatabase = FirebaseDatabase.getInstance().getReference("flawInfoList"); // 하자 리스트에 대한 database 입니다.
        flawInfoWithPhotoListDatabase = FirebaseDatabase.getInstance().getReference("flawInfoWithPhotoList"); // 사진을 첨부한 하자 리스트에 대한 database 입니다.

        flawInfoList = new ArrayList<>();
        flawInfoWithPhotoList = new ArrayList<>();

        settingButton = (Button)findViewById(R.id.settingButton);
        checkListButton = (Button)findViewById(R.id.checkListButton);
        flawListButton = (Button)findViewById(R.id.flawListButton);
        attachPhotoButton = (Button)findViewById(R.id.attachPhotoButton);
        analysisButton = (Button)findViewById(R.id.analysisButton);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });

        checkListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkListIntent = new Intent(getApplicationContext(), CheckListActivity.class);
                checkListIntent.putExtra("flawInfoList", (Serializable) flawInfoList);
                checkListIntent.putExtra("flawInfoWithPhotoList", (Serializable) flawInfoWithPhotoList);
                startActivity(checkListIntent);
            }
        });

        flawListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flawListIntent = new Intent(getApplicationContext(), FlawListActivity.class);
                startActivity(flawListIntent);
            }
        });

        attachPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 바로 카메라가 실행되도록 하였습니다.
                Intent takePhotoIntent = new Intent();
                takePhotoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhotoIntent, Take_CAMERA);
            }
        });

        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent analysisIntent = new Intent(getApplicationContext(), AnalysisActivity.class);
                analysisIntent.putExtra("flawInfoList", (Serializable) flawInfoList);
                analysisIntent.putExtra("flawInfoWithPhotoList", (Serializable) flawInfoWithPhotoList);
                startActivity(analysisIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Take_CAMERA :
                    if (data != null) {
                        Bundle t_nail = data.getExtras();
                        bm = (Bitmap)t_nail.get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        bm.recycle();

                        Intent attachPhotoIntent = new Intent(getApplicationContext(), FlawListActivity.class);
                        attachPhotoIntent.putExtra("bm", byteArray);
                        startActivity(attachPhotoIntent);

                    } break;
                default:
                    break;
            }
        }
    }
}
