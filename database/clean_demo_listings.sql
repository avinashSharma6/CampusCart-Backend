-- CampusCart demo cleanup.
-- Removes listings and listing-dependent records while preserving users and categories.
-- Back up campuscart_db before running this script.

START TRANSACTION;

DELETE FROM reports;
DELETE FROM wishlist_items;
DELETE FROM listings;

COMMIT;

SELECT
    (SELECT COUNT(*) FROM users) AS users_remaining,
    (SELECT COUNT(*) FROM categories) AS categories_remaining,
    (SELECT COUNT(*) FROM listings) AS listings_remaining,
    (SELECT COUNT(*) FROM wishlist_items) AS wishlist_items_remaining,
    (SELECT COUNT(*) FROM reports) AS reports_remaining;
