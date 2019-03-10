package com.example.postgresdemo.mapper;

import com.example.postgresdemo.exception.ApiValidationException;
import uk.gov.dwp.tuo.gen.domain.BirthCaseEnrichment;

// Want something to deal with serialisation & deserialisation (and to ensure validation of the latter)
public class BirthMapper {

    public BirthCaseEnrichment getFromString() throws ApiValidationException {
        return new BirthCaseEnrichment();
    }



//    // Generate an exception for someone to throw.
//    private ApiValidationException apiValidationException( ){
//
//    }




}
