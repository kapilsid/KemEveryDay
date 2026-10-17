# Pure Kotlin/Compose MVP. Add shrinking rules alongside production SDKs.

# WorkManager (pulled in by Google Mobile Ads) creates its Room database by reflection.
# R8 full mode strips the generated WorkDatabase_Impl constructor without this, crashing release builds at launch.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
