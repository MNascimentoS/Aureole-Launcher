# Add project-specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard

# R8 Full Mode configuration for Aureole Launcher
# Ensures effective obfuscation without broad package-wide keep rules.

# Preserve attributes required for Kotlin metadata and reflection/serialization
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Remove debug and verbose logs (Log.d and Log.v) in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
