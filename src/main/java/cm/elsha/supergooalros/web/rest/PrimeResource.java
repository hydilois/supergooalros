package cm.elsha.supergooalros.web.rest;

import com.codahale.metrics.annotation.Timed;
import cm.elsha.supergooalros.domain.Prime;
import cm.elsha.supergooalros.repository.PrimeRepository;
import cm.elsha.supergooalros.repository.search.PrimeSearchRepository;
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
 * REST controller for managing Prime.
 */
@RestController
@RequestMapping("/api")
public class PrimeResource {

    private final Logger log = LoggerFactory.getLogger(PrimeResource.class);
        
    @Inject
    private PrimeRepository primeRepository;
    
    @Inject
    private PrimeSearchRepository primeSearchRepository;
    
    /**
     * POST  /primes : Create a new prime.
     *
     * @param prime the prime to create
     * @return the ResponseEntity with status 201 (Created) and with body the new prime, or with status 400 (Bad Request) if the prime has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/primes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Prime> createPrime(@Valid @RequestBody Prime prime) throws URISyntaxException {
        log.debug("REST request to save Prime : {}", prime);
        if (prime.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("prime", "idexists", "A new prime cannot already have an ID")).body(null);
        }
        Prime result = primeRepository.save(prime);
        primeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/primes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("prime", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /primes : Updates an existing prime.
     *
     * @param prime the prime to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated prime,
     * or with status 400 (Bad Request) if the prime is not valid,
     * or with status 500 (Internal Server Error) if the prime couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/primes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Prime> updatePrime(@Valid @RequestBody Prime prime) throws URISyntaxException {
        log.debug("REST request to update Prime : {}", prime);
        if (prime.getId() == null) {
            return createPrime(prime);
        }
        Prime result = primeRepository.save(prime);
        primeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("prime", prime.getId().toString()))
            .body(result);
    }

    /**
     * GET  /primes : get all the primes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of primes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/primes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Prime>> getAllPrimes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Primes");
        Page<Prime> page = primeRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/primes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /primes/:id : get the "id" prime.
     *
     * @param id the id of the prime to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the prime, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/primes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Prime> getPrime(@PathVariable Long id) {
        log.debug("REST request to get Prime : {}", id);
        Prime prime = primeRepository.findOne(id);
        return Optional.ofNullable(prime)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /primes/:id : delete the "id" prime.
     *
     * @param id the id of the prime to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/primes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePrime(@PathVariable Long id) {
        log.debug("REST request to delete Prime : {}", id);
        primeRepository.delete(id);
        primeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("prime", id.toString())).build();
    }

    /**
     * SEARCH  /_search/primes?query=:query : search for the prime corresponding
     * to the query.
     *
     * @param query the query of the prime search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/primes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Prime>> searchPrimes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Primes for query {}", query);
        Page<Prime> page = primeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/primes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
