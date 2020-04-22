from twitter.main.controllers import main
from twitter.admin.controllers import admin

from flask import Flask, jsonify
from pymongo import MongoClient
from nltk.corpus import stopwords



app = Flask(__name__)
app.config["DEBUG"] = True
app.config["TESTING"] = True

client = MongoClient("mongodb://localhost:27017")
db = client.TwitterTestDB
tasks_collection = db.New_Twitter_Tweets


@app.route('/api/query11', methods=['GET'])
def query11():
    ans = tasks_collection.count()
    return jsonify(total_count=ans)


@app.route('/api/query1', methods=['GET'])
def query1():
    cursor = tasks_collection.aggregate([
        {'$group':{'_id': '$location','count': {'$sum': 1}}},
        {"$sort" : {"count" : -1}}
    ])
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)


@app.route('/api/query12/<countryName>', methods=['GET'])
def query12(countryName):

    country = countryName.replace("_", " ")
    print(countryName)
    cursor = tasks_collection.aggregate([
        {"$match":{"location" : countryName}},
        {'$group':{'_id': '$location','count': {'$sum': 1}}},
        {"$sort" : {"count" : -1}}
    ])
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)


@app.route('/api/query2', methods=['GET'])
def query2():
    cursor = tasks_collection.aggregate([
        {'$group':
             {'_id': {'location': '$location', 'date': '$date'},
              'count': {'$sum': 1} } },
        {"$sort" : { "_id.location" : 1, "_id.date" : 1, "Count" : -1}}
    ])
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)

@app.route('/api/query3', methods=['GET'])
def query3():
    en_stops = list(set(stopwords.words('english')))
    word_stops = ['de', 'la', 'que', 'en', 'el', '&amp;', '-', 'por','del', 'los', 'se', 'e', 'le', 'con', '|', 'es', 'da','una', 'les', 'al', 'em', 'un', 'para', 'las']
    cursor = tasks_collection.aggregate([
        {'$project': {'word': {'$split': ["$tweet", " "]}}},
        {'$unwind': "$word"},
        {'$project': {'word': {'$toLower': "$word"}}},
        {'$match': {
            '$and': [
                {'word': {'$ne': ""}},
                {'word': {'$nin': en_stops}},
                {'word' : {'$nin' : word_stops}}
            ]}},
        {'$group':{'_id': {'word': '$word'},'count': {'$sum': 1}}},
        {'$sort': {'count': -1}},
        {'$limit': 100}
    ])
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)


@app.route('/api/query4', methods=['GET'])
def query4():
    en_stops = list(set(stopwords.words('english')))
    fr_stops = list(set(stopwords.words('french')))
    cursor = tasks_collection.aggregate([
        { "$project" : {"location": "$location" , "word" : { "$split" : ["$tweet", " "] }}},
        { "$unwind" : "$word"},
        {'$project': {'word': {'$toLower': "$word"}, 'location': 1}},
        {'$match': {
            '$and': [
                {'word': {'$ne': ""}},
                {'word': {'$nin': en_stops}},
                {'word': {'$nin': fr_stops}}
            ]}},
        { "$group" : { "_id": {"location": "$location","word":  "$word"} , "total" : { "$sum" : 1 } } },
        { "$sort" : { "total" :-1}}  ,
        { "$group" : { "_id" : "$_id.location" , "Top_Words" : { "$push" : {"word" : "$_id.word", "total" : "$total"}}}},
        { "$project" : {"location" :1 , "top100Words" : { "$slice" : ["$Top_Words" ,100]}}}
        ], allowDiskUse=True)
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)


# db.tweets.createIndex( { tweet: "text" } )
@app.route('/api/query5', methods=['GET'])
def query5():
    """Query to find Top 10 preventive / precautionary measures suggested by WHO worldwide /country wise

    :returns: A json representation of array of documents(ans) which is passed an argument.
    """
    WHO_db = client.WHOTweets
    tweets = WHO_db.tweets
    cursor = tweets.find({"$text": {
        "$search": "\"Here are some simple measures that you can adopt to prevent the spread of #coronavirus:\""}},
        {"tweet": 1, "_id": 0})
    ans = []
    for doc in cursor:
        ans.append(doc)
    #return jsonify(ans)
    cursor1 = tweets.aggregate(
        [{"$match": {"$text": {"$search": "\"protect yourself\" -dengue -Hepatitis -Influenza -syphilis -HIV -leptospirosis -foodborne -DelhiPollution -mosquito -SafeFood"}}},
         {"$project":  {"tweet": 1, "_id": 0}}
         ],allowDiskUse=True)

    for doc in cursor1:
        ans.append(doc)
    return jsonify(ans)

# db.New_Twitter_Tweets.createIndex( { tweet: "text" } )
@app.route('/api/query6', methods=['GET'])
def query6():
    cursor = tasks_collection.aggregate(
        [{"$match": {"$text": {"$search": "donations"}}},
         {"$group": {"_id": "$location", "count": {"$sum": 1}}},
         {'$sort': {'count': -1}}
         ], allowDiskUse=True)
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)

@app.route('/api/query7', methods=['GET'])
def query7():
    cursor = tasks_collection.aggregate([
        { "$match": { "$text": { "$search": "corona" } } },
        { "$group": { "_id": "$location", "count": { "$sum": 1 } } },
        { "$sort" : { "count" : -1 } },{"$project":{"_id":1}}
    ], allowDiskUse=True)
    ans = []
    for doc in cursor:
        ans.append(doc)
    return jsonify(ans)

