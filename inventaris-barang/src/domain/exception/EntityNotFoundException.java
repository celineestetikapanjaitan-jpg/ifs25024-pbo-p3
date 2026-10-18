package domain.exception;

/**
 * Exception yang dilempar ketika barang dengan ID tertentu tidak ditemukan.
 */
public class EntityNotFoundException extends DomainException {
    private static final long serialVersionUID = 1L;

    /** ID entitas yang tidak ditemukan. */
    private final int id;

    public EntityNotFoundException(int id) {
        super("Barang dengan ID " + id + " tidak ditemukan.");
        this.id = id;
    }

    /** Mengembalikan ID entitas yang tidak ditemukan. */
    public int getId() {
        return id;
    }
}
