package com.example.postgresdemo.controller;

import com.example.postgresdemo.model.BirthCaseEntity;
import com.example.postgresdemo.repository.BirthRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.dwp.integration.api.customerverification.controller.BirthCasesApi;
import uk.gov.dwp.integration.api.customerverification.domain.BirthCase;

import java.util.Optional;

@Controller
public class BirthController implements BirthCasesApi {

    @Autowired
    private BirthRepository birthRepository;

    public ResponseEntity<BirthCase> birthCasesBirthCaseIdGet(
            @ApiParam(value = "ID of the Birth Case to return",required=true)
            @PathVariable("birthCaseId") Long birthCaseId) throws Exception {

        // Try creating a BirthCaseEntity and mapping to BirthCase domain object
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        BirthCaseEntity bce = optionalBirthCaseEntity.get();
        System.out.println(bce.getDateOfBirth() + " " + bce.getName());

        // While I work out why dateofbirth is causing issues, do a rather more brutal mapping job
        BirthCase bc = new BirthCase();
        bc.setName(bce.getName());
        bc.setDateOfBirth(bce.getDateOfBirth());

        return new ResponseEntity<BirthCase>(bc,HttpStatus.OK);
    }

}
