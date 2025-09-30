package com.ai.customer.job

import com.ai.customer.service.ProductService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProductSyncJob(private val productService: ProductService) {

    // run once at startup
    @Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
    fun initialRun() {
        productService.syncProducts()
    }

    // run daily at midnight
    @Scheduled(cron = "0 0 0 * * *")
    fun dailyRun() {
        productService.syncProducts()
    }
}