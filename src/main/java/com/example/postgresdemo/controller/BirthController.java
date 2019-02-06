package com.example.postgresdemo.controller;

import com.example.postgresdemo.model.BirthCaseEntity;
import com.example.postgresdemo.model.EnrichmentEntity;
import com.example.postgresdemo.repository.BirthRepository;
import com.example.postgresdemo.repository.EnrichmentRepository;
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
import uk.gov.dwp.tuo.gen.domain.BirthCaseEnrichment;
import uk.gov.dwp.tuo.gen.domain.BirthCaseStatus;
import uk.gov.dwp.tuo.gen.domain.OrganisationsToInformResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

// TODO : all a bit happy path at the moment
// TODO : add ability to push a notification (which needs an update to YAML first)
// TODO : MUST put a service between the controller and the repos (possibly serverImpls too)
@Controller
public class BirthController implements BirthCasesApi {

    @Autowired
    private BirthRepository birthRepository;

    @Autowired
    private EnrichmentRepository enrichmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    /*
    Still need ability to post enrichment (birthCasesBirthCaseIdSubmitPost) but need that to have a payload
    which needs changes to the yaml.  Shouldn't be hard but it'll wait for now

    The domain model is specced as {council + list of organisations}.  Internally probably an idea to, at least
    initially, store it as a set of council + organisation pairs.  That's the way the enrichmentEntity and
    thereby the enrichment repo are set up in this prototype.

    Will need a layer to split a domain object in to a set of entity objects (and vice versa). Another night
     */








    /**
     * Pass back a requested birth case
     * @param birthCaseId
     * @return
     */
    public ResponseEntity<BirthCase> birthCasesBirthCaseIdGet(
        @ApiParam(value = "ID of the Birth Case to return",required=true)
        @PathVariable("birthCaseId") Long birthCaseId) { //throws java.lang.Exception {

        HttpStatus httpStatus=null;
        BirthCase bc = null;

        System.out.println("Enter birthCasesBirthCaseIdGet");

        // Try creating a BirthCaseEntity and mapping to BirthCase domain object
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        if( optionalBirthCaseEntity.isPresent() ) {

            // Just map the entity we received to a domain object
            bc = modelMapper.map( optionalBirthCaseEntity.get(), BirthCase.class );
            httpStatus = HttpStatus.OK;

        } else {

            // return an empty object (for now)
            bc = new BirthCase();
            httpStatus = HttpStatus.NOT_FOUND;
        }
        System.out.println("birthCasesBirthCaseIdGet returning object");

        // Wrap it up in http-goodness & return
        return new ResponseEntity<BirthCase>(bc, httpStatus);
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

    /**
     * User hits the submit button.  Dump the notifications to screen & return a status
     * @param birthCaseId
     * @return
     */
    @Override
    public ResponseEntity<BirthCaseStatus> birthCasesBirthCaseIdSubmitPost(@ApiParam(value = "ID of the Birth Case to return",required=true) @PathVariable("birthCaseId") Long birthCaseId) {

        HttpStatus httpStatus=null;

        // ?? Is anything passed in, beyond the ID ??
        // Just acknowledge
        BirthCaseStatus status = new BirthCaseStatus();

        // Try creating a BirthCaseEntity and mapping to BirthCase domain object
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        if( optionalBirthCaseEntity.isPresent() ) {

            // Grab what we can
            BirthCase birth = modelMapper.map(optionalBirthCaseEntity.get(), BirthCase.class);
            List<EnrichmentEntity> enrichments = enrichmentRepository.findByBirthId( birthCaseId );

            // Spin through the list and dump to screen.  There IS a more elegant way ...
            System.out.println("=== SUBMISSION ==========================================");
            System.out.println(birth.toString());
//            for(EnrichmentEntity enrichment:enrichments){
//                System.out.println( enrichment.toString() );
//                System.out.println(  enrichment.getId() + " council = " + enrichment.getCouncil() + "    org = " + enrichment.getOrganisation() + "  response = " + enrichment.getResponse());
//            }
            BirthCaseEnrichment bce=enrichmentEntityListAsBirthCaseEnrichment(enrichments);
            System.out.println(bce.toString());


            status.setStatus("Submitted");
            httpStatus = HttpStatus.OK;
        } else {

            status.setStatus("Not found");
            httpStatus = HttpStatus.NOT_FOUND;
        }

        // Pass something back
        return new ResponseEntity<BirthCaseStatus>(status, httpStatus);
    }

    /**
     * Given list of enrichment entities, can we create a birth case enrichment domain object
     * @return
     */
    private BirthCaseEnrichment enrichmentEntityListAsBirthCaseEnrichment(List<EnrichmentEntity> entities) {

        BirthCaseEnrichment bce=new BirthCaseEnrichment();

        // If there's something to do ....
        if(entities.size()>0) {
            bce.setCouncil(entities.get(0).getCouncil());
            for(EnrichmentEntity enrichment:entities){

                OrganisationsToInformResponse r=new OrganisationsToInformResponse();
                r.setOrganisation(enrichment.getOrganisation());
                r.setResponse(enrichment.getResponse()==null || enrichment.getResponse().equals(""));
                bce.addOrganisationsToInformItem(r);

//                System.out.println( enrichment.toString() );
//                System.out.println(  enrichment.getId() + " council = " + enrichment.getCouncil() + "    org = " + enrichment.getOrganisation());
            }
        }

        return bce;
    }






}
