package com.anduong.finn.yff;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Context;
import android.widget.Chronometer;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.anduong.finn.yff.Utilities.*;


/**
 * Created by An Duong on 6/11/2017.
 */

public class UserMainAct extends AppCompatActivity {
    final Context context = this;
    private TabHost host;
    private TabHost.TabSpec spec;
    private TextView planName;
    private TextView exerciseName,exerciseRep;
    private TextView timerText;
    private Button tabOnOffBtn,resetPlanBtn,shufflePlanBtn;
    private LinearLayout buttonsParent, exerciseCheckList;
    private ArrayList<Button> weekdayBtnList;
    private ArrayList<LinearLayout> exerciseList;
    static  List<WeekScheduler> weeksList;
    private HorizontalScrollView weekdayScroll;
    private Chronometer timer;

    private int timerStopAt;
    private boolean timerOn;

    static boolean timerStarted = false;
    static long timeStopped = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);
        host = (TabHost) findViewById(R.id.tabHost);
        tabOnOffBtn = (Button) findViewById(R.id.main_switch_btn);
        resetPlanBtn = (Button) findViewById(R.id.main_delete_btn);
        shufflePlanBtn = (Button) findViewById(R.id.main_shuffle_btn);
        buttonsParent = (LinearLayout) findViewById(R.id.schedule_btn_parent);
        exerciseCheckList = (LinearLayout) findViewById(R.id.schedule_check_list);
        weekdayScroll = (HorizontalScrollView) findViewById(R.id.schedule_scroll);
        timerText = (TextView) findViewById(R.id.schedule_timer_text);
        timer = (Chronometer) findViewById(R.id.schedule_chronometer);
        weeksList = Saver.loadSchedulesData(context);
        weekdayBtnList = new ArrayList<Button>();
        timerOn = false;
        planName = (TextView) findViewById(R.id.schedule_plan_name);
        exerciseList = new ArrayList<>();

        host.setup();
        animatingTabWidget();
        debugLog(host+"");

        //tab 1
        createTab("Schedule", R.id.tab1);
        makeTabOneContent();

        //tab 2
        createTab("Journal", R.id.tab2);
        makeTabTwoContent();

        //tab 3
        createTab("Setting", R.id.tab3);
        makeTabThreeContent();

        Saver.saveSchedulesData((ArrayList)weeksList,context);
    }
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }//disable going back
    private void animatingTabWidget(){
        host.getTabWidget().setY(300);
        tabOnOffBtn.setOnClickListener(new View.OnClickListener() {
            boolean isOn = false;
            @Override
            public void onClick(View view) {
                if(isOn){
                    host.getTabWidget().animate().translationYBy(300);
                    host.getTabContentView().animate().translationYBy(190);
                    tabOnOffBtn.setTextColor(ColorStateList.valueOf(Color.BLACK));
                    isOn = false;
                }else{
                    host.getTabWidget().animate().translationYBy(-300);
                    host.getTabContentView().animate().translationYBy(-190);
                    tabOnOffBtn.setTextColor(ColorStateList.valueOf(Color.GREEN));
                    isOn = true;
                }
            }
        });
    }//hide tabWidget
    private void createTab(String title, int viewID){
        spec = host.newTabSpec(title);
        spec.setContent(viewID);
        spec.setIndicator(title);
        host.addTab(spec);
    }//make tab child

    //start Tab One Content
    private void makeTabOneContent(){

        for(int i = 0; i < 10 ; i++){
            addExerciseToCheckList("Squat "+i, 10);
        }

        timer.setVisibility(View.GONE);
        planName.setText("Plan");//TODO store plan name somewhere for current session
        setupWeekdayButtonsFor(buttonsParent);
        setupResetBtn();
        setupShuffleBtn();
        timerListening();
        scrollTodaysButton();
    }
    private void setupWeekdayButtonsFor(LinearLayout parent){
        for(int btnIndex = 0; btnIndex < parent.getChildCount();btnIndex++){
            Button weekdayBtn = (Button) parent.getChildAt(btnIndex);
            weekdayBtnList.add(weekdayBtn);
            weekdayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setFocusButtonColor((Button) view);
                }
            });
        }
        highlightTodaysBtnIn(weekdayBtnList);
    }//add and setup onclick function to the layout
    private void setupResetBtn(){
        setButtonTextClickColor(resetPlanBtn, Color.RED);
        resetPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMainAct.this, PlanSelectAct.class));
                Utilities.debugLog("User cancel plan setter, moving to PlanSelectAct");
            }
        });
    }//TODO add confirm box with user, if yes move to PlanSelect else do nothing
    private void setupShuffleBtn(){
        setButtonTextClickColor(shufflePlanBtn,Color.GREEN);
        for(int childIndex = 0; childIndex < exerciseCheckList.getChildCount(); childIndex++){
            exerciseList.add((LinearLayout) exerciseCheckList.getChildAt(childIndex));
        }

        shufflePlanBtn.setOnClickListener(new View.OnClickListener() {//TODO refactor clean up
            @Override
            public void onClick(View view) {
                debugLog("shuffling");
                shufflePlanBtn.setEnabled(false);

                for(LinearLayout child : exerciseList){
                    setInvisibleAndSlideDownAnimation(context,child);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exerciseCheckList.removeAllViews();
                        Collections.shuffle(exerciseList);
                        for(LinearLayout child : exerciseList){
                            exerciseCheckList.addView(child);
                            setVisibleAndSlideUpAnimation(context,child);
                        }
                        shufflePlanBtn.setEnabled(true);
                    }
                },500);
            }
        });
    }//click will animate and shuffle exerciseCheckList children order
    private void highlightTodaysBtnIn(ArrayList<Button> btnList){
        for(Button todayBtn : btnList){
            if(todayBtn.getText().toString().equals(getCurrentWeekDay())){
                todayBtn.setBackgroundColor(Color.BLACK);
                todayBtn.setTextColor(ColorStateList.valueOf(Color.WHITE));
            }
        }
    }// Auto highlight today's weekday
    private void scrollTodaysButton(){
        weekdayScroll.post(new Runnable() {
            @Override
            public void run() {
                int scrollTo = 0;
                final int count = weekdayBtnList.size();
                for(int i = 0 ; i <count ; i ++){
                    final Button todayBtn = weekdayBtnList.get(i);

                    if(todayBtn.getText().toString().equals(Utilities.getCurrentWeekDay())){
                        scrollTo -= 250;// adjust to center
                        break;
                    }

                    scrollTo += todayBtn.getWidth();
                }
                weekdayScroll.scrollTo(scrollTo,0);
            }
        });


    }// Auto scroll to today's day (x,y)
    private void addExerciseToCheckList(String name, int reps){
        View inflatedView = getLayoutInflater().inflate(R.layout.schedule_exercise,exerciseCheckList,false);
        LinearLayout exerciseWrapper = (LinearLayout) inflatedView.findViewById(R.id.exercise_parent);
        exerciseName = (TextView) inflatedView.findViewById(R.id.exercise_child);
        exerciseRep = (TextView) inflatedView.findViewById(R.id.exercise_child_rep);
        exerciseName.setText(name);
        exerciseRep.setText("x"+reps);

        exerciseWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable backgroundColor = (ColorDrawable) view.getBackground();
                if(timerOn){
                    if(backgroundColor.getColor() == Color.WHITE){
                        view.setBackgroundColor(Color.parseColor("#51ef9d"));
                    }else{
                        view.setBackgroundColor(Color.WHITE);
                    }
                }
            }
        });
        exerciseCheckList.addView(inflatedView);
    }//TODO delete later
    private void setFocusButtonColor(Button btn){
        for(Button selectedBtn : weekdayBtnList){
            if(selectedBtn.equals(btn)){
                selectedBtn.setTextColor(Color.WHITE);
                selectedBtn.setBackgroundColor(Color.BLACK);
            }else{
                selectedBtn.setTextColor(Color.BLACK);
                selectedBtn.setBackgroundColor(Color.WHITE);
            }
        }
    }//click will highlight button, unhighlight other buttons in the layout
    private void timerListening(){
        timer.setBase(SystemClock.elapsedRealtime());

        timerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mainHeaderLayout.setVisibility(View.GONE);
                if(timerStarted){
                    timerText.setTextColor(Color.RED);
                    timerText.setText("Workout Stopped");
                    timerStarted = false;
                    timeStopped = SystemClock.elapsedRealtime();
                    timer.stop();
                }else{

                    timer.setVisibility(View.VISIBLE);
                    timerText.setText("Workout Started");
                    timerText.setTextColor(Color.GREEN);
                    timerStarted = true;

                    if(timeStopped == 0){
                        timer.setBase( SystemClock.elapsedRealtime() );
                    }else{
                        long intervalOnPause = (SystemClock.elapsedRealtime() - timeStopped);
                        timer.setBase( timer.getBase() + intervalOnPause );
                    }

                    timer.start();
                    timerOn = true;
                }
            }
        });
        timerText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(timerText.getText().toString().equals("Workout Stopped")){
                    //Utilities.setVisibleAndPop(context,mainHeaderLayout);
                    timerText.setText("Click to Start Workout");
                    timerText.setTextColor(Color.GRAY);
                    timer.setVisibility(View.GONE);
                    timer.setBase(SystemClock.elapsedRealtime());
                    setVisibleAndPop(context,timerText);
                    timeStopped = 0;
                    timerOn= false;

                    for(int i = 0; i < exerciseCheckList.getChildCount(); i++){
                        LinearLayout wrapper = (LinearLayout) exerciseCheckList.getChildAt(i);
                        LinearLayout outer = (LinearLayout) wrapper.getChildAt(0);
                        LinearLayout inner = (LinearLayout) outer.getChildAt(0);// peeling through the wrapper to get to the child

                        inner.setBackgroundColor(Color.WHITE);
                    }
                }
                return true;
            }
        });
    }//click timer will start, click again to stop.  Long press to reset, then reset clicked exercise card color.

    //start Tab Two Content
    private void makeTabTwoContent(){
        UserInfo user = Saver.loadUserData(context);
        TextView journalName = (TextView) findViewById(R.id.journal_nameView);
        TextView journalStart = (TextView) findViewById(R.id.journal_startView);
        TextView journalGoal = (TextView) findViewById(R.id.journal_goalView);

        journalName.setText(user.getName() + "'s Journal");
        journalStart.setText("Start: " + user.getBeginWeight() + " lbs");
        journalGoal.setText("Target: " + user.getTargetWeight() + " lbs");
    }

    //start Tab Three Content
    private void makeTabThreeContent(){
    }

}