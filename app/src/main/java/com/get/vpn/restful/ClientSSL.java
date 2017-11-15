package com.get.vpn.restful;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by istmedia-m1 on 9/14/17.
 */

public class ClientSSL {

    private static Boolean bInit = false;
    private static OkHttpClient.Builder mSSLClientBuilder;
    @Nullable
    public static  OkHttpClient.Builder getClientBuilder(Context context) {
        if (!bInit){
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                //fd.crt fastvd.com 公钥
                InputStream caInput = new BufferedInputStream(context.getAssets().open("fd.crt"));
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    //    Log.i("Longer", "ca=" + ((X509Certificate) ca).getSubjectDN());
                    //    Log.i("Longer", "key=" + ((X509Certificate) ca).getPublicKey());
                } finally {
                    caInput.close();
                }
                // create keystore
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keystore = KeyStore.getInstance(keyStoreType);
                keystore.load(null, null);
                keystore.setCertificateEntry("ca", ca);

                //create TrustManager
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keystore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                SSLContext sslContext = SSLContext.getInstance("TLSv1", "AndroidOpenSSL");
                sslContext.init(null, trustManagers, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                mSSLClientBuilder = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager);
                bInit = true;
            } catch(Exception e)
            {
                bInit = false;
            }
        }

        if (bInit) {
            return mSSLClientBuilder;
        }else {
            return null;
        }

    }


}
