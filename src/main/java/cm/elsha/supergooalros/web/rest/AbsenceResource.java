package cm.elsha.supergooalros.web.rest;

import com.codahale.metrics.annotation.Timed;
import cm.elsha.supergooalros.domain.Absence;
import cm.elsha.supergooalros.repository.AbsenceRepository;
import cm.elsha.supergooalros.repository.search.AbsenceSearchRepository;
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
 * REST controller for managing Absence.
 */
@RestController
@RequestMapping("/api")
public class AbsenceResource {

    private final Logger log = LoggerFactory.getLogger(AbsenceResource.class);
        
    @Inject
    private AbsenceRepository absenceRepository;
    
    @Inject
    private AbsenceSearchRepository absenceSearchRepository;
    
    /**
     * POST  /absences : Create a new absence.
     *
     * @param absence the absence to create
     * @return the ResponseEntity with status 201 (Created) and with body the new absence, or with status 400 (Bad Request) if the absence has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/absences",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Absence> createAbsence(@RequestBody Absence absence) throws URISyntaxException {
        log.debug("REST request to save Absence : {}", absence);
        if (absence.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("absence", "idexists", "A new absence cannot already have an ID")).body(null);
        }
        Absence result = absenceRepository.save(absence);
        absenceSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/absences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("absence", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /absences : Updates an existing absence.
     *
     * @param absence the absence to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated absence,
     * or with status 400 (Bad Request) if the absence is not valid,
     * or with status 500 (Internal Server Error) if the absence couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/absences",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Absence> updateAbsence(@RequestBody Absence absence) throws URISyntaxException {
        log.debug("REST request to update Absence : {}", absence);
        if (absence.getId() == null) {
            return createAbsence(absence);
        }
        Absence result = absenceRepository.save(absence);
        absenceSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("absence", absence.getId().toString()))
            .body(result);
    }

    /**
     * GET  /absences : get all the absences.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of absences in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/absences",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Absence>> getAllAbsences(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Absences");
        Page<Absence> page = absenceRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/absences");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /absences/:id : get the "id" absence.
     *
     * @param id the id of the absence to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the absence, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/absences/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Absence> getAbsence(@PathVariable Long id) {
        log.debug("REST request to get Absence : {}", id);
        Absence absence = absenceRepository.findOne(id);
        return Optional.ofNullable(absence)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /absences/:id : delete the "id" absence.
     *
     * @param id the id of the absence to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/absences/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        log.debug("REST request to delete Absence : {}", id);
        absenceRepository.delete(id);
        absenceSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("absence", id.toString())).build();
    }

    /**
     * SEARCH  /_search/absences?query=:query : search for the absence corresponding
     * to the query.
     *
     * @param query the query of the absence search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/absences",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Absence>> searchAbsences(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Absences for query {}", query);
        Page<Absence> page = absenceSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/absences");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
