package homey.model.tag;

import static homey.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TransactionStageTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TransactionStage(null));
    }

    @Test
    public void constructor_invalidStageName_throwsIllegalArgumentException() {
        String invalidStageName = "prospect closed negotiating";
        assertThrows(IllegalArgumentException.class, () -> new TransactionStage(invalidStageName));
    }

    @Test
    public void isValidStageName() {
        // null stage name throws NullPointerException
        assertThrows(NullPointerException.class, () -> TransactionStage.isValid(null));

        // invalid stage names
        assertTrue(!TransactionStage.isValid(""));
        assertTrue(!TransactionStage.isValid("potential"));
        assertTrue(!TransactionStage.isValid("prospect closed negotiating"));

        /* valid stage names */
        for (int i = 0; i < TransactionStage.VALID_STAGES.length; i++) {
            assertTrue(TransactionStage.isValid(TransactionStage.VALID_STAGES[i]));
        }
    }
}
