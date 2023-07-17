package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldReturnCashCardOnRetrieve() {
		var response = restTemplate.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		var id = documentContext.read("$.id");
		var amount = documentContext.read("$.amount");
		assertThat(id).isNotNull();
		assertThat(id).isEqualTo(99);
		assertThat(amount).isNotNull();
		assertThat(amount).isEqualTo(1000.0);
	}

	@Test
	public void shouldReturnValidCashCard() {
		var response = restTemplate.getForEntity("/cashcards/999", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	public void shouldCreateNewCashCardOnPost() {
		CashCard cashCard = new CashCard(null, 25000.0);
		ResponseEntity<Void> response = restTemplate.postForEntity("/cashcards", cashCard, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI createdResourceUri = response.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(createdResourceUri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		var id = documentContext.read("$.id");
		var amount = documentContext.read("$.amount");
		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(25000.0);
	}
}
