package com.campuscart.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "wishlist_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_wishlist_user_listing",
        columnNames = {"user_id", "listing_id"}
    ),
    indexes = @Index(
        name = "idx_wishlist_user_created",
        columnList = "user_id, created_at"
    )
)
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_wishlist_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "listing_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_wishlist_listing")
    )
    private Listing listing;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
