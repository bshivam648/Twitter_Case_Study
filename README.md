# Twitter_Case_Study

Twitter Producer -> Written in Scala to extract the Tweets based on the keywords(“corona”,”COVID19”) and broadcast to Kafka topic.

   Step1. Created a twitter client using twitter api

  Step2. Created a kafka producer 

  Step3. Fetching data from twitter, which is in json format

  Step4. Filter data using data() method of CreateData class

  Step5. Write final data to kafka topic using kafka producer

Twitter Consumer -> Written in Scala to consume the data into the kafka topic. Inserting the Data present in Kafka Topic into MongoDb in JSON format

  Step1. Get data from your twitter Kafka topic

  Step2. Insert it into Mongo/ElasticSearch(as per your choice) after doing some basic transformations.

Country List -> Contains name of all country

It also contains India States, India Cities, USA cites, Spain Cities, Italy Cities.
Queries -> Used Flask framework written in python to construct the API. Pymongo was used to write the queries.

These are following queries

i. Need to find overall number of tweets on coronavirus (keywords can be virus/covid-19/corona etc etc) per country in the last 3 months.

ii. Next, need to find overall number of tweets per country on a daily basis

iii. Also need to find the top 100 words occurring on tweets involving coronavirus. (words should be nouns/verbs and not involving common ones like the, is, are, etc etc).

iv. Find the top 100 words occurring on tweets involving coronavirus on a per country basis.

v. Top 10 preventive / precautionary measures suggested by WHO worldwide /country wise

vi. Total no. of donations received towards COVID 19 country wise, in all the affected countries

vii. Ranking of impacted countries over the last 2 month on a week basis, to see who was standing where at any given week


#Files
TwitterProducerAndConsumer/main/scala - Contains Twitter Producer and Consumer, and other required files.

TwitterProducerAndConsumer/test/scala - Contains ExtractUsefulData Tests and FilterData Tests in Test.scala

apiQueries/twitter - _init_.py for queries, run.py for running the queries init file, tweets_extractor.py for extracting WHO tweets using tweepy, csv_to_mongo.py, WHOSEARO_tweets.csv

apiQueries/test - test.py for testing queries


