package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    @Test
    @DisplayName("specification-based: calculate sums all rules")
    void shouldAggregateAllPriceRules() {

        ShoppingCart cart = mock(ShoppingCart.class);

        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);

        when(cart.getItems()).thenReturn(List.of());

        when(rule1.priceToAggregate(any())).thenReturn(10.0);
        when(rule2.priceToAggregate(any())).thenReturn(5.0);

        Amazon amazon = new Amazon(cart, List.of(rule1, rule2));

        double result = amazon.calculate();

        assertThat(result).isEqualTo(15.0);

        verify(rule1).priceToAggregate(any());
        verify(rule2).priceToAggregate(any());
    }

    @Test
    @DisplayName("structural-based: addToCart calls shopping cart")
    void shouldCallShoppingCartAdd() {

        ShoppingCart cart = mock(ShoppingCart.class);

        Amazon amazon = new Amazon(cart, List.of());

        Item item = new Item(null, "Test", 1, 5);

        amazon.addToCart(item);

        verify(cart).add(item);
    }
}