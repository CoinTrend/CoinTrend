-keep class com.cointrend.data.api.coingecko.models.** { *; }

-keepclassmembers,allowobfuscation class * {
@com.google.gson.annotations.SerializedName <fields>;
}

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *


#-keepattributes Signature
#-keep class kotlin.coroutines.Continuation
#
#-keep class kotlin.** { *; }
#-keep class kotlin.Metadata { *; }
#-dontwarn kotlin.**
#-keepclassmembers class **$WhenMappings {
#    <fields>;
#}
#-keepclassmembers class kotlin.Metadata {
#    public <methods>;
#}
#
#-keepclassmembers class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator CREATOR;
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
##    public static ** valueOf(java.lang.String);
#}