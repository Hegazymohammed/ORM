package org.hegazy.reflection.orm;

import java.sql.SQLException;

public abstract class EntityManager <T> {

     public static  <T>  EntityManager<T>   of(Class<T> clss){
       return new EntityManagerImpl();
   }
   public abstract void persist(T t) throws SQLException, IllegalAccessException;
}
