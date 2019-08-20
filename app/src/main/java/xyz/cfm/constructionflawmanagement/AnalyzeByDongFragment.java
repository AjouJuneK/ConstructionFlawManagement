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

public class AnalyzeByDongFragment extends Fragment {

    private View view;
    private LinearLayout analysisByDongFragmentLinearLayout;
    private PieChart dongPieChart;
    private List<FlawInfo> retrievedFlawInfoList;
    private List<FlawInfoWithPhoto> retrievedFlawInfoWithPhotoList;
    private List<LinearLayout> insertedFlawListLayoutList;
    private List<FlawInfo> convergedFlawInfoList;
    private List<List<FlawInfo>> sameDongFlawList;

    private OnFragmentInteractionListener mListener;

    public AnalyzeByDongFragment() {
        // Required empty public constructor
    }

    public static AnalyzeByDongFragment newInstance() {
        AnalyzeByDongFragment fragment = new AnalyzeByDongFragment();
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
        view = inflater.inflate(R.layout.fragment_analyze_by_dong, container, false);
        analysisByDongFragmentLinearLayout = (LinearLayout)view.findViewById(R.id.analysisByDongFragmentLinearLayout);
        dongPieChart = (PieChart)view.findViewById(R.id.dongPieChart);

        retrievedFlawInfoList = (ArrayList<FlawInfo>) getActivity().getIntent().getSerializableExtra("flawInfoList");
        retrievedFlawInfoWithPhotoList = (ArrayList<FlawInfoWithPhoto>) getActivity().getIntent().getSerializableExtra("flawInfoWithPhotoList");

        insertedFlawListLayoutList = new ArrayList<>();
        convergedFlawInfoList = new ArrayList<>();
        sameDongFlawList = new ArrayList<>(); //

        // 일반 하자리스트와 사진첨부 하자리스트를 합치고 Top hit 순으로 정렬한다
        convergeAndSortFlawInfoList(retrievedFlawInfoList, retrievedFlawInfoWithPhotoList);
        // 동별 하자 분석 리스트 그리기
        drawFlawInfoListOfList(sameDongFlawList);
        // 원형 그래프 그리기
        drawPieChart(sameDongFlawList);

        return view;
    }

    public void drawFlawInfoListOfList(List<List<FlawInfo>> sameDongFlawList) {
        for(int i = 0; i < sameDongFlawList.size(); i++) {
            drawFlowInfoList(sameDongFlawList.get(i));
        }
    }

    public void drawFlowInfoList(List<FlawInfo> flawInfoList) {

        // convert dps to pixels using display scale factor
        final float scale = view.getContext().getResources().getDisplayMetrics().density;
        final LinearLayout currentFlawListLinearLayout = new LinearLayout(view.getContext());
        TextView dongTextView = new TextView(view.getContext());
        TextView dongHitTextView = new TextView(view.getContext());

        // 동
        dongTextView.setText(flawInfoList.get(0).getDong());
        dongTextView.setTextColor(Color.parseColor("#000000"));
        dongTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dongTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(40 * scale + 0.5f));
        dongTextViewLayoutParams.weight = 1;
        dongTextView.setLayoutParams(dongTextViewLayoutParams);
        currentFlawListLinearLayout.addView(dongTextView);

        // 해당 동의 하자 개수
        dongHitTextView.setText(Integer.toString(flawInfoList.size()));
        dongHitTextView.setTextColor(Color.parseColor("#000000"));
        dongHitTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dongHitTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(40 * scale + 0.5f));
        dongHitTextViewLayoutParams.weight = 1;
        dongHitTextView.setLayoutParams(dongHitTextViewLayoutParams);
        currentFlawListLinearLayout.addView(dongHitTextView);

        // Layout
        analysisByDongFragmentLinearLayout.addView(currentFlawListLinearLayout);
        insertedFlawListLayoutList.add(currentFlawListLinearLayout);
    }

    public void drawPieChart(List<List<FlawInfo>> sameDongFlawList) {

        dongPieChart.setUsePercentValues(true);
        dongPieChart.getDescription().setEnabled(false);
        dongPieChart.setExtraOffsets(5, 10, 5, 5);
        dongPieChart.setDragDecelerationFrictionCoef(0.95f);
        dongPieChart.setDrawEntryLabels(false);
        dongPieChart.setDrawHoleEnabled(false);
        dongPieChart.setHoleColor(Color.WHITE);
        dongPieChart.setTransparentCircleRadius(61f);
        dongPieChart.animateY(1000, Easing.EaseInOutCubic);

        ArrayList<PieEntry> dongEntries = new ArrayList<PieEntry>();
        int numOfEtc = 0;
        for(int i = 0; i < sameDongFlawList.size(); i++) {
            switch (i)
            {
                case 0:
                    dongEntries.add(new PieEntry(sameDongFlawList.get(0).size(), sameDongFlawList.get(0).get(0).getDong()));
                    break;
                case 1:
                    dongEntries.add(new PieEntry(sameDongFlawList.get(1).size(), sameDongFlawList.get(1).get(0).getDong()));
                    break;
                case 2:
                    dongEntries.add(new PieEntry(sameDongFlawList.get(2).size(), sameDongFlawList.get(2).get(0).getDong()));
                    break;
                case 3:
                    dongEntries.add(new PieEntry(sameDongFlawList.get(3).size(), sameDongFlawList.get(3).get(0).getDong()));
                    break;
                default:
                    numOfEtc += sameDongFlawList.get(i).size();
                    break;
            }
        }
        dongEntries.add(new PieEntry(numOfEtc, "기타"));

        PieDataSet dongDataSet = new PieDataSet(dongEntries, " (동별 현황)");
        dongDataSet.setFormSize(16f);
        dongDataSet.setSliceSpace(3f);
        dongDataSet.setSelectionShift(5f);
        dongDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData dongData = new PieData(dongDataSet);
        dongData.setValueTextSize(12f);

        dongPieChart.setData(dongData);
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
                o1.getDong().compareTo(o2.getDong());
                if(Integer.parseInt(o1.getDong()) < Integer.parseInt(o2.getDong())) {
                    return -1;
                } else if(Integer.parseInt(o1.getDong()) > Integer.parseInt(o2.getDong())) {
                    return 1;
                }
                return 0;
            }
        });
        sameDongFlawList = getSameDongFlawList(flawInfoList); // 동의 오름차순으로 정렬된것을 토대로 개수 추출
        // Top hit 순으로 재정렬
        Collections.sort(sameDongFlawList, new Comparator<List<FlawInfo>>() {
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

    public List<List<FlawInfo>> getSameDongFlawList(List<FlawInfo> flawInfoList) {
        List<List<FlawInfo>> returningFlawInfoList = new ArrayList<>();

        // 동 list
        List<String> dongList = new ArrayList<String>();
        for(int i = 0; i < flawInfoList.size(); i++) {
            dongList.add(flawInfoList.get(i).getDong());
        }

        List<String> reducedDongList = new ArrayList<>();

        // 중복 제거를 위해
        for(int i = 0; i < flawInfoList.size(); i++) {
            if(i < 1) {
                reducedDongList.add(flawInfoList.get(i).getDong());
            } else {
                if(flawInfoList.get(i).getDong().equals(flawInfoList.get(i - 1).getDong())) {
                    // skip
                } else {
                    reducedDongList.add(flawInfoList.get(i).getDong());
                }
            }
        }

        // 동별로 묶어준다.
        for(int i = 0; i < reducedDongList.size(); i++) {
            List<FlawInfo> certainDongFlawList = new ArrayList<>();
            String currentDong = reducedDongList.get(i);
            for(int j = 0; j < flawInfoList.size(); j++) {
                if(flawInfoList.get(j).getDong().equals(currentDong)) {
                    certainDongFlawList.add(flawInfoList.get(j));
                }
            }
            returningFlawInfoList.add(certainDongFlawList);
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
