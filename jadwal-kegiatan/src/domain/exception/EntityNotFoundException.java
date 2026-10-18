package domain.exception;

/**
 * Exception yang dilempar ketika entitas dengan ID tertentu tidak ditemukan.
 */
public class EntityNotFoundException extends DomainException {
    private static final long serialVersionUID = 1L;

    /** ID entitas yang tidak ditemukan. */
    private final int id;

    public EntityNotFoundException(int id) {
        super("Kegiatan dengan ID " + id + " tidak ditemukan.");
        this.id = id;
    }

    /** Mengembalikan ID entitas yang tidak ditemukan. */
    public int getId() {
        return id;
    }
}
