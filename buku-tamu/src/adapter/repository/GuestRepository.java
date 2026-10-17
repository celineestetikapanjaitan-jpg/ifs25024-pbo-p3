package adapter.repository;

import domain.entity.Guest;
import domain.repository.IGuestRepository;

/**
 * Implementasi repository tamu menggunakan basis generic {@link InMemoryRepository}.
 */
public class GuestRepository extends InMemoryRepository<Guest, Integer> implements IGuestRepository {
    /** Penghitung ID otomatis, bertambah setiap kali data baru disimpan. */
    private int idCounter = 0;

    public GuestRepository() {
        super(Guest::getId);
    }

    @Override
    public Guest save(String name, String purpose) {
        Guest entity = new Guest(nextId(), name, purpose);
        return save(entity);
    }

    /** Menghasilkan ID unik berikutnya. */
    private int nextId() {
        return ++idCounter;
    }
}
