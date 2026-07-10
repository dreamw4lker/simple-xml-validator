package com.github.dreamw4lker.simplexvalfx.utils;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class SSLUtil {
    private static HostnameVerifier defaultHostnameVerifier;
    private static SSLSocketFactory defaultSslSocketFactory;

    public static void enableSSLVerificationIfNeeded() {
        if (defaultSslSocketFactory != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSslSocketFactory);
        }
        if (defaultHostnameVerifier != null) {
            HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
        }
    }

    public static void disableSSLVerification() {
        try {
            // Save defaults before overriding
            defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
            defaultSslSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();

            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
