package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.dto.ArtisanOrderDTO;
import com.Kalakriti.Kalakriti.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
SELECT new com.Kalakriti.Kalakriti.dto.ArtisanOrderDTO(
o.id,
p.name,
oi.quantity,
oi.price,
u.name,
o.status,
o.createdAt
)
FROM OrderItem oi
JOIN oi.order o
JOIN oi.product p
JOIN o.user u
WHERE p.artisan.id = :artisanId
""")
    Page<ArtisanOrderDTO> findOrdersForArtisan(Long artisanId, Pageable pageable);
}