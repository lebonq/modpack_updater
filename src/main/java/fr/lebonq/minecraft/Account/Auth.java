package fr.lebonq.minecraft.account;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.codec.Charsets;
import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;

/**
 * recupere les informations pour se login
 * 
 * Highly inspired by :
 * https://www.spigotmc.org/threads/how-to-get-api-mojang-minecraft-client-access-token.159019/
 */
public class Auth {
    static final String AUTHSERVER = "https://authserver.mojang.com";

    private Auth() {
        throw new IllegalStateException("Auth Class");
    }

    public static String[] authenticate(String username, String password, String pClientToken) throws IOException {

        String genClientToken = null;
        if (pClientToken == null)
            genClientToken = UUID.randomUUID().toString();// on cree un uuid random
        else
            genClientToken = pClientToken;

        // Setting up json POST request
        String payload = "{\"agent\": {\"name\": \"Minecraft\",\"version\": 1},\"username\": \"" + username
                + "\",\"password\": \"" + password + "\",\"clientToken\": \"" + genClientToken + "\"}";

        String output = postReadURL(payload, new URL(AUTHSERVER + "/authenticate"));

        AppController.LOGGER.log(Level.INFO,"{}",output);

        // On recupere les donnees
        JsonParser vParser = new JsonParser();
        JsonObject vBody = (JsonObject) vParser.parse(output);
        String vAccesToken = vBody.get("accessToken").getAsString();
        String vClientToken = vBody.get("clientToken").getAsString();
        String vUUID = vBody.get("selectedProfile").getAsJsonObject().get("id").getAsString();
        String vDisplayName = vBody.get("selectedProfile").getAsJsonObject().get("name").getAsString();

        String[] vReturn = { vAccesToken, vUUID, vDisplayName, vClientToken };
        for (String string : vReturn) {
            AppController.LOGGER.log(Level.INFO,"{}",string);
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
            String output = postReadURL(payload, new URL(AUTHSERVER + "/validate"));
            if(output.isEmpty()) return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String refresh(String pAccessToken, String pClientToken) throws IOException{
        String payload = "{\"accessToken\": \"" + pAccessToken + "\",\"clientToken\": \"" + pClientToken + "\"}";
        String response = postReadURL(payload, new URL(AUTHSERVER + "/refresh"));
        JsonParser vParser =  new JsonParser();
        return vParser.parse(response).getAsJsonObject().get("accessToken").getAsString();
    }

    private static String postReadURL(String payload, URL url) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) (url.openConnection());

        con.setReadTimeout(15000);
        con.setConnectTimeout(15000);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream out = con.getOutputStream();
        out.write(payload.getBytes(Charsets.UTF_8));
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder vBd = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null)
            vBd.append(line);

        in.close();

        return vBd.toString();
    }

}
