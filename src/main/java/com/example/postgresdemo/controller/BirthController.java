package com.example.postgresdemo.controller;

import com.example.postgresdemo.model.BirthCaseEntity;
import com.example.postgresdemo.repository.BirthRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.dwp.tuo.gen.controller.BirthCasesApi;
import uk.gov.dwp.tuo.gen.domain.BirthCase;
//import uk.gov.dwp.tuo.gen.domain.BirthCaseEnrichment;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.lang.Exception;

@Controller
public class BirthController implements BirthCasesApi {

    @Autowired
    private BirthRepository birthRepository;

    public ResponseEntity<BirthCase> birthCasesBirthCaseIdGet(
        @ApiParam(value = "ID of the Birth Case to return",required=true)
        @PathVariable("birthCaseId") Long birthCaseId) { //throws java.lang.Exception {

        BirthCase bc = null;

        System.out.println("Enter birthCasesBirthCaseIdGet");

        // Try creating a BirthCaseEntity and mapping to BirthCase domain object
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        if( optionalBirthCaseEntity.isPresent() ) {


            BirthCaseEntity bce = optionalBirthCaseEntity.get();

            System.out.println("birthCasesBirthCaseIdGet got entiy");
            System.out.println(bce.getDateOfBirth() + " " + bce.getName());


            // While I work out why dateofbirth is causing issues, do a rather more brutal mapping job
            bc = new BirthCase();
            bc.setName(bce.getName());
            bc.setDateOfBirth(bce.getDateOfBirth());
        } else {
            // return an empty object (for now)
            bc = new BirthCase();
        }
        System.out.println("birthCasesBirthCaseIdGet returning object");

        return new ResponseEntity<BirthCase>(bc,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BirthCase> birthCasesPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody BirthCase body) {
//    public ResponseEntity<BirthCase> birthCasesPost() {


        BirthCaseEntity bce= new BirthCaseEntity();
        bce.setName(body.getName());
        bce.setDateOfBirth(body.getDateOfBirth());

        BirthCaseEntity savedBce= birthRepository.save(bce);

        BirthCase bc=new BirthCase();
        bc.setName(savedBce.getName());
        bc.setDateOfBirth(savedBce.getDateOfBirth());

        // In time want to return an href, but for now pass back the
        return new ResponseEntity<BirthCase>(bc,HttpStatus.OK);

    }



}
