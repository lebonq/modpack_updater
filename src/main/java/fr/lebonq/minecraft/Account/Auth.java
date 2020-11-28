package fr.lebonq.minecraft.Account;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * recupere les informations pour se login
 * 
 * Highly inspired by :
 * https://www.spigotmc.org/threads/how-to-get-api-mojang-minecraft-client-access-token.159019/
 */
public class Auth {
    private final static String authserver = "https://authserver.mojang.com";

    public static String[] authenticate(String username, String password, String pClientToken) throws Exception {

        String genClientToken = null;
        if (pClientToken == null)
            genClientToken = UUID.randomUUID().toString();// on cree un uuid random
        else
            genClientToken = pClientToken;

        // Setting up json POST request
        String payload = "{\"agent\": {\"name\": \"Minecraft\",\"version\": 1},\"username\": \"" + username
                + "\",\"password\": \"" + password + "\",\"clientToken\": \"" + genClientToken + "\"}";

        String output = postReadURL(payload, new URL(authserver + "/authenticate"));

        System.out.println(output);

        // On recupere les donnees
        JsonParser vParser = new JsonParser();
        JsonObject vBody = (JsonObject) vParser.parse(output);
        String vAccesToken = vBody.get("accessToken").getAsString();
        String vClientToken = vBody.get("clientToken").getAsString();
        String vUUID = vBody.get("selectedProfile").getAsJsonObject().get("id").getAsString();
        String vDisplayName = vBody.get("selectedProfile").getAsJsonObject().get("name").getAsString();

        String[] vReturn = { vAccesToken, vUUID, vDisplayName, vClientToken };
        for (String string : vReturn) {
            System.out.println(string);
        }

        return vReturn;
    }

    /**
     * Permet de valider l'access token cad peut etre lancer avec Minecraft
     * @param pAccessToken
     * @param pClientToken
     * @return
     */
    public static boolean validate(String pAccessToken, String pClientToken) {
        String payload = "{\"accessToken\": \"" + pAccessToken + "\",\"clientToken\": \"" + pClientToken + "\"}";

        try {
            String output = postReadURL(payload, new URL(authserver + "/validate"));
            //System.out.println(output);
            if(output.isEmpty()) return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String refresh(String pAccessToken, String pClientToken) throws Exception{
        String payload = "{\"accessToken\": \"" + pAccessToken + "\",\"clientToken\": \"" + pClientToken + "\"}";
        String response = postReadURL(payload, new URL(authserver + "/refresh"));
        JsonParser vParser =  new JsonParser();
        String vToken = vParser.parse(response).getAsJsonObject().get("accessToken").getAsString();
        
        //System.out.println(vToken);
        return vToken;
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
