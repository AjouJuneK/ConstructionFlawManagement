package xyz.cfm.constructionflawmanagement;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class CheckListFragment extends Fragment {

    private View view;
    private LinearLayout checkListFragmentLinearLayout;
    private String currentCoopName;
    private List<FlawInfo> retrievedFlawInfoList;
    private List<FlawInfo> flawInfoListToDraw;
    private List<LinearLayout> insertedFlawListLayoutList;

    private DatabaseReference coopDatabase;
    private DatabaseReference flawInfoListDatabase;

    private Spinner checkListCoopSpinner;
    private List<Cooperation> retrievedCoops;
    private ArrayList<String> coopNameArrayList;
    private ArrayAdapter checkListCoopSpinnerAdapter;

    private OnFragmentInteractionListener mListener;

    public CheckListFragment() {
        // Required empty public constructor
    }

    public static CheckListFragment newInstance() {
        CheckListFragment fragment = new CheckListFragment();
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
                checkListCoopSpinner.setAdapter(checkListCoopSpinnerAdapter);
                currentCoopName = "전체";
                checkListCoopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        flawInfoListToDraw.clear();
                        currentCoopName = checkListCoopSpinner.getItemAtPosition(position).toString();

                        checkListFragmentLinearLayout.removeAllViews();
                        if(currentCoopName.equals("전체")) {
                            sortAndDrawFlawInfoList(retrievedFlawInfoList);
                        } else {
                            for(int i = 0; i < retrievedFlawInfoList.size(); i++) {
                                if(retrievedFlawInfoList.get(i).getCoopName().equals(currentCoopName))
                                    flawInfoListToDraw.add(retrievedFlawInfoList.get(i));
                            }
                            sortAndDrawFlawInfoList(flawInfoListToDraw);
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

        view = inflater.inflate(R.layout.fragment_check_list, container, false);
        checkListFragmentLinearLayout = (LinearLayout)view.findViewById(R.id.checkListFragmentLinearLayout);

        // 협력업체 set
        coopDatabase = FirebaseDatabase.getInstance().getReference("coops"); // 협력업체에 대한 database 입니다.
        checkListCoopSpinner = (Spinner)view.findViewById(R.id.checkListFragmentSpinner);
        retrievedCoops = new ArrayList<>();
        coopNameArrayList = new ArrayList<String>();
        checkListCoopSpinnerAdapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.support_simple_spinner_dropdown_item, coopNameArrayList);

        // 하자 리스트 set
        flawInfoListDatabase = FirebaseDatabase.getInstance().getReference("flawInfoList"); // 하자 리스트에 대한 database 입니다.
        retrievedFlawInfoList = (ArrayList<FlawInfo>) getActivity().getIntent().getSerializableExtra("flawInfoList");
        flawInfoListToDraw = new ArrayList<>();
        insertedFlawListLayoutList = new ArrayList<>(); // 화면 상에 그려진 각 하자 리스트에 대한 layout을 담는 list
        sortAndDrawFlawInfoList(retrievedFlawInfoList);

        return view;
    }

    public void sortAndDrawFlawInfoList(List<FlawInfo> flawInfoList) {

        Collections.sort(flawInfoList, new Comparator<FlawInfo>() {
            @Override
            public int compare(FlawInfo o1, FlawInfo o2) {
                o1.getDong().compareTo(o2.getDong());
                if(Integer.parseInt(o1.getDong()) < Integer.parseInt(o2.getDong())) {
                    return -1;
                } else if (Integer.parseInt(o1.getDong()) > Integer.parseInt(o2.getDong())) {
                    return 1;
                }
                return 0;
            }
        });
        // TODO 호수로 정렬
//        String tempString = "";
//
//        List<String> hoStringList = new ArrayList<>();
//        List<Integer> dongOverlapFlagList = new ArrayList<>();
//        int numOfOverlaps = 0;
//        for(int i = 0; i < numOfFlawInfo; i++) {
//            if(i > 0) {
//                if(flawInfoList.get(i).getDong().equals(flawInfoList.get(i - 1).getDong())){
//                    dongOverlapFlagList.add(1);
//                } else {
//                    dongOverlapFlagList.add(0);
//                }
//            } else {
//                dongOverlapFlagList.add(0);
//            }
//            hoStringList.add(flawInfoList.get(i).getHo());
//        }
//
//        List<Integer> overlapIndices = new ArrayList<>();
//        for(int i = 0; i < numOfFlawInfo; i++) {
//            if(i > 0) {
//                if(dongOverlapFlagList.get(i) == 1) { //
//                    if(dongOverlapFlagList.get(i - 1) == 0) {
//                        numOfOverlaps++;
//                        overlapIndices.add(i - 1);
//                    }
//                }
//            }
//        }
//        for(int i = 0; i < numOfOverlaps; i++) {
//            List<FlawInfo> subHoList = new ArrayList<>();
//            int length = 0;
//            subHoList.add(flawInfoList.get(overlapIndices.get(i)));
//            for (int j = overlapIndices.get(i) + 1; dongOverlapFlagList.get(j) != 1; j++) {
////                length++;
//                subHoList.add(flawInfoList.get(j));
//            }
//        }
//        for(int i = 0; i < numOfFlawInfo; i++) {
//            tempString += dongOverlapFlagList.get(i) + ",";
////            tempString += flawInfoList.get(i).getDong() + ",";
//        }
//        Toast.makeText(view.getContext(), "" + tempString, Toast.LENGTH_SHORT).show();
//        Toast.makeText(view.getContext(), "" + flawInfoList.get(0).getDong() + " , " + flawInfoList.get(1).getDong() + " , " + flawInfoList.get(2).getDong(), Toast.LENGTH_SHORT).show();
        drawFlawList(flawInfoList);
    }

    public void drawFlawList(List<FlawInfo> flawInfoList) {
        for(int i = 0; i < flawInfoList.size(); i++) {
            drawFlawInfo(flawInfoList.get(i));
        }
    }

    public void drawFlawInfo(final FlawInfo flawInfo) {

        // convert dps to pixels using display scale factor
        final float scale = view.getContext().getResources().getDisplayMetrics().density;

        final LinearLayout currentFlawListLinearLayout = new LinearLayout(view.getContext());
        currentFlawListLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView coopTextView = new TextView(view.getContext());
        TextView dongTextView = new TextView(view.getContext());
        TextView hoTextView = new TextView(view.getContext());
        TextView roomTextView = new TextView(view.getContext());
        TextView flawInfoTextView = new TextView(view.getContext());
        final CheckBox checkBox = new CheckBox(view.getContext());

        // 업체 정보
        coopTextView.setText(flawInfo.getCoopName());
        coopTextView.setTextColor(Color.parseColor("#000000"));
        coopTextView.setWidth((int)(40 * scale + 0.5f));
        coopTextView.setHeight((int)(40 * scale + 0.5f));
        coopTextView.setGravity(Gravity.CENTER);
        currentFlawListLinearLayout.addView(coopTextView);

        // 동
        dongTextView.setText(flawInfo.getDong());
        dongTextView.setTextColor(Color.parseColor("#000000"));
        dongTextView.setWidth((int)(50 * scale + 0.5f));
        dongTextView.setHeight((int)(40 * scale + 0.5f));
        dongTextView.setGravity(Gravity.CENTER);
        currentFlawListLinearLayout.addView(dongTextView);

        // 호수
        hoTextView.setText(flawInfo.getHo());
        hoTextView.setTextColor(Color.parseColor("#000000"));
        hoTextView.setWidth((int)(50 * scale + 0.5f));
        hoTextView.setHeight((int)(40 * scale + 0.5f));
        hoTextView.setGravity(Gravity.CENTER);
        currentFlawListLinearLayout.addView(hoTextView);

        // 실
        roomTextView.setText(flawInfo.getRoom());
        roomTextView.setTextColor(Color.parseColor("#000000"));
        roomTextView.setWidth((int)(70 * scale + 0.5f));
        roomTextView.setHeight((int)(40 * scale + 0.5f));
        roomTextView.setGravity(Gravity.CENTER);
        currentFlawListLinearLayout.addView(roomTextView);

        // 하자 정보
        flawInfoTextView.setText(flawInfo.getFlawInfo());
        flawInfoTextView.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams flawInfoLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(40 * scale + 0.5f));
        flawInfoLinearLayoutParams.weight = 1;
        flawInfoTextView.setLayoutParams(flawInfoLinearLayoutParams);
        flawInfoTextView.setGravity(Gravity.CENTER);
        currentFlawListLinearLayout.addView(flawInfoTextView);

        // checkbox
        LinearLayout.LayoutParams checkBoxLinearLayoutParams = new LinearLayout.LayoutParams((int)(40 * scale + 0.5f), (int)(40 * scale + 0.5f));
        checkBox.setLayoutParams(checkBoxLinearLayoutParams);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setChecked(flawInfo.isChecked());
        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference updateFlawInfoDatabase = flawInfoListDatabase.child(flawInfo.getFlawInfoKey());
                flawInfo.setChecked(checkBox.isChecked());
                updateFlawInfoDatabase.setValue(flawInfo);
            }
        });
        currentFlawListLinearLayout.addView(checkBox);

        // Layout set
        checkListFragmentLinearLayout.addView(currentFlawListLinearLayout);
        insertedFlawListLayoutList.add(currentFlawListLinearLayout);
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
        void onFragmentInteraction(Uri uri);
    }
}
