package org.me.gcu.my_button_application;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//This is the work of Laura Ferguson s2130158

public class Plannedroadworks extends AppCompatActivity {


    ListView plannedRoadworksListView;
    AlertDialog.Builder builder;
    SearchView searchView;

    ArrayList<String> titles;
    ArrayList<String> description;
    ArrayList<String> pubDate;
    ArrayList<Date> startDateList;
    ArrayList<Date> endDateList;

    ArrayAdapter adapter;

    private Button currentIncidentButton;
    private Button roadworksButton;
    private Button plannedRoadworksButton;

    private List<ListItems> listDates = new ArrayList<ListItems>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plannedroadworks);

        //This search bar crashes the app currently

        /*searchView=findViewById(R.id.searchBar);
        plannedRoadworksListView=findViewById(R.id.plannedRoadworksListView);
        adapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, titles);
        plannedRoadworksListView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {

                adapter.getFilter().filter(q);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                adapter.getFilter().filter(s);

                return false;
            }
        });*/

        currentIncidentButton=findViewById(R.id.currentIncidentButton);
        currentIncidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Plannedroadworks.this,MainActivity.class);
                startActivity(intent);
            }

        });

        roadworksButton=findViewById(R.id.roadworksButton);
        roadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Plannedroadworks.this,MainActivity2.class);
                startActivity(intent);
            }
        });

        plannedRoadworksButton=findViewById(R.id.plannedRoadworksButton);
        plannedRoadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Plannedroadworks.this,Plannedroadworks.class);
                startActivity(intent);
            }
        });

        plannedRoadworksListView = (ListView) findViewById(R.id.plannedRoadworksListView);
        titles = new ArrayList<String>();
        description = new ArrayList<String>();
        pubDate = new ArrayList<String>();



        plannedRoadworksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //toast isnt suitable, too short
                //Toast.makeText(MainActivity.this, "Details: " + description.get(i), Toast.LENGTH_LONG).show();

                builder = new AlertDialog.Builder(Plannedroadworks.this);
                builder.setMessage(description.get(i)+pubDate.get(i));
                AlertDialog alert = builder.create();
                alert.setTitle("Details: ");
                alert.show();
            }
        });

        new Plannedroadworks.BackgroundProcess().execute();
    }



    public InputStream getInputStream(URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch(IOException e)
        {
            return null;
        }
    }

    //NEW THREAD
    public class BackgroundProcess extends AsyncTask<Integer, Void, Exception>
    {
        ProgressDialog progressDialog = new ProgressDialog(Plannedroadworks.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading!!!!! :)...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try
            {
                //retrieve data from RSS feed
                //set up URL object
                URL url = new URL("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);

                //extract data from feed
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "ISO_8859_1");

                //set to show when we are inside each item tag
                boolean insideItem = false;

                //returns type of current event (which tag and where)
                int eventType = xpp.getEventType();

                //run through every element in document
                //Loop through while not at the end of the document
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    //start reading at the start tag
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        //specify what happens to each tag and begin with each item
                        if(xpp.getName().equalsIgnoreCase("item"))
                        {
                            //set bool to show we are inside a tag
                            insideItem = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("title"))
                        {
                            //check again that we are inside a tag
                            if(insideItem == true)
                            {
                                //add string object of titles
                                titles.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideItem == true)
                            {

                                description.add(xpp.nextText().replaceAll("<br />", " "));

                                //I realise by not collecting these attributes into a single list
                                //ive made it difficult to attach start dates and end dates with each "item" to calculate the difference
                                //String startDate1=" ";
                                //String endDate1=" ";

                                if(description.contains("Start Date"))
                                {
                                    String[] splitText = xpp.getText().split("<br/>");
                                    StringBuilder stringBuilder = new StringBuilder();

                                    for(String str : splitText)
                                    {
                                        stringBuilder.append(str).append(" ");

                                        if(str.contains("Start Date"))
                                        {
                                            try
                                            {
                                                String startDate = str.split("Start Date: ")[1];
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd, MMM yyyy - HH:mm");
                                                Date dateStart = dateFormat.parse(startDate);
                                                startDateList.add(dateStart);
                                                //listDates.setStartDate(dateStart);
                                                Log.e("Start ", "Date: " + dateStart);
                                            }
                                            catch (Exception e)
                                            {
                                                Log.e("Error: ", e.getMessage());
                                            }

                                        }
                                        if(description.contains("End Date"))
                                        {
                                            try
                                            {
                                                String endDate = str.split("Start Date: ")[1];
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd, MMM yyyy - HH:mm");
                                                Date dateEnd = dateFormat.parse(endDate);
                                                endDateList.add(dateEnd);
                                                Log.e("Start ", "Date: " + endDate);
                                            }
                                            catch (ParseException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }

                        }
                        else if(xpp.getName().equalsIgnoreCase("pubdate"))
                        {
                            if(insideItem==true)
                            {
                                pubDate.add(xpp.nextText());
                            }
                        }

                    }
                    //
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                    }
                    //increment the loop counter
                    eventType = xpp.next();

                }


            }
            catch(MalformedURLException e)
            {
                exception = e;
            }
            catch(XmlPullParserException e)
            {
                exception = e;
            }
            catch(IOException e)
            {
                exception = e;
            }
            //catch (ParseException e) {
            //    e.printStackTrace();
            //}
            //catch (ParseException e) {
            //    e.printStackTrace();
           // }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Plannedroadworks.this, android.R.layout.simple_expandable_list_item_1, titles);

            plannedRoadworksListView.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }
}