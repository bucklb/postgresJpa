package com.example.postgresdemo.SBPF;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.bouncycastle.operator.bc.BcAESSymmetricKeyUnwrapper;
import org.junit.Test;
import org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter;

import static org.junit.Assert.assertEquals;

/*
    Toy with the filtering when we can't annotate the class

    Appears to work as wanted, but assumes we can add the annotation to class.  Otherwise may need a MixIt ?

    LOOKS like might be slightly problematic if dealing with properties that are NOT strings
    - basically if a method is not able to be null then we're in trouble when deserializing
      but OK when it comes to blocking from the string which in many ways is the key after all

    COULD look at a complex multi mixIn approach to do several filters in a single hit (by different Class)
    BUT simpler (for now) to work with a single filter and maybe repeat a number of times
    SO filter out confidentials/system data in all cases and then work out what else to include/exclude thereafter ??

 */
public class SBPFTest2 {

    // May want to use this if wanting multiple mixIns
    class MyMixIn
    {
        Class c;
        String name;
        String[] props;
    }



    // The MixIn stuff (which presumably relies on reflecting a class in to a wrapper ???
    @JsonFilter("AClassMixIn")
    class AClassMixIn {}
    @JsonFilter("DynamicMixIn")
    public class DynamicMixIn {    }
    @JsonFilter("DynamicMixInToo")
    public class DynamicMixInToo {    }

    private void passClass(Class c) {
        System.out.println(c.getName());
    }

    private AClass getTestAClass() {
        BClass[] bclass = {
                new BClass("ego","big","small"),
                new BClass("super-ego","little","tall")};
        String[] color = {"black", "blue"};

        AClass aClass = new AClass("69","naughty", color,69, bclass);

        return aClass;
    }


    @Test
    public void classy(){

        System.out.println(AClassMixIn.class.getSimpleName());

    }





    /*
        Pass in the class so can illustrate the effect
        NOTE : tests for de-serialisation REALLY need noNull to be true
     */
    private String illustrateMixInClass(Object obj, String[] props ,Class c, Class m, boolean inc, boolean noNull) throws Exception {

//        String[] props = {"id","size","color","nonesuch"};

        // Need a filterProvider and a mapper
        ObjectMapper mapper = new ObjectMapper();

        if (noNull) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        } else {
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        }

        // Add a filter and a MixIn, but only if we get given one
        if ( c != null && m!=null ) {
            mapper.addMixIn(c, m);

            // Add a provider
            FilterProvider filterProvider = new SimpleFilterProvider();
            ((SimpleFilterProvider) filterProvider).addFilter(m.getSimpleName(),
                    inc ? SimpleBeanPropertyFilter.filterOutAllExcept(props)
                        : SimpleBeanPropertyFilter.serializeAllExcept(props));

            mapper.setFilterProvider(filterProvider);
        }

        // Do the mapping
        String jSon=mapper.writeValueAsString( obj );
        System.out.println(jSon);
        return jSon;
    }

    // Presumably the lot if we don't specify anything much to filter
    @Test
    public void SbpfTest_raw() throws Exception {

        String[] props = {};

        // Pass null(s) to the illustration :
        String jSon=null;
        AClass a = null;
        AClass aClass=getTestAClass();
        ObjectMapper om = new ObjectMapper();
        ObjectMapper noNullOM = new ObjectMapper();

        // Must set the mapper to match the one used in the routine
        noNullOM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jSon = illustrateMixInClass(aClass,props,null, DynamicMixIn.class, false, true );
        a = noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon, noNullOM.writeValueAsString(a));

        jSon = illustrateMixInClass(aClass,props,null, DynamicMixIn.class, false, false );
        a = om.readValue(jSon, AClass.class);
        assertEquals(jSon, om.writeValueAsString(a));

    }


    // Look at the effect of applying a mixIn/filter to different classes and using different versions
    @Test
    public void SbpfTest_MixIn_1() throws Exception {

//        String[] in_props = {"sal","height","Nonesuch"};
        String[] props = {"id","size","color","noeSuch"};
//        String[] props = {};


        String jSon=null;
        ObjectMapper noNullOM = new ObjectMapper();
        noNullOM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectMapper allowNullOM = new ObjectMapper();
        AClass a=null;
        AClass aClass=getTestAClass();

        // Apply "dynamicFilter" to all.  Id is lost from AClass & BClass (A also loses color & B also loses size)
        jSon = illustrateMixInClass(aClass,props,Object.class, DynamicMixIn.class, false, true );
        a=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(a));

        // Apply "dynamicFilter" to AClass only.  Id is lost ONLY from AClass.  B keeps id & size.
        // Can't do deserialisation test as not setting noNull
        jSon = illustrateMixInClass(aClass,props,AClass.class, DynamicMixInToo.class, false, false );

        // Apply to B only.  Id is lost ONLY from BClass.  A keeps id & color
        jSon = illustrateMixInClass(aClass,props,BClass.class, DynamicMixInToo.class, false, true );
        a=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(a));


        System.out.println("--------------------------------------------------------------------------------");

        // Apply "dynamicFilter" to AClass only.  Id is lost.  Use noNull = true this time
        jSon = illustrateMixInClass(aClass,props,AClass.class, DynamicMixIn.class, false, true );   // Finally.  Can we get an object back again

        // Null to Null
        a=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(a));

        System.out.println( jSon );

        // Apply "dynamicFilter" to AClass only.  Id is lost.  Use noNull = true this time
        jSon = illustrateMixInClass(aClass,props,null, DynamicMixIn.class, true, true );   // Finally.  Can we get an object back again
    }

    // Should be possible to chain stuff together?
    @Test
    public void  SbfTest_Combo() throws Exception {

        String jSon=null;
        String[] ex_props = {"id","color","noneSuch"};              // clear 'confidential' (or non-useful) stuff out
        String[] in_props= {"sal","bclass","height","Nonesuch"};    // if we don't specify that subclass is included we lose it
        ObjectMapper noNullOM = new ObjectMapper();
        noNullOM.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        AClass a=null;
        AClass aClass=getTestAClass();

        // Filter out the "confidential" bits.  Could assert that id not in jSon
        jSon = illustrateMixInClass(aClass, ex_props, Object.class, DynamicMixIn.class, false, true );
        aClass=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(aClass));

        // Pick out fom the available parts the specifics that need to be available.  Assert that "size" & "name" is now missing??
        jSon = illustrateMixInClass(aClass, in_props, Object.class, DynamicMixIn.class, true, true );
        aClass=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(aClass));
    }

    // Should be possible to chain stuff together?
    @Test
    public void  SbfTest_FullCombo() throws Exception {

        String jSon=null;
        String[] ex_props = {"id","color","noneSuch"};              // clear 'confidential' (or non-useful) stuff out
        String[] in_propsA= {"sal","bclass","height","Nonesuch"};    // if we don't specify that subclass is included we lose it
        String[] in_propsB= {"height","Nonesuch"};    // if we don't specify that subclass is included we lose it
        ObjectMapper noNullOM = new ObjectMapper();
        noNullOM.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        AClass a=null;
        AClass aClass=getTestAClass();

        // Filter out the "confidential" bits.  Applies to ClassA and to ClassB
        jSon = illustrateMixInClass(aClass, ex_props, Object.class, DynamicMixIn.class, false, true );
        aClass=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(aClass));

        // Pick out what has been requested from ClassA itself, leaving classB alone
        jSon = illustrateMixInClass(aClass, in_propsA, AClass.class, DynamicMixInToo.class, true, true );
        aClass=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(aClass));

        // Pick out what has been requested from ClassB(s), leaving classA alone
        jSon = illustrateMixInClass(aClass, in_propsB, BClass.class, DynamicMixIn.class, true, true );
        aClass=noNullOM.readValue(jSon, AClass.class);
        assertEquals(jSon,noNullOM.writeValueAsString(aClass));


    }





    @Test
    public void SbpfTest_MixIn() throws Exception {

//        AClass aClass = new AClass();
        AClass aClass = getTestAClass();
        String[] propertiesToExclude = {"id","size"};

        // This approach means EVERY object gets treated as having the DynamicMixIn (and dynamicFilter applied to it)
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(Object.class, DynamicMixIn.class);

        FilterProvider filterProvider = new SimpleFilterProvider()
                // Apply the DynamicFilter too the classes linked to it (A & B & anything else, frankly)
                .addFilter("DynamicMixIn",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
//                                .filterOutAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_AClassMixIn() throws Exception {

//        AClass aClass = new AClass();
        AClass aClass = getTestAClass();
        String[] propertiesToExclude = {"id","size"};
        // Only treat AClass as getting the filter
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(AClass.class, DynamicMixIn.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // Apply the DynamicFilter too te one class that's linked to it (A)
                .addFilter("DynamicMixIn",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_BClassMixIn() throws Exception {

//        AClass aClass = new AClass();
        AClass aClass = getTestAClass();
        String[] propertiesToExclude = {"id"};
        // Only treat BClass as getting the filter
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(BClass.class, DynamicMixIn.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // Apply the DynamicFilter too te one class that's linked to it (B)
                .addFilter("DynamicMixIn",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_MultiMixIn() throws Exception {

//        AClass aClass = new AClass();
        AClass aClass = getTestAClass();
        String[] propertiesToExclude = {"id","size"};
        // Treat multiple specific classes
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(AClass.class, DynamicMixIn.class)
                .addMixIn(BClass.class, DynamicMixIn.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // filter applies to everything linked to DynamicMixin (both A & B)
                .addFilter("DynamicMixIn",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_MultiMultiMixIn() throws Exception {

//        AClass aClass = new AClass();
        AClass aClass = getTestAClass();
        // Looking to add two MixIns (with a Filter each) so sensible to have an exclude list each
        String[] propertiesToExclude = {"id"};
        String[] propertiesToInclude = {"id","name"};
        String[] propertiesToExcludeToo = {"size","height"};
        // Treat multiple specific classes
        ObjectMapper mapper = new ObjectMapper()
                // Add a MixIn for each class
                .addMixIn(AClass.class, DynamicMixIn.class)
                .addMixIn(BClass.class, DynamicMixInToo.class);

        // Exclude nulls?? Specifically the nulCheck value
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


        // Can't mix and atch properties to include/exclude
        FilterProvider filterProvider = new SimpleFilterProvider()
                // Add the two filters linked to the two MixIns added
                .addFilter("DynamicMixIn",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                                .filterOutAllExcept(propertiesToInclude)
                )
                .addFilter("DynamicMixInToo",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExcludeToo)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }





}
