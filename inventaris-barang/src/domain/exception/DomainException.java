package domain.exception;

/**
 * Base checked exception untuk hierarki domain inventaris barang.
 */
public class DomainException extends Exception {
    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
