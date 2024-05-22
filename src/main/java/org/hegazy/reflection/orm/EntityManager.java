package org.hegazy.reflection.orm;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public abstract class EntityManager <T> {

     public static  <T>  EntityManager<T>   of(Class<T> clss){
       return new EntityManagerImpl();
   }
   public abstract void persist(T t) throws SQLException, IllegalAccessException;

    public abstract  T find(Class<T> person,Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

}
