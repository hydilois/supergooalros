package cm.elsha.supergooalros.web.rest;

import com.codahale.metrics.annotation.Timed;
import cm.elsha.supergooalros.domain.QuotaHebdo;
import cm.elsha.supergooalros.repository.QuotaHebdoRepository;
import cm.elsha.supergooalros.repository.search.QuotaHebdoSearchRepository;
import cm.elsha.supergooalros.web.rest.util.HeaderUtil;
import cm.elsha.supergooalros.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing QuotaHebdo.
 */
@RestController
@RequestMapping("/api")
public class QuotaHebdoResource {

    private final Logger log = LoggerFactory.getLogger(QuotaHebdoResource.class);
        
    @Inject
    private QuotaHebdoRepository quotaHebdoRepository;
    
    @Inject
    private QuotaHebdoSearchRepository quotaHebdoSearchRepository;
    
    /**
     * POST  /quota-hebdos : Create a new quotaHebdo.
     *
     * @param quotaHebdo the quotaHebdo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new quotaHebdo, or with status 400 (Bad Request) if the quotaHebdo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/quota-hebdos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QuotaHebdo> createQuotaHebdo(@Valid @RequestBody QuotaHebdo quotaHebdo) throws URISyntaxException {
        log.debug("REST request to save QuotaHebdo : {}", quotaHebdo);
        if (quotaHebdo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("quotaHebdo", "idexists", "A new quotaHebdo cannot already have an ID")).body(null);
        }
        QuotaHebdo result = quotaHebdoRepository.save(quotaHebdo);
        quotaHebdoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/quota-hebdos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("quotaHebdo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quota-hebdos : Updates an existing quotaHebdo.
     *
     * @param quotaHebdo the quotaHebdo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated quotaHebdo,
     * or with status 400 (Bad Request) if the quotaHebdo is not valid,
     * or with status 500 (Internal Server Error) if the quotaHebdo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/quota-hebdos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QuotaHebdo> updateQuotaHebdo(@Valid @RequestBody QuotaHebdo quotaHebdo) throws URISyntaxException {
        log.debug("REST request to update QuotaHebdo : {}", quotaHebdo);
        if (quotaHebdo.getId() == null) {
            return createQuotaHebdo(quotaHebdo);
        }
        QuotaHebdo result = quotaHebdoRepository.save(quotaHebdo);
        quotaHebdoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("quotaHebdo", quotaHebdo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quota-hebdos : get all the quotaHebdos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of quotaHebdos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/quota-hebdos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QuotaHebdo>> getAllQuotaHebdos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of QuotaHebdos");
        Page<QuotaHebdo> page = quotaHebdoRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quota-hebdos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /quota-hebdos/:id : get the "id" quotaHebdo.
     *
     * @param id the id of the quotaHebdo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the quotaHebdo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/quota-hebdos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QuotaHebdo> getQuotaHebdo(@PathVariable Long id) {
        log.debug("REST request to get QuotaHebdo : {}", id);
        QuotaHebdo quotaHebdo = quotaHebdoRepository.findOne(id);
        return Optional.ofNullable(quotaHebdo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /quota-hebdos/:id : delete the "id" quotaHebdo.
     *
     * @param id the id of the quotaHebdo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/quota-hebdos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteQuotaHebdo(@PathVariable Long id) {
        log.debug("REST request to delete QuotaHebdo : {}", id);
        quotaHebdoRepository.delete(id);
        quotaHebdoSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("quotaHebdo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/quota-hebdos?query=:query : search for the quotaHebdo corresponding
     * to the query.
     *
     * @param query the query of the quotaHebdo search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/quota-hebdos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QuotaHebdo>> searchQuotaHebdos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of QuotaHebdos for query {}", query);
        Page<QuotaHebdo> page = quotaHebdoSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quota-hebdos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
