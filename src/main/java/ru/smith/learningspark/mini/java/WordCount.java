package ru.smith.learningspark.mini.java;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;

public class WordCount {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("wordCount");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<String> input = sparkContext.textFile(args[0]);
        JavaRDD<String> words = input.flatMap((FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" ")).iterator());
        JavaPairRDD<String, Integer> counts =
                words.mapToPair((PairFunction<String, String, Integer>) s -> new Tuple2<>(s,1)).reduceByKey((Function2<Integer, Integer, Integer>) Integer::sum);
        counts.saveAsTextFile(args[1]);
    }
}
