package com.ai.customer.dto

data class ProductDto(
    val id: Long,
    val title: String,
    val vendor: String?,
    val productType: String?,
    val variants: List<VariantDto> = emptyList()
)
