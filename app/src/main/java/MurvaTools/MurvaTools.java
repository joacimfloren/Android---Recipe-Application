package MurvaTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import com.murva.murvapes.LogInAccountActivity;
import com.murva.murvapes.LogInActivity;
import com.murva.murvapes.MainActivity;
import com.murva.murvapes.R;
import com.murva.murvapes.RecipeFeedFragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import Helpers.ApiHelper;
import Models.Account;
import Models.CommentOnRecipe;
import Models.Favorite;
import Models.LogInAccount;
import Models.Recipe;
import Models.Token;
import Models.UserToken;

/**
 * Created by Johan Rasmussen on 2016-11-22.
 */

public class MurvaTools {

    private static String ApiURL = "http://52.211.99.140/api/";
    private static String ApiVersion = "1";

    public static URL getURL(String uri) throws MalformedURLException {
        return new URL(ApiURL + "v" + ApiVersion + "/" + uri);
    }

    public static void FetchRecipesByAccountId(final String accountId, final RecipeFeedFragment recipeFeedFragment){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = MurvaTools.getURL("accounts/"+accountId+"/recipes");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Accept", "application/json");
                    int statusCode = connection.getResponseCode();

                    if(statusCode == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        connection.disconnect();

                        Gson gson = new Gson();
                        JsonReader jsonReader = new JsonReader(new StringReader(sb.toString()));
                        /*InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);
                        String line = scanner.nextLine();
                        scanner.close();
                        connection.disconnect();*/


                        //JsonReader jsonReader = new JsonReader(new StringReader(line));
                        jsonReader.setLenient(true);
                        Recipe[] recipes = gson.fromJson(jsonReader, Recipe[].class);

                        GlobalData.myRecipes = new ArrayList<Recipe>();
                        for(Recipe r : recipes){
                            GlobalData.myRecipes.add(r);
                        }
                    }
                }
                catch(MalformedJsonException e){
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (recipeFeedFragment != null) {
                    recipeFeedFragment.GetRecipeAdapter().updateList(recipeFeedFragment.getRecipeList());
                }
            }
        }.execute();
    }
    public static void FetchRecipesByPage(final int page, final MainActivity mainActivity, final RecipeFeedFragment recipeFeedFragment){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = MurvaTools.getURL("recipes?page=" + page);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Accept", "application/json");
                    int statusCode = connection.getResponseCode();

                    if(statusCode == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        connection.disconnect();
                        /*InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);
                        String line = scanner.nextLine();
                        scanner.close();
                        connection.disconnect();*/

                        Gson gson = new Gson();
                        JsonReader jsonReader = new JsonReader(new StringReader(sb.toString()));
                        //JsonReader jsonReader = new JsonReader(new StringReader(line));
                        jsonReader.setLenient(true);
                        Recipe[] recipes = gson.fromJson(jsonReader, Recipe[].class);

                        for(Recipe r : recipes){
                            GlobalData.recipes.add(r);
                        }
                    }
                }
                catch(MalformedJsonException e){
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (mainActivity != null) {
                   mainActivity.updateRecipeFeed();
                }
                if (recipeFeedFragment != null) {
                    recipeFeedFragment.GetRecipeAdapter().updateList(recipeFeedFragment.getRecipeList());
                }
            }
        }.execute();
    }

    public static void SaveRecipeListToDisk(List<Recipe> recipes, String key, Context context){
        try {
            FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(recipes);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Recipe> LoadRecipeListFromDisk(String key, Context context) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(key);
            ObjectInputStream is = new ObjectInputStream(fis);
            recipes = (List<Recipe>) is.readObject();
            is.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static void DeleteRecipeListFromDisk(String key, Context context){
        try {
            File dir = context.getFilesDir();
            File file = new File(dir, key);
            boolean deleted = file.delete();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void SignUp(final Account acc, final View view, final Context context){
        final Boolean[] ok = {false};

        //Intent intent = new Intent(c)
        new AsyncTask<Void, Void, String>(){
            @Override
            protected void onPreExecute(){
                //textView.setText("Fetching...");
            }

            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;

                try {
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(acc);

                    URL url = MurvaTools.getURL("accounts/password");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");

                    OutputStream outputStream = connection.getOutputStream();
                    OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);


                    outWriter.write(jsonStr);
                    outWriter.flush();
                    outWriter.close();
                    outputStream.close();

                    //connection.connect();
                    String message = connection.getResponseMessage();
                    int statusCode = connection.getResponseCode();
                    switch(statusCode){
                        case 201:
                            return null;
                        case 400:
                            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
                            StringBuilder sb = new StringBuilder();
                            String line;

                            while ((line = br.readLine()) != null) {
                                sb.append(line+"\n");
                            }
                            br.close();
                            return sb.toString();
                        default:
                            return null;
                    }
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                } finally{
                    if(connection != null){
                        connection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Intent intent = new Intent(context, LogInAccountActivity.class);
                //textView.setText(result);
                if(result != null) {
                    showSnackbar(view, ApiHelper.getFirstErrorcode(result));
                    return;
                }
                context.startActivity(intent);
            }
        }.execute();

    }

    public static void showSnackbar(View view, String str){
        Snackbar.make(view, str, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        return;
    }
    public static void LogIn(final LogInAccount acc, final View view, final Context context){
        final Boolean[] ok = {false};

        //Intent intent = new Intent(c)
        new AsyncTask<Void, Void, String>(){

            @Override
            protected void onPreExecute(){
                //textView.setText("Fetching...");
            }

            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    
                    //String str = "grant_type=password&username=David&password=Murva23%";
                    String query = "grant_type="+URLEncoder.encode(acc.grant_type.toString(),"UTF-8");
                    query += "&";
                    query += "username="+URLEncoder.encode(acc.username.toString(),"UTF-8");
                    query += "&";
                    query += "password="+ URLEncoder.encode(acc.password.toString(),"UTF-8") ;

                    URL url = MurvaTools.getURL("tokens/password");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Accept", "application/json");

                    OutputStream outputStream = connection.getOutputStream();
                    OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);

                    outWriter.write(query);
                    outWriter.flush();
                    outWriter.close();
                    outputStream.close();

                    //connection.connect();
                    String message = connection.getResponseMessage();
                    int statusCode = connection.getResponseCode();
                    StringBuilder sb = new StringBuilder();
                    String line;

                    switch (statusCode){
                        case 200:
                            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

                            while ((line = br.readLine()) != null) {
                                sb.append(line+"\n");
                            }

                            Gson gson = new Gson();
                            GlobalData.tokenEncoded = gson.fromJson(sb.toString(), Token.class);
                            readJWT(GlobalData.tokenEncoded.access_token.toString());

                            return null;
                        case 400:
                            BufferedReader bre = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
                            sb = new StringBuilder();
                            while ((line = bre.readLine()) != null) {
                                sb.append(line+"\n");
                            }
                            bre.close();
                            return sb.toString();
                        default:
                            return null;
                    }
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    if(connection != null){
                        connection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                if(result != null) {
                    showSnackbar(view, ApiHelper.getFirstErrorcode(result));
                    return;
                }


                context.startActivity(intent);
                ((Activity) context).finish();

            }

        }.execute();

    }

    public static void FetchRecipeById(final RecipeCallback callback, final int id) {
        new AsyncTask<Void, Void, Recipe>(){
            @Override
            protected Recipe doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = MurvaTools.getURL("recipes/" + id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Accept", "application/json");
                    int statusCode = connection.getResponseCode();

                    if(statusCode == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        connection.disconnect();

                        //Johan
                        /*
                        InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);
                        String line = scanner.nextLine();
                        scanner.close();
                        connection.disconnect();*/

                        Gson gson = new Gson();
                        //JsonReader jsonReader = new JsonReader(new StringReader(line));
                        JsonReader jsonReader = new JsonReader(new StringReader(sb.toString()));
                        jsonReader.setLenient(true);
                        Recipe recipe = gson.fromJson(jsonReader, Recipe.class);
                        return recipe;
                    }
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }

                return null;
            }
            @Override
            protected void onPostExecute(Recipe recipe) {
                if(recipe != null) {
                    callback.callback(recipe);
                }
            }
        }.execute();
    }

    public static void FetchCommentsOfRecipe(final CommentsCallback callback, final Recipe recipe) {
        new AsyncTask<Void, Void, List<CommentOnRecipe>>(){
            @Override
            protected List<CommentOnRecipe> doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {

                    URL url = MurvaTools.getURL("recipes/" + recipe.id + "/comments");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Accept", "application/json");
                    int statusCode = connection.getResponseCode();

                    if(statusCode == 200) {

                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        connection.disconnect();
                        /*InputStream inputStream = connection.getInputStream();

                        Scanner scanner = new Scanner(inputStream);
                        String line = scanner.nextLine();
                        scanner.close();
                        connection.disconnect();*/

                        Gson gson = new Gson();
                        JsonReader jsonReader = new JsonReader(new StringReader(sb.toString()));
                        jsonReader.setLenient(true);
                        CommentOnRecipe[] comments = gson.fromJson(jsonReader, CommentOnRecipe[].class);
                        List<CommentOnRecipe> commentList = new ArrayList<CommentOnRecipe>();
                        for(CommentOnRecipe c : comments){
                            commentList.add(c);
                        }

                        return commentList;
                    }
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }

                return null;
            }
            @Override
            protected void onPostExecute(List<CommentOnRecipe> list) {
                callback.callback(recipe, list);
            }
        }.execute();
    }

    public static void FetchFavoriteRecipes(final MurvaCallback callback) {
        final List<Recipe> favoriteRecipes = new ArrayList<Recipe>();

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = MurvaTools.getURL("accounts/" + GlobalData.tokenDecoded.userId + "/favorites");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
                    connection.addRequestProperty("Accept", "application/json");
                    int statusCode = connection.getResponseCode();

                    if(statusCode == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        connection.disconnect();
                        Gson gson = new Gson();
                        JsonReader jsonReader = new JsonReader(new StringReader(sb.toString()));
                        jsonReader.setLenient(true);
                        Recipe[] recipes = gson.fromJson(jsonReader, Recipe[].class);
                        GlobalData.favouriteRecipes = new ArrayList<Recipe>();
                        for(Recipe r : recipes){
                            GlobalData.favouriteRecipes.add(r);
                        }
                    }
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }

                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callback.callback();
            }
        }.execute();
    }

    public static void CreateComment(final CreateCommentCallback callback, final int recipeId, final Models.Comment comment) {
        new AsyncTask<Void, Void, Pair<Integer, String>>() {
            @Override
            protected Pair<Integer, String> doInBackground(Void... params) {
                HttpURLConnection connection = null;
                Pair<Integer, String> pair = null;

                try {
                    String errors = null;
                    Integer id = null;

                    connection = Connector.getPostConnector(getURL("recipes/" + recipeId + "/comments"), new Gson().toJson(comment));
                    errors = Connector.getError(connection);

                    if (errors == null) {
                        String location = connection.getHeaderField("Location");
                        String[] splittedLocationString = location.split("/");
                        id = Integer.parseInt(splittedLocationString[splittedLocationString.length - 1]);
                    }

                    pair = new Pair<Integer, String>(id, errors);
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                return pair;
            }

            @Override
            protected void onPostExecute(Pair<Integer, String> result) {
                callback.callback(result);
            }
        }.execute();

    }

    public interface RecipePostCallback {
        void pre();
        void post(Pair<Integer, String> response);
    }

    public static void CreateRecipe(final Models.RecipeCreation recipe, final RecipePostCallback cb) {
        new AsyncTask<Void, Void, Pair<Integer, String>>() {
            @Override
            protected void onPreExecute() {
                cb.pre();
            }

            @Override
            protected Pair<Integer, String> doInBackground(Void... params) {
                HttpURLConnection connection = null;
                String errors = null;
                Pair<Integer, String> pair = null;

                try {
                    Integer id = null;

                    connection = Connector.getPostConnector(getURL("recipes"), new Gson().toJson(recipe));
                    errors = Connector.getError(connection);

                    if (errors == null) {
                        String location = connection.getHeaderField("Location");
                        String[] splittedLocationString = location.split("/");
                        id = Integer.parseInt(splittedLocationString[splittedLocationString.length - 1]);
                    }

                    pair = new Pair<Integer, String>(id, errors);

                } catch(IOException e){
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                return pair;
            }

            @Override
            protected void onPostExecute(Pair<Integer, String> response) {
                cb.post(response);
            }
        }.execute();

    }

    public static void PutImage(final String partialUrl, final InputStream imgStream, final PutImageCallback callback) {
        new AsyncTask<Void, Void, Pair<Boolean, String>>() {
            @Override
            protected Pair<Boolean, String> doInBackground(Void... params) {
                HttpURLConnection connection = null;
                Pair<Boolean, String> pair = null;

                try {
                    String errors = null;

                    connection = Connector.getPutImageConnector(getURL(partialUrl), imgStream);
                    errors = Connector.getError(connection);

                    if (errors == null) {
                        pair = new Pair<Boolean, String>(true, errors);
                    }
                    else {
                        pair = new Pair<Boolean, String>(false, errors);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                return pair;
            }

            @Override
            protected void onPostExecute(Pair<Boolean, String> result) {
                callback.callback(result);
            }
        }.execute();
    }

    public static void UpdateAccount(final String username, final String id, final View view){
        new AsyncTask<Void, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                try{
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(username);
                    URL url = new URL("http://52.211.99.140/api/v1/accounts/"+id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("PATCH");
                    connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Content-Type", "application/json");
                    OutputStream outputStream = connection.getOutputStream();
                    OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);

                    outWriter.write(jsonStr);
                    outWriter.flush();
                    outWriter.close();
                    outputStream.close();
                    return connection.getResponseCode();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer statusCode) {
                super.onPostExecute(statusCode);

                if(statusCode != null && (statusCode == 204 || statusCode == 200 )){
                    GlobalData.User.userName = username;
                    showSnackbar(view, "Saved changes");
                } else {
                    showSnackbar(view, "Could not save changes");
                }
            }
        }.execute();
    }

    public static void Delete(final String url , final ErrorCallback callback){
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;
                String error = null;

                try {
                    connection = Connector.getDeleteConnector(getURL(url));
                    error = Connector.getError(connection);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }

                return error;
            }

            @Override
            protected void onPostExecute(String str) {
                callback.callback(str);
            }
        }.execute();
    }

    public static void Update(final String url, final String jsonObject, final ErrorCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;
                String error = null;

                try {
                    connection = Connector.getPatchConnector(getURL(url), jsonObject);
                    error = Connector.getError(connection);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }

                return error;
            }

            @Override
            protected void onPostExecute(String str) {
                callback.callback(str);
            }
        }.execute();
    }

    public static void PutFavoritesToAccount(final Recipe recipe, boolean add) {
        final List<Favorite> favoriteRecipesIds = new ArrayList<Favorite>();

        if (add) {
            GlobalData.favouriteRecipes.add(recipe);
        }
        else {
            for (Recipe r: GlobalData.favouriteRecipes) {
                if (r.id == recipe.id) {
                    GlobalData.favouriteRecipes.remove(r);
                    break;
                }
            }
        }

        for (Recipe r: GlobalData.favouriteRecipes) {
            favoriteRecipesIds.add(new Favorite(r.id));
        }

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection connection = null;

                try {
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(favoriteRecipesIds);

                    URL url = new URL("http://52.211.99.140/api/v1/accounts/" + GlobalData.tokenDecoded.userId + "/favorites");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = connection.getOutputStream();
                    OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);

                    outWriter.write(jsonStr);
                    outWriter.flush();
                    outWriter.close();
                    outputStream.close();

                    String message = connection.getResponseMessage();
                    int statusCode = connection.getResponseCode();

                    StringBuilder sb = new StringBuilder();
                    String line;

                    if (statusCode != 204) {
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }

                        br.close();
                        return sb.toString();
                    }

                    return null;
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
            }
        }.execute();
    }

    public interface ErrorCallback{
        void callback(String error);
    }

    public interface AccountCallback {
        void callback(Account account);
    }

    public interface MurvaCallback {
        void callback();
    }

    public interface RecipeCallback {
        void callback(Recipe recipe);
    }

    public interface CommentsCallback {
        void callback(Recipe recipe, List<CommentOnRecipe> comments);
    }

    public interface CreateCommentCallback {
        public void callback(Pair<Integer, String> response);
    }

    public interface PutImageCallback {
        public void callback(Pair<Boolean, String> result);
    }

    public static void FetchAccountById(final String id, final AccountCallback ac) throws Exception {
        // Fetch stuff..
        new AsyncTask<Void, Void, Account>() {
            @Override
            protected Account doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://52.211.99.140/api/v1/accounts/"+id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + GlobalData.tokenEncoded.access_token);
                    int statusCode = connection.getResponseCode();

                    if(statusCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);
                        String line = scanner.nextLine();
                        scanner.close();
                        connection.disconnect();

                        Gson gson = new Gson();
                        //JsonReader jsonReader = new JsonReader(new StringReader(line));
                        //jsonReader.setLenient(true);
                        Account account = gson.fromJson(line, Account.class);
                        return account;
                    }
                } catch(MalformedJsonException e){
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Account account) {
                super.onPostExecute(account);

                ac.callback(account);
            }
        }.execute();

    }

    public static void SaveToken(Context context){
        try {
            FileOutputStream fos = context.openFileOutput(context.getString(R.string.token_storage), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(GlobalData.tokenEncoded);
            os.close();
            fos.close();

            fos = context.openFileOutput(context.getString(R.string.token_storage_decoded), Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(GlobalData.tokenDecoded);
            os.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void LoadToken(Context context){
        Token token = null;
        UserToken token_decoded = null;
        try {
            FileInputStream fis = context.openFileInput(context.getString(R.string.token_storage));
            ObjectInputStream is = new ObjectInputStream(fis);
            token = (Token) is.readObject();
            is.close();
            fis.close();

            fis = context.openFileInput(context.getString(R.string.token_storage_decoded));
            is = new ObjectInputStream(fis);
            token_decoded = (UserToken) is.readObject();
            is.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(token != null){
            GlobalData.tokenEncoded = token;
            try {
                readJWT(GlobalData.tokenEncoded.access_token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(token_decoded != null){
            GlobalData.tokenDecoded = token_decoded;
        }

    }

    public static void ClearToken(Context context){
        try {
            FileOutputStream fos = context.openFileOutput(context.getString(R.string.token_storage), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(new Token());
            os.close();
            fos.close();

            fos = context.openFileOutput(context.getString(R.string.token_storage_decoded), Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(new UserToken());
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readJWT(String strEncoded) throws Exception{
        String[] line = strEncoded.split(("\\."));
        byte[] byteDecoded = Base64.decode(line[1], Base64.DEFAULT);
        String strDecoded = new String(byteDecoded, "UTF-8");
        Gson gson = new Gson();
        GlobalData.tokenDecoded = gson.fromJson(strDecoded, UserToken.class);
        Log.d("userId: ", GlobalData.tokenDecoded.userId.toString());
        Log.d("exp: ", GlobalData.tokenDecoded.exp.toString());
    }
}
