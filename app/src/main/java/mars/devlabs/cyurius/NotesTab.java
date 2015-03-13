package mars.devlabs.cyurius;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class NotesTab extends Activity {


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    String downloadLinkRoot = "https://sites.google.com/site/digitalnotesassembly/";
    String semSub;

    //static int current;

    /*static void readFile(ArrayList<String> notesArrayList, Context c) {
        File fileToRead = new File(notesArrayList.get(current));
        Intent read = new Intent(Intent.ACTION_VIEW);
        read.setDataAndType(Uri.fromFile(fileToRead), "application/pdf");
        read.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        c.startActivity(read);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_tab);

        ListView notesListView=(ListView)findViewById(R.id.notesListView);
        final ArrayList<String> notesArrayList = new ArrayList<String>();

        Bundle bun = getIntent().getExtras();
        if (bun != null) semSub = String.valueOf(bun.getString("semSub"));
        //Toast.makeText(NotesTab.this, semSub, Toast.LENGTH_SHORT).show();




        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        final boolean isConnected=((activeNetwork!=null)&&(activeNetwork.isConnectedOrConnecting()));

        if(isConnected) {
            File cardPath = new File(Environment.getExternalStorageDirectory() + "/Cyurius");
            cardPath.mkdirs();
            File notesListFile = new File(cardPath, ( "list.txt"));
            DownloadFile downloadNotes=new DownloadFile();
            downloadNotes.setPath(notesListFile);
            downloadNotes.setContext(NotesTab.this);
            downloadNotes.setShowProgress(false);
            downloadNotes.setUpdateListView(true);
            downloadNotes.setArrayList(notesArrayList);
            downloadNotes.setListView(notesListView);
            downloadNotes.setReadPDF(false);
            downloadNotes.setSemSub(semSub + "list");
            downloadNotes.execute(downloadLinkRoot+semSub+"/"+semSub+"list.txt");
            if(downloadNotes.isCancelled()) Log.i("wheeeeeeeeeeeeheeeeeeeeeeee", "cancelled task");
        }

        else {
            File f = new File("/data/data/" + getPackageName() +  "/shared_prefs/" + getPackageName()+ "_preferences.xml");
            if(f.exists()) {
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(NotesTab.this);
                String showString=preferences.getString(semSub+"list", null);

                if(showString==null)
                    Toast.makeText(NotesTab.this, "Not connected to internet :-/", Toast.LENGTH_SHORT).show();

                else {
                    String showStringArray[]=showString.split(";");
                    //Copy showStringArray to notesArrayList and then set the adapter
                    Collections.addAll(notesArrayList, showStringArray);
                    Collections.sort(notesArrayList);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NotesTab.this, android.R.layout.simple_list_item_1, notesArrayList);
                    notesListView.setAdapter(arrayAdapter);
                }
            }
            else
                Toast.makeText(NotesTab.this, "Not connected to internet :-/", Toast.LENGTH_SHORT).show();
        }

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                File cardPath = new File(Environment.getExternalStorageDirectory() + "/Cyurius/");
                cardPath.mkdirs();
                File notesFile = new File(cardPath, (notesArrayList.get(i)));

                final DownloadFile downloadFile = new DownloadFile();

                if (isConnected == true && notesFile.exists() == false) {


                    downloadFile.setPath(notesFile);
                    downloadFile.setContext(NotesTab.this);
                    downloadFile.setShowProgress(true);
                    downloadFile.setUpdateListView(false);
                    downloadFile.setReadPDF(true);
                    downloadFile.setCurrentItem(i);
                    downloadFile.setSemSub(semSub);
                    downloadFile.execute(downloadLinkRoot + semSub + "/" + notesArrayList.get(i));
                    downloadFile.setURLString(downloadLinkRoot + semSub + "/" + notesArrayList.get(i));

                    //Show_download_Alert_box(view.getContext(), "Download", i);


                } else if (isConnected == false && notesFile.exists() == false) {


                    Toast.makeText(NotesTab.this, "Not connected to internet :-/", Toast.LENGTH_SHORT).show();
                } else if (notesFile.exists()) {
                    Intent read = new Intent(Intent.ACTION_VIEW);
                    read.setDataAndType(Uri.fromFile(notesFile), "application/pdf");
                    read.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(read);
                }

            }

        });
    }




    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_tab, menu);
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
