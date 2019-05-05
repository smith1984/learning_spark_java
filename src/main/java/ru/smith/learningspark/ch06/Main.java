package ru.smith.learningspark.ch06;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;


import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Main");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> rdd = sc.textFile(args[0]);

        final Accumulator<Integer> blankLines = sc.accumulator(0);
        JavaRDD<String> callSigns = rdd.flatMap((FlatMapFunction<String, String>) line -> {
            if (line.equals(""))
                blankLines.add(1);
            return Arrays.asList(line.split(" ")).iterator();
        });
        callSigns.saveAsTextFile("output");
        System.out.println("Blank lines: "  + blankLines.value());

        sc.stop();
    }
}
