package com.example.test.Service.ServiceImpl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class TestService {


    ObjectMapper objectMapper = new ObjectMapper() {
        @Override
        public <T> T readValue(String s, Class<T> aClass) {
            return null;
        }

        @Override
        public String writeValue(Object o) {
            return null;
        }
    };
    public boolean isValidDate(String date) {
        // Define the date pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Set lenient to false to strictly check the pattern
        dateFormat.setLenient(false);

        try {
            // Try parsing the date
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public String getTest(String date) {
        // Define the date pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Set lenient to false to strictly check the pattern
        dateFormat.setLenient(false);

        try {
            // Try parsing the date
            dateFormat.parse(date);
            return date;
        } catch (ParseException e) {
            return date;
        }
    }


    public String getSoapGetCurrentAccount(String accountNumber, String username, String password) {

        String soapBody =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                        "xmlns:axis=\"http://ws.apache.org/axis2\" xmlns:xsd=\"http://fi/xsd\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<axis:GetCurrentAccount>" +
                        "<axis:request>" +
                        "<xsd:accountNumber>" + accountNumber + "</xsd:accountNumber>" +
                        "<xsd:password>" + password + "</xsd:password>" +
                        "<xsd:username>" + username + "</xsd:username>" +
                        "</axis:request>" +
                        "</axis:GetCurrentAccount>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        HttpResponse<String> response = Unirest.post("http://192.168.220.53:8080/axis2/services/CBLFIWebServices.CBLFIWebServicesHttpSoap11Endpoint/")
                .header("Content-Type", "text/xml;charset=UTF-8")
                .header("SOAPAction", "urn:GetCurrentAccount")
                .body(soapBody)
                .asString();

        return response.getBody();
    }

    public String getWSO2API(Object request){

        String  jsonString;
        //JsonNode nnode = null;

        try {
            String json = "{\"a\": 10, \"b\": \"hello\"}";
            JsonNode node = new JsonNode(json);

            int a = node.getObject().optInt("a");
            String b = node.getObject().optString("b");

            System.out.println(a); // 10
            System.out.println(b); // hello

            //jsonString = objectMapper.writeValue(request);

            //nnode = new JsonNode(jsonString);

            //String status = node.getObject().getString("accountNumber");

            String tokenUrl = "https://apim-uat01.thecitybank.com:9443/oauth2/token";
            String apiUrl   = "https://apim-uat01.thecitybank.com/fi/doFinacleFI/v1";

            // Step 1: Request Token
            HttpResponse<JsonNode> tokenResponse = Unirest.post(tokenUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("grant_type", "password")
                    .field("client_id", "oeeInnFYql83LF2hOnIg4PVsIJQa")
                    .field("client_secret", "lsIaDQB7A6V9jykjSjrUZ7FpqQ4a")
                    .field("username", "LWF")
                    .field("password", "u2tlwf25")
                    .field("scope", "lwf_scope")
                    .asJson();

            if (tokenResponse.getStatus() != 200) {
                System.out.println("Failed to get token: " + tokenResponse.getStatusText());
            }

            JSONObject tokenJson = tokenResponse.getBody().getObject();
            String accessToken = tokenJson.getString("access_token");
            System.out.println("Access Token: " + accessToken);

            // Step 2: Call protected API with cbNO in JSON body
            HttpResponse<JsonNode> apiResponse = Unirest.post(apiUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .body("{\n" +
                            "    \"channelName\": \"CSW\",\n" +
                            "    \"channelSecret\": \"string\",\n" +
                            "    \"channelTransactionId\": \"18521768379328565\",\n" +
                            "    \"InitSolId\": \"101\",\n" +
                            "    \"IsMultiRec\": \"Y\",\n" +
                            "    \"Shakil\" : \"\",\n" +
                            "    \"PartTrnRec\": [\n" +
                            "        {\n" +
                            "            \"AcctId\": \"3102527499001\",\n" +
                            "            \"amountValue\": \"100\",\n" +
                            "            \"CreditDebitFlg\": \"C\",\n" +
                            "            \"currencyCode\": \"BDT\",\n" +
                            "            \"PartTrnRmks\": \"072825020141\",\n" +
                            "            \"Rate\": \"\",\n" +
                            "            \"RefNum\": \"072825020141\",\n" +
                            "            \"SerialNum\": \"TRN0\",\n" +
                            "            \"TransactionCode\": \"720\",\n" +
                            "            \"TresRate\": \"\",\n" +
                            "            \"TrnParticulars\": \"072825020141U01- MARGIN INCREASE/DECREASE-MARGIN|CS/CB2527499/20210439\",\n" +
                            "            \"ValueDt\": \"2025-05-01T00:00:00.000\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"AcctId\": \"1861122601002\",\n" +
                            "            \"amountValue\": \"100\",\n" +
                            "            \"CreditDebitFlg\": \"D\",\n" +
                            "            \"currencyCode\": \"BDT\",\n" +
                            "            \"PartTrnRmks\": \"072825020141\",\n" +
                            "            \"Rate\": \"\",\n" +
                            "            \"RefNum\": \"072825020141\",\n" +
                            "            \"SerialNum\": \"TRN1\",\n" +
                            "            \"TransactionCode\": \"720\",\n" +
                            "            \"TresRate\": \"\",\n" +
                            "            \"TrnParticulars\": \"072825020141U01- MARGIN INCREASE/DECREASE-MARGIN|CS/CB2527499/20210439\",\n" +
                            "            \"ValueDt\": \"2025-05-01T00:00:00.000\"\n" +
                            "        }\n" +
                            "    ],\n" +
                            "    \"TrnSubType\": \"CI\",\n" +
                            "    \"TrnType\": \"T\"\n" +
                            "}")
                    .asJson();

//            HttpResponse<JsonNode> apiResponse = Unirest.post(apiUrl)
//                    .header("Authorization", "Bearer " + accessToken)
//                    .header("Content-Type", "application/json")
//                    .body(jsonString)
//                    .asJson();

            System.out.println("API Response Code: " + apiResponse.getStatus());
            System.out.println("API Response Body: " + apiResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "sendResponse";
    }
}
