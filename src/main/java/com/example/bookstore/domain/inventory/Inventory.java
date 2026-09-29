package com.example.bookstore.domain.inventory;

/** 单本图书的库存聚合，三种数量的转移只能通过领域行为完成。 */
public final class Inventory {
    private final long bookId;
    private int availableQuantity;
    private int reservedQuantity;
    private int soldQuantity;
    private long version;

    public Inventory(long bookId, int availableQuantity, int reservedQuantity, int soldQuantity, long version) {
        if (bookId <= 0 || availableQuantity < 0 || reservedQuantity < 0 || soldQuantity < 0 || version < 0) {
            throw new IllegalArgumentException("库存数据不合法");
        }
        this.bookId = bookId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.soldQuantity = soldQuantity;
        this.version = version;
    }

    public void reserve(int quantity) {
        requirePositive(quantity);
        if (availableQuantity < quantity) throw new IllegalStateException("库存不足");
        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void confirmSale(int quantity) {
        requirePositive(quantity);
        if (reservedQuantity < quantity) throw new IllegalStateException("预占库存不足");
        reservedQuantity -= quantity;
        soldQuantity += quantity;
    }

    public void release(int quantity) {
        requirePositive(quantity);
        if (reservedQuantity < quantity) throw new IllegalStateException("预占库存不足");
        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }

    private void requirePositive(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("数量必须大于 0"); }
    public long bookId() { return bookId; }
    public int availableQuantity() { return availableQuantity; }
    public int reservedQuantity() { return reservedQuantity; }
    public int soldQuantity() { return soldQuantity; }
    public long version() { return version; }
}
