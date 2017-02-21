package Models;

import java.io.Serializable;

/**
 * Created by joacimfloren on 2016-11-25.
 */

public class CommentOnRecipe{
    public int id;
    public String text;
    public int grade;
    public Commenter commenter;
    public String image;
    public int created;

    public class Commenter {
        public String id;
        public String userName;
    }
}
