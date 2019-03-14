package com.example.postgresdemo.Validator;


import com.example.postgresdemo.controller.BirthController;
import com.example.postgresdemo.exception.ApiError;
import com.example.postgresdemo.exception.ApiErrorHandler;
import com.example.postgresdemo.exception.ApiValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.dwp.tuo.gen.domain.BirthCase;
import uk.gov.dwp.tuo.gen.domain.BirthCaseEnrichment;
import uk.gov.dwp.tuo.gen.domain.OrganisationsToInformResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//
// Want to make sure exception gets exercised
public class ExceptionTest {

    private static final String ERROR_MESSAGE = "Oops you did it again";
    // For work version will need to inject what we want in to the controller
//    @Mock
//    private ClaimManagementService claimManagementService;
//
//    @InjectMocks
//    private ClaimController claimController;

    private MockMvc mockMvc;

    // Don't have the service layer at home, so just mock the controller?  Might need service/mock & controller/InjectMock
    @Mock
    private BirthController birthController;

    @Mock
    private MessageSource messageSource;

    // Make sure the MVC is setup and has our controller in place
    @Before
    public void setup() {
        // NOTE - in order to get the advice MUST SPECIFY IT with the builder
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.birthController)
                .setControllerAdvice(new ApiErrorHandler(messageSource))
                .build();
    }

    @Test
    public void testApiHandler() throws Exception{

        BirthCaseEnrichment bce = new BirthCaseEnrichment();
        bce.setCouncil("x");
        bce.setStatus(BirthCaseEnrichment.StatusEnum.GREEN);

        OrganisationsToInformResponse o=new OrganisationsToInformResponse();
        o.setOrganisation("!!~!!~££$$||||");
        o.setStatus(OrganisationsToInformResponse.StatusEnum.BLUE);
        ArrayList<OrganisationsToInformResponse> oL=new ArrayList<>();
        oL.add(o);



//        bce.setOrganisationsToInform(oL);
        bce.setOrganisationsToInformResponse(oL);

        String input = new ObjectMapper().writeValueAsString(bce);

        // Hit the endpoint we mocked out & see if we get back the values we set up in the exception
        MvcResult result = mockMvc.perform(post("/birth-cases/69/enrichment")
                .content(input).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        ArrayList<ApiError> apiErrors = getOrderedApiErrorsFromMvcResponse(result.getResponse());;

        System.out.println(apiErrors.size());
        System.out.println(apiErrors.get(0).getField());
        System.out.println(apiErrors.get(0).getLocalizedErrorMessage());
    }




    @Test
    public void testApiException() throws Exception {
        // Just want to provoke the controller to throw an exception.  We ought to be getting a response with expected errors back if advice works
        ApiValidationException avEx=new ApiValidationException(new ArrayList<ApiError>());
        avEx.getApiErrors().add(new ApiError("key","value"));

        // Not sure which is better, if either ...
//        doThrow(avEx).when(birthController).birthCasesBirthCaseIdGet( 69l );
        willThrow(avEx).given(birthController).birthCasesBirthCaseIdGet(69l);

        // Hit the endpoint we mocked out & see if we get back the values we set up in the exception
        MvcResult result = mockMvc.perform(get("/birth-cases/69"))
                .andExpect(status().isBadRequest()).andReturn();
        ArrayList<ApiError> apiErrors = getOrderedApiErrorsFromMvcResponse(result.getResponse());;

        // Expect to get bak the list we put in when creating our exception
        Assert.assertEquals(1, apiErrors.size());
        Assert.assertEquals("key",   apiErrors.get(0).getField());
        Assert.assertEquals("value", apiErrors.get(0).getLocalizedErrorMessage());
    }

    // Put response in form of an ORDERED list of ApiErrors.  Possibly overkill, but code was available
    private ArrayList<ApiError> getOrderedApiErrorsFromMvcResponse(MockHttpServletResponse mvcResponse) throws Exception {
        ArrayList<ApiError> apiErrors = new ArrayList<>();
        String content = mvcResponse.getContentAsString();
        ArrayList<ApiError> errors = new ObjectMapper().readValue(content, ArrayList.class);

        for (Object apiError : errors) {
            apiErrors.add(new ObjectMapper().convertValue(apiError, ApiError.class));
        }

        Collections.sort(apiErrors, new Comparator<ApiError>() {
            public int compare(ApiError s1, ApiError s2) {
                return s1.getField().compareToIgnoreCase(s2.getField());
            }
        });

        return apiErrors;
    }
}
