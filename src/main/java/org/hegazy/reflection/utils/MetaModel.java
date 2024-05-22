package org.hegazy.reflection.utils;

import org.hegazy.reflection.model.Column;
import org.hegazy.reflection.model.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaModel {

    private Class<?>clss;

    public MetaModel(Class<?>clss) {
            this.clss=clss;
        }
     public static MetaModel of(Class<?>clss){

        return new MetaModel(clss);
     }

     public List<ColumnField>getColumnName(){
         Field[]fields=clss.getDeclaredFields();
         List<ColumnField>columnFields=new ArrayList<>();
           for(Field field:fields){
               if(field.getAnnotation(Column.class)!=null)
                     columnFields.add(new ColumnField(field));
           }
            return columnFields;

         }

     public PrimaryKeyField getPrimaryKey(){
        Field[]fields= clss.getDeclaredFields();
        for(Field field:fields){
            Id primaryKey=field.getAnnotation(Id.class);
            if(primaryKey!=null)
                    return new PrimaryKeyField(field);
        }
        throw new  IllegalArgumentException("primary key doesn't exist");
     }
    public String buildInsertRequest() {
        //insert into person(id,name,age)values(?,?,?)

        String names=buildColumnNames();
        String questionMarks=buildQuestionMarkElements();
        return "insert into "+clss.getSimpleName()+" ( "+names+" ) values ( "+questionMarks+") ;";
    }

    public String buildSeletRequest() {
         //select id,name,age from id= ?
        String columns=buildColumnNames();
        return "select "+columns+" from "+this.clss.getSimpleName()+
                "where "+getPrimaryKey().getName()+" ?";
    }

    private String buildColumnNames(){
        String primaryKey=getPrimaryKey().getName();
        List<String>columnNames=getColumnName().stream().map(column->column.getField().getName()).collect(Collectors.toList());
        columnNames.add(0,primaryKey);
        String names=String.join(", ",columnNames);
        return names;
    }

    private String buildQuestionMarkElements(){
       String NQuestionMarks= IntStream.range(0,getColumnName().size()+1).mapToObj(value -> "?").collect(Collectors.joining(", "));
       return NQuestionMarks;
    }
}
