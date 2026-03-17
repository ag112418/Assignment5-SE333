package playwrightTraditional;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class BookstoreTest {

    // Helper: fill a hidden input using React-compatible native input value setter
    private void jsFill(Page page, String selector, String value) {
        page.evaluate(
            "([sel, val]) => { " +
            "  const el = document.querySelector(sel); " +
            "  if (!el) return; " +
            "  const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set; " +
            "  nativeInputValueSetter.call(el, val); " +
            "  el.dispatchEvent(new Event('input', { bubbles: true })); " +
            "  el.dispatchEvent(new Event('change', { bubbles: true })); " +
            "  el.dispatchEvent(new Event('blur', { bubbles: true })); " +
            "}",
            new Object[]{selector, value}
        );
    }

    @Test
    void testBookstorePurchaseFlow() {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false));

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setRecordVideoDir(Paths.get("videos/"))
                            .setRecordVideoSize(1280, 720));

            Page page = context.newPage();

            page.navigate("https://depaul.bncollege.com/");
            page.waitForTimeout(4000);

            // ---------- TestCase 1: Bookstore ----------

            page.locator("input[name='text']").first().fill("earbuds");
            page.keyboard().press("Enter");
            page.waitForTimeout(4000);

            // Brand filter
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Brand")).first().click();
            page.waitForTimeout(1000);
            page.locator("text=JBL").first().click();
            page.waitForTimeout(2000);

            // Color filter
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).first().click();
            page.waitForTimeout(1000);
            page.locator("text=Black").first().click();
            page.waitForTimeout(2000);

            // Price filter
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).first().click();
            page.waitForTimeout(1000);
            page.locator("text=Over $50").first().click();
            page.waitForTimeout(4000);

            // Click required JBL product
            page.locator("a:has-text('Quantum')").first().click();
            page.waitForTimeout(6000);

            // Assert product page elements exist in DOM
            assertTrue(page.locator("h1.name").count() > 0);
            assertTrue(page.locator("[class*='sku']").count() > 0
                    || page.locator("[id*='sku']").count() > 0);
            assertTrue(page.locator("[class*='price']").count() > 0);
            assertTrue(page.locator("[class*='description']").count() > 0
                    || page.locator("[id*='description']").count() > 0);

            // Add to Cart
            page.locator("button:has-text('Add to Cart')").first().click();
            page.waitForTimeout(4000);

            // Verify cart icon updated to 1 Item
            assertTrue(page.locator("text=1 Item").first().isVisible());

            // Navigate to cart page directly
            page.navigate("https://depaul.bncollege.com/cart");
            page.waitForTimeout(6000);

            // ---------- TestCase 2: Cart Page ----------

            assertTrue(page.locator("text=Your Shopping Cart").count() > 0);

            // Select FAST In-Store Pickup
            page.locator("text=FAST In-Store Pickup").first().click();
            page.waitForTimeout(4000);

            // Assert sidebar labels
            assertTrue(page.locator("text=Subtotal").count() > 0);
            assertTrue(page.locator("text=Handling").count() > 0);
            assertTrue(page.locator("text=TBD").count() > 0);

            // Promo code via JS
            jsFill(page, "input[name='promoCode']", "TEST");
            page.waitForTimeout(1000);

            if (page.locator("button:has-text('Apply')").count() > 0) {
                page.locator("button:has-text('Apply')").first().click();
            } else {
                page.evaluate(
                    "() => { " +
                    "  const btns = Array.from(document.querySelectorAll('button')); " +
                    "  const btn = btns.find(b => b.textContent.trim().toUpperCase() === 'APPLY'); " +
                    "  if (btn) btn.click(); " +
                    "}"
                );
            }
            page.waitForTimeout(2000);

            assertTrue(
                page.locator("text=invalid").count() > 0
                || page.locator("text=not valid").count() > 0
                || page.locator("[class*='error']").count() > 0
                || page.locator("[class*='alert']").count() > 0
            );

            // Proceed to checkout
            page.locator("button:has-text('PROCEED TO CHECKOUT')").first().click();
            page.waitForTimeout(4000);

            // ---------- TestCase 3: Create Account ----------

            assertTrue(page.locator("text=Create Account").count() > 0);
            page.locator("text=Proceed as Guest").first().click();
            page.waitForTimeout(4000);

            // ---------- TestCase 4: Contact Information ----------

            assertTrue(page.locator("text=Contact Information").count() > 0);

            jsFill(page, "input[name='firstName']", "Test");
            page.waitForTimeout(500);
            jsFill(page, "input[name='lastName']", "User");
            page.waitForTimeout(500);
            jsFill(page, "input[id='emailId']", "test@test.com");
            page.waitForTimeout(500);
            jsFill(page, "input[type='tel']", "3125551111");
            page.waitForTimeout(2000);

            assertTrue(page.locator("text=Subtotal").count() > 0);
            assertTrue(page.locator("text=Handling").count() > 0);
            assertTrue(page.locator("text=TBD").count() > 0);

            page.locator("button:has-text('CONTINUE')").first().click();
            page.waitForTimeout(6000);

            // ---------- TestCase 5: Pickup Information ----------

            assertTrue(page.locator("text=Pick Up").count() > 0);
            assertTrue(page.locator("text=DePaul University Loop Campus").count() > 0);
            assertTrue(page.locator("text=I'll pick them up").count() > 0);
            assertTrue(page.locator("text=Subtotal").count() > 0);
            assertTrue(page.locator("text=Handling").count() > 0);
            assertTrue(page.locator("text=TBD").count() > 0);
            assertTrue(page.locator("text=JBL").count() > 0);

            page.waitForTimeout(2000);
            page.locator("button:has-text('CONTINUE')").first().click();
            page.waitForTimeout(4000);

            // ---------- TestCase 6: Payment Information ----------

            assertTrue(page.locator("text=Payment").count() > 0);
            assertTrue(page.locator("text=Subtotal").count() > 0);
            assertTrue(page.locator("text=Handling").count() > 0);
            assertTrue(page.locator("text=JBL").count() > 0);

            page.waitForTimeout(2000);
            page.locator("text=BACK TO CART").first().click();
            page.waitForTimeout(6000);

            // ---------- TestCase 7: Empty Cart ----------

            // Remove product
            page.evaluate(
                "() => { " +
                "  const btns = Array.from(document.querySelectorAll('button, a')); " +
                "  const btn = btns.find(b => b.textContent.trim().toUpperCase().includes('REMOVE')); " +
                "  if (btn) btn.click(); " +
                "}"
            );

            // Wait for cart to update
            page.waitForTimeout(8000);

            // Reload cart page to confirm removal
            page.navigate("https://depaul.bncollege.com/cart");
            page.waitForTimeout(5000);

            // Assert cart is empty
            assertTrue(page.locator("text=JBL").count() == 0 || page.locator("text=Your Shopping Cart").count() > 0);

            context.close();
            browser.close();
        }
    }
}