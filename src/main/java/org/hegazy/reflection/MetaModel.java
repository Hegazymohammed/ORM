package org.hegazy.reflection;

import org.hegazy.reflection.model.PrimaryKey;
import org.hegazy.reflection.utils.ColumnField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
               if(field.getAnnotation(PrimaryKey.class)!=null)
                     columnFields.add(new ColumnField(field));
           }
            return columnFields;

         }

}
