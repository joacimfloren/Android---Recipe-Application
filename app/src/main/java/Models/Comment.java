package Models;

/**
 * Created by Johan Rasmussen on 2016-11-04.
 */

public class Comment {
    public int Id;
    public String Text;
    public int Grade;
    public String Image;
    public int Created;
    public String CommenterId;
    public int RecipeId;

    public Account Account;
    public Recipe Recipe;

    public Comment() {}

    public Comment(String text, int grade, String accountId, int recipeId) {
        this.Text = text;
        this.Grade = grade;
        this.CommenterId = accountId;
        this.RecipeId = recipeId;
    }
}
