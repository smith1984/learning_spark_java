package ru.smith.learningspark.ch04;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;

public final class BasicAvg {

    public static class AvgCount implements Serializable {
    public AvgCount(int total, int num) {
        total_ = total;
        num_ = num;
    }
    public int total_;
    public int num_;
    public float avg() {
        return total_ / (float) num_;
    }
}

    public static void main(String[] args) throws Exception {
        String master;
        if (args.length > 0) {
            master = args[0];
        } else {
            master = "local";
        }

        JavaSparkContext sc = new JavaSparkContext(
                master, "basicavg", System.getenv("SPARK_HOME"), System.getenv("JARS"));

        Function<Integer, AvgCount> createAcc = (Function<Integer, AvgCount>) x -> new AvgCount(x,1);

        Function2<AvgCount, Integer, AvgCount> addAndCount = (Function2<AvgCount, Integer, AvgCount>) (a, x) -> {
            a.total_ += x;
            a.num_ += 1;
            return a;
        };
        Function2<AvgCount, AvgCount, AvgCount> combine = (Function2<AvgCount, AvgCount, AvgCount>) (a, b) -> {
            a.total_ += b.total_;
            a.num_ += b.num_;
            return a;
        };
        JavaRDD<String> input = sc.textFile("test.txt");
        JavaRDD<String> words = input.flatMap(x -> Arrays.asList(x.split(" ")).iterator());
        JavaPairRDD<String, Integer> result = words.mapToPair(x -> new Tuple2<>(x, 1)).reduceByKey(Integer::sum);
        JavaPairRDD<String, AvgCount>  avgCounts = result.combineByKey(createAcc, addAndCount, combine);
        avgCounts.collectAsMap().forEach((k,v) -> System.out.println(k + ":" + v.avg()));

        sc.stop();
    }
}


