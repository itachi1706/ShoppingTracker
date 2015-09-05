package com.itachi1706.shoppingtracker.utility;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itachi1706.shoppingtracker.Database.ListDB;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.JSONCart;
import com.itachi1706.shoppingtracker.Objects.ListItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.utility
 */
public class CartJsonHelper {

    public static void storeJsonCart(SharedPreferences sp, JSONCart[] cartList){
        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(cartList, JSONCart[].class);
        sp.edit().putString("cart_json", jsonString).apply();
    }

    public static JSONCart[] getJsonCart(SharedPreferences sp){
        String json = sp.getString("cart_json", "blanked");
        if (json.equals("blanked")){
            return new JSONCart[0];
        }
        Gson gson = new Gson();
        return gson.fromJson(json, JSONCart[].class);
    }

    public static void addCartItemToJsonCart(CartItem item, SharedPreferences sp){
        JSONCart[] items = CartJsonHelper.getJsonCart(sp);
        List<CartItem> cartItems = new ArrayList<>();
        if (items.length == 0){
            //No Items, just add
            cartItems.add(item);
            storeJsonCart(sp, convertJsonCartListToArray(convertCartItemsToJsonCart(cartItems)));
            return;
        }

        //There's existing items, replace list
        cartItems = convertJsonCartToCartItems(items);
        removeExistingItem(cartItems, item);
        cartItems.add(item);
        storeJsonCart(sp, convertJsonCartListToArray(convertCartItemsToJsonCart(cartItems)));
    }

    public static List<CartItem> removeExistingItem(List<CartItem> cartItems, CartItem item){
        for (Iterator<CartItem> iterator = cartItems.iterator(); iterator.hasNext();){
            CartItem cartItem = iterator.next();
            if (cartItem.getId() == item.getId()){
                iterator.remove();
            }
        }

        return cartItems;
    }

    public static void removeCartItemFromJsonCart(CartItem item, SharedPreferences sp){
        JSONCart[] items = CartJsonHelper.getJsonCart(sp);
        List<CartItem> cartItems;
        if (items.length == 0){
            //Nothing to remove
            return;
        }

        cartItems = convertJsonCartToCartItems(items);
        for (Iterator<CartItem> iterator = cartItems.iterator(); iterator.hasNext();){
            CartItem cartItem = iterator.next();
            if (cartItem.getId() == item.getId()){
                iterator.remove();
            }
        }
        storeJsonCart(sp, convertJsonCartListToArray(convertCartItemsToJsonCart(cartItems)));
    }

    public static JSONCart[] convertJsonCartListToArray(List<JSONCart> jsonCartList){
        return jsonCartList.toArray(new JSONCart[jsonCartList.size()]);
    }

    public static List<JSONCart> convertCartItemsToJsonCart(List<CartItem> cartItemList){
        List<JSONCart> jsonCarts = new ArrayList<>();
        for (CartItem item : cartItemList){
            jsonCarts.add(new JSONCart(item.getId(), item.getQty(), item.getBasePrice()));
        }
        return jsonCarts;
    }

    public static List<CartItem> convertJsonCartToCartItems(JSONCart[] jsonCarts){
        List<CartItem> cartItems = new ArrayList<>();

        List<ListItem> listItems = GenerateSampleData.generateAllItemsOnly();
        ListDB db = new ListDB(StaticReferences.APP_CONTEXT);
        List<ListItem> dbItems = db.getAllItems();

        for (ListItem item : dbItems){
            listItems.add(item);
        }

        for (JSONCart cart : jsonCarts){
            ListItem itemToAdd = findCartItem(cart.getId(), listItems);

            if (itemToAdd == null) continue;

            cartItems.add(new CartItem(cart.getId(), cart.getQty(), cart.getBasePrice(), itemToAdd));
        }

        return cartItems;
    }

    private static ListItem findCartItem(long id, List<ListItem> listItems){
        for (ListItem item : listItems){
            if (item.getId() == id){
                return item;
            }
        }
        return null;
    }

}
