package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountResourceTest {

    @Test
    @Order(1)
    void testRetriveAll(){
        Response result = given()
                .when().get("/accounts")
                .then()
                .statusCode(200)
                .body(
                        containsString("George Baird"),
                        containsString("Mary Taylor"),
                        containsString("Diana Rigg")
                )
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(3));
    }

    @Test
    @Order(2)
    void testGetAccount(){
        Account account = given()
                .when().get("/accounts/{accountNumber}", 545454545L)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(account.getAccountNumber(), equalTo(545454545L));
        assertThat(account.getCustomerName(), equalTo("Diana Rigg"));
        assertThat(account.getBalance(), equalTo(new BigDecimal("422.00")));
        assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
    }

    @Test
    @Order(3)
    void testCreateAccount(){
        Account account = new Account(323435L, 112233L, "Sherlock Holmes", new BigDecimal("150.00"));

        Account resultAccount = given()
                .contentType(ContentType.JSON)
                .body(account)
                .when().post("/accounts")
                .then()
                .statusCode(201)
                .extract()
                .as(Account.class);
        assertThat(resultAccount, notNullValue());
        assertThat(resultAccount, equalTo(account));
    }
}
