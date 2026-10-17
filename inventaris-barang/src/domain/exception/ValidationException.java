package domain.exception;

/**
 * Exception yang dilempar saat validasi input domain inventaris barang gagal.
 */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
