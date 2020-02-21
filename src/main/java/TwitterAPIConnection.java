
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.text.MessageFormat;
import javax.ws.rs.client.Client;

import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.client.WebTarget;

import javax.ws.rs.core.MediaType;

public class TwitterAPIConnection {

    private static final String BASE_URI = "https://campus.cs.le.ac.uk/tyche/CO7214Rest3/rest/soa/";

    public static void main(String[] args) throws TwitterException, IOException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey("jHik3XZoUZ3gjpbxaYkaZ9Uja") // Consumer key
                .setOAuthConsumerSecret("pq77EPvXX842B6b8Wr383QUnm1T2yTAZ56m4yYMHUt6Cnw9ZEK") // consumer secret
                .setOAuthAccessToken("3150854921-Z8dZAQYpm61UEZlsAhjVoKYznl67DnHhtxkbZGJ") // Token
                .setOAuthAccessTokenSecret("rY7RyOBXaC7CxaU26Po6N6y0oPov03V8Ry9R6QFVAEhTv"); // Token Secret

        TwitterFactory tf = new TwitterFactory(cb.build());// creating Instance of twitter factory
        Twitter twitter = tf.getInstance(); // Instance of twitter

        Client client = ClientBuilder.newClient();

        String passcode = "****";

        WebTarget target = client.target(BASE_URI + MessageFormat.format("getUserName/{0}", new Object[]{passcode}));
        // fetching User name from our Service.

        String username = target.request(MediaType.APPLICATION_JSON).get(String.class); // getting user name
        System.out.println(username);
        TwitterAPIConnection tac = new TwitterAPIConnection(); // new instance to invoke all the methods created

        // First Method....
        int output_value = 0;
        output_value = tac.usersFollowing(username, twitter);// returns Friend list of an input User
        WebTarget target_1 = client.target(BASE_URI + MessageFormat.format("submitNumberFollowed/{0}/{1}/{2}",
                new Object[]{passcode, username, output_value}));// we are passing our results which received from
        // twitter api to our Service(SOA)
        // (in this case friendlist of an input User)
        // Our Service either returns pass or Fail

        System.out.println(target_1.request(MediaType.APPLICATION_JSON).get(String.class));

        // Second Method....
        // username = "@simransingha30";
        int output_value1 = tac.totalTweetsNumber(username, twitter);// returns the total number of Tweets
        System.out.println(output_value1);

        WebTarget target_2 = client.target(BASE_URI + MessageFormat.format("submitNumberOfTweetsReceived/{0}/{1}/{2}",
                new Object[]{passcode, username, Integer.toString(output_value1)}));
        // we are passing our results which received from
        // twitter api to our Service(SOA)
        // (in this case Total Tweets of friends)
        // Our Service either returns Passed or Fail

        System.out.println(target_2.request(MediaType.APPLICATION_JSON).get(String.class));

        // Third Method

        int output_value3 = 0;

        output_value3 = tac.getTotalReTweetsnumber(username, twitter);
        System.out.println(output_value3);
        WebTarget target_3 = client.target(BASE_URI + MessageFormat.format("submitNumberOfRetweets/{0}/{1}/{2}",
                new Object[]{passcode, username, output_value3}));// we are passing our results which received from
        // twitter api to our Service(SOA)
        // (in this case Retweet of a user)
        // Our Service either returns Passed or Fail

        System.out.println(target_3.request(MediaType.APPLICATION_JSON).get(String.class));

        // Fourth Method
        String output_value4 = null;

        output_value4 = tac.mostActiveFriend(username, twitter);
        System.out.println(output_value4);
        WebTarget target_4 = client.target(BASE_URI + MessageFormat.format("submitMostActiveFollowed/{0}/{1}/{2}",
                new Object[]{passcode, username, output_value4}));// we are passing our results which received from
        // twitter api to our Service(SOA)
        // (in this case maximum tweets of a friend)
        // Our Service either returns Passed or Fail

        System.out.println(target_4.request(MediaType.APPLICATION_JSON).get(String.class));
    }

    public int usersFollowing(String userName, Twitter twitter) {// Total friends of the User..

        long cursor = -1;// Initializing variable
        // Declaring Lists
        List<User> friend = new ArrayList<User>();
        PagableResponseList<User> page = null;
        do {
            try {
                page = twitter.getFriendsList(userName, cursor);// returns a collections of users(Friends of the Input
                // user)
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            for (User user : page) {// loop to fetch every user
                friend.add(user);// adding user to a list
            }
        } while ((cursor = page.getNextCursor()) != 0);// with this loop cursor will iterate through every page until
        // it wont find one(mens 0), then we are breaking the loop

        return friend.size();// returing the size of the list.

    }

    public int totalTweetsNumber(String userName, Twitter twitter) throws TwitterException {// Total Tweet count of the
        // friends
        /* Initializing Variables */
        long cursor = -1;
        int countTweet = 0;
        /**/
        IDs allIds = twitter.getFriendsIDs(userName, cursor);// This method returns array of numeric ids of all the
        // friends of the user

        for (long userID : allIds.getIDs()) {// Looping based on ids.

            User user = twitter.showUser(userID);// fetching user based on id.
            countTweet += user.getStatusesCount();// we are fetching total count of Tweets of a friend and with every
            // loop increments, we are adding the Tweet

        }

        return countTweet;// returns the total count

    }

    public int getTotalReTweetsnumber(String userName, Twitter twitter) {// most number of Retweets by a friend in
        // "January 2018"
        // Initializing all variables
        ResponseList<Status> allTwtData = null;
        int val = 1;
        Calendar calender = new GregorianCalendar(2018, 0, 1);
        Date startDate = calender.getTime();
        calender = new GregorianCalendar(2018, 1, 1);
        Date endDate = calender.getTime();
        int count = 0;

        try {
            allTwtData = twitter.getUserTimeline(userName, new Paging(val++, 200)); // From this method we are getting
            // all the recent Tweets of the
            // user,

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        for (Status usrTwt : allTwtData) {// looping to check created at date and status Retweet .

            if (usrTwt.getCreatedAt().after(startDate) && usrTwt.getCreatedAt().before(endDate) && usrTwt.isRetweet())
                // Validation to check 'Created at' date
                // also we are checking if its Retweeted or not .

                count = count + 1; // count if it matches above mentioned condition.
        }
        return count;// return count of Retweet (January 2018 in our case)

    }

    public String mostActiveFriend(String username, Twitter twitter) throws TwitterException { // Friends with highest
        // number of Tweets

        long cursor = -1;// initializing cursor

        IDs allIds = twitter.getFriendsIDs(username, cursor); // This method returns array of numeric ids of all the
        // friends of the user

        int userTweet = 0;// initializing variables
        int maxTweet = 0;
        String maxTweetUser = null;

        for (long userId : allIds.getIDs()) { // Loop allIds to get each friends id one by one.

            User user = twitter.showUser(userId);// fetching user based on id.
            userTweet = user.getStatusesCount(); // we are getting total count of Tweets of the user

            if (userTweet > maxTweet) { // Basically this validation is to find a user(friends of an input user) who has
                // the maximum tweets

                maxTweet = userTweet; // assigning tweet count to maxtweet variable ,it will compare the count of
                // previous user and current user ,who so ever has more tweets that will be
                // stored in this .

                maxTweetUser = user.getScreenName();// fetching the name of the user(user who's the friend of
                // the input user)
            }

        }

        return maxTweetUser;// Return ScreenName of the user with most number of tweets ..

    }
}