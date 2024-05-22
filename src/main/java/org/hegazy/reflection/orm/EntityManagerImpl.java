package org.hegazy.reflection.orm;

import org.hegazy.reflection.utils.ColumnField;
import org.hegazy.reflection.utils.MetaModel;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    }
}
