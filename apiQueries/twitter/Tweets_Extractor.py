#!/usr/bin/env python
# encoding: utf-8

import tweepy
import csv

#Twitter API credentials
consumer_key = "Zs86N3iPPgEWx8YkO5vxtksAg"
consumer_secret = "zoc4T9JooWSPg0wwRQL6j3Rnngjjr2HuHANBySK2hnR04Vdj7x"
access_key = "941528903629332480-PxtBWo1oQEuV70wbxRIJz5YtgWQ7NvE"
access_secret = "cavPvmxfpVQK9FBu9ODRJdjGdLbCeLUFsXlowyzzeSVT9"


'''
retrieve recent tweets from given twitter screen_name using twitter api
and write them to a csv file

screen_name : twitter handle of any twitter user
'''
def get_all_tweets(screen_name):
    # Twitter only allows access to a users most recent 3240 tweets with this method

    #  authorize twitter, initialize tweepy
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_key, access_secret)
    api = tweepy.API(auth)

    # initialize a list to hold all the tweepy Tweets
    alltweets = []

    # make initial request for most recent tweets (200 is the maximum allowed count)
    new_tweets = api.user_timeline(screen_name = screen_name,count=200, tweet_mode='extended')

    # save most recent tweets
    alltweets.extend(new_tweets)

    # save the id of the oldest tweet less one
    oldest = alltweets[-1].id - 1

    # keep grabbing tweets until there are no tweets left to grab
    while len(new_tweets) > 0:
        print( "getting tweets before %s" % (oldest))

        #all subsiquent requests use the max_id param to prevent duplicates
        new_tweets = api.user_timeline(screen_name = screen_name,count=200,max_id=oldest, tweet_mode='extended')

        #save most recent tweets
        alltweets.extend(new_tweets)

        #update the id of the oldest tweet less one
        oldest = alltweets[-1].id - 1

        print( "...%s tweets downloaded so far" % (len(alltweets)))


    # transform the tweepy tweets into a 2D array that will populate the csv
    outtweets = []
    for tweet in alltweets:
        full_text = on_status(tweet)
        outtweets.append([tweet.id_str, tweet.created_at, full_text])

    # write the csv
    with open('%s_tweets.csv' % screen_name, 'w') as f:
        writer = csv.writer(f)
        writer.writerow(["_id","date","tweet"])
        writer.writerows(outtweets)

    pass


'''
function to get full text of tweet in the case when tweet is retweeted

status : tweet retrieved using twitter api
'''
def on_status(status):
    full_text = ""
    if hasattr(status, "retweeted_status"):
        try:
            full_text = status.retweeted_status.extended_tweet["full_text"]
        except AttributeError:
            full_text = status.retweeted_status.full_text
    else:
        try:
            full_text = status.extended_tweet["full_text"]
        except AttributeError:
            full_text = status.full_text

    return full_text


if __name__ == '__main__':
    # pass in the username of the account you want to download
    get_all_tweets("WHOSEARO")
