package domain.exception;

/**
 * Exception yang dilempar saat validasi input domain inventaris barang gagal.
 */
public class ValidationException extends DomainException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
