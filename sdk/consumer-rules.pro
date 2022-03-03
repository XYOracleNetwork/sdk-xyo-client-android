-keep class network.xyo.**,** { *; }
-keepclassmembers class network.xyo.**,** { *; }

-keep class org.spongycastle.**
-dontwarn org.spongycastle.jce.provider.X509LDAPCertStoreSpi
-dontwarn org.spongycastle.x509.util.LDAPStoreHelper