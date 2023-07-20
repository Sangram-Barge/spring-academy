package example.cashcard;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

  private final CashCardRepository cashCardRepository;

  public CashCardController(CashCardRepository cashCardRepository) {
    this.cashCardRepository = cashCardRepository;
  }

  @GetMapping("/{cardId}")
  public ResponseEntity<CashCard> findById(@PathVariable Long cardId, Principal principal) {
    return cashCardRepository.findByIdAndOwner(cardId, principal.getName())
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb, Principal principal) {
    CashCard createdCashCard = cashCardRepository.save(new CashCard(null, cashCard.amount(),principal.getName() ));
    URI createdPathUri = ucb.path("cashcards/{id}")
        .buildAndExpand(createdCashCard.id())
        .toUri();
    return ResponseEntity.created(createdPathUri).build();
  }

  @GetMapping
  public ResponseEntity<Iterable<CashCard>> findAll(Pageable pageable, Principal principal) {
    Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
        )
    );
    return ResponseEntity.ok(page.getContent());
  }
}
