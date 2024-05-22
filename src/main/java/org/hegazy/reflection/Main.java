package org.hegazy.reflection;

import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

     Logger logger=Logger.getLogger(this.getClass().getSimpleName());
    public static void main(String[] args) throws SQLException {

        String questionMarksElement =
                IntStream.range(0,3)
                        .mapToObj(index -> "?")
                        .collect(Collectors.joining(", "));
        System.out.println(questionMarksElement);

    }
}