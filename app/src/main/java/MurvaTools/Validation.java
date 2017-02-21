package MurvaTools;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import Helpers.ApiHelper;
import Models.Account;
import Models.Direction;
import Models.DirectionCreation;
import Models.LogInAccount;
import Models.Recipe;
import Models.RecipeCreation;

/**
 * Created by denilsson2 on 2016-11-30.
 */

public class Validation {
    private static Pattern pUsername = Pattern.compile("[A-Za-z0-9_]+");
    private static Pattern pAlphaNum = Pattern.compile("[^a-zA-Z0-9]");
    private static Pattern pDigit = Pattern.compile("[0-9]");
    private static Pattern pLetters = Pattern.compile("[A-Za-z]");
    private static Pattern pLettersLower = Pattern.compile("[a-z]");
    private static Pattern pLettersUpper = Pattern.compile("[A-Z]");

    public static Boolean validateSignUp(Account acc, View view){
        Boolean ok = true;
        List<String> list = new ArrayList<String>();
        if(ok && (acc.userName.equals("") || acc.userName.equals(null))){
            list.add("UserNameMissing");
            //e.errors.add("UserNameMissing");
            ok = false;
        }
        if(ok && (acc.userName == "") || (acc.userName.length() < 5)){
            list.add("InvalidUserName");
            ok = false;
        }
        if(ok && acc.Password == ""){
            list.add("PasswordMissing");
            ok = false;
        }
        if(ok && acc.Password.length() < 5){
            list.add("PasswordTooShort");
            ok = false;
        }
        if(ok && !pAlphaNum.matcher(acc.Password).find()){
            list.add("PasswordRequiresNonAlphanumeric");
            ok = false;
        }
        if(ok && !pDigit.matcher(acc.Password).find()){
            list.add("PasswordRequiresDigit");
            ok = false;
        }
        if(ok && !pLettersLower.matcher(acc.Password).find()){
            list.add("PasswordRequiresLower");
            ok = false;
        }
        if(ok && !pLettersUpper.matcher(acc.Password).find()){
            list.add("PasswordRequiresUpper");
            ok = false;
        }
        if (ok){
            return ok;
        }

        MurvaTools.showSnackbar(view, ApiHelper.getError(list.get(0).toString()));
        return ok;
    }
    public static Boolean validateLogIn(LogInAccount acc, View view){
        Boolean ok = true;
        List<String> list = new ArrayList<String>();
        if (acc.password.equals("") || acc.password.equals(null) || acc.username.equals("") || acc.username.equals(null)) {
            list.add("miss");
            ok = false;
        }
        if(ok){
            return ok;
        }
        MurvaTools.showSnackbar(view, ApiHelper.getError(list.get(0).toString()));
        return ok;
    }

    public static List<String> validateCreateRecipe(RecipeCreation recipe) {
        List<String> errors = new ArrayList<String>();

        if (recipe.name.length() < 5 || recipe.name.length() > 70)
            errors.add("Recipe name must be between 5 and 70 chars.");

        if (recipe.description.length() < 10 || recipe.description.length() > 300)
            errors.add("Description must be between 10 and 300 chars.");

        if (recipe.directions.isEmpty())
            errors.add("Has to have at least one direction.");

        List<Integer> orders = new ArrayList<Integer>();
        if (!recipe.directions.isEmpty()) {
            for (int i = 0; i < recipe.directions.size(); i++) {
                DirectionCreation direction = recipe.directions.get(i);

                if (direction.description.length() < 5 || direction.description.length() > 120)
                    errors.add("Directions (" + Integer.toString(direction.order) + ") must be between 5 and 120 chars.");

                if (!orders.contains(direction.order)) {
                    orders.add(direction.order);
                } else {
                    errors.add("Each order must be unique.");
                }
            }
        }

        return errors;
    }
}
