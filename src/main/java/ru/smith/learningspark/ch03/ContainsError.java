package ru.smith.learningspark.ch03;

import org.apache.spark.api.java.function.Function;

public class ContainsError implements Function<String, Boolean>{
    private String query;

    ContainsError(String query) {
        this.query = query;
    }

    @Override
    public Boolean call(String s){
        return s.contains("error");
    }
}
