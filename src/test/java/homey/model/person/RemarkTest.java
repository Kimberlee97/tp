package homey.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RemarkTest {
    @Test
    public void nullRemark_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void equals_sameValue_returnsTrue() {
        Remark remark = new Remark("Hello");
        assertTrue(remark.equals(remark));
    }

    @Test
    public void equals_differentValue_returnsFalse() {
        assertFalse(new Remark("A").equals(new Remark("B")));
    }

    @Test
    public void hashCode_equalRemarks_sameHash() {
        assertEquals(new Remark("Hi").hashCode(), new Remark("Hi").hashCode());
    }
}
