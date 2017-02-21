package Models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Johan Rasmussen on 2016-11-04.
 */

public class Recipe implements Comparable<Recipe>, Serializable {

    public int id;
    public String name;
    public String description;
    public String image;
    public int created;
    public String accountId;
    public Account creator;

    public List<AccountRecipe> AccountRecipes;
    public List<Comment> Comments;
    public List<Direction> directions;

    // Needed for sorting
    public int compareTo(Recipe other) {
        return name.compareTo(other.name);
    }

}
