package ru.smith.learningspark.ch03;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

import java.util.Arrays;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Main");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);

        JavaRDD<String> inputRDD = sparkContext.textFile(args[0]);
        JavaRDD<String> errorsRDD = inputRDD.filter(x -> x.contains("error"));
        JavaRDD<String> warningsRDD = inputRDD.filter(x -> x.contains("warning"));
        JavaRDD<String> badLinesRDD = errorsRDD.union(warningsRDD);
        System.out.println(badLinesRDD.count());
        badLinesRDD.take(10).forEach(System.out::println);

        JavaRDD<String> errors = inputRDD.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String s) throws Exception {
                return s.contains("error");
            }
        });
        errors.collect().forEach(System.out::println);

        JavaRDD<String> err = sparkContext.textFile(args[0]).filter(new ContainsError("error"));
        err.collect().forEach(System.out::println);

        JavaRDD<Integer> rdd = sparkContext.parallelize(Arrays.asList(1, 2, 3, 4));
        /*JavaRDD<Integer> result = rdd.map(new Function<Integer, Integer>() {
            @Override
            public Integer call(Integer x) throws Exception {
                return x * x;
            }
        });*/
        JavaRDD<Integer> result = rdd.map((Function<Integer, Integer>) x -> x * x);
        System.out.println(StringUtils.join(result.collect(), ","));

        JavaRDD<String> lines = sparkContext.parallelize(Arrays.asList("hello world", "hi"));
        JavaRDD<String> words = lines.flatMap((FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" ")).iterator());
        words.collect().forEach(System.out::println);

        Integer sum = rdd.reduce((x, y) -> x + y);
        System.out.println(sum);

        JavaDoubleRDD resultDouble = rdd.mapToDouble(x -> x * x);
        resultDouble.collect().forEach(System.out::println);

        sparkContext.stop();
    }
}
