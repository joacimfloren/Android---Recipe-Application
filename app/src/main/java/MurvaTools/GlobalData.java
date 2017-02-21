package MurvaTools;

import java.util.ArrayList;
import java.util.List;

import Models.Account;
import Models.Recipe;
import Models.Token;
import Models.UserToken;

/**
 * Created by Johan on 2016-11-22.
 */

public class GlobalData {
    public static List<Recipe> recipes = new ArrayList<Recipe>();
    public static List<Recipe> myRecipes = new ArrayList<Recipe>();
    public static List<Recipe> favouriteRecipes = new ArrayList<Recipe>();
    public static Token userToken = new Token();
    public static Token tokenEncoded = new Token();
    //API-token dekoded
    public static UserToken tokenDecoded = new UserToken();
    public static Account User = new Account();
}
