package org.hegazy.reflection;

import org.hegazy.reflection.model.Person;
import org.hegazy.reflection.orm.EntityManager;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {

     Logger logger=Logger.getLogger(this.getClass().getSimpleName());
    public static void main(String[] args) throws SQLException, IllegalAccessException {

        EntityManager<Person>entityManager=EntityManager.of(Person.class);
        Person mohammed=new Person("mohammed",24);
        Person ali =new Person("ali",23 );
        Person helmy=new Person("helmy",32  );
        Person mostafa=new Person("mostafa",35);
        entityManager.persist(mohammed);
        entityManager.persist(ali);
        entityManager.persist(helmy);
        entityManager.persist(mostafa);

    }
}