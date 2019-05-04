package ru.smith.learningspark.ch04;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Main");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);

        JavaRDD<String> lines = sparkContext.parallelize(Arrays.asList("hello world", "hi"));

        PairFunction<String, String, String> keyData = (PairFunction<String, String, String>) s -> new Tuple2<>(s.split(" ")[0], s);
        JavaPairRDD<String, String> pairs = lines.mapToPair(keyData);
        pairs.collect().forEach(System.out::println);

        Function<Tuple2<String, String>, Boolean> longWordFilter = (Function<Tuple2<String, String>, Boolean>) tuple -> tuple._2.length() < 20;
        JavaPairRDD<String, String> result = pairs.filter(longWordFilter);


        JavaRDD<String> input = sparkContext.textFile("test.txt");
        JavaRDD<String> words = input.flatMap((FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" ")).iterator());
        JavaPairRDD<String, Integer> resultTuple =
                words.mapToPair((PairFunction<String, String, Integer>) word -> new Tuple2<>(word, 1))
                        .reduceByKey((Function2<Integer, Integer, Integer>) Integer::sum);
        resultTuple.collect().forEach(System.out::println);

        Map<String, Long> words_ = words.countByValue();
        words_.forEach((k,v) -> System.out.println(k + ": " + v));


        sparkContext.stop();
    }
}
