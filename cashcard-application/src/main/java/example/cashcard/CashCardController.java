package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
