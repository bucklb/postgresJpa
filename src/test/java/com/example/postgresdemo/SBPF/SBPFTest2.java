package com.example.postgresdemo.SBPF;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;

/*
    Toy with the filtering when we can't annotate the class

    Appears to work as wanted, but assumes we can add the annotation to class.  Otherwise may need a MixIt ?
 */
public class SBPFTest2 {

    class AClass
    {
        public String id = "42";
        public String name = "Fred";
        public String[] color = {"black", "blue"};
        public int sal = 56;
        public BClass[] bclass = {new BClass(), new BClass(), new BClass()};
    }

    class BClass
    {

        public String id = "99";
        public String size = "90";
        public String height = "tall";
        public String nulCheck =null;
    }

    @JsonFilter("test")
    class AClassMixIn {}


    // Presumably the lot
    @Test
    public void SbpfTest_raw() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String[] ignorableFieldNames = {  };
        String[] ignorableFieldNames1 = {  };
        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("filterAClass",SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames))
                .addFilter("filterBClass", SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames1));
        ObjectWriter writer = mapper.writer(filters);
        System.out.println(writer.writeValueAsString(new AClass()));
    }


    @JsonFilter("dynamicFilter")
    public class DynamicMixIn {
    }

    @JsonFilter("dynamicFilterToo")
    public class DynamicMixInToo {
    }



    @Test
    public void SbpfTest_MixIn() throws Exception {

        AClass aClass = new AClass();
        String[] propertiesToExclude = {"id","size"};

        // This approach means EVERY object gets treated as having the DynamicMixIn (and dynamicFilter applied to it)
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(Object.class, DynamicMixIn.class);

        FilterProvider filterProvider = new SimpleFilterProvider()
                // Apply the DynamicFilter too the classes linked to it (A & B & anything else, frankly)
                .addFilter("dynamicFilter",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
//                                .filterOutAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_AClassMixIn() throws Exception {

        AClass aClass = new AClass();
        String[] propertiesToExclude = {"id","size"};
        // Only treat AClass as getting the filter
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(AClass.class, DynamicMixIn.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // Apply the DynamicFilter too te one class that's linked to it (A)
                .addFilter("dynamicFilter",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_BClassMixIn() throws Exception {

        AClass aClass = new AClass();
        String[] propertiesToExclude = {"id"};
        // Only treat BClass as getting the filter
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(BClass.class, DynamicMixIn.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // Apply the DynamicFilter too te one class that's linked to it (B)
                .addFilter("dynamicFilter",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_MultiMixIn() throws Exception {

        AClass aClass = new AClass();
        String[] propertiesToExclude = {"id","size"};
        // Treat multiple specific classes
        ObjectMapper mapper = new ObjectMapper()
                .addMixIn(AClass.class, DynamicMixIn.class)
                .addMixIn(BClass.class, DynamicMixIn.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // filter applies to everything linked to DynamicMixin (both A & B)
                .addFilter("dynamicFilter",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }

    @Test
    public void SbpfTest_MultiMultiMixIn() throws Exception {

        AClass aClass = new AClass();
        // Looking to add two MixIns (with a Filter each) so sensible to have an exclude list each
        String[] propertiesToExclude = {"id"};
        String[] propertiesToExcludeToo = {"size","height"};
        // Treat multiple specific classes
        ObjectMapper mapper = new ObjectMapper()
                // Add a MixIn for each class
                .addMixIn(AClass.class, DynamicMixIn.class)
                .addMixIn(BClass.class, DynamicMixInToo.class);
        FilterProvider filterProvider = new SimpleFilterProvider()
                // Add the two filters linked to the two MixIns added
                .addFilter("dynamicFilter",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExclude)
                )
                .addFilter("dynamicFilterToo",
                        SimpleBeanPropertyFilter
                                .serializeAllExcept(propertiesToExcludeToo)
                );
        mapper.setFilterProvider(filterProvider);

        System.out.println(mapper.writeValueAsString(aClass)); // {"name":"abc"}

    }





}
