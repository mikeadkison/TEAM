package am.te.myapplication.Service;

import android.app.Activity;

import am.te.myapplication.FriendDetails;
import am.te.myapplication.Model.Agent;
import am.te.myapplication.State;

/**
 * Removes a friend from a user.
 *
 * @author Mitchell Manguno, Mike Adkison
 * @version 1.0
 * @since 2015 March 22
 */
public class RemoveFriendTask extends UserTask {
    private static String idOfFriend;
    private static Activity mActivity;

    /**
     * Creates a RemoveFriendTask instance.
     *
     * @param id the id of the initial friend to remove
     * @param activity the initial activity that calls this task
     */
    public RemoveFriendTask(String id, Activity activity) {
        this.idOfFriend = id;
        this.mActivity = activity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!State.local) {
            return removeFriend();
        }

        return false;
    }

    /**
     * Sends a request to a php get handler to remove a user's friend.
     *
     * @return boolean - true if the query does not throw an exception, else returns false.
     **/
    protected boolean removeFriend() {
        String TAG = FriendDetails.class.getSimpleName();

        String link = server_url + "/deletefriend.php?userID="
                + Agent.getUniqueIDofCurrentlyLoggedIn()
                + "&friendID=" + idOfFriend;

        String response = fetchHTTPResponseAsStr(TAG, link);

        if (response.contains("success")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            mActivity.finish();
        }
    }
}