package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
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
		assertThat(amount).isEqualTo(123.45);
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

	@Test
	public void shouldReturnAllCashCardsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext context = JsonPath.parse(response.getBody());
		int length = context.read("$.length()");
		assertThat(length).isEqualTo(3);
		JSONArray ids = context.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);
	}

	@Test
	public void shouldReturnPageOfCashCards() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext context = JsonPath.parse(response.getBody());
		JSONArray page = context.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	public void shouldReturnSortedCashCardsDescending() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext context = JsonPath.parse(response.getBody());
		JSONArray page = context.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
		var amount = context.read("$[0].amount");
		assertThat(amount).isEqualTo(150.00);
	}

	@Test
	public void shouldReturnSortedCashCardsAscending() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,asc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext context = JsonPath.parse(response.getBody());
		JSONArray page = context.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
		var amount = context.read("$[0].amount");
		assertThat(amount).isEqualTo(1.00);
	}

	@Test
	public void shouldReturnSortedCashCardsWithNoParameters() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext context = JsonPath.parse(response.getBody());
		JSONArray page = context.read("$[*]");
		assertThat(page.size()).isEqualTo(3);
		JSONArray amounts = context.read("$..amount");
		assertThat(amounts).containsExactlyInAnyOrder(1.00, 123.45, 150.00);
	}
}
