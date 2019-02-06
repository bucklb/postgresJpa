package com.example.postgresdemo.controller;

import com.example.postgresdemo.model.BirthCaseEntity;
import com.example.postgresdemo.repository.BirthRepository;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.dwp.tuo.gen.controller.BirthCasesApi;
import uk.gov.dwp.tuo.gen.domain.BirthCase;

import javax.validation.Valid;
import java.util.Optional;

// TODO : all a bit happy path at the moment
@Controller
public class BirthController implements BirthCasesApi {

    @Autowired
    private BirthRepository birthRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Pass back a requested birth case
     * @param birthCaseId
     * @return
     */
    public ResponseEntity<BirthCase> birthCasesBirthCaseIdGet(
        @ApiParam(value = "ID of the Birth Case to return",required=true)
        @PathVariable("birthCaseId") Long birthCaseId) { //throws java.lang.Exception {

        BirthCase bc = null;

        System.out.println("Enter birthCasesBirthCaseIdGet");

        // Try creating a BirthCaseEntity and mapping to BirthCase domain object
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        if( optionalBirthCaseEntity.isPresent() ) {

            // Just map the entity we received to a domain object
            bc = modelMapper.map( optionalBirthCaseEntity.get(), BirthCase.class );

        } else {

            // return an empty object (for now)
            bc = new BirthCase();
        }
        System.out.println("birthCasesBirthCaseIdGet returning object");

        // Wrap it up in hhtp-goodness & return
        return new ResponseEntity<BirthCase>(bc, HttpStatus.OK);
    }

    /**
     * Create the requested birthcase
     * @param body
     * @return
     */
    @Override
    public ResponseEntity<BirthCase> birthCasesPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody BirthCase body) {

        // Put in fit state to save
        BirthCaseEntity bce = modelMapper.map(body, BirthCaseEntity.class);

        // Save the entity.  YAML says we pass back the domain object (but make more sense to pack back its id, to me)
        BirthCase bc = modelMapper.map( birthRepository.save(bce), BirthCase.class );

        // TODO : aren't we meant to be passing back an href, or even an id would make more sense?!
        return new ResponseEntity<BirthCase>(bc, HttpStatus.OK);
    }



}
