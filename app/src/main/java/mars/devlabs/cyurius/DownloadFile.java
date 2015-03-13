package mars.devlabs.cyurius;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kienme on 18/12/14..
 *
 */

public class DownloadFile extends AsyncTask<String, Integer, String> {


    private boolean cancelFlag = false;
    private ProgressDialog mProgressDialog;
    private File outputFile;
    private Context context;
    private Boolean showProgress;
    private Boolean updateListView;
    private ArrayList arrayList;
    private ListView listView;
    private Boolean readPDF;
    private int currentItem;
    private String semSub;
    private Boolean finishedDownload;
    private String URLString;

    int fileLength;


    DownloadFile() {
        //readPDF=false;
        finishedDownload=false;
    }

    void setSemSub(String s) {
        semSub=s;
    }
    void setCurrentItem(int c) {
        currentItem=c;
    }
    void setReadPDF(Boolean r) {
        readPDF=r;
    }
    void setPath(File f) {
        outputFile=f;
    }
    void setContext(Context c) {
        context=c;
    }
    void setShowProgress(Boolean p) {
        showProgress=p;
    }
    void setUpdateListView(Boolean u) {
        updateListView=u;
    }
    void setArrayList(ArrayList a) {
        arrayList=a;
    }
    void setListView(ListView l) {
        listView=l;
    }
    void setURLString(String u) {
        URLString=u;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(context);

        if(showProgress) {


            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle("Downloading... ");
            mProgressDialog.setMessage("Press back key to cancel");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (!finishedDownload) {
                        Toast.makeText(context, "Download cancelled", Toast.LENGTH_SHORT).show();
                        outputFile.delete();
                        readPDF = false;
                        //cancelFlag = true;
                        cancel(true);

                        if(isCancelled()) Log.i("CANCEL STATUS", "CANCELLED");
                        else Log.i("CANCEL STATUS", "NOT CANCELLED");

                    }
                }
            });
            mProgressDialog.show();
        }

        else {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("Fetching data...");
            mProgressDialog.setMessage("Please wait.");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }
    //public int fileLength;
    //public String fileSize;

    @Override
    protected String doInBackground(String... strings) {

        if((!readPDF)||((readPDF)&&(!outputFile.exists()))) {
            try {
                URL url = new URL(strings[0]);

                //URLConnection connection=url.openConnection();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoOutput(true);

                connection.connect();
                int fileLength = connection.getContentLength();

                /*DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                String fileSize = df.format((float)fileLength/1048576);*/

                if(showProgress)
                publishProgress(-33, fileLength*100/1048576);

                InputStream input = connection.getInputStream();

                FileOutputStream output = new FileOutputStream(outputFile);
                byte data[] = new byte[1024];
                long total = 0;
                int count;

                //Toast.makeText(context, "File Size: " + fileLength, Toast.LENGTH_LONG).show();

                while ((count = input.read(data)) != -1) {
                    total += count;
                    //if(showProgress)
                    //Toast.makeText(context, "File Size: " + total, Toast.LENGTH_LONG).show();
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }


                //output.flush();
                output.close();
                //input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(showProgress){
            //DecimalFormat df = new DecimalFormat();
            //df.setMaximumFractionDigits(2);
            //fileSize = df.format((float)fileLength/1000000);
            Toast.makeText(context, "Downloaded complete", Toast.LENGTH_SHORT).show();
        }


        String listToPut=new String();

        finishedDownload=true;
        mProgressDialog.cancel();

        //Populate listview, update sharedpreferences
        if(updateListView) {
            //ArrayList<String> arrayList = new ArrayList<String>();
            BufferedReader reader = null;
            try {
                reader=new BufferedReader(new FileReader(outputFile));
                String line;
                while((line=reader.readLine())!=null) {
                    arrayList.add(line);
                    listToPut=listToPut+line+";";
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                if(reader!=null) {
                    try {
                        reader.close();
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            outputFile.delete();

            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString(semSub, listToPut);
            editor.commit();

            Collections.sort(arrayList);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);


        }

        if(readPDF) {
            //NotesTab.readFile(arrayList, context);
            //File fileToRead = new File(Environment.getExternalStorageDirectory()+"/Cyurius/"+semSub+"/"+arrayList.get(currentItem).toString());
            Intent read = new Intent(Intent.ACTION_VIEW);
            read.setDataAndType(Uri.fromFile(/*fileToRead*/outputFile), "application/pdf");
            //read.setData(Uri.fromFile(fileToRead));
            //read.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            read.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(read);
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(showProgress)
        if(values[0]==-33) mProgressDialog.setTitle("Downloading("+(float)values[1]/100.0+" MB)");

        else
        mProgressDialog.setProgress(values[0]);
    }
}


