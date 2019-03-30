package com.example.postgresdemo.controller;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.exception.BBException;
import com.example.postgresdemo.exception.JwtValidationException;
import com.example.postgresdemo.model.BirthCaseEntity;
import com.example.postgresdemo.model.EnrichmentEntity;
import com.example.postgresdemo.repository.BirthRepository;
import com.example.postgresdemo.repository.EnrichmentRepository;
import com.example.postgresdemo.service.QueueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.mapstruct.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.tuo.gen.controller.BirthCasesApi;
import uk.gov.dwp.tuo.gen.domain.BirthCase;
import uk.gov.dwp.tuo.gen.domain.BirthCaseEnrichment;
import uk.gov.dwp.tuo.gen.domain.BirthCaseStatus;
import uk.gov.dwp.tuo.gen.domain.OrganisationsToInformResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Look at pulling in queue stuff as dependency

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

    @Autowired
    private QueueService sqs;

    // Want to see if I can get header values from the request(s)
    @Autowired
    private HttpServletRequest httpServletRequest;

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
    @Override
    public ResponseEntity<BirthCase> birthCasesBirthCaseIdGet(
        @ApiParam(value = "ID of the Birth Case to return",required=true)
        @PathVariable("birthCaseId") Long birthCaseId) { //throws java.lang.Exception {

        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println(httpServletRequest.getHeader("random"));
        System.out.println(httpServletRequest.getHeader("other"));
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");


        if(2>12) {
            throw new ApiValidationException("key", "value");
        }



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
    public ResponseEntity<BirthCase> birthCasesPost( @ApiParam(value = "" ,required=true ) @Valid @RequestBody BirthCase body) {





        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println(httpServletRequest.getHeader("random"));
        System.out.println(httpServletRequest.getHeader("other"));
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");



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
    public ResponseEntity<BirthCaseStatus> birthCasesBirthCaseIdSubmitPost(
            @ApiParam(value = "ID of the Birth Case to return",required=true)
            @PathVariable("birthCaseId") Long birthCaseId) {

        HttpStatus httpStatus=null;

        // ?? Is anything passed in, beyond the ID ??
        // Just acknowledge
        BirthCaseStatus status = new BirthCaseStatus();
        BirthCase birth = null;

        // Try creating a BirthCaseEntity and mapping to BirthCase domain object
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        if( optionalBirthCaseEntity.isPresent() ) {

            // Grab what we can
            birth = modelMapper.map(optionalBirthCaseEntity.get(), BirthCase.class);
            List<EnrichmentEntity> enrichments = enrichmentRepository.findByBirthId( birthCaseId );

            // Work what we have in database to the desired response (and dump to console)
            BirthCaseEnrichment bce=enrichmentEntityListAsBirthCaseEnrichment(enrichments);


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
                    bceObj = mapper.readValue(bceStr, BirthCaseEnrichment.class);
                    System.out.println(bceObj.toString());
                } catch (Exception ex) {
                    System.out.println("DEserialisation not so good");
                    System.out.println(ex.getStackTrace());
                }
            }

            System.out.println(bce.equals(bceObj));



            System.out.println("=== SUBMISSION ==========================================");
            System.out.println(birth.toString());
            System.out.println(bce.toString());

//            status.setStatus("Submitted");
            httpStatus = HttpStatus.OK;

        } else {

//            status.setStatus("Not found");
            httpStatus = HttpStatus.NOT_FOUND;
        }

        // Attempt to get something to a queue.  Minimalist for now but entry could have case + organisations + lots
//        sqs.sendMessage( "{CaseId : " + birthCaseId + "}","testing" );

        // Pass something back
        return new ResponseEntity<BirthCaseStatus>(status, httpStatus);
    }


    /**
     * Create an enrichment for the birth.  Yaml has no payload though ...
     * @param birthCaseId
     * @return
     */
    @Override
    public  ResponseEntity<BirthCaseEnrichment> birthCasesBirthCaseIdEnrichmentPost(
            @ApiParam(value = "ID of the Birth Case to return",required=true)
            @PathVariable("birthCaseId") Long birthCaseId,@ApiParam(value = "" ,required=true )
            @Valid @RequestBody BirthCaseEnrichment body) {

        HttpStatus httpStatus=null;
        BirthCaseEnrichment bce=body;

        // Will need the birthCase to add the enrichment
        Optional<BirthCaseEntity> optionalBirthCaseEntity = birthRepository.findById( birthCaseId );
        if( optionalBirthCaseEntity.isPresent() ) {

            // Grab the birth entity (complete with its id) as we need it to be in each EnrichmentEntity
            BirthCaseEntity birthCaseEntity = optionalBirthCaseEntity.get();

            // Convert to the entity form we need
            List<EnrichmentEntity> entities = enrichmentEntities(bce);
            for( EnrichmentEntity e : entities ) {
//                System.out.println(e.toString());
                e.setBirthCaseEntity(birthCaseEntity);
                enrichmentRepository.save(e);
            }

            // Want something to hand back.  For now return the entity list melded back to an enrichment
            bce = enrichmentEntityListAsBirthCaseEnrichment( entities );
            httpStatus = HttpStatus.OK;

            // Retrieve the added enrichment

        } else {
            bce=new BirthCaseEnrichment(); bce.setCouncil("Not found");
            httpStatus = HttpStatus.NOT_FOUND;
        }


        // Pass something back
        return new ResponseEntity<BirthCaseEnrichment>(bce, httpStatus);

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

                boolean b = !(enrichment.getResponse()==null
                        || enrichment.getResponse().equals("")
                        || enrichment.getResponse().equals("FALSE"));

                OrganisationsToInformResponse r=new OrganisationsToInformResponse();
                r.setOrganisation(enrichment.getOrganisation());
                r.setResponse(b);
//                bce.addOrganisationsToInformItem(r);
                bce.addOrganisationsToInformResponseItem(r);

            }
        }

        return bce;
    }

    /**
     * generate a dummy OrganisationsToInformResponse
     * @return
     */
    private OrganisationsToInformResponse createOTIR(String org, boolean b){
        OrganisationsToInformResponse otir=new OrganisationsToInformResponse();
        otir.setOrganisation(org);
        otir.setResponse(b);
        return otir;
    }

    /**
     * generate a new EnrichmentEntity
     * @param cncl
     * @param org
     * @param b
     * @return
     */
    private EnrichmentEntity createEnrichmentEntity(String cncl, String org, boolean b){
        EnrichmentEntity entity = new EnrichmentEntity();
        entity.setCouncil(cncl);
        entity.setOrganisation(org);
        entity.setResponse(b?"TRUE":"FALSE");
        return entity;
    }


    /**
     * list of enrichment entities from a BirthCaseEnrichment
     * @param birthCaseEnrichment
     * @return
     */
    private List<EnrichmentEntity> enrichmentEntities (BirthCaseEnrichment birthCaseEnrichment)   {
        ArrayList<EnrichmentEntity> entities = new ArrayList<>();

//        for(OrganisationsToInformResponse otir : birthCaseEnrichment.getOrganisationsToInform()) {
        for(OrganisationsToInformResponse otir : birthCaseEnrichment.getOrganisationsToInformResponse()) {
            entities.add(
                    createEnrichmentEntity(birthCaseEnrichment.getCouncil(), otir.getOrganisation(), otir.isResponse() ) );
        }

        return entities;
    }

    /**
     * Generate a BirthCaseEnrichment pending allowing one as payload
     * @return
     */
    private BirthCaseEnrichment createDummyBirthCaseEnrichment(){

        // Generate a fake enrichment
        BirthCaseEnrichment bce = new BirthCaseEnrichment();

        bce.setCouncil("municipal");
//        bce.addOrganisationsToInformItem(createOTIR("org1",true));
//        bce.addOrganisationsToInformItem(createOTIR("org2",false));
        bce.addOrganisationsToInformResponseItem(createOTIR("org1",true));
        bce.addOrganisationsToInformResponseItem(createOTIR("org2",false));

        return bce;
    }

}
