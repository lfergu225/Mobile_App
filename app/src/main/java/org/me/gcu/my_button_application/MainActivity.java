package org.me.gcu.my_button_application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.me.gcu.my_button_application.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//This is the work of Laura Ferguson s2130158

public class MainActivity extends AppCompatActivity {

    ListView currentIncidentListView;
    AlertDialog.Builder builder;

    ArrayList<String> titles;
    ArrayList<String> description;
    ArrayList<String> pubDate;
    private ArrayAdapter adapter;

    private Button currentIncidentButton;
    private Button roadworksButton;
    private Button plannedRoadworksButton;
    private Button googleM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //search bar produces an error that the reference is an empty object

        /*EditText searchBar = (EditText)findViewById(R.id.SearchBar);

        adapter = new ArrayAdapter(this, R.layout.activity_main, R.id.currentIncidentListView, description);
        currentIncidentListView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //(MainActivity.this).adapter.getFilter().filter(charSequence);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/


        currentIncidentButton=findViewById(R.id.currentIncidentButton);
        currentIncidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //crashes the app at the moment
        /*
        googleM=findViewById(R.id.googleM);
        googleM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            }
        });*/

        roadworksButton=findViewById(R.id.roadworksButton);
        roadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent);
            }
        });

        plannedRoadworksButton=findViewById(R.id.plannedRoadworksButton);
        plannedRoadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Plannedroadworks.class);
                startActivity(intent);
            }
        });



        currentIncidentListView = (ListView) findViewById(R.id.currentIncidentListView);
        titles = new ArrayList<String>();
        description = new ArrayList<String>();
        pubDate = new ArrayList<String>();



        currentIncidentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Toast.makeText(MainActivity.this, "Details: " + description.get(i), Toast.LENGTH_LONG).show();

                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(description.get(i)+pubDate.get(i));
                AlertDialog alert = builder.create();
                alert.setTitle("Details: ");
                alert.show();

            }
        });

        new BackgroundProcess().execute();
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
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

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
                URL url = new URL("https://trafficscotland.org/rss/feeds/currentincidents.aspx");
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
                                description.add(xpp.nextText());
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

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, titles);


            currentIncidentListView.setAdapter(adapter);


            progressDialog.dismiss();
        }
    }

}