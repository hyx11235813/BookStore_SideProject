package yixian.website.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationPatternsTest {

    @Test
    void accountMustBeSixToSixteenLettersOrDigits() {
        assertTrue(ValidationPatterns.isValidAccount("abc123"));
        assertFalse(ValidationPatterns.isValidAccount("abc12"));
        assertFalse(ValidationPatterns.isValidAccount("abc 123"));
        assertFalse(ValidationPatterns.isValidAccount(null));
    }

    @Test
    void emailNeedsLocalPartAndDomain() {
        assertTrue(ValidationPatterns.isValidEmail("user@example.com"));
        assertFalse(ValidationPatterns.isValidEmail("user@example"));
        assertFalse(ValidationPatterns.isValidEmail("user.example.com"));
    }

    @Test
    void passwordNeedsLetterAndDigitWithoutSpaces() {
        assertTrue(ValidationPatterns.isValidPassword("abcd1234"));
        assertFalse(ValidationPatterns.isValidPassword("abcdefgh"));
        assertFalse(ValidationPatterns.isValidPassword("abc 1234"));
        assertFalse(ValidationPatterns.isValidPassword("a1"));
    }

    @Test
    void nameAllowsChineseLettersAndDigits() {
        assertTrue(ValidationPatterns.isValidName("王小明"));
        assertTrue(ValidationPatterns.isValidName("Tom123"));
        assertFalse(ValidationPatterns.isValidName("bad name!"));
        assertFalse(ValidationPatterns.isValidName(""));
    }
}
