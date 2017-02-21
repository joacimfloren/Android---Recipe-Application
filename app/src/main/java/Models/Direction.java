package Models;

/**
 * Created by Johan Rasmussen on 2016-11-04.
 */

public class Direction {
    public int Id;
    public int order;
    public String description;
    public int RecipeId;
    public Recipe Recipe;

    public  Direction(int order, String description) {
        this.order = order;
        this.description = description;
    }
}
