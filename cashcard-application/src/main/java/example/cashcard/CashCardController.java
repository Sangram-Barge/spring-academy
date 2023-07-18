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
  public ResponseEntity<CashCard> findById(@PathVariable Long cardId) {
    return cashCardRepository.findById(cardId)
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb) {
    CashCard createdCashCard = cashCardRepository.save(cashCard);
    URI createdPathUri = ucb.path("cashcards/{id}")
        .buildAndExpand(createdCashCard.id())
        .toUri();
    return ResponseEntity.created(createdPathUri).build();
  }

  @GetMapping
  public ResponseEntity<Iterable<CashCard>> findAll(Pageable pageable) {
    Page<CashCard> page = cashCardRepository.findAll(
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
        )
    );
    return ResponseEntity.ok(page.getContent());
  }
}
