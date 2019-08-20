package xyz.cfm.constructionflawmanagement;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnalyzeByCoopFragment extends Fragment {

    private View view;
    private LinearLayout analysisByCoopFragmentLinearLayout;
    private PieChart coopPieChart;
    private List<FlawInfo> retrievedFlawInfoList;
    private List<FlawInfoWithPhoto> retrievedFlawInfoWithPhotoList;
    private List<LinearLayout> insertedFlawListLayoutList;
    private List<FlawInfo> convergedFlawInfoList;
    private List<List<FlawInfo>> sameCoopFlawList;

    private OnFragmentInteractionListener mListener;

    public AnalyzeByCoopFragment() {
        // Required empty public constructor
    }

    public static AnalyzeByCoopFragment newInstance() {
        AnalyzeByCoopFragment fragment = new AnalyzeByCoopFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_analyze_by_coop, container, false);
        analysisByCoopFragmentLinearLayout = (LinearLayout)view.findViewById(R.id.analysisByCoopFragmentLinearLayout);
        coopPieChart = (PieChart)view.findViewById(R.id.coopPieChart);

        retrievedFlawInfoList = (ArrayList<FlawInfo>) getActivity().getIntent().getSerializableExtra("flawInfoList");
        retrievedFlawInfoWithPhotoList = (ArrayList<FlawInfoWithPhoto>) getActivity().getIntent().getSerializableExtra("flawInfoWithPhotoList");

        insertedFlawListLayoutList = new ArrayList<>();
        convergedFlawInfoList = new ArrayList<>();
        sameCoopFlawList = new ArrayList<>(); //

        // 일반 하자리스트와 사진첨부 하자리스트를 합치고 Top hit 순으로 정렬한다
        convergeAndSortFlawInfoList(retrievedFlawInfoList, retrievedFlawInfoWithPhotoList);
        // 업체별 하자 분석 리스트 그리기
        drawFlawInfoListOfList(sameCoopFlawList);
        // 원형 그래프 그리기
        drawPieChart(sameCoopFlawList);

        return view;
    }

    public void drawFlawInfoListOfList(List<List<FlawInfo>> sameCoopFlawList) {
        for(int i = 0; i < sameCoopFlawList.size(); i++) {
            drawFlowInfoList(sameCoopFlawList.get(i));
        }
    }

    public void drawFlowInfoList(List<FlawInfo> flawInfoList) {

        // convert dps to pixels using display scale factor
        final float scale = view.getContext().getResources().getDisplayMetrics().density;
        final LinearLayout currentFlawListLinearLayout = new LinearLayout(view.getContext());
        TextView coopTextView = new TextView(view.getContext());
        TextView coopHitTextView = new TextView(view.getContext());

        // 동
        coopTextView.setText(flawInfoList.get(0).getCoopName());
        coopTextView.setTextColor(Color.parseColor("#000000"));
        coopTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams coopTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(40 * scale + 0.5f));
        coopTextViewLayoutParams.weight = 1;
        coopTextView.setLayoutParams(coopTextViewLayoutParams);
        currentFlawListLinearLayout.addView(coopTextView);

        // 해당 동의 하자 개수
        coopHitTextView.setText(Integer.toString(flawInfoList.size()));
        coopHitTextView.setTextColor(Color.parseColor("#000000"));
        coopHitTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams coopHitTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(40 * scale + 0.5f));
        coopHitTextViewLayoutParams.weight = 1;
        coopHitTextView.setLayoutParams(coopHitTextViewLayoutParams);
        currentFlawListLinearLayout.addView(coopHitTextView);

        // Layout
        analysisByCoopFragmentLinearLayout.addView(currentFlawListLinearLayout);
        insertedFlawListLayoutList.add(currentFlawListLinearLayout);
    }

    public void drawPieChart(List<List<FlawInfo>> sameCoopFlawList) {

        coopPieChart.setUsePercentValues(true);
        coopPieChart.getDescription().setEnabled(false);
        coopPieChart.setExtraOffsets(5, 10, 5, 5);
        coopPieChart.setDragDecelerationFrictionCoef(0.95f);
        coopPieChart.setDrawEntryLabels(false);
        coopPieChart.setDrawHoleEnabled(false);
        coopPieChart.setHoleColor(Color.WHITE);
        coopPieChart.setTransparentCircleRadius(61f);
        coopPieChart.animateY(1000, Easing.EaseInOutCubic);

        ArrayList<PieEntry> coopEntries = new ArrayList<PieEntry>();
        int numOfEtc = 0;
        for(int i = 0; i < sameCoopFlawList.size(); i++) {
            switch (i)
            {
                case 0:
                    coopEntries.add(new PieEntry(sameCoopFlawList.get(0).size(), sameCoopFlawList.get(0).get(0).getCoopName()));
                    break;
                case 1:
                    coopEntries.add(new PieEntry(sameCoopFlawList.get(1).size(), sameCoopFlawList.get(1).get(0).getCoopName()));
                    break;
                case 2:
                    coopEntries.add(new PieEntry(sameCoopFlawList.get(2).size(), sameCoopFlawList.get(2).get(0).getCoopName()));
                    break;
                case 3:
                    coopEntries.add(new PieEntry(sameCoopFlawList.get(3).size(), sameCoopFlawList.get(3).get(0).getCoopName()));
                    break;
                default:
                    numOfEtc += sameCoopFlawList.get(i).size();
                    break;
            }
        }
        coopEntries.add(new PieEntry(numOfEtc, "기타"));

        PieDataSet coopDataSet = new PieDataSet(coopEntries, " (업체별 현황)");
        coopDataSet.setFormSize(16f);
        coopDataSet.setSliceSpace(3f);
        coopDataSet.setSelectionShift(5f);
        coopDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData coopData = new PieData(coopDataSet);
        coopData.setValueTextSize(12f);

        coopPieChart.setData(coopData);
    }

    public void convergeAndSortFlawInfoList(List<FlawInfo> flawInfoList, List<FlawInfoWithPhoto> flawInfoWithPhotoList) {
        convergedFlawInfoList.clear();
        for(int i = 0; i < flawInfoList.size(); i++) {
            convergedFlawInfoList.add(flawInfoList.get(i));
        }
        for(int i = 0; i < flawInfoWithPhotoList.size(); i++) {
            FlawInfoWithPhoto currentflawInfoWithPhoto = flawInfoWithPhotoList.get(i);
            FlawInfo flawInfoPasser = new FlawInfo(currentflawInfoWithPhoto.getFlawInfoWithPhotoKey(),
                    currentflawInfoWithPhoto.getDong(),
                    currentflawInfoWithPhoto.getHo(),
                    currentflawInfoWithPhoto.getRoom(),
                    currentflawInfoWithPhoto.getFlawInfo(),
                    currentflawInfoWithPhoto.getCoopName(),
                    currentflawInfoWithPhoto.isChecked());
            convergedFlawInfoList.add(flawInfoPasser);
        }
        sortFlawInfoList(convergedFlawInfoList);
    }

    public void sortFlawInfoList(List<FlawInfo> flawInfoList) {
        Collections.sort(flawInfoList, new Comparator<FlawInfo>() {
            @Override
            public int compare(FlawInfo o1, FlawInfo o2) {
                return o1.getCoopName().compareToIgnoreCase(o2.getCoopName());
            }
        });
        sameCoopFlawList = getSameCoopFlawList(flawInfoList); // 동의 오름차순으로 정렬된것을 토대로 개수 추출
        // Top hit 순으로 재정렬
        Collections.sort(sameCoopFlawList, new Comparator<List<FlawInfo>>() {
            @Override
            public int compare(List<FlawInfo> o1, List<FlawInfo> o2) {
                ((Integer)o1.size()).compareTo((Integer)o2.size());
                if(o1.size() > o2.size()) {
                    return -1;
                } else if (o1.size() < o2.size()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public List<List<FlawInfo>> getSameCoopFlawList(List<FlawInfo> flawInfoList) {
        List<List<FlawInfo>> returningFlawInfoList = new ArrayList<>();

        // 동 list
        List<String> coopList = new ArrayList<String>();
        for(int i = 0; i < flawInfoList.size(); i++) {
            coopList.add(flawInfoList.get(i).getCoopName());
        }

        List<String> reducedCoopList = new ArrayList<>();

        // 중복 제거를 위해
        for(int i = 0; i < flawInfoList.size(); i++) {
            if(i < 1) {
                reducedCoopList.add(flawInfoList.get(i).getCoopName());
            } else {
                if(flawInfoList.get(i).getCoopName().equals(flawInfoList.get(i - 1).getCoopName())) {
                    // skip
                } else {
                    reducedCoopList.add(flawInfoList.get(i).getCoopName());
                }
            }
        }

        // 동별로 묶어준다.
        for(int i = 0; i < reducedCoopList.size(); i++) {
            List<FlawInfo> certainCoopFlawList = new ArrayList<>();
            String currentCoop = reducedCoopList.get(i);
            for(int j = 0; j < flawInfoList.size(); j++) {
                if(flawInfoList.get(j).getCoopName().equals(currentCoop)) {
                    certainCoopFlawList.add(flawInfoList.get(j));
                }
            }
            returningFlawInfoList.add(certainCoopFlawList);
        }
        return returningFlawInfoList;
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
