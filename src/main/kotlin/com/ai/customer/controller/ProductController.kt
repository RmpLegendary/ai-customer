package com.ai.customer.controller

import com.ai.customer.dto.ProductDto
import com.ai.customer.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class ProductController(private val service: ProductService) {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("products", emptyList<ProductDto>())
        return "products"
    }

    @GetMapping("/products")
    fun getProducts(@RequestParam(defaultValue = "50") limit: Int, model: Model): String {
        model.addAttribute("products", service.getProducts(limit))
        return "fragments/product-table :: table"
    }

    @PostMapping("/products")
    fun addProduct(
        @RequestParam title: String,
        @RequestParam vendor: String?,
        @RequestParam productType: String?,
        @RequestParam variantTitle: String,
        @RequestParam variantPrice: Double,
        @RequestParam(required = false, defaultValue = "false") variantAvailable: Boolean,
        model: Model
    ): String {
        service.addProductWithVariant(title, vendor, productType, variantTitle, variantPrice, variantAvailable)
        model.addAttribute("products", service.getProducts(50))
        return "fragments/product-table :: table"
    }
}