package mars.devlabs.cyurius;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class MainActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    static SectionsPagerAdapter mSectionsPagerAdapter;
    static int timesTapped = 0;
    static Activity act;

    static void raiseToast(String toastText) {
        Toast.makeText(act, toastText, Toast.LENGTH_LONG).show();
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        act = this;
        //boolean inPage1=false;


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        if (mViewPager.getCurrentItem() == 1) {

            /*mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {
                    mViewPager.setCurrentItem(1);
                }

                @Override
                public void onPageSelected(int i) {

                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    mViewPager.setCurrentItem(1);
                }
            });*/
        }

    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;


        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
            mFragmentManager = fm;
        }

        public void removeFragment(int position) {
            Fragment ff = getItem(position + 1);
            mFragmentManager.beginTransaction().remove(ff).commit();
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView;
            Bundle args = getArguments();

            if (args.getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.notes_tab, container, false);
                TextView title = (TextView) rootView.findViewById(R.id.notesTabTitle);
                title.setText("\nARCHIVE\n");

                File file = new File(Environment.getExternalStorageDirectory() + "/Cyurius/");
                final File files[] = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".pdf");
                    }
                });

                if ((files == null)) {
                    //Toast.makeText(act, "Start downloading notes!", Toast.LENGTH_LONG).show();
                } else {
                    String stringListPDF[] = new String[files.length];
                    for (int q = 0; q < stringListPDF.length; ++q) {
                        stringListPDF[q] = files[q].getName();
                    }

                    final ListView listViewPDF = (ListView) rootView.findViewById(R.id.notesListView);
                    final ArrayList<String> arrayListPDF = new ArrayList<String>();
                    Collections.addAll(arrayListPDF, stringListPDF);
                    final ArrayAdapter<String> arrayAdapterPDF = new ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, arrayListPDF);
                    listViewPDF.setAdapter(arrayAdapterPDF);

                    listViewPDF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent read = new Intent(Intent.ACTION_VIEW);
                            read.setDataAndType(Uri.fromFile(files[position]), "application/pdf");
                            read.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(read);
                        }
                    });

                    listViewPDF.setLongClickable(true);
                    listViewPDF.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            Show_Alert_box(view.getContext(), "Delete this file?", position);

                            return true;
                        }

                        private void Show_Alert_box(final Context context, String message, final int position) {
                            final int pos = position;

                            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Delete");
                            alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String str = listViewPDF.getItemAtPosition(pos).toString();
                                    try {
                                        File file = new File(android.os.Environment.getExternalStorageDirectory() + "/Cyurius/" + str);
                                        if (file.exists()) {
                                            file.delete();
                                            arrayListPDF.remove(position);
                                            arrayAdapterPDF.notifyDataSetChanged();
                                            //listViewPDF.invalidateViews();
                                            Toast.makeText(context,"File Deleted",Toast.LENGTH_SHORT).show();
                                        }

                                        arrayAdapterPDF.notifyDataSetChanged();

                                    } catch (Exception e) {

                                    }
                                }
                            });
                            alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });

                            alertDialog.setMessage(message);
                            alertDialog.show();

                        }



                }
                    );

                }

            } else if (args.getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.main_fragment, container, false);
                TextView mars = (TextView) rootView.findViewById(R.id.section_label);

                mars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (timesTapped < 9) ++timesTapped;
                        else if (timesTapped == 9) {
                            raiseToast("Mathews, Abhijith, Ravikiran, Shashank");
                            ++timesTapped;
                        }
                    }
                });
            } else {
                rootView = inflater.inflate(R.layout.sem_select_fragment, container, false);

                //SEM 1
                Button sem1button = (Button) rootView.findViewById(R.id.button1);
                sem1button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 1);
                        startActivity(startSubjectTab);
                    }
                });

                //SEM 2
                Button sem2button = (Button) rootView.findViewById(R.id.button2);
                sem2button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 1);
                        startActivity(startSubjectTab);
                    }
                });

                //SEM 3
                Button sem3button = (Button) rootView.findViewById(R.id.button3);
                sem3button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 3);
                        startActivity(startSubjectTab);
                    }
                });
                //SEM 4
                Button sem4button = (Button) rootView.findViewById(R.id.button4);
                sem4button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 4);
                        startActivity(startSubjectTab);
                    }
                });
                //SEM 5
                Button sem5button = (Button) rootView.findViewById(R.id.button5);
                sem5button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 5);
                        startActivity(startSubjectTab);
                    }
                });

                //SEM 6
                Button sem6button = (Button) rootView.findViewById(R.id.button6);
                sem6button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 6);
                        startActivity(startSubjectTab);
                    }
                });

                //SEM 7
                Button sem7button = (Button) rootView.findViewById(R.id.button7);
                sem7button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 7);
                        startActivity(startSubjectTab);
                    }
                });
                //SEM 8
                Button sem8button = (Button) rootView.findViewById(R.id.button8);
                sem8button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startSubjectTab = new Intent("mars.devlabs.cyurius.SubjectTab");
                        startSubjectTab.putExtra("semNum", 8);
                        startActivity(startSubjectTab);
                    }
                });

                /*if(mViewPager.getCurrentItem()==0)
                {
                    mViewPager.setCurrentItem(1);
                    //mSectionsPagerAdapter.removeFragment(0);
                    //mViewPager.setAdapter(mSectionsPagerAdapter);
                }*/
            }


            //text.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap back button to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

}