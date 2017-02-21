package Mockup;

import java.util.ArrayList;
import java.util.List;

import Models.Account;
import Models.Comment;
import Models.Direction;
import Models.Recipe;

/**
 * Created by Johan Rasmussen on 2016-11-04.
 */

public class Mockup {
    public static Recipe getRecipe() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Grilled chicken with spinach and lime";
        recipe.image = "";
        recipe.description = "A juciy murva filet with extra crisp and smeg.";
        Account recipeCreator = new Account();
        recipeCreator.userName = "jke";
        recipe.creator = recipeCreator;
        recipe.Comments = new ArrayList<Comment>();
        recipe.directions = new ArrayList<Direction>();

        /* ************************  Comments  *************************** */

        Comment comment1 = new Comment();
        comment1.Image = "";
        comment1.Text = "Tasted like shit.";
        comment1.Grade = 1;
        Account commenter1 = new Account();
        commenter1.userName = "denilson3";
        comment1.Account = commenter1;
        Comment comment2 = new Comment();
        comment2.Image = "";
        comment2.Text = "Alright actually. Like yo mama.";
        comment2.Grade = 3;
        Account commenter2 = new Account();
        commenter2.userName = "rasmussen94";
        comment2.Account = commenter2;
        Comment comment3 = new Comment();
        comment3.Image = "";
        comment3.Text = "Best I've ever had.";
        comment3.Grade = 5;
        Account commenter3 = new Account();
        commenter3.userName = "znittzel_Hitler";
        comment3.Account = commenter3;

        recipe.Comments.add(comment1);
        recipe.Comments.add(comment2);
        recipe.Comments.add(comment3);

        /* ************************  Directions  *************************** */

        Direction direction1 = new Direction(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras vitae ligula metus.");
        Direction direction2 = new Direction(2, "Lorem ipsum dolor sit amet.");
        Direction direction3 = new Direction(3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras murva.");
        recipe.directions.add(direction1);
        recipe.directions.add(direction2);
        recipe.directions.add(direction3);

        return recipe;
    }

    public static void CreateMockupMyRecipes(List<Recipe> mockupMyRecipes){
        mockupMyRecipes.clear();
        Recipe recipe = new Recipe();
        recipe.name = "En natt i berlin";
        mockupMyRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Räserbajs";
        mockupMyRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Napalm ur röven";
        mockupMyRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Bomben i bastun";
        mockupMyRecipes.add(recipe);
    }

    public static void CreateMockupRecipes(List<Recipe> mockupRecipes){
        mockupRecipes.clear();
        Recipe recipe = new Recipe();
        recipe = new Recipe();
        recipe.name = "Gratinerad Murva";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Slashy Margarita";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Lufttorkad Kantarell Murva";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Enåannan corronitta";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Fiskdoppad kabel";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Slemmig rabarber";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Min sallad";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "En natt i berlin";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Varma mackor med slashy sås";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Fin granatäpple";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Räserbajs";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Napalm ur röven";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Ett päron";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Cykeljävel";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Mmm så gott";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Ronnys Honung";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Bänkar lite";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Stuvade mackaroner";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Fläskig corronitttaaaaaa";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Jag hämtar barnen idag";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Oj nu blev det kaos i brallan";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "En liten maträtt";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Mat Tinas Kola";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "För gammalt";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Falukorv";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Bomben i bastun";
        mockupRecipes.add(recipe);
        recipe = new Recipe();
        recipe.name = "Hemlig skit";
        mockupRecipes.add(recipe);
    }
}
