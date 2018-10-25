package com.example.luis.qrscannerfrandreas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import adapters.ThirdLevelListAdapter;
import sql.DatabaseHelper;

public class TourExpListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;


    private ExpandableListView expandableListView;


    ArrayList<String> parent = new ArrayList<String>(databaseHelper.getTours());
    ArrayList<String> level1 = new ArrayList<String>(getChild(parent));
    ArrayList<String> level2 = new ArrayList<String>(getChild(level1));
    ArrayList<String> level3 = new ArrayList<String>(getChild(level2));

    LinkedHashMap<String, ArrayList<String>> thirdLevelq1 = new LinkedHashMap<>();

    private ArrayList getChild(ArrayList parent) {
        ArrayList<ArrayList<ArrayList<String>>> child_level1= new ArrayList<>();
        ArrayList<ArrayList<String>> child_level2= new ArrayList<>();
        ArrayList<String> child_level3= new ArrayList<>();

        for (int i = 0; i < parent.size(); i++)

        {
           // child_level1.add(Integer.toString(i));

            for (int j = 0; j < parent.size(); j++) {

            //    child_level2.add(Integer.toString(j));


                for (int k = 0; k < parent.size(); k++) {

                //    child_level3.add(Integer.toString(k));
                }
            }
        }
        ArrayList<String> child;
        child = "leer";
        return child; 
    }


            /**
            String[] parent = new String[]{"What is View?", "What is  Layout?", "What is Dynamic Views?"};
            String[] q1 = new String[]{"List View", "Grid View"};
            String[] q2 = new String[]{"Linear Layout", "Relative Layout"};
            String[] q3 = new String[]{"Recycle View"};
            String[] des1 = new String[]{"A layout that organizes its children into a single horizontal or vertical row. It creates a scrollbar if the length of the window exceeds the length of the screen."};
            String[] des2 = new String[]{"Enables you to specify the location of child objects relative to each other (child A to the left of child B) or to the parent (aligned to the top of the parent)."};
            String[] des3 = new String[]{"This list contains linear layout information"};
            String[] des4 = new String[]{"This list contains relative layout information,Displays a scrolling grid of columns and rows"};
            String[] des5 = new String[]{"Under the RecyclerView model, several different components work together to display your data. Some of these components can be used in their unmodified form; for example, your app is likely to use the RecyclerView class directly. In other cases, we provide an abstract class, and your app is expected to extend it; for example, every app that uses RecyclerView needs to define its own view holder, which it does by extending the abstract RecyclerView.ViewHolder class."};

            LinkedHashMap<String, String[]> thirdLevelq1 = new LinkedHashMap<>();
            LinkedHashMap<String, String[]> thirdLevelq2 = new LinkedHashMap<>();
            LinkedHashMap<String, String[]> thirdLevelq3 = new LinkedHashMap<>();
             */
            /**
             * Second level array list
             */
            List<String[]> secondLevel = new ArrayList<>();
            /**
             * Inner level data
             */
            List<LinkedHashMap<String, String[]>> data = new ArrayList<>();


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                setUpAdapter();
            }

            private void setUpAdapter() {

                /**
                secondLevel.add(q1);
                secondLevel.add(q2);
                secondLevel.add(q3);
                thirdLevelq1.put(q1[0], des1);
                thirdLevelq1.put(q1[1], des2);
                thirdLevelq2.put(q2[0], des3);
                thirdLevelq2.put(q2[1], des4);
                thirdLevelq3.put(q3[0], des5);

                data.add(thirdLevelq1);
                data.add(thirdLevelq2);
                data.add(thirdLevelq3);
                 */
                expandableListView = findViewById(R.id.ExpList);
                //passing three level of information to constructor
                ThirdLevelListAdapter thirdLevelListAdapterAdapter = new ThirdLevelListAdapter(this, parent, secondLevel, data);
                expandableListView.setAdapter(thirdLevelListAdapterAdapter);
                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)
                            expandableListView.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }
                });


            }
        }
