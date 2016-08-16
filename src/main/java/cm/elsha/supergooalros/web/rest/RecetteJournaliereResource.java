package cm.elsha.supergooalros.web.rest;

import com.codahale.metrics.annotation.Timed;
import cm.elsha.supergooalros.domain.RecetteJournaliere;
import cm.elsha.supergooalros.repository.RecetteJournaliereRepository;
import cm.elsha.supergooalros.repository.search.RecetteJournaliereSearchRepository;
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
 * REST controller for managing RecetteJournaliere.
 */
@RestController
@RequestMapping("/api")
public class RecetteJournaliereResource {

    private final Logger log = LoggerFactory.getLogger(RecetteJournaliereResource.class);
        
    @Inject
    private RecetteJournaliereRepository recetteJournaliereRepository;
    
    @Inject
    private RecetteJournaliereSearchRepository recetteJournaliereSearchRepository;
    
    /**
     * POST  /recette-journalieres : Create a new recetteJournaliere.
     *
     * @param recetteJournaliere the recetteJournaliere to create
     * @return the ResponseEntity with status 201 (Created) and with body the new recetteJournaliere, or with status 400 (Bad Request) if the recetteJournaliere has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/recette-journalieres",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RecetteJournaliere> createRecetteJournaliere(@RequestBody RecetteJournaliere recetteJournaliere) throws URISyntaxException {
        log.debug("REST request to save RecetteJournaliere : {}", recetteJournaliere);
        if (recetteJournaliere.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("recetteJournaliere", "idexists", "A new recetteJournaliere cannot already have an ID")).body(null);
        }
        RecetteJournaliere result = recetteJournaliereRepository.save(recetteJournaliere);
        recetteJournaliereSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/recette-journalieres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("recetteJournaliere", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recette-journalieres : Updates an existing recetteJournaliere.
     *
     * @param recetteJournaliere the recetteJournaliere to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated recetteJournaliere,
     * or with status 400 (Bad Request) if the recetteJournaliere is not valid,
     * or with status 500 (Internal Server Error) if the recetteJournaliere couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/recette-journalieres",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RecetteJournaliere> updateRecetteJournaliere(@RequestBody RecetteJournaliere recetteJournaliere) throws URISyntaxException {
        log.debug("REST request to update RecetteJournaliere : {}", recetteJournaliere);
        if (recetteJournaliere.getId() == null) {
            return createRecetteJournaliere(recetteJournaliere);
        }
        RecetteJournaliere result = recetteJournaliereRepository.save(recetteJournaliere);
        recetteJournaliereSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("recetteJournaliere", recetteJournaliere.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recette-journalieres : get all the recetteJournalieres.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of recetteJournalieres in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/recette-journalieres",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RecetteJournaliere>> getAllRecetteJournalieres(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of RecetteJournalieres");
        Page<RecetteJournaliere> page = recetteJournaliereRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recette-journalieres");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /recette-journalieres/:id : get the "id" recetteJournaliere.
     *
     * @param id the id of the recetteJournaliere to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the recetteJournaliere, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/recette-journalieres/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RecetteJournaliere> getRecetteJournaliere(@PathVariable Long id) {
        log.debug("REST request to get RecetteJournaliere : {}", id);
        RecetteJournaliere recetteJournaliere = recetteJournaliereRepository.findOne(id);
        return Optional.ofNullable(recetteJournaliere)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /recette-journalieres/:id : delete the "id" recetteJournaliere.
     *
     * @param id the id of the recetteJournaliere to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/recette-journalieres/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRecetteJournaliere(@PathVariable Long id) {
        log.debug("REST request to delete RecetteJournaliere : {}", id);
        recetteJournaliereRepository.delete(id);
        recetteJournaliereSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("recetteJournaliere", id.toString())).build();
    }

    /**
     * SEARCH  /_search/recette-journalieres?query=:query : search for the recetteJournaliere corresponding
     * to the query.
     *
     * @param query the query of the recetteJournaliere search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/recette-journalieres",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RecetteJournaliere>> searchRecetteJournalieres(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of RecetteJournalieres for query {}", query);
        Page<RecetteJournaliere> page = recetteJournaliereSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/recette-journalieres");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
