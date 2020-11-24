package fr.lebonq.minecraft.Account;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * recupere les informations pour se login
 * 
 * Highly inspired by :  https://www.spigotmc.org/threads/how-to-get-api-mojang-minecraft-client-access-token.159019/
 */
public class Auth {
    private final static String authserver = "https://authserver.mojang.com";

    public static String[] authenticate(String username, String password) throws Exception {

        String genClientToken = UUID.randomUUID().toString();//on cree un uuid random

        // Setting up json POST request
        String payload = "{\"agent\": {\"name\": \"Minecraft\",\"version\": 1},\"username\": \"" + username
                + "\",\"password\": \"" + password + "\",\"clientToken\": \"" + genClientToken + "\"}";

        String output = postReadURL(payload, new URL(authserver + "/authenticate"));

        System.out.println(output);
        
        //On recupere les donnees
        JsonParser vParser = new JsonParser();
        JsonObject vBody = (JsonObject) vParser.parse(output);
        String vAccesToken = vBody.get("accessToken").getAsString();
        String vUUID = vBody.get("selectedProfile").getAsJsonObject().get("id").getAsString();
        String vDisplayName = vBody.get("selectedProfile").getAsJsonObject().get("name").getAsString();

        String[] vReturn = {vAccesToken,vUUID,vDisplayName};
        for (String string : vReturn) {
            System.out.println(string);
        }

        return vReturn;
    }

    private static String postReadURL(String payload, URL url) throws Exception {
        HttpsURLConnection con = (HttpsURLConnection) (url.openConnection());

        con.setReadTimeout(15000);
        con.setConnectTimeout(15000);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream out = con.getOutputStream();
        out.write(payload.getBytes("UTF-8"));
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String output = "";
        String line = null;
        while ((line = in.readLine()) != null)
            output += line;

        in.close();

        return output;
    }

}
