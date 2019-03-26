package com.example.postgresdemo.onReflection;

import com.example.postgresdemo.model.Answer;
import com.example.postgresdemo.model.BirthCaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

public class reflectionTests {


    private static final String Test_Forename="Test";
    private static final String Test_Surname="TestSS";
    private static final String Child_DOB = "20-09-2018";
    private static final String Date_Of_Registration = "19-02-2019";
    private static final String Registration_District = "Cardiff";
    private static final String Reference_num="5c65894fc305d2b5dbe98d01";
    private static final String Sex_Of_Child ="Male";

    @Test
    public void nodAndSmile() { }

    // Want to see about using reflection to trim the data that people get
    @Test
    public void play() {
        Dummy dummy = new Dummy();
        System.out.println(dummy.forename);
    }

    // This illustrates that can get to a class and its field(s).  Need to do this with an instance of the class
    @Test
    public void reflectOne() throws Exception {
        Class<?> c=Class.forName(      "com.example.postgresdemo.model.Answer");
        Field f = c.getDeclaredField("text");
        System.out.println(f.toString() + " : ");

    }

    // works as surname is publiv
    @Test
    public void reflectTwo() throws Exception {
        Dummy dummy = new Dummy();
        Field f = dummy.getClass().getField("surname");
        System.out.println(f.toString() + " : " + f.get(dummy));

    }

    // Only reveals PUBLIC fields, and text isn't
    @Test
    public void reflectThree() throws Exception {
        Answer dummy = new Answer();
        Field f = dummy.getClass().getField("text");
        System.out.println(f.toString() + " : ");

    }

    // Gets the field, but not its value
    @Test
    public void reflectFour() throws Exception {
        Dummy dummy = new Dummy();
        Field f = dummy.getClass().getDeclaredField("forename");
        System.out.println(f.toString() + " : " + f.getName());
        System.out.println(f.toString() + " : " + f.get(dummy));

    }

    // Gets the field, but not its value
    @Test
    public void reflectFive() throws Exception {

        // What does the result look like then??
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper nonNullMapper = new ObjectMapper();


        Dummy dummy = new Dummy();
        Field f = dummy.getClass().getDeclaredField("forename");
        f.setAccessible(true);
        System.out.println(f.toString() + " : " + f.get(dummy));

        // Now try overwriting it
        f.set(dummy,null);
        System.out.println(f.toString() + " : " + f.get(dummy));

        // Look at the serialisation using mapper in default.  Will show forename as null
        System.out.println(mapper.writeValueAsString(dummy));

        // mapper has been used in default mode, so modifying it has bugger all effect
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println(mapper.writeValueAsString(dummy));

        // Turn off outputting null.  Cannot do it after the first use, so do it straight away or create new one & set it
        nonNullMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println(nonNullMapper.writeValueAsString(dummy));

    }

    // Next step might be to look at zapping SUB-objects





    // =========================================== CLASS TO MELD ================================================
    // Want something to play with ...
    private class Dummy {
        private String forename="Test";
        public  String surname="TestSS";
        private  String child_DOB = "20-09-2018";
        private  String date_Of_Registration = "19-02-2019";
        private  String registration_District = "Cardiff";
        private  String reference_num="5c65894fc305d2b5dbe98d01";
        private  String sex_Of_Child ="Male";

        public String getForename() {
            return forename;
        }

        public void setForename(String forename) {
            this.forename = forename;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getChild_DOB() {
            return child_DOB;
        }

        public void setChild_DOB(String child_DOB) {
            this.child_DOB = child_DOB;
        }

        public String getDate_Of_Registration() {
            return date_Of_Registration;
        }

        public void setDate_Of_Registration(String date_Of_Registration) {
            this.date_Of_Registration = date_Of_Registration;
        }

        public String getRegistration_District() {
            return registration_District;
        }

        public void setRegistration_District(String registration_District) {
            this.registration_District = registration_District;
        }

        public String getReference_num() {
            return reference_num;
        }

        public void setReference_num(String reference_num) {
            this.reference_num = reference_num;
        }

        public String getSex_Of_Child() {
            return sex_Of_Child;
        }

        public void setSex_Of_Child(String sex_Of_Child) {
            this.sex_Of_Child = sex_Of_Child;
        }
    }

}
