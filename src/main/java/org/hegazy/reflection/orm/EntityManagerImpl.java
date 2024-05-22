package org.hegazy.reflection.orm;

import org.hegazy.reflection.utils.ColumnField;
import org.hegazy.reflection.utils.MetaModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class EntityManagerImpl<T> extends EntityManager<T> {

    private AtomicLong idGenerator=new AtomicLong(3);
    @Override
    public void persist(T t) throws SQLException, IllegalAccessException {
        MetaModel metaModel=MetaModel.of(t.getClass());
        String sql=metaModel.buildInsertRequest();
        PreparedStatement preparedStatement=prepareStatmentWith(sql)
                .andParameters(t);
        preparedStatement.executeUpdate();
    }

    @Override
    public T find(Class<T> person, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MetaModel model=MetaModel.of(person);
        String sql=model.buildSeletRequest();
        PreparedStatement statement=prepareStatmentWith(sql).andPrimaryKey(primaryKey);
       ResultSet resultSet= statement.executeQuery();
        return getBuildInstanceFrom(person,resultSet);
    }

    private   T getBuildInstanceFrom(Class<?>aclass,ResultSet resultSet) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        MetaModel metaModel=MetaModel.of(aclass );
         T t= (T) aclass.getConstructor().newInstance();
         Field primaryKeyField =metaModel.getPrimaryKey().getField();
         String primaryKeyColumnName=metaModel.getPrimaryKey().getName();
         Class<?>primaryKeyType= primaryKeyField.getType();
        resultSet.next();
        if(primaryKeyType==long.class){
           long primaryKey= resultSet.getInt(primaryKeyColumnName);
           primaryKeyField.setAccessible(true);
           primaryKeyField.set(t,primaryKey);

        }
        for(ColumnField columnField:metaModel.getColumnName()){
            Field field= columnField.getField();
             Class<?>columnType=field.getType();
             String columnName=columnField.getField().getName();
             if(columnType==int.class){
                 int value=resultSet.getInt(columnName);
                 field.set(t,value);
             }else if(columnType==String.class){
                 String value=resultSet.getString(columnName);
                 field.set(t,value);
             }

        }

        return t;
    }


    private PreparedStatementWrapper prepareStatmentWith(String sql) throws SQLException {
        //create statement
        //for sake of simplicty i'll write url and password here and then read it from file
        String url="jdbc:mysql://localhost:3306/reflection";
        String userName="root";
        String password="root";

      Connection connection= DriverManager.getConnection(url,userName,password ) ;
          PreparedStatement preparedStatemen=  connection.prepareStatement(sql);

        //set parameters
        return new PreparedStatementWrapper(preparedStatemen);
    }


    private class PreparedStatementWrapper{
        private PreparedStatement preparedStatement;

        public PreparedStatementWrapper(PreparedStatement preparedStatement) {
            this.preparedStatement = preparedStatement;
        }

        public PreparedStatement andParameters(T t) throws SQLException, IllegalAccessException {
            MetaModel metaModel=MetaModel.of(t.getClass());
            Class<?>primaryKeyType=  metaModel.getPrimaryKey().getType();
            if(primaryKeyType==long.class){
                long id = idGenerator.incrementAndGet();
                preparedStatement.setLong(1, id);
                Field field = metaModel.getPrimaryKey().getField();
                field.setAccessible(true);
                field.set(t, id);
            }
            List<ColumnField> fields=metaModel.getColumnName();
            for(int index=0;index<fields.size();++index ){
                ColumnField columnField=metaModel.getColumnName().get(index);
               Class<?>typeField= columnField.getType();
              Field field= columnField.getField();
              field.setAccessible(true);
              Object value=field.get(t);
              if(typeField==int.class){
                  preparedStatement.setInt(index+2,(int)value);
              }
              else if(typeField==String.class){
                  preparedStatement.setString(index+2,(String) value);
              }
            }
            return preparedStatement;
        }

        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
            if(primaryKey.getClass()== Long.class){
                preparedStatement.setLong(1,(Long)primaryKey);;
            }
            return preparedStatement;
        }
    }
}
