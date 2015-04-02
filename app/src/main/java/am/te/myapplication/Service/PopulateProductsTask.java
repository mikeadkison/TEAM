package am.te.myapplication.Service;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.te.myapplication.Homepage;
import am.te.myapplication.Model.Agent;
import am.te.myapplication.Model.Listing;
import am.te.myapplication.Util.AlertListingAdapter;

/**
 * Created by Collin on 3/22/15.
 */
public class PopulateProductsTask extends UserTask {

    private List<Listing> products;
    private AlertListingAdapter arrayAdapter;
    private Activity activity;
    private String id;
    private boolean notify;

    public PopulateProductsTask(List<Listing> products, boolean notify,
               AlertListingAdapter arrayAdapter, Activity activity, String id) {
        this.products = products;
        this.notify = notify;
        this.arrayAdapter = arrayAdapter;
        this.activity = activity;
        this.id = id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("Getting listings for " + id);
        // get a list of possible friends from database
        ArrayList<Listing> theListings = new ArrayList<>();
        String TAG = Homepage.class.getSimpleName();
        String link = "http://artineer.com/sandbox" + "/getlistings.php?userID=" + id;
        try {//kek

            String result = fetchHTTPResponseAsStr(TAG, link);

            //now need to populate friends with users from result of database query
            if (result.equals("0 results")) {
                Log.d(TAG, result);
                return false;
            }
            String[] resultLines = result.split("<br>");
            System.out.println(result);
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                Listing newListing = null;
                try {
                    JSONObject lineOfArray = jsonArray.getJSONObject(i);
                    String title = lineOfArray.getString("title");
                    String price = lineOfArray.getString("price");
                    String description = lineOfArray.getString("description");
                    String id = lineOfArray.getString("listingID");
                    newListing = new Listing(title, Double.parseDouble(price), description, id);
                    theListings.add(newListing);

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                if (newListing != null) {
                    try {
                        JSONObject lineOfArray = jsonArray.getJSONObject(i);
                        String seen = lineOfArray.getString("hasSeenDeals");
                        newListing.setHasBeenSeen(Boolean.valueOf(seen));

                        //If it has not been seen
                        if (!(Boolean.valueOf(seen))) {
                            //We must notify
                            notify = true;
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            products.clear();
            products.addAll(theListings);
            System.out.println("the listings size: " + theListings.size());
            Collections.sort(products);

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    arrayAdapter.notifyDataSetChanged();
                }
            });


                return true;
            } catch (Exception e) {
                Log.e(TAG, "EXCEPTION on homepage>>>", e);
                return false;
            }
    }
}