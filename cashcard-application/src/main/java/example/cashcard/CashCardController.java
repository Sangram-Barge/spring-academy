package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
}
