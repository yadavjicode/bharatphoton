package com.app.bharatphoton.retrofit

object  Constants {
    const val SHARED_PREF_NAME = "shared_pref"

    object SharedPref {
        var AUTH_TOKEN = "auth_token"
    }

    object EndPoints {
        //Authentication
        const val REGISTER = "register"
        const val VERIFY_OTP = "verifyOtp"
        const val LOGIN = "login"
        const val CHANGE_PASSWORD = "changePassword"
        const val DELETE_ACCOUNT = "deleteAccount"
        const val UPDATE_DEVICE_TOKEN = "updateDeviceToken"
        const val SOCIAL_LOGIN = "socialLogin"
        const val RESENDOTP = "resendOtp"
        const val FORGET_PASSWORD = "forgetPassword"
        const val RESET_PASSWORD = "resetPassword"
        const val ADD_QUERY = "addQuote"

        // s3
        const val S3 = "s3"

        // Profile
        const val EDIT_USER = "editUser"
        const val USER_PROFILE = "userProfile"
        const val CHANGE_EMAIL_PHONE = "chnageEmailPhoneno"
        const val VERIFY_EMAIL_PHONE = "verifyPhoneEmail"

        // Listing
        const val CATEGORY_LISTING = "categoryListing"
        const val SUB_CATEGORY_LISTING = "subcategoryListing"
        const val STATE_LISTING = "stateListing"
        const val CITY_LISTING = "cityListing"
        const val PRODUCT_LISTING = "productByCategoryId"

        // Products
        const val PRODUCT_DETAILS = "productDetails"
        const val PRODUCT_LIST = "productListing"
        const val SUB_CAT_PRODUCT_LIST = "listByCatSubcategory"
        const val INSTALLED_PRODUCT_LIST = "installedProductListing"

        // Wishlist
        const val ADD_TO_WISHLIST = "addToWishlist"
        const val WISHLIST = "wishlistListing"
        const val DELETE_WISHLIST = "deleteFromWishlist/{id}"

        // CMS
        const val CMS_DATA = "cmsData"
        const val FAQ_LIST = "faqList"
        const val TROUBLESHOOT_VIDEO = "getTroubleVideo"

        // Cart
        const val ADD_TO_CART = "addToCart"
        const val CART_ITEM_COUNT = "cartItemCount"
        const val CART_DETAILS = "cartDetails"
        const val REMOVE_FROM_CART = "removeFromCart"
        const val SUBSCRIPTION_LIST = "subscriptionListing"

        // Home
        const val HOME_PAGE = "homepage"

        // Coupon
        const val COUPON_LIST = "couponList"
        const val APPLY_COUPON = "applyCoupon"
        const val REMOVE_COUPON = "removeCoupon"

        // Address
        const val ADDRESS_LIST = "addressList"
        const val ADD_ADDRESS = "addAddress"
        const val DELETE_ADDRESS = "removeAddress/{id}"
        const val UPDATE_DELIVERY_ADDRESS = "updateDeliveryAddress"
        const val ADD_INSTALLATION_ADDRESS = "addInstallationAddress"

        // Checkout
        const val CHECK_OUT = "checkout"
        const val CHECK_INSTALLATION = "installation"
        const val REMOVE_INSTALLATION = "removeInstallation"


        // Order
        const val CREATE_ORDER = "createOrder"
        const val ORDER_LISTING = "orderListing"
        const val ORDER_DETAILS = "orderDetails"
        const val CANCEL_ORDER = "cancelOrder"
        const val VERIFY_PAYMENT = "verifyPayment"
        const val RE_ORDER = "reorder"


        // Review
        const val ADD_REVIEW = "addReview"
        const val REVIEW_LIST = "reviewList"

        // Subscription
        const val CHECK_SUBSCRIPTION = "subscriptionInstallation"
        const val SUBSCRIPTION_LISTING = "subscriptionListing"
        const val ADD_SUBSCRIPTION_TO_CART = "addProductSubsciption"
        const val SUBSCRIPTION_CART_LIST = "subscriptionCartList"
        const val UPDATE_QUANTITY = "updateQuantity"
        const val PURCHASE_SUBSCRIPTION = "purchaseSubscription"
        const val RENEW_PLAN = "renewPlan"
        const val VERIFY_SUBSCRIPTION_PAYMENT = "verifysubscriptionPayment"
        const val PURCHASED_SUBSCRIPTION_LISTING = "purchasedSubListing"
        const val SUBSCRIPTION_SERVICE_LOG = "subscriptionServiceLog"
        const val SIN_DETAILS_SERVICE_LOG = "productServiceLog"
        const val PAYMENT_HISTORY = "paymentHistory"

        // Complaint
        const val REGISTER_COMPLAINT = "registerComplaint"
        const val COMPLAINT_LISTING = "complaintListing"
        const val SIN_DETAILS = "sinDetails"

        // warranty
        const val AVAILABLE_WARRANTY = "avilableWarrenty"
        const val WARRANTY_LIST = "warrentyList"
        const val RENEW_WARRANTY = "renewWarrenty"
        const val VERIFY_WARRANTY_PAYMENT = "verifyWarrentyPayment"

        // Pay Now
        const val PAY_NOW = "payNow"
        const val VERIFY_ONLINE_PAYMENT = "verifyOnlinePayment"

        // Notification
        const val NOTIFICATION_LIST = "notificationListing"
        const val NOTIFICATION_COUNT = "notificationCount"

        // Reports
        const val REPORTS = "userReport"
        const val MONTHLY_REPORT = "monthelyReport"

    }
}