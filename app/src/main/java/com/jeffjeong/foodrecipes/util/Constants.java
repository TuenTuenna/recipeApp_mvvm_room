package com.jeffjeong.foodrecipes.util;

// 상수를 모아놓은 클래스
public class Constants {

    // 베이스 URL
    public static final String BASE_URL = "https://www.food2fork.com";

    // 네트워크 시간제한 3초 -> 3초 까지만 데이터를 기다린다.
    public static final int NETWORK_TIMEOUT = 3000;

    public static final int CONNECTION_TIMEOUT = 10; // 10 seconds
    public static final int READ_TIMEOUT = 2; // 2 seconds
    public static final int WRITE_TIMEOUT = 2; // 2 seconds

    public static final int RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; // 30 days (in seconds)

    // 90cbcb2c55003dab09a9ae03f3296239
    // 7e08f11ca009d1d5e1cdd8efc81a0978
    public static final String API_KEY = "7e08f11ca009d1d5e1cdd8efc81a0978";

    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };


}
