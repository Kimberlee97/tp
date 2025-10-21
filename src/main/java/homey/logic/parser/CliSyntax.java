package homey.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_TRANSACTION = new Prefix("s/");
    public static final Prefix PREFIX_RELATION = new Prefix("r/");
    public static final Prefix PREFIX_CLIENT = new Prefix("client");
    public static final Prefix PREFIX_VENDOR = new Prefix("vendor");
    public static final Prefix PREFIX_MEETING = new Prefix("m/");
    public static final Prefix PREFIX_REMARK = new Prefix("r/");
}
