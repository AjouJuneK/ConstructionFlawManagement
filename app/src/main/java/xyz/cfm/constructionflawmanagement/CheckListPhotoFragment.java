package xyz.cfm.constructionflawmanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CheckListPhotoFragment extends Fragment {

    private View view;
    private LinearLayout checkListPhotoFragmentLinearLayout;
    private String currentCoopName;
    private List<FlawInfoWithPhoto> retrievedFlawInfoWithPhotoList;
    private List<FlawInfoWithPhoto> flawInfoWithPhotoListToDraw;
    private List<LinearLayout> insertedFlawWithPhotoListLayoutList;

    private DatabaseReference coopDatabase;
    private DatabaseReference flawInfoWithPhotoListDatabase;

    private Spinner checkListPhotoCoopSpinner;
    private List<Cooperation> retrievedCoops;
    private ArrayList<String> coopNameArrayList;
    private ArrayAdapter checkListPhotoCoopSpinnerAdapter;

    private OnFragmentInteractionListener mListener;

    public CheckListPhotoFragment() {
        // Required empty public constructor
    }

    public static CheckListPhotoFragment newInstance() {
        CheckListPhotoFragment fragment = new CheckListPhotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // 업체별 보여주기 위해, 업체 데이터 불러오기
        coopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retrievedCoops.clear();
                coopNameArrayList.clear();
                coopNameArrayList.add("전체");
                for(DataSnapshot coopSnapshot : dataSnapshot.getChildren()) {
                    Cooperation retrievedCoop = coopSnapshot.getValue(Cooperation.class);
                    retrievedCoops.add(retrievedCoop);
                    coopNameArrayList.add(retrievedCoop.getCoopName());
                }
                checkListPhotoCoopSpinner.setAdapter(checkListPhotoCoopSpinnerAdapter);
                currentCoopName = "전체";
                checkListPhotoCoopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        flawInfoWithPhotoListToDraw.clear();
                        currentCoopName = checkListPhotoCoopSpinner.getItemAtPosition(position).toString();

                        checkListPhotoFragmentLinearLayout.removeAllViews();
                        if(currentCoopName.equals("전체")) {
                            sortAndDrawFlawInfoWithPhotoList(retrievedFlawInfoWithPhotoList);
                        } else {
                            for(int i = 0; i < retrievedFlawInfoWithPhotoList.size(); i++) {
                                if(retrievedFlawInfoWithPhotoList.get(i).getCoopName().equals(currentCoopName))
                                    flawInfoWithPhotoListToDraw.add(retrievedFlawInfoWithPhotoList.get(i));
                            }
                            sortAndDrawFlawInfoWithPhotoList(flawInfoWithPhotoListToDraw);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_check_list_photo, container, false);
        checkListPhotoFragmentLinearLayout = (LinearLayout)view.findViewById(R.id.checkListPhotoFragmentLinearLayout);

        // 협력업체 set
        coopDatabase = FirebaseDatabase.getInstance().getReference("coops"); // 협력업체에 대한 database 입니다.
        checkListPhotoCoopSpinner = (Spinner)view.findViewById(R.id.checkListPhotoFragmentSpinner);
        retrievedCoops = new ArrayList<>();
        coopNameArrayList = new ArrayList<String>();
        checkListPhotoCoopSpinnerAdapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.support_simple_spinner_dropdown_item, coopNameArrayList);

        // 사진첨부 하자 리스트 set
        flawInfoWithPhotoListDatabase = FirebaseDatabase.getInstance().getReference("flawInfoWithPhotoList"); // 사진을 첨부한 하자 리스트에 대한 database 입니다.
        retrievedFlawInfoWithPhotoList = (ArrayList<FlawInfoWithPhoto>) getActivity().getIntent().getSerializableExtra("flawInfoWithPhotoList");
        flawInfoWithPhotoListToDraw = new ArrayList<>();
        insertedFlawWithPhotoListLayoutList = new ArrayList<>(); // 화면 상에 그려진 각 사진첨부 하자 리스트에 대한 layout을 담는 list
        sortAndDrawFlawInfoWithPhotoList(retrievedFlawInfoWithPhotoList);

        return view;
    }

    public void sortAndDrawFlawInfoWithPhotoList(List<FlawInfoWithPhoto> flawInfoWithPhotoList) {

        final int numOfFlawInfoWithPhoto = flawInfoWithPhotoList.size();
        Collections.sort(flawInfoWithPhotoList, new Comparator<FlawInfoWithPhoto>() {
            @Override
            public int compare(FlawInfoWithPhoto o1, FlawInfoWithPhoto o2) {
                o1.getDong().compareTo(o2.getDong());
                if(Integer.parseInt(o1.getDong()) < Integer.parseInt(o2.getDong())) {
                    return -1;
                } else if (Integer.parseInt(o1.getDong()) > Integer.parseInt(o2.getDong())) {
                    return 1;
                }
                return 0;
            }
        });
        // TODO 호수로 정렬 (check list fragment 참조)
        drawFlawInfoWithPhotoList(flawInfoWithPhotoList);
    }

    public void drawFlawInfoWithPhotoList(List<FlawInfoWithPhoto> flawInfoWithPhotoList) {
        for(int i = 0; i < flawInfoWithPhotoList.size(); i++) {
            drawFlawInfoWithPhoto(flawInfoWithPhotoList.get(i));
        }
    }

    public void drawFlawInfoWithPhoto(final FlawInfoWithPhoto flawInfoWithPhoto) {

        // convert dps to pixels using display scale factor
        final float scale = view.getContext().getResources().getDisplayMetrics().density;

        final LinearLayout currentFlawListWithPhotoLinearLayout = new LinearLayout(view.getContext());
        currentFlawListWithPhotoLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView flawInfoTextView = new TextView(view.getContext());
        TextView spacing1 = new TextView(view.getContext());
        ImageView photoImageView = new ImageView(view.getContext());
        TextView spacing2 = new TextView(view.getContext());
        final CheckBox checkBox = new CheckBox(view.getContext());
        final LinearLayout currentFlawListSpacingLinearLayout = new LinearLayout(view.getContext());
        TextView spacing = new TextView(view.getContext());

        // 하자 정보 말뭉치
        String flawInfoString = flawInfoWithPhoto.getCoopName() + " ";
        flawInfoString += flawInfoWithPhoto.getDong() + "동 ";
        flawInfoString += flawInfoWithPhoto.getHo() + "호 \n";
        flawInfoString += flawInfoWithPhoto.getRoom() + " ";
        flawInfoString += flawInfoWithPhoto.getFlawInfo();
        flawInfoTextView.setText(flawInfoString);
        flawInfoTextView.setTextSize(16);
        flawInfoTextView.setTextColor(Color.parseColor("#000000"));
        flawInfoTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams flawInfoTextViewLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        flawInfoTextViewLinearLayoutParams.weight = 1;
        flawInfoTextView.setLayoutParams(flawInfoTextViewLinearLayoutParams);
        currentFlawListWithPhotoLinearLayout.addView(flawInfoTextView);

        // spacing1
        LinearLayout.LayoutParams spacing1LinearLayoutParams = new LinearLayout.LayoutParams((int)(16 * scale + 0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
        spacing1.setLayoutParams(spacing1LinearLayoutParams);
        currentFlawListWithPhotoLinearLayout.addView(spacing1);

        // 이미지
        byte[] bytes = Base64.decode(flawInfoWithPhoto.getPhotoInString(), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray( bytes, 0, bytes.length);
        photoImageView.setImageBitmap(bm);
        LinearLayout.LayoutParams photoImageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        photoImageView.setLayoutParams(photoImageViewLayoutParams);
        currentFlawListWithPhotoLinearLayout.addView(photoImageView);

        // spacing2
        LinearLayout.LayoutParams spacing2LinearLayoutParams = new LinearLayout.LayoutParams((int)(16 * scale + 0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
        spacing2.setLayoutParams(spacing2LinearLayoutParams);
        currentFlawListWithPhotoLinearLayout.addView(spacing2);

        // checkbox
        LinearLayout.LayoutParams checkBoxLinearLayoutParams = new LinearLayout.LayoutParams((int)(40 * scale + 0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
        checkBox.setLayoutParams(checkBoxLinearLayoutParams);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setChecked(flawInfoWithPhoto.isChecked());
        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference updateFlawInfoWithPhotoDatabase = flawInfoWithPhotoListDatabase.child(flawInfoWithPhoto.getFlawInfoWithPhotoKey());
                flawInfoWithPhoto.setChecked(checkBox.isChecked());
                updateFlawInfoWithPhotoDatabase.setValue(flawInfoWithPhoto);
            }
        });
        currentFlawListWithPhotoLinearLayout.addView(checkBox);

        // spacing
        spacing.setHeight((int)(8 * scale + 0.5f));
        spacing.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        currentFlawListSpacingLinearLayout.addView(spacing);


        // Layout set
        checkListPhotoFragmentLinearLayout.addView(currentFlawListWithPhotoLinearLayout);
        insertedFlawWithPhotoListLayoutList.add(currentFlawListWithPhotoLinearLayout);
        checkListPhotoFragmentLinearLayout.addView(currentFlawListSpacingLinearLayout);
        insertedFlawWithPhotoListLayoutList.add(currentFlawListSpacingLinearLayout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
