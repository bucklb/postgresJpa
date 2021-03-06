package com.example.postgresdemo.Validator;

import com.example.postgresdemo.exception.ApiError;
import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.mapper.BirthMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.tuo.gen.domain.BirthCaseEnrichment;
import uk.gov.dwp.tuo.gen.domain.OrganisationsToInformResponse;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

//
// Keep a simple example of serialize/deserialize to hand.  Probably better here than in application
//
public class MapperTest {

    ObjectMapper mapper;
    BirthMapper birthMapper;

    String bceStr;
    BirthCaseEnrichment bceObj;
    BirthCaseEnrichment bce;


    @Before
    public void setup() {
        // Create bespoke mapper
        birthMapper = new BirthMapper();

        mapper = new ObjectMapper();
        bceStr = "";
        bceObj = null;
    }


    // Generate some data to test mapping with ...
    private BirthCaseEnrichment birthCaseEnrichment(){
        BirthCaseEnrichment bce = new BirthCaseEnrichment();
        bce.setCouncil("county");
        bce.setStatus( BirthCaseEnrichment.StatusEnum.BLACK );
//        bce.setOrganisationsToInform(new ArrayList<OrganisationsToInformResponse>());
        bce.setOrganisationsToInformResponse(new ArrayList<OrganisationsToInformResponse>());
        OrganisationsToInformResponse otir=new OrganisationsToInformResponse();
        otir.setStatus(OrganisationsToInformResponse.StatusEnum.BLACK);
        otir.setOrganisation("org");
        otir.setResponse(true);

        bce.getOrganisationsToInformResponse().add(otir);
        return bce;
    }

    // Just test for Exception, but could be cleverer
    @Test(expected = MismatchedInputException.class)
    public void testNullStringSerialisationThrows() throws Exception{

        // Leave string null & expect problems with deserialize

        bceObj = mapper.readValue(bceStr, BirthCaseEnrichment.class);
        System.out.println(bceObj.toString());
    }

    // Just test for Exception, but could be cleverer
    @Test(expected = RuntimeException.class)
    public void testNullObjectSerialisationThrows() throws Exception{

        // Null object not so good
        bceStr = mapper.writeValueAsString(bce);
        System.out.println(bceObj.toString());
    }

    // If we just do a new, what happens
    @Test
    public void testEmptyEnrichmentMapping(){

        // Populate with something
        bce = new BirthCaseEnrichment();

        // Experiment with serialisation etc
        ObjectMapper mapper = new ObjectMapper();
        String bceStr = "";
        BirthCaseEnrichment bceObj = null;

        // Serialise a nested object
        try {
            // The string generated by serialisation not terribly close to object's toString
            bceStr = mapper.writeValueAsString(bce);
            System.out.println(bceStr);
        } catch (Exception ex) {
            System.out.println("Serialisation not so good");
            System.out.println(ex.getStackTrace());
        }

        List<ApiError> apiErrors=null;


        // Deserialise a nested object
        if (bceStr != null ) {
            try{
//                bceObj = mapper.readValue(bceStr, BirthCaseEnrichment.class);
                bceObj = birthMapper.getValidatedFromString(bceStr);
                System.out.println(bceObj.toString());
            } catch (ApiValidationException ex) {
                apiErrors = ex.getApiErrors();
                System.out.println("DEserialisation API Errors");
                System.out.println(ex.getStackTrace());
            } catch (Exception e) {
                System.out.println("DEserialisation not so good");
            }
        }

        // Object should fail as council was null
        assertNotNull( apiErrors );
        assertEquals(2,apiErrors.size());
        assert(apiErrors.get(0).getLocalizedErrorMessage().contains("null"));
    }

    // Enrichment with NULL organisations object
    @Test
    public void testNullOrganisationsMapping(){

        // Populate with just council
        bce = new BirthCaseEnrichment();
        bce.setCouncil("district");

        // Experiment with serialisation etc
        ObjectMapper mapper = new ObjectMapper();
        String bceStr = "";
        BirthCaseEnrichment bceObj = null;

        // Serialise a nested object
        try {
            // The string generated by serialisation not terribly close to object's toString
            bceStr = mapper.writeValueAsString(bce);
            System.out.println(bceStr);
        } catch (Exception ex) {
            System.out.println("Serialisation not so good");
            System.out.println(ex.getStackTrace());
        }

        // Deserialise a nested object
        if (bceStr != null ) {
            try{
//                bceObj = mapper.readValue(bceStr, BirthCaseEnrichment.class);
                bceObj = birthMapper.getValidatedFromString(bceStr);
                System.out.println(bceObj.toString());
            } catch (Exception ex) {
                System.out.println("DEserialisation not so good");
                System.out.println(ex.getStackTrace());
            }
        }

        // readValue would allow duff stuff through, validated call wont
//        // The object should come through unscathed.  ??Will date formats be a problem?? When passing internally should be dd-MM-yyyy
//        assert( bceObj.equals( bce ) );
    }

    // Enrichment with NULL organisations object
    @Test
    public void testEmptyOrganisationsMapping(){

        // Populate with just council
        bce = new BirthCaseEnrichment();
        bce.setCouncil("district");
//        bce.setOrganisationsToInform(new ArrayList<OrganisationsToInformResponse>());
        bce.setOrganisationsToInformResponse(new ArrayList<OrganisationsToInformResponse>());

        // Experiment with serialisation etc
        ObjectMapper mapper = new ObjectMapper();
        String bceStr = "";
        BirthCaseEnrichment bceObj = null;

        // Serialise a nested object
        try {
            // The string generated by serialisation not terribly close to object's toString
            bceStr = mapper.writeValueAsString(bce);
            System.out.println(bceStr);
        } catch (Exception ex) {
            System.out.println("Serialisation not so good");
            System.out.println(ex.getStackTrace());
        }

        // Deserialise a nested object
        if (bceStr != null ) {
            try{
//                bceObj = mapper.readValue(bceStr, BirthCaseEnrichment.class);
                bceObj = birthMapper.getValidatedFromString(bceStr);
                System.out.println(bceObj.toString());
            } catch (Exception ex) {
                System.out.println("DEserialisation not so good");
                System.out.println(ex.getStackTrace());
            }
        }

        // Validating with mapping, so should be raising exception and not passing it through
//        // The object should come through unscathed.  ??Will date formats be a problem?? When passing internally should be dd-MM-yyyy
//        assert( bceObj.equals( bce ) );
    }

    // Try a vanilla case
    @Test
    public void testValidMapping(){

        // Populate with something
        bce = birthCaseEnrichment();

        // Experiment with serialisation etc
        ObjectMapper mapper = new ObjectMapper();
        String bceStr = "";
        BirthCaseEnrichment bceObj = null;

        // Serialise a nested object
        try {
            // The string generated by serialisation not terribly close to object's toString
            bceStr = mapper.writeValueAsString(bce);
            System.out.println(bceStr);
        } catch (Exception ex) {
            System.out.println("Serialisation not so good");
            System.out.println(ex.getStackTrace());
        }

        // Deserialise a nested object and now VALIDATE
        if (bceStr != null ) {
            try{
//                bceObj = mapper.readValue(bceStr, BirthCaseEnrichment.class);
                bceObj = birthMapper.getValidatedFromString(bceStr);
                System.out.println(bceObj.toString());
            } catch (ApiValidationException ex) {
                System.out.println("DEserialisation not so good");
                System.out.println(ex.getStackTrace());

            }
        }

        // The object should come through unscathed.  ??Will date formats be a problem?? When passing internally should be dd-MM-yyyy
        assert( bceObj.equals( bce ) );
    }









}
