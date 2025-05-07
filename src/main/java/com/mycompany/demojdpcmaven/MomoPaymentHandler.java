package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MomoPaymentHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) requestBody.append(line);
        reader.close();

        try {
            JSONObject req = new JSONObject(requestBody.toString());

            String amount = req.getString("amount");
            String orderInfo = req.getString("orderInfo");

            String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
            String partnerCode = "MOMO";
            String accessKey = "F8BBA842ECF85";
            String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
            String orderId = System.currentTimeMillis() + "";
            String requestId = orderId;
            String redirectUrl = "https://your-react-app.com/payment-success";
            String ipnUrl = "https://your-backend.com/api/momo-ipn";
            String requestType = "captureWallet";
            String extraData = "";

            String raw = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(raw, secretKey);

            JSONObject body = new JSONObject();
            body.put("partnerCode", partnerCode);
            body.put("accessKey", accessKey);
            body.put("requestId", requestId);
            body.put("amount", amount);
            body.put("orderId", orderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", redirectUrl);
            body.put("ipnUrl", ipnUrl);
            body.put("extraData", extraData);
            body.put("requestType", requestType);
            body.put("signature", signature);
            body.put("lang", "vi");

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(endpoint);
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            CloseableHttpResponse res = client.execute(post);
            String responseBody = EntityUtils.toString(res.getEntity(), StandardCharsets.UTF_8);
            client.close();

            byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
            String errJson = "{\"error\":\"" + e.getMessage() + "\"}";
            byte[] responseBytes = errJson.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.getResponseBody().close();
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKeySpec);
        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
