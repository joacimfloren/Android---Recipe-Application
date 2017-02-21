package Models;

import java.util.List;

/**
 * Created by Johan Rasmussen on 2016-11-04.
 */

public class Account {
    public double latitude;
    public double longitude;
    public String id;
    public String userName;
    public String Password;



    public List<AccountRecipe> AccountRecipes;
    public List<Comment> Comments;
    public List<Recipe> Recipes;
}
