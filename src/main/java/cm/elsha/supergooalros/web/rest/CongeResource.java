package cm.elsha.supergooalros.web.rest;

import com.codahale.metrics.annotation.Timed;
import cm.elsha.supergooalros.domain.Conge;
import cm.elsha.supergooalros.repository.CongeRepository;
import cm.elsha.supergooalros.repository.search.CongeSearchRepository;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Conge.
 */
@RestController
@RequestMapping("/api")
public class CongeResource {

    private final Logger log = LoggerFactory.getLogger(CongeResource.class);
        
    @Inject
    private CongeRepository congeRepository;
    
    @Inject
    private CongeSearchRepository congeSearchRepository;
    
    /**
     * POST  /conges : Create a new conge.
     *
     * @param conge the conge to create
     * @return the ResponseEntity with status 201 (Created) and with body the new conge, or with status 400 (Bad Request) if the conge has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/conges",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conge> createConge(@RequestBody Conge conge) throws URISyntaxException {
        log.debug("REST request to save Conge : {}", conge);
        if (conge.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("conge", "idexists", "A new conge cannot already have an ID")).body(null);
        }
        Conge result = congeRepository.save(conge);
        congeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/conges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("conge", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /conges : Updates an existing conge.
     *
     * @param conge the conge to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated conge,
     * or with status 400 (Bad Request) if the conge is not valid,
     * or with status 500 (Internal Server Error) if the conge couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/conges",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conge> updateConge(@RequestBody Conge conge) throws URISyntaxException {
        log.debug("REST request to update Conge : {}", conge);
        if (conge.getId() == null) {
            return createConge(conge);
        }
        Conge result = congeRepository.save(conge);
        congeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("conge", conge.getId().toString()))
            .body(result);
    }

    /**
     * GET  /conges : get all the conges.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of conges in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/conges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Conge>> getAllConges(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Conges");
        Page<Conge> page = congeRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/conges");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /conges/:id : get the "id" conge.
     *
     * @param id the id of the conge to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the conge, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/conges/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conge> getConge(@PathVariable Long id) {
        log.debug("REST request to get Conge : {}", id);
        Conge conge = congeRepository.findOne(id);
        return Optional.ofNullable(conge)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /conges/:id : delete the "id" conge.
     *
     * @param id the id of the conge to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/conges/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteConge(@PathVariable Long id) {
        log.debug("REST request to delete Conge : {}", id);
        congeRepository.delete(id);
        congeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("conge", id.toString())).build();
    }

    /**
     * SEARCH  /_search/conges?query=:query : search for the conge corresponding
     * to the query.
     *
     * @param query the query of the conge search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/conges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Conge>> searchConges(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Conges for query {}", query);
        Page<Conge> page = congeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/conges");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
