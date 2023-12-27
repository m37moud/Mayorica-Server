package com.example.data.localizedMessages.languages

import org.koin.core.annotation.Single
import org.thechance.api_gateway.data.localizedMessages.languages.LocalizedMessages

@Single
class EgyptianArabicLocalizedMessages : LocalizedMessages {

    // region identity
    override val invalidRequestParameter: String = "مدخل بيانات غلط يا باشا"
    override val invalidAddressLocation: String = "مش عارف عنوانك يا زميل"
    override val userAlreadyExist: String = "الاسم ده موجود قبل كده، شوفلك غيره"
    override val invalidInformation: String = "امسك حرامي"
    override val invalidFullName: String = "الاسم اللي دخلته مينفعش يا صاحبي"
    override val invalidUsername: String = "انت شفاف يا باشا؟"
    override val passwordCannotBeLessThan8Characters: String = "بالذمة شفت باسورد أقل من 8 حروف يا كبير"
    override val usernameCannotBeBlank: String = "في اي يسطا"
    override val passwordCannotBeBlank: String = "فين الباسورد!! هنخم؟"
    override val invalidEmail: String = "الايميل يسطا!!"
    override val invalidPhone: String ="رقم الموبايل مش صح يا عم !!"
    override val notFound: String = "مش موجود ياعم"
    override val invalidCredentials: String = "امسك حرامي"
    override val userCreatedSuccessfully: String = "اعمل حفلة بقى عندك حساب يا ابن المحظوظة 🎉"
    override val unknownError: String = "حصل ايرور لو كانت عارفه كنت هقولك"
    override val userNotFound: String = "مش لاقينك ياعم"
    override val invalidPermission: String = "مش عندك الصلاحية ياعم"
    override val alreadyInFavorite: String = "موجود يا عم"
    // endregion

    // region taxi
    override val taxiCreatedSuccessfully: String = "اعمل حفلة بقى عندك تكسي يا ابن المحظوظة 🎉"
    override val tripCreatedSuccessfully: String = "تم انشاء الرحلة"
    override val tripUpdated: String = "تم تحديث الرحلة بنجاح"
    override val tripCanceled: String = "تم الغاء الرحلة"
    override val tripFinished: String = "تم إنهاء الرحلة بنجاح"
    override val tripArrived: String = "تم الوصول الى العميل"
    override val receivedNewTrip: String = "رحلة جديدة يا معلم الحق بسرعة"
    override val newOrderComing: String = "اوردر جديد وصل يا كبير"
    override val receivedNewDeliveryOrder: String = "جالك اوردر جديد ابسط يا عم"
    override val taxiUpdateSuccessfully: String = "حدثنالك التاكسي يا عم 🎉"
    override val taxiDeleteSuccessfully: String = "مسحنالك التاكسي مع الاسف"
    override val invalidId: String = "مش لاقيين التاكسي ده!!"
    override val invalidPlate: String = "الرقم اللي دخلته مش مظبوط يعم"
    override val invalidColor: String = "مش مظبوط اللون اللي دخلته يعم"
    override val invalidCarType: String = "مش مظبوط نوع العربية اللي دخلته يعم"
    override val seatOutOfRange: String = "مش مظبوط عدد المقاعد اللي دخلته يعم"
    override val invalidLocation: String = "مش مظبوط المكان اللي دخلته يعم"
    override val invalidRate: String = "مش مظبوط التقييم اللي دخلته  يعم"
    override val invalidDate: String = "مش مظبوط التاريخ اللي دخلته يعم"
    override val invalidPrice: String = "مش مظبوط السعر اللي دخلته يعم"
    override val alreadyExist: String = "التاكسي ده موجود قبل كده ياعم"
    override val requiredQuery: String = " في حاجات ناقصة يا معلم"
    override val rideApproved: String = "تم قبول رحلتك والتاكسى فى طريقه اليك"
    override val taxiArrivedToUserLocation: String = "تم وصول التاكسى الى موقعك"
    override val taxiArrivedToDestination: String = "حمد لله على سلامتك تم الوصول الى الوجهة"

    // endregion

    //region restaurant
    override val restaurantCreatedSuccessfully: String = "بقى عندك مطعم جديد"
    override val restaurantUpdateSuccessfully: String = "حدثنالك المطعم يا سيدي"
    override val restaurantDeleteSuccessfully: String = "مسحنا المطعم خلاص"
    override val restaurantInvalidId: String = "رقم حسابك التعريفي غلط"
    override val restaurantInvalidName: String = "الاسم غلط"
    override val restaurantInvalidLocation: String = "الموقع غلط"
    override val restaurantInvalidDescription: String = "وصف المطعم مش مناسب"
    override val restaurantInvalidPriceLevel: String = "اسعار مطعمك مش متاحة"
    override val restaurantInvalidRate: String = "التقييم مش صالح"
    override val restaurantInvalidPhone: String = "رقم الموبايل غلط"
    override val restaurantInvalidTime: String = "الوقت غلط"
    override val restaurantInvalidPage: String = "الصفحه مش موجودة"
    override val restaurantInvalidPageLimit: String = "عدد الصفحات مش مناسب"
    override val restaurantInvalidOneOrMoreIds: String = "الارقام التعريفيه غلط"
    override val restaurantInvalidPermissionUpdateLocation: String = "إذن تحديث الموقع مش صالح"
    override val restaurantInvalidUpdateParameter: String = "البيانات اللي دخلتها مش مناسبة"
    override val restaurantInvalidPropertyRights: String = "مش عندك حقوق ملكية"
    override val restaurantInvalidPrice: String = "السعر مش مناسب"
    override val restaurantInvalidCuisineLimit: String = "اقصى عدد للمطابخ مش مناسب"
    override val restaurantInvalidAddress: String = "العنوان غلط"
    override val restaurantInvalidEmail: String = "الإيميل غلط"
    override val restaurantInvalidRequestParameter: String = "بيانات الأوردر مش مناسبة"
    override val restaurantErrorAdd: String = "حصل مشكله في الإضافة"
    override val restaurantClosed: String = "المطعم مقفول"
    override val restaurantInsertOrderError: String = "حصل مشكلة في إضافة الأوردر"
    override val restaurantInvalidReceivedOrders: String = "الأوردر اللي وصلنا مش صالح"
    override val restaurantNotFound: String = "مش لاقيين المطعم ده!!"
    override val deletedSuccessfully: String = "مسحناها ياعم"
    override val cuisineNameAlreadyExisted: String = "المطبخ موجود مرة يا عم مش فرح هو"

    override val missingParameter: String = "في حاجة ناقصة يا عم"
    override val tokensNotFound: String = "مش لاقيين التوكنات دي ياعم"
    override val tokenNotRegister: String = "التوكن ده مش مسجل ياعم"
    override val alreadyUpdated: String = "اتحدثت مرة يا عم مش مربة هيه"
    override val cancelOrderError: String = "هذا الطلب تم الغائه من قبل او تم قبوله من قبل"
    override val orderApproved: String = "تم وصول الطلب إلى المطعم وفى انتظار التحضير"
    override val orderCanceled: String = "يؤسفنا إبلاغك بإلغاء طلبك"
    override val orderInCooking: String = "جارى تحضير طلبك فى المطبخ الان"
    override val orderFinished: String = "تم الانتحاء من تحضير طلبك وفى اتظار الديليفرى"
    override val newOrderTitle: String = "طلب جديد"
    override val newOrderBody: String = "لديك طلب جديد فى القائمة"
    override val orderApprovedFromDelivery: String = "تم قبول الطلب من الديليفرى"
    override val orderArrivedToRestaurant: String = "الديليفرى فى طريقه اليك"
    override val orderArrivedToClient: String = "تم وصول طلبك بالهناء والشفاء"
    override val cartIsAlreadyEmpty: String = "هتشتري ازاي والسله فاضيه اصلا!!"
    override val invalidQuantity: String = "مدخل كميه مش صح"

    //endregion

    // region notification
    override val notificationNotSent: String = "لم يتم إرسال الإشعار ياعم"
    //endregion


    // region chat
    override val supportAgentNotFound: String = "لايوجد احد للدعم ياعم"
    //endregion
}
