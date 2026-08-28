class Tweet{
    private int tweetId;
    private int timestamp;

    public Tweet(int tweetId, int timestamp){
        this.tweetId = tweetId;
        this.timestamp = timestamp;
    }

    public int getTweetId(){
        return this.tweetId;
    }

    public int getTimestamp(){
        return this.timestamp;
    }
}

class User{
    private int userId;
    private Set<User> following;
    private Set<User> followers;
    private LinkedList<Tweet> tweets;
    private LinkedList<Tweet> newsFeedCache;

    public User(int userId){
        this.userId = userId;
        this.following = new HashSet<>();
        this.followers = new HashSet<>();
        this.tweets = new LinkedList<>();
        this.newsFeedCache = new LinkedList<>();
    }

    public Set<User> getFollowing(){
        return this.following;
    }

    public void addFollowee(User followee){
        this.following.add(followee);
    }

    public void removeFollowee(User followee){
        this.following.remove(followee);
    }

    public Set<User> getFollowers(){
        return this.followers;
    }

    public void addFollower(User follower){
        this.followers.add(follower);
    }

    public void removeFollower(User follower){
        this.followers.remove(follower);
    }

    public LinkedList<Tweet> getTweets(){
        return this.tweets;
    }

    public Tweet postTweet(int tweetId, int timestamp){
        Tweet new_tweet = new Tweet(tweetId, timestamp);

        // Add the new tweet to the front of the user's tweet list (most recent first)
        this.tweets.addFirst(new_tweet);
    
        return new_tweet;
    }

    public LinkedList<Tweet> getNewsFeedCache(){
        return this.newsFeedCache;
    }

    public void addTweetIntoCache(Tweet tweet){
        this.newsFeedCache.addFirst(tweet);
    }

    /**
    * Merges a list of tweets from a followee into the existing cached news feed,
    * maintaining descending order based on timestamp (latest first).
    */
    public void mergeTweetsIntoCache(LinkedList<Tweet> followee_tweets){
        LinkedList<Tweet> merged_list = new LinkedList<>();

        Iterator<Tweet> it1 = this.newsFeedCache.iterator();
        Iterator<Tweet> it2 = followee_tweets.iterator();

        Tweet t1 = it1.hasNext() ? it1.next() : null;
        Tweet t2 = it2.hasNext() ? it2.next() : null;

        while(t1 != null || t2 != null){
            if(t2 == null || (t1 != null && t1.getTimestamp() >= t2.getTimestamp())){
                merged_list.add(t1);
                t1 = it1.hasNext() ? it1.next() : null;
            }
            else if(t1 == null || (t2 != null && t1.getTimestamp() < t2.getTimestamp())){
                merged_list.add(t2);
                t2 = it2.hasNext() ? it2.next() : null;
            }
        }

        this.newsFeedCache = merged_list;
    }

    /*
    * Removes tweets posted by a followee from the cached news feed.
    */
    public void removeTweetsFromCache(LinkedList<Tweet> followee_tweets){
        if(followee_tweets == null){
            return;
        }
        
        Set<Integer> followee_tweets_id = new HashSet<>();

        for(Tweet followee_tweet : followee_tweets){
            followee_tweets_id.add(followee_tweet.getTweetId());
        }

        Iterator<Tweet> it = this.newsFeedCache.iterator();
        while(it.hasNext()){
            Tweet tweet = it.next();
            if(followee_tweets_id.contains(tweet.getTweetId())){
                it.remove();
            }
        }
    }
}

class UserStore{
    Map<Integer, User> user_mapper;

    public UserStore(){
        user_mapper = new HashMap<>();
    }

    public User getUserById(int userId){
        user_mapper.putIfAbsent(userId, new User(userId));
        return user_mapper.get(userId);
    }
}

class Twitter {
    static int curr_timestamp = 0;
    final int NEWS_FEED_LIMIT = 10;
    UserStore user_store;

    public Twitter() {
        user_store = new UserStore();
    }
    
    /*
    * Posts a tweet for a given user and adds it to the news feed cache of the user and all their followers.
    */
    public void postTweet(int userId, int tweetId) {
        User user = user_store.getUserById(userId);
        Tweet new_tweet = user.postTweet(tweetId, curr_timestamp++);

        user.addTweetIntoCache(new_tweet);

        Set<User> followers = user.getFollowers();
        for(User follower : followers){
            follower.addTweetIntoCache(new_tweet);
        }
    }
    
    /**
    * Retrieves the news feed for a given user, consisting of the latest tweets in the user's cache.
    * It limits the number of tweets returned by the constant NEWS_FEED_LIMIT.
    */
    public List<Integer> getNewsFeed(int userId) {
        List<Integer> news_feed = new ArrayList<>();
    
        User user = user_store.getUserById(userId);
        LinkedList<Tweet> newsFeedCache = user.getNewsFeedCache();

        for(Tweet tweet : newsFeedCache){
            news_feed.add(tweet.getTweetId());
            if(news_feed.size() == NEWS_FEED_LIMIT){
                break;
            }
        }

        return news_feed;
    }
    
    public void follow(int followerId, int followeeId) {
        User follower = user_store.getUserById(followerId);
        User followee = user_store.getUserById(followeeId);

        if(follower.getFollowing().contains(followee)){
            return;
        }

        follower.addFollowee(followee);
        followee.addFollower(follower);

        follower.mergeTweetsIntoCache(followee.getTweets());
    }
    
    public void unfollow(int followerId, int followeeId) {
        User follower = user_store.getUserById(followerId);
        User followee = user_store.getUserById(followeeId);

        if(!follower.getFollowing().contains(followee)){
            return;
        }

        follower.removeFollowee(followee);
        followee.removeFollower(follower);

        follower.removeTweetsFromCache(followee.getTweets());
    }
}

/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter obj = new Twitter();
 * obj.postTweet(userId,tweetId);
 * List<Integer> param_2 = obj.getNewsFeed(userId);
 * obj.follow(followerId,followeeId);
 * obj.unfollow(followerId,followeeId);
 */