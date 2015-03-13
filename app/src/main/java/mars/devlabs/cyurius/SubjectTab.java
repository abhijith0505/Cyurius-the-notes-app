package mars.devlabs.cyurius;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SubjectTab extends Activity {

    String downloadLinkRoot = "https://sites.google.com/site/digitalnotesassembly/";
    String semNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_tab);

        Bundle bun = getIntent().getExtras();
        if (bun != null) semNum = String.valueOf(bun.getInt("semNum"));

        if(semNum.contentEquals("1")){
            TextView title=(TextView)findViewById(R.id.subjectTabTitle);
            title.setText("\nSUBJECT\n");
        }

        ListView subjectsListView=(ListView)findViewById(R.id.subjectListView);
        final ArrayList<String> subjectsArrayList = new ArrayList<String>();

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        boolean isConnected=((activeNetwork!=null)&&(activeNetwork.isConnectedOrConnecting()));


        //Download appropriate subjects.txt file, save in sharedpreferences, delete the file, populate listview
        if(isConnected) {
            //Download file
            File cardPath = new File(Environment.getExternalStorageDirectory() + "/Cyurius");
            cardPath.mkdirs();
            File subjectsListFile = new File(cardPath, (semNum + "subjects.txt"));
            DownloadFile downloadSubjects = new DownloadFile();
            downloadSubjects.setPath(subjectsListFile);
            downloadSubjects.setContext(SubjectTab.this);
            downloadSubjects.setShowProgress(false);
            downloadSubjects.setUpdateListView(true);
            downloadSubjects.setArrayList(subjectsArrayList);
            downloadSubjects.setListView(subjectsListView);
            downloadSubjects.setReadPDF(false);
            downloadSubjects.setSemSub(semNum+"subjects");
            downloadSubjects.execute(downloadLinkRoot + "subjects/" + semNum + "subjects.txt");
        }

        else {
            File f = new File("/data/data/" + getPackageName() +  "/shared_prefs/" + getPackageName()+ "_preferences.xml");
            if(f.exists()) {
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(SubjectTab.this);
                String showString=preferences.getString(semNum+"subjects", null);

                if(showString==null)
                    Toast.makeText(SubjectTab.this, "Not connected to internet :-/", Toast.LENGTH_SHORT).show();

                else {
                    String showStringArray[]=showString.split(";");
                    //copy showStringArray to subjectsArrayList
                    //subjectsArrayList=Arrays.asList(showStringArray)
                    Collections.addAll(subjectsArrayList, showStringArray);
                    Collections.sort(subjectsArrayList);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SubjectTab.this, android.R.layout.simple_list_item_1, subjectsArrayList);
                    subjectsListView.setAdapter(arrayAdapter);
                }
            }
            else
                Toast.makeText(SubjectTab.this, "Not connected to internet :-/", Toast.LENGTH_SHORT).show();
        }

        subjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(SubjectTab.this, "You clicked - " + semNum + subjectsArrayList.get(i), Toast.LENGTH_LONG).show();

                if(!subjectsArrayList.get(0).equals("blip")) {
                    Intent startNotesTab = new Intent("mars.devlabs.cyurius.NotesTab");
                    startNotesTab.putExtra("semSub", (semNum + subjectsArrayList.get(i)));
                    startActivity(startNotesTab);
                }
                else
                    Toast.makeText(SubjectTab.this, "blip", Toast.LENGTH_SHORT).show();
            }
        });


        //check if sharedpreference exists else toast "no internet"
        //else {        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}