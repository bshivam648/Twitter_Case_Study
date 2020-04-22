from pymongo import MongoClient
import pandas as pd

# create a mongo client
client = MongoClient()

# get the database
db=client.WHOTweets

# get the collection
tweets = db.tweets

# read the csv file
df = pd.read_csv("WHOSEARO_tweets.csv")

# convert in dataframe
records_ = df.to_dict(orient = 'records')

# insert data into the database collection
result = db.tweets.insert_many(records_ )