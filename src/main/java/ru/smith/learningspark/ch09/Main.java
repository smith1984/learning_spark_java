package ru.smith.learningspark.ch09;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

public class Main {
    public static void main(String[] args) {
//        SparkConf conf = new SparkConf().setAppName("Main");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//
//        SQLContext SQLContext = new SQLContext(sc);
//        //SchemaRDD - Dataset<Row>
//        Dataset<Row> inputSQLCtx = SQLContext.jsonFile("./files/testweet.json");
//        inputSQLCtx.registerTempTable("tweets");
//        Dataset<Row> topTweets = SQLContext.sql("SELECT text, retweetCount FROM tweets ORDER BY retweetCount LIMIT 10");
//        topTweets.show();
//        sc.stop();

        SparkSession sparkSession = SparkSession.builder().appName("Main").getOrCreate();
        Dataset<Row> input = sparkSession.read().json("./files/testweet.json");
        input.show();
        input.createOrReplaceTempView("tweets");
        Dataset<Row> topTweets = sparkSession.sql("SELECT text, retweetCount FROM tweets ORDER BY retweetCount LIMIT 10");
        topTweets.show();
        JavaRDD<String> topTweetsText = topTweets.toJavaRDD().map(row -> row.getString(0));
        topTweetsText.collect().forEach(System.out::println);
        sparkSession.udf().register("strLenJava", (UDF1<String, Integer>) String::length, DataTypes.IntegerType);
        Dataset<Row> tweetLength = sparkSession.sql("SELECT strLenJava(text) from tweets");
        tweetLength.show();
        sparkSession.close();
    }
}
