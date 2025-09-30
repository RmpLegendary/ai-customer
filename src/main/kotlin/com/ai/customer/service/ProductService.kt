package com.ai.customer.service

import com.ai.customer.dto.ProductDto
import com.ai.customer.dto.VariantDto
import com.ai.customer.repository.ProductRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.atomic.AtomicLong

@Service
class ProductService(private val repository: ProductRepository) {
    private val restTemplate = RestTemplate()
    private val mapper = jacksonObjectMapper()
    private val localIdGenerator = AtomicLong(1000000) // для новых продуктов/вариантов через форму

    fun getProducts(limit: Int): List<ProductDto> = repository.findProducts(limit)

    fun addProductWithVariant(
        title: String, vendor: String?, productType: String?,
        variantTitle: String, variantPrice: Double, available: Boolean
    ): ProductDto {
        val productId = localIdGenerator.incrementAndGet()
        val variantId = localIdGenerator.incrementAndGet()
        val variant = VariantDto(variantId, variantTitle, variantPrice, available)

        repository.insertProductWithVariants(productId, title, vendor, productType, listOf(variant))
        return repository.findProducts(1).first()
    }

    fun syncProducts() {
        val json = restTemplate.getForObject("https://famme.no/products.json", String::class.java)
            ?: return
        val root = mapper.readTree(json).path("products")

        root.take(50).forEach { product ->
            val productId = product["id"].asLong()
            val title = product["title"].asText()
            val vendor = product.get("vendor")?.asText()
            val productType = product.get("product_type")?.asText()

            val variants = product["variants"].map { v ->
                VariantDto(
                    id = v["id"].asLong(),
                    title = v["title"].asText(),
                    price = v["price"].asDouble(),
                    available = v["available"].asBoolean()
                )
            }

            repository.insertProductWithVariants(productId, title, vendor, productType, variants)
        }
    }
}