package com.ai.customer.repository

import com.ai.customer.dto.ProductDto
import com.ai.customer.dto.VariantDto
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(private val jdbcClient: JdbcClient) {

    fun findProducts(limit: Int): List<ProductDto> {
        val products = jdbcClient.sql(
            """select id, title, vendor, product_type from products order by created_at desc limit :limit"""
        )
            .param("limit", limit)
            .query { rs, _ ->
                ProductDto(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    vendor = rs.getString("vendor"),
                    productType = rs.getString("product_type")
                )
            }
            .list()

        if (products.isEmpty()) return products

        val productIds = products.map { it.id }
        val variants = jdbcClient.sql(
            """select id, product_id, title, price, available from variants where product_id in (:ids)"""
        )
            .param("ids", productIds)
            .query { rs, _ ->
                VariantDto(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    price = rs.getBigDecimal("price").toDouble(),
                    available = rs.getBoolean("available")
                ) to rs.getLong("product_id")
            }
            .list()

        val variantsByProduct = variants.groupBy({ it.second }, { it.first })

        return products.map { p ->
            p.copy(variants = variantsByProduct[p.id] ?: emptyList())
        }
    }

    fun insertProductWithVariants(
        id: Long,
        title: String,
        vendor: String?,
        productType: String?,
        variants: List<VariantDto>
    ) {
        jdbcClient.sql(
            """
            insert into products(id, title, vendor, product_type)
            values(:id, :title, :vendor, :productType)
            on conflict (id) do update set
                title = excluded.title,
                vendor = excluded.vendor,
                product_type = excluded.product_type
            """
        )
            .param("id", id)
            .param("title", title)
            .param("vendor", vendor)
            .param("productType", productType)
            .update()

        variants.forEach { v ->
            jdbcClient.sql(
                """
                insert into variants(id, product_id, title, price, available)
                values(:id, :productId, :title, :price, :available)
                on conflict (id) do update set
                    title = excluded.title,
                    price = excluded.price,
                    available = excluded.available
                """
            )
                .param("id", v.id)
                .param("productId", id)
                .param("title", v.title)
                .param("price", v.price)
                .param("available", v.available)
                .update()
        }
    }
}