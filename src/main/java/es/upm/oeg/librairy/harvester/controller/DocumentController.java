package es.upm.oeg.librairy.harvester.controller;

import es.upm.oeg.librairy.harvester.builder.DateBuilder;
import es.upm.oeg.librairy.harvester.data.Document;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RestController
@RequestMapping("/documents")
@Api(tags="/documents", description = "index documents from external sources")
public class DocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentController.class);

    @PostConstruct
    public void setup(){
        LOG.info("Initialized Documents Controller");
    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "save structured information", nickname = "postDocuments", response=String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Accepted", response = String.class),
    })
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> create(@RequestBody Document document)  {
        String date = DateBuilder.now();
        try {

            LOG.info("saving: " + document);
            return new ResponseEntity("done!", HttpStatus.ACCEPTED);
        }catch (RuntimeException e){
            LOG.warn("Process error",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error("IO Error", e);
            return new ResponseEntity("error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
