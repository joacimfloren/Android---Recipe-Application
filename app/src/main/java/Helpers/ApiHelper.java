package Helpers;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import Models.ApiError;

/**
 * Created by rikardolsson on 2016-12-01.
 */

public class ApiHelper {
    private static final Map<String, String> myMap;
    static
    {
        myMap = new HashMap<String, String>();
        myMap.put("UserNameMissing", "Username is missing");
        myMap.put("InvalidUserName", "Invalid username");
        myMap.put("DuplicateUserName", "Username already exist");
        myMap.put("PasswordMissing", "password is missing");
        myMap.put("PasswordTooShort", "Password is to short");
        myMap.put("PasswordRequiresNonAlphanumeric", "Password requires nonalpanumeric");
        myMap.put("PasswordRequiresDigit", "Password requires a digit");
        myMap.put("PasswordRequiresLower", "Password requires lowercase");
        myMap.put("PasswordRequiresUpper", "Password requires upperrcase");
        myMap.put("LongitudeMissing", "Longitude is missing");
        myMap.put("LatitudeMissing", "Lattitude is missing");
        //
        myMap.put("TokenMissing", "You have to be logged in");
        myMap.put("TokenInvalid", "You have to be logged in");
        myMap.put("RecipeIdDoesNotExist", "Recipe does not exist");
        myMap.put("invalid_request", "Invalid request");
        myMap.put("unsupported_grant_type", "Username or password is incorrect");
        myMap.put("invalid_client", "Username or password is incorrect");
        myMap.put("NameMissing", "Name is missing");
        myMap.put("NameWrongLength", "Length of name is to long");
        myMap.put("DescriptionMissing", "You must add a description");
        myMap.put("DescriptionWrongLength", "The description must be between 10 - 300 chars.");
        myMap.put("DirectionsMissing", "You must add a description");
        myMap.put("DirectionOrderMissing", "The order of the description is missing");
        myMap.put("DirectionDescriptionMissing", "You must add text to direction");
        myMap.put("DirectionDescriptionWrongLength", "A direction must be between 5 - 120 chars.");
        myMap.put("TextMissing", "Text is missing");
        myMap.put("TextWrongLength", "Text length is wrong");
        myMap.put("GradeMissing", "you must add a grade");
        myMap.put("CommenterAlreadyComment", "You have already commented on this recipe");
        myMap.put("TermMissing", "Term is missing");
        myMap.put("miss", "You have to fill in username and password");
        myMap.put("401", "Unauthorized.");
        myMap.put("500", "Server error.");
        myMap.put("404", "Bad Request.");
    };

    public static String getError(String err) throws NullPointerException{
        if (err == null || err.equals(""))
            return null;

        return ApiHelper.myMap.get(err);
    }

    public static String getFirstErrorcode(String result) {
        Gson gson = new Gson();
        ApiError e = gson.fromJson(result, ApiError.class);
        String error = null;

        if (e == null)
            return null;

        if (e.error != null) {
            if (!e.error.equals(""))
                error = ApiHelper.getError(e.error);
        } else if (e.errors != null) {
            if (!e.errors.isEmpty())
                error = ApiHelper.getError(e.errors.get(0));
        }

        return error;
    }
}
