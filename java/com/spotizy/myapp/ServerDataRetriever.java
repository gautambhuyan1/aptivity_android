package com.spotizy.myapp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Gautam_Bhuyan on 1/8/2016.
 */
public class ServerDataRetriever {

    private static final int HTTP_STATUS_OK = 200;

    public static class ServerException extends Exception {
        public ServerException(String msg) {
            super(msg);
        }

        public ServerException(String msg, Throwable thr) {
            super (msg, thr);
        }
    }


    protected static synchronized String getFromServer (String url) throws ServerException {

        HttpClient client = new DefaultHttpClient();
        HttpGet req = new HttpGet(url);
        byte[] buf = new byte[1024];
        String retVal = null;
        System.out.println("#####  Retrieving from server "+url);

        try {
            //System.out.println("#### Tyring here"+req.toString());
            HttpResponse rsp = client.execute(req);
            //System.out.println("#####  Retrieving rsp "+rsp.toString());
            StatusLine status = rsp.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                //System.out.println("#####  Error");
                throw new ServerException("Invalid response");
            }
            HttpEntity entity = rsp.getEntity();
            InputStream ist = entity.getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readcnt = 0;
            while ((readcnt = ist.read(buf)) != -1) {
                //System.out.println("#####  Count = "+readcnt    );
                out.write(buf, 0, readcnt);
            }
            retVal = new String(out.toByteArray());
        }
        catch (Exception e) {
            System.out.println("#### Failed "+e.getMessage());
            throw new ServerException("Improper response from server"+e.getMessage(), e);
        }
        System.out.println("#####  My string:"+retVal);
        return retVal;
    }
}
