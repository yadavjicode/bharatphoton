package com.app.bharatphoton.retrofit

import com.app.bharatphoton.services.AddressService
import com.app.bharatphoton.services.AuthService
import com.app.bharatphoton.services.CartService
import com.app.bharatphoton.services.CartSubscriptionService
import com.app.bharatphoton.services.CheckOutService
import com.app.bharatphoton.services.ComplaintService
import com.app.bharatphoton.services.CouponService
import com.app.bharatphoton.services.HomePageService
import com.app.bharatphoton.services.ListingService
import com.app.bharatphoton.services.NotificationService
import com.app.bharatphoton.services.OrderService
import com.app.bharatphoton.services.PayNowService
import com.app.bharatphoton.services.PaymentHistoryService
import com.app.bharatphoton.services.ProductService
import com.app.bharatphoton.services.ProfileService
import com.app.bharatphoton.services.PurchaseSubscriptionService
import com.app.bharatphoton.services.ReportsService
import com.app.bharatphoton.services.ReviewService
import com.app.bharatphoton.services.SubscriptionService
import com.app.bharatphoton.services.WarrantyService
import com.app.bharatphoton.services.WishListService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun AuthService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): AuthService = retrofitClient.retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun ProfileService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): ProfileService = retrofitClient.retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun ListingService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): ListingService = retrofitClient.retrofit.create(ListingService::class.java)

    @Provides
    @Singleton
    fun ProductService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): ProductService = retrofitClient.retrofit.create(ProductService::class.java)

    @Provides
    @Singleton
    fun WishListService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): WishListService = retrofitClient.retrofit.create(WishListService::class.java)

    @Provides
    @Singleton
    fun CartService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): CartService = retrofitClient.retrofit.create(CartService::class.java)

    @Provides
    @Singleton
    fun SubscriptionService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): SubscriptionService = retrofitClient.retrofit.create(SubscriptionService::class.java)

    @Provides
    @Singleton
    fun HomePageService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): HomePageService = retrofitClient.retrofit.create(HomePageService::class.java)

    @Provides
    @Singleton
    fun CouponService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): CouponService = retrofitClient.retrofit.create(CouponService::class.java)

    @Provides
    @Singleton
    fun AddressService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): AddressService = retrofitClient.retrofit.create(AddressService::class.java)

    @Provides
    @Singleton
    fun CheckOutService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): CheckOutService = retrofitClient.retrofit.create(CheckOutService::class.java)

    @Provides
    @Singleton
    fun OrderService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): OrderService = retrofitClient.retrofit.create(OrderService::class.java)

    @Provides
    @Singleton
    fun ReviewService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): ReviewService = retrofitClient.retrofit.create(ReviewService::class.java)

    @Provides
    @Singleton
    fun cartSubscriptionService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): CartSubscriptionService = retrofitClient.retrofit.create(CartSubscriptionService::class.java)

    @Provides
    @Singleton
    fun purchaseSubscriptionService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): PurchaseSubscriptionService = retrofitClient.retrofit.create(PurchaseSubscriptionService::class.java)

    @Provides
    @Singleton
    fun paymentHistoryService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): PaymentHistoryService = retrofitClient.retrofit.create(PaymentHistoryService::class.java)

    @Provides
    @Singleton
    fun complaintService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): ComplaintService = retrofitClient.retrofit.create(ComplaintService::class.java)

    @Provides
    @Singleton
    fun warrantyService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): WarrantyService = retrofitClient.retrofit.create(WarrantyService::class.java)

    @Provides
    @Singleton
    fun payNowService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): PayNowService = retrofitClient.retrofit.create(PayNowService::class.java)

    @Provides
    @Singleton
    fun notificationService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): NotificationService = retrofitClient.retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun reportsService(retrofitClient: com.app.bharatphoton.retrofit.RetrofitClient): ReportsService = retrofitClient.retrofit.create(ReportsService::class.java)


}