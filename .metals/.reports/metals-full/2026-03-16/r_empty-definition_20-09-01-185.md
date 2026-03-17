error id: file://<WORKSPACE>/src/test/java/org/example/Amazon/AmazonIntegrationTest.java:_empty_/PriceRule#
file://<WORKSPACE>/src/test/java/org/example/Amazon/AmazonIntegrationTest.java
empty definition using pc, found symbol in pc: _empty_/PriceRule#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 838
uri: file://<WORKSPACE>/src/test/java/org/example/Amazon/AmazonIntegrationTest.java
text:
```scala
package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;
    private Amazon amazon;

    @BeforeEach
    void setup() {

        database = new Database();

        // Reset DB before each test (assignment requirement)
        database.resetDatabase();

        cart = new ShoppingCartAdaptor(database);

        List<PriceRule@@> rules = List.of(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );

        amazon = new Amazon(cart, rules);
    }

    @Test
    @DisplayName("specification-based: regular item total with delivery")
    void shouldCalculateTotalPriceForRegularItems() {

        Item book = new Item(ItemType.OTHER, "Book", 2, 10);

        amazon.addToCart(book);

        double total = amazon.calculate();

        // regular cost = 2 * 10 = 20
        // delivery (1 item) = 5
        assertThat(total).isEqualTo(25.0);
    }

    @Test
    @DisplayName("specification-based: electronics adds extra cost")
    void shouldAddElectronicsExtraCost() {

        Item laptop = new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000);

        amazon.addToCart(laptop);

        double total = amazon.calculate();

        // regular cost = 1000
        // delivery = 5
        // electronics extra = 7.5
        assertThat(total).isEqualTo(1012.5);
    }

    @Test
    @DisplayName("structural-based: delivery cost tier 4 items")
    void shouldApplyCorrectDeliveryTier() {

        Item item1 = new Item(ItemType.OTHER, "A", 1, 10);
        Item item2 = new Item(ItemType.OTHER, "B", 1, 10);
        Item item3 = new Item(ItemType.OTHER, "C", 1, 10);
        Item item4 = new Item(ItemType.OTHER, "D", 1, 10);

        amazon.addToCart(item1);
        amazon.addToCart(item2);
        amazon.addToCart(item3);
        amazon.addToCart(item4);

        double total = amazon.calculate();

        // regular cost = 40
        // delivery = 12.5
        assertThat(total).isEqualTo(52.5);
    }

    @Test
    @DisplayName("structural-based: no items should cost zero")
    void shouldReturnZeroForEmptyCart() {

        double total = amazon.calculate();

        assertThat(total).isEqualTo(0.0);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/PriceRule#