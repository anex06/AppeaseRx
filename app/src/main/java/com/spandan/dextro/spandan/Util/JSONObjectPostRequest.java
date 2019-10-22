package com.spandan.dextro.spandan.Util;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JSONObjectPostRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private JSONObject params;

    public JSONObjectPostRequest(int method, String url, JSONObject params,
                                 Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }


    public JSONObjectPostRequest(String url, JSONObject params,
                                 Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}

