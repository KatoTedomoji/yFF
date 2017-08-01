package com.anduong.finn.yff;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Context;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import static com.anduong.finn.yff.Utilities.*;


/**
 * Created by An Duong on 6/11/2017.
 */

public class UserMainAct extends AppCompatActivity {
    final Context context = this;
    private TabHost host;
    private TabHost.TabSpec spec;
    private TextView planName, weeksLeft;
    private TextView exerciseName,exerciseRep,exerciseLbs;
    private TextView timerText;
    private TextView loadingView;
    private Button tabOnOffBtn,resetPlanBtn,shufflePlanBtn;
    private LinearLayout buttonsParent, exerciseCheckList;
    private ArrayList<Button> weekdayBtnList;
    private ArrayList<LinearLayout> exerciseList;
    private HorizontalScrollView weekdayScroll;
    private Chronometer timer;
    private String planTxt = "";
    private HashMap<String, ArrayList<View>> dayExercisesMap;
    private static String currentDayHighlight;
    private static String[] TABLE_NAMES = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private boolean timerOn;
    private LinearLayout timersChildLayout,timerWeekdayLayout,timerWeekendLayout;
    private Switch timerSwitch;

    static boolean timerStarted = false;
    static long timeStopped = 0;

    private UserInfoPlanDBHandler planDB = new UserInfoPlanDBHandler(context);
    private UserInfoDBHandler userDB = new UserInfoDBHandler(context);

    private ArrayList<Integer> viewID = new ArrayList<>();
    private static String[] map_content;
    private static String dbPlanString;

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
        weekdayBtnList = new ArrayList<Button>();
        timerOn = false;
        planName = (TextView) findViewById(R.id.schedule_plan_name);
        weeksLeft = (TextView) findViewById(R.id.schedule_week_num_view);
        exerciseList = new ArrayList<>();
        dayExercisesMap = new HashMap<>();
        timersChildLayout = (LinearLayout) findViewById(R.id.timer_children_layout) ;
        timerSwitch = (Switch) findViewById(R.id.setting_parent_switch);
        timerWeekdayLayout = (LinearLayout) findViewById(R.id.setting_timer_weekday_layout);
        timerWeekendLayout = (LinearLayout) findViewById(R.id.setting_timer_weekend_layout);

        host.setup();
        animatingTabWidget();

        //tab 1
        createTab("Schedule", R.id.tab1);
        makeTabOneContent();

        //tab 2
        createTab("Journal", R.id.tab2);
        makeTabTwoContent();

        //tab 3
        createTab("Setting", R.id.tab3);
        makeTabThreeContent();

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

        dbPlanString = userDB.get("current_plan_name");

        String[] temp = dbPlanString.split("_");
        planTxt = temp[0];

        setupWeeksLeft();
        planName.setText(planTxt.toUpperCase());

        timer.setVisibility(View.GONE);

        getWorkoutMap();
        setupWeekdayButtonsFor(buttonsParent);
        setupExerciseListForEachDayBtn();

        highlightTodaysBtnIn(weekdayBtnList);
        setupResetBtn();
        timerListening();
        scrollTodaysButton();
        setUpExerciseViewColor(getWorkoutMap());
    }//set text for planName view

    private void loadingAnimation(final ArrayList<View> exerciseList){
        exerciseCheckList.removeAllViews();
        loadingView.setVisibility(View.VISIBLE);
    final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            loadingView.setVisibility(View.GONE);
            for(int childIndex = 0; childIndex < exerciseList.size() ; childIndex++){
                exerciseCheckList.addView(exerciseList.get(childIndex));
                setVisibleAndFadeIn(context, exerciseCheckList.getChildAt(childIndex));
            }//refill exerciseParent
        }
    }, 5000);
}//TODO loading animation for mainuseract
    private void setupWeeksLeft(){
        int startWeek = Integer.parseInt(userDB.get("current_plan_start_date"));
        String currentWeekStr = getCurrentDate().replaceAll("-","");
        int currentWeek = Integer.parseInt(currentWeekStr);
        if(getCurrentWeekDay().equalsIgnoreCase("Monday")){
            if(startWeek > currentWeek){
                int weeksLeftInt = Integer.parseInt(userDB.get(userDB.KEY_CURRENT_PLAN_DURATION));
                weeksLeftInt = weeksLeftInt-1;
                if(weeksLeftInt < 0){
                    debugLog("workout plan completed, weeks left = " + weeksLeftInt);
                    startActivity(new Intent(UserMainAct.this, PlanSelectAct.class));
                }else{
                    userDB.updateCurrentPlanDuration(weeksLeftInt);
                    debugLog(weeksLeftInt + " weeks left");
                }
            }
            weeksLeft.setText(userDB.get(userDB.KEY_CURRENT_PLAN_DURATION));
        }
    }
    private void setupExerciseListForEachDayBtn(){
        for(String name: TABLE_NAMES){
            dayExercisesMap.put(name, new ArrayList<View>());
        }

        DatabaseHandler db = new DatabaseHandler(context,planTxt);

        for(String name : TABLE_NAMES){
            for(Button dayBtn : weekdayBtnList){
                String dayBtnText = dayBtn.getText().toString();
                if(dayBtnText.equalsIgnoreCase(name)){
                    ArrayList<View> dayViewList = dayExercisesMap.get(name);
                    for(String rowString : db.getRowString(name)) {
                        addExerciseToCheckList(rowString, dayViewList);
                    }
                    //loadingAnimation(dayViewList);
                }
            }
        }
    }
    private void setupWeekdayButtonsFor(LinearLayout parent){
        for(int btnIndex = 0; btnIndex < parent.getChildCount();btnIndex++){
            final Button weekdayBtn = (Button) parent.getChildAt(btnIndex);
            weekdayBtnList.add(weekdayBtn);
            weekdayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentDayHighlight = weekdayBtn.getText().toString();

                    if(currentDayHighlight.equalsIgnoreCase(getCurrentWeekDay())){
                        timerText.setEnabled(true);
                        timerText.setText("Click to start workout.");
                    }else{
                        timerText.setEnabled(false);
                        timerText.setText("This day hasn't come.");
                        timer.setVisibility(View.GONE);
                    }

                    setFocusButtonColor((Button) view);

                    exerciseCheckList.removeAllViews();
                    for(String name: TABLE_NAMES){
                        if(weekdayBtn.getText().toString().equals(name)){
                            ArrayList<View> viewList = dayExercisesMap.get(name);
                            for(View exerciseView : viewList){
                                exerciseCheckList.addView(exerciseView);
                            }
                            setupShuffleBtnFor(exerciseCheckList);
                        }
                    }

                }
            });
        }
    }//add and setup onclick function to the layout
    private void setupResetBtn(){
        setButtonTextClickColor(resetPlanBtn, Color.RED);
        resetPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Warning");
                dialogBuilder.setMessage("If you click ok, this will reset your current progress and earn you an incomplete in your journal.");
                dialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userDB.updateCurrentPlan("none");
                        startActivity(new Intent(UserMainAct.this, PlanSelectAct.class));
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        debugLog("User cancel reset plan");
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }
    private void setupShuffleBtnFor(final LinearLayout parent){
        exerciseList.clear();
        setButtonTextClickColor(shufflePlanBtn,Color.GREEN);
        for(int childIndex = 0; childIndex < parent.getChildCount(); childIndex++){
            exerciseList.add((LinearLayout) parent.getChildAt(childIndex));
        }

        shufflePlanBtn.setOnClickListener(new View.OnClickListener() {
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
                        parent.removeAllViews();
                        Collections.shuffle(exerciseList);
                        for(LinearLayout child : exerciseList){
                            parent.addView(child);
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

                currentDayHighlight = todayBtn.getText().toString();

                ArrayList<View> viewList = dayExercisesMap.get(currentDayHighlight);
                for(View exerciseView : viewList){
                    exerciseCheckList.addView(exerciseView);
                }
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
    private void addExerciseToCheckList(String exerciseInfo, ArrayList<View> dayViewList){
        View inflatedView = getLayoutInflater().inflate(R.layout.schedule_exercise,exerciseCheckList,false);

        LinearLayout exerciseWrapper = (LinearLayout) inflatedView.findViewById(R.id.exercise_parent);
        exerciseName = (TextView) inflatedView.findViewById(R.id.exercise_child);
        exerciseLbs = (TextView) inflatedView.findViewById(R.id.exercise_child_lbs);
        exerciseRep = (TextView) inflatedView.findViewById(R.id.exercise_child_rep);

        if(exerciseInfo.equals("") || exerciseInfo.equals(null)){
        }else{
            String[] exerciseInfoList = exerciseInfo.split(",");

            exerciseName.setText(exerciseInfoList[0]);
            exerciseRep.setText("x"+ exerciseInfoList[1] +" reps");
            exerciseLbs.setText(exerciseInfoList[2] + " lbs");

            LinearLayout outerLayer = (LinearLayout) exerciseWrapper.getParent();
            LinearLayout finalLayer = (LinearLayout) outerLayer.getParent();

            dayViewList.add(finalLayer);
            exerciseWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorDrawable backgroundColor = (ColorDrawable) view.getBackground();
                    if(timerOn && timerStarted && currentDayHighlight.equals(getCurrentWeekDay())){
                        if(backgroundColor.getColor() == Color.WHITE){
                            view.setBackgroundColor(getGreenColor());
                            map_content[view.getId()] = "1";
                            planDB.updateExerciseMapAt(dbPlanString,1,arrayToString(map_content));
                        }else{
                            view.setBackgroundColor(Color.WHITE);
                            if(currentDayHighlight.equalsIgnoreCase(getCurrentWeekDay())){
                                map_content[view.getId()] = "?";
                            }else {
                                map_content[view.getId()] = "0";
                            }
                            planDB.updateExerciseMapAt(dbPlanString,1,arrayToString(map_content));
                        }
                        debugLog(planDB.getRowString(dbPlanString).toString() + "\nASD");
                    }
                }
            });
        }
    }
    private void setUpExerciseViewColor(String map){
        map = map.replace("[","");
        map = map.replace("]","");

        map_content = map.split("\\.");
        debugLog(map);
        ArrayList<View> views = new ArrayList<>();

        for(String name : TABLE_NAMES){
            for(View view : dayExercisesMap.get(name)){
                View child = ((LinearLayout)view).getChildAt(0);
                View childChild = ((LinearLayout)child).getChildAt(0);
                views.add(childChild);
            }
        }

        for(View view : views){
            int id = View.generateViewId()-1;
            view.setId(id);
            viewID.add(id);
        }

        for(int i = 0; i <= views.size()-1; i++){
            if(map_content[i].equalsIgnoreCase("?")){
                views.get(i).setBackgroundColor(Color.WHITE);
            }else if(map_content[i].equalsIgnoreCase("1")){
                views.get(i).setBackgroundColor(getGreenColor());
            }else if(map_content[i].equalsIgnoreCase("0")){
                views.get(i).setBackgroundColor(Color.RED);
            }
        }
    }
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
    private String getWorkoutMap(){
        ArrayList<String> rowStr = planDB.getRowString(dbPlanString);
        String[] info = rowStr.get(rowStr.size()-1).split(",");
        String map = info[2];

        return map;
    }
    //start Tab Two Content
    private void makeTabTwoContent(){//TODO read from database
        setJournalHeader();
        setJournalText();
    }
    private void setJournalHeader(){
        UserInfoDBHandler userDB = new UserInfoDBHandler(context);
        ArrayList<String> userInfo = userDB.getUserInfo();

        TextView journalName = (TextView) findViewById(R.id.journal_nameView);
        TextView journalStart = (TextView) findViewById(R.id.journal_startView);
        TextView journalGoal = (TextView) findViewById(R.id.journal_goalView);

        journalName.setText(userInfo.get(1) + "'s Journal");
        journalStart.setText("Started at " + userInfo.get(5) + " lbs, ");
        journalGoal.setText("on " + userInfo.get(2));
    }
    private void setJournalText(){
        TextView journalTextView = (TextView) findViewById(R.id.journalView);
        String text = userDB.getTableCount() +" is how many tables there are\n";
        ArrayList<String> arr = userDB.getAllTableNames();
        for(String a : arr){
            text += a + " \n";
        }

        journalTextView.setText("not yet implemented");
    }//TODO

    //start Tab Three Content
    private void makeTabThreeContent(){
        setTimerStatus();
        setTimerSwitchListener();
        listeningToTimerButton();
    }//TODO setting GUI code
    private void setTimerStatus(){
        if(userDB.getTimerStatus()){
            timerSwitch.setChecked(true);
            timersChildLayout.setVisibility(View.VISIBLE);
        }else{
            timerSwitch.setChecked(false);
            timersChildLayout.setVisibility(View.GONE);
        }
    }
    private void setTimerSwitchListener(){
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    timersChildLayout.setVisibility(View.VISIBLE);
                    userDB.updateTimerStatus(true);
                }else{
                    timersChildLayout.setVisibility(View.GONE);
                    userDB.updateTimerStatus(false);
                }
            }
        });
    }
    private void listeningToTimerButton(){
        timerWeekendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainAct.this, TimeSelectorAct.class);
                intent.putExtra("week","Weekend");
                startActivity(intent);
            }
        });
        timerWeekdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainAct.this, TimeSelectorAct.class);
                intent.putExtra("week","Weekday");
                startActivity(intent);
            }
        });
    }
}